<template>
  <div>
    <!--    项目基本信息表单-->
    <el-form ref="projectForm" :model="projectInfo" label-width="80px" inline >
      <el-form-item label="项目名称" >
        <el-input v-model="projectInfo.projectName" style="width: 485px" disabled></el-input>
      </el-form-item>
      <el-form-item label="项目经理">
        <el-input v-model="projectInfo.managerName" disabled></el-input>
      </el-form-item>
      <el-form-item label="指导老师">
        <el-input v-model="projectInfo.teacherName" disabled></el-input>
      </el-form-item>
      <el-form-item label="项目类型">
        <el-input v-model="projectInfo.projectType" disabled></el-input>
      </el-form-item>
      <el-form-item label="特殊资源">
        <el-input v-model="resource" disabled></el-input>
      </el-form-item>
      <el-form-item label="开始时间">
          <el-date-picker type="date"
                          disabled
                          v-model="projectInfo.startTime"
                          style="width: 195px;"
                          format="yyyy 年 MM 月 dd 日"
                          value-format="yyyy-MM-dd">
          </el-date-picker>
      </el-form-item>
          <el-form-item label="开始时间">
          <el-date-picker type="date"
                          disabled
                          v-model="projectInfo.endTime"
                          style="width: 195px;"
                          format="yyyy 年 MM 月 dd 日"
                          value-format="yyyy-MM-dd">
          </el-date-picker>
      </el-form-item>
      <el-form-item label="项目描述">
        <el-input type="textarea" v-model="projectInfo.projectDesc" style="width: 485px" disabled></el-input>
      </el-form-item>
      <el-form-item label="项目成员">
        <div>
          <!-- 用户列表区域 -->
          <el-row>
            <el-table :data="memberInfo" border stripe disabled>
              <el-table-column label="姓名" prop="tuserInfo.name" width="100px" align="center"></el-table-column>
              <el-table-column label="班级" prop="tuserInfo.class" width="100px" align="center"></el-table-column>
              <el-table-column label="学号" prop="sid" width="135px" align="center"></el-table-column>
              <el-table-column label="岗位" prop="position" width="150px" align="center"></el-table-column>
            </el-table>
          </el-row>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>


<script>
export default {
  props: ['projectInfo', 'memberInfo'],
  name: 'ViewDialogContent',
  data () {
    return {
      // 需要的特殊资源，不必传给后端，仅在前端展示
      resource: '服务器算力资源',
    }
  },

  mounted() {
  },

  methods: {
  }
}
</script>

<style>
.grid-content {
  border-radius: 4px;
  min-height: 36px;
}

/*header样式*/
.header {
  font-size: 20px;
  font-weight: bold;
  text-align: center;
}
</style>

