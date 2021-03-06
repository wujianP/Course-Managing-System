package cc.mrbird.febs.project.service.impl;

import cc.mrbird.febs.common.domain.QueryRequest;
import cc.mrbird.febs.project.domain.ProjectInfo;
import cc.mrbird.febs.project.dao.ProjectInfoMapper;
import cc.mrbird.febs.project.domain.ProjectPeople;
import cc.mrbird.febs.project.service.ProjectInfoService;
import cc.mrbird.febs.project.service.ProjectPeopleService;
import cc.mrbird.febs.system.domain.Menu;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hyl
 */
@Service
public class ProjectInfoServiceImpl extends ServiceImpl<ProjectInfoMapper, ProjectInfo> implements ProjectInfoService {
    @Autowired
    private ProjectPeopleService projectPeopleService;

//    @Autowired
//    public ProjectInfoServiceImpl(ProjectInfoMapper projectInfoMapper) {
//        this.projectInfoMapper = projectInfoMapper;
//    }
//
//    public ProjectInfoServiceImpl() {
//
//    }

    @Override
    public List<ProjectInfo> findProjectInfo(String username) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("group_leader", username);
        return (List<ProjectInfo>) this.baseMapper.selectList(wrapper);
    }


    @Override
    public List<ProjectInfo> findMyProjectInfo(String sid) {
        List<ProjectPeople> projectPeopleList = projectPeopleService.findBySid(sid);
        //只要pid
        List<String> pidList = projectPeopleList.stream().map(ProjectPeople::getPid).collect(Collectors.toList());
        List<ProjectInfo> projectInfoList = this.baseMapper.selectBatchIds(pidList);
        return projectInfoList;
    }

    @Override
    public long createProjectInfo(ProjectInfo projectInfo) {
        this.save(projectInfo);
        return projectInfo.getPid();
    }

    @Override
    public ProjectInfo findByPid(String pid) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("pid", pid);
        return this.baseMapper.selectOne(wrapper);
    }

    @Override
    public void updateProjectInfo(ProjectInfo projectInfo, String pid) {
        this.baseMapper.updateProjectInfo(projectInfo, pid);
    }

    @Override
    public void deleteProjectInfo(String pid) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("pid", pid);
        this.baseMapper.delete(wrapper);
    }

    @Override
    public List<ProjectInfo> findProjectInfoList(String pid) {
        return this.baseMapper.findProjectInfoList(pid);
    }
}
