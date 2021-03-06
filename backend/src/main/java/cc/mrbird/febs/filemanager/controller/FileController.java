package cc.mrbird.febs.filemanager.controller;

import cc.mrbird.febs.common.authentication.JWTUtil;
import cc.mrbird.febs.common.domain.FebsResponse;
import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.ProjectUtil;
import cc.mrbird.febs.filemanager.model.*;
import cc.mrbird.febs.filemanager.service.*;
import cc.mrbird.febs.filemanager.util.*;
import cc.mrbird.febs.project.domain.ProjectPeople;
import cc.mrbird.febs.project.domain.TUserInfo;
import cc.mrbird.febs.project.service.ProjectPeopleService;
import cc.mrbird.febs.project.service.TUserInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/uploader")
public class FileController {
    @Value("${prop.upload-folder}")
    private String uploadFolder;

    @Resource
    private FileInfoService fileInfoService;

    @Resource
    private ChunkService chunkService;

    @Resource
    private ProjectPeopleService projectPeopleService;

    @Resource
    private TUserInfoService tUserInfoService;

    private final Logger logger = LoggerFactory.getLogger(FileController.class);

    //从shiro中得到用户信息
    String getUsername() {
        String username = "";
        String token = (String) SecurityUtils.getSubject().getPrincipal();
        if (StringUtils.isNotBlank(token)) {
            username = JWTUtil.getUsername(token);
        }
        return username;
    }


    /**
     * 上传文件块
     *
     * @param chunk
     * @return
     */
    @PostMapping("/chunk")
    public String uploadChunk(TChunkInfo chunk) {
        String apiRlt = "200";

        MultipartFile file = chunk.getUpfile();
        logger.info("file originName: {}, chunkNumber: {}", file.getOriginalFilename(), chunk.getChunkNumber());

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(FileInfoUtils.generatePath(uploadFolder, chunk));
            //文件写入指定路径
            Files.write(path, bytes);
            if (chunkService.saveChunk(chunk) < 0) apiRlt = "415";

        } catch (IOException e) {
            e.printStackTrace();
            apiRlt = "415";
        }
        return apiRlt;
    }

    @GetMapping("/chunk")
    public UploadResult checkChunk(TChunkInfo chunk, HttpServletResponse response) {
        UploadResult ur = new UploadResult();

        //默认返回其他状态码，前端不进去checkChunkUploadedByResponse函数，正常走标准上传
        response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);

        String file = uploadFolder + "/" + chunk.getIdentifier() + "/" + chunk.getFilename();

        //先判断整个文件是否已经上传过了，如果是，则告诉前端跳过上传，实现秒传
        if (FileInfoUtils.fileExists(file)) {
            ur.setSkipUpload(true);
            ur.setLocation(file);
            response.setStatus(HttpServletResponse.SC_OK);
            ur.setMessage("完整文件已存在，直接跳过上传，实现秒传");
            return ur;
        }

        //如果完整文件不存在，则去数据库判断当前哪些文件块已经上传过了，把结果告诉前端，跳过这些文件块的上传，实现断点续传
        ArrayList<Integer> list = chunkService.checkChunk(chunk);
        if (list != null && list.size() > 0) {
            ur.setSkipUpload(false);
            ur.setUploadedChunks(list);
            response.setStatus(HttpServletResponse.SC_OK);
            ur.setMessage("部分文件块已存在，继续上传剩余文件块，实现断点续传");
            return ur;
        }
        return ur;
    }

    @PostMapping("/mergeFile")
    public String mergeFile(@RequestBody TFileInfoVO fileInfoVO) throws FebsException {

        String rlt = "FAILURE";

        //前端组件参数转换为model对象
        TFileInfo fileInfo = new TFileInfo();
        fileInfo.setFilename(fileInfoVO.getName());
        fileInfo.setIdentifier(fileInfoVO.getUniqueIdentifier());
        fileInfo.setId(fileInfoVO.getId());
        fileInfo.setTotalSize(fileInfoVO.getSize());
        fileInfo.setUploadBy(this.getUsername());

        String role = ProjectUtil.getUserRole();
        if (role.equals("学生") || role.equals("项目经理")) {
            String userSid = this.tUserInfoService.findByUsername(this.getUsername()).getSid();
            String pid;

            List<ProjectPeople> projectPeople = this.projectPeopleService.findBySid(userSid);
            //防止一个用户对应多个项目（这个后续需要可以更改）
            if (projectPeople.size() == 1) {
                pid = projectPeople.get(0).getPid();
                fileInfo.setRefProjectId(pid);
            } else {
                throw new FebsException("一个用户对应多个项目");
            }
        } else if (role.equals("老师" )|| role.equals("管理员")) {
            fileInfo.setRefProjectId(role);
        }else{
            throw new FebsException("您不是项目干系人，无法上传文件");
        }



        //进行文件的合并操作
        String filename = fileInfo.getFilename();
        String file = uploadFolder + "/" + fileInfo.getIdentifier() + "/" + filename;
        String folder = uploadFolder + "/" + fileInfo.getIdentifier();
        String fileSuccess = FileInfoUtils.merge(file, folder, filename);

        fileInfo.setLocation(file);

        //文件合并成功后，保存记录至数据库
        if ("200".equals(fileSuccess)) {
            if (fileInfoService.addFileInfo(fileInfo) > 0) rlt = "SUCCESS";
        }

        //如果已经存在，则判断是否同一个项目，同一个项目的不用新增记录，否则新增
        if ("300".equals(fileSuccess)) {
            List<TFileInfo> tfList = fileInfoService.selectFileByParams(fileInfo);
            if (tfList != null) {
                if (tfList.size() == 0 || (tfList.size() > 0 && !fileInfo.getRefProjectId().equals(tfList.get(0).getRefProjectId()))) {
                    if (fileInfoService.addFileInfo(fileInfo) > 0) rlt = "SUCCESS";
                }
            }
        }

        return rlt;
    }

    /**
     * 查询列表
     *
     * @return ApiResult
     */
    @RequestMapping(value = "/selectFileList", method = RequestMethod.GET)
    public FebsResponse selectFileList(TFileInfo file) throws FebsException {
        String role = ProjectUtil.getUserRole();
        if (role.equals("学生") || role.equals("项目经理")) {
            String userSid = this.tUserInfoService.findByUsername(this.getUsername()).getSid();
            String pid;

            List<ProjectPeople> projectPeople = this.projectPeopleService.findBySid(userSid);
            //防止一个用户对应多个项目（这个后续需要可以更改）
            if (projectPeople.size() == 1) {
                pid = projectPeople.get(0).getPid();
            } else if (projectPeople.size() == 0) {
                //SQL语句中做了更改，可以直接查看老师或管理员发布的文件
                pid = "";
            } else {
                throw new FebsException("一个用户对应多个项目");
            }
            List<TFileInfo> list = fileInfoService.selectFileList(file, pid);
            return new FebsResponse().code("200").message("请求成功").status("success").data(list);
        } else if (role.equals("老师") || role.equals("管理员")) {
            List<TFileInfo> list = fileInfoService.findAll();
            return new FebsResponse().code("200").message("请求成功").status("success").data(list);
        } else {
            throw new FebsException("您不是项目干系人，无法查看文件");
        }

    }

    /**
     * 下载文件
     *
     * @param req
     * @param resp
     */
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void download(HttpServletRequest req, HttpServletResponse resp) {

        String location = req.getParameter("location");
        String fileName = req.getParameter("filename");
        BufferedInputStream bis = null;   //指定文件带缓冲区读取流
        BufferedOutputStream bos = null;   //写入流
        OutputStream fos = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(location));
            fos = resp.getOutputStream();
            bos = new BufferedOutputStream(fos);
            ServletUtils.setFileDownloadHeader(req, resp, fileName);
            int byteRead = 0;
            byte[] buffer = new byte[819200];
            while ((byteRead = bis.read(buffer, 0, 819200)) != -1) {
                bos.write(buffer, 0, byteRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bos.flush();
                bis.close();
                fos.close();
                bos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 文件删除
     */
    @RequestMapping(value = "/deleteFile", method = RequestMethod.POST)
    public ApiResult deleteFile(@RequestBody TFileInfo tFileInfo) {
        return ApiResult.success(fileInfoService.deleteFile(tFileInfo));
    }
}
