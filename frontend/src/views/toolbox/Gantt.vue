<template>
  <div id="app">
    <div class="wl-gantt-demo">
      <wlGantt
        ref="wl-gantt-demo"
        use-card
        use-real-time
        end-date="2021-9-01"
        start-date="2020-9-01"
        date-type="monthAndDay"
        :data="data"
        :columns="columns"
        :contextMenuOptions="contextMenuOptions"
        @selection-change="selectionChange"
        @expand-change="expandChange"
        @timeChange="timeChange"
        @taskRemove="taskRemove"
        @preChange="preChange"
        @taskAdd="taskAdd"
      >
        <template slot="prv">
          <!-- <el-table-column type="selection" width="55" align="center"> </el-table-column> -->
          <el-table-column type="index" width="55" label="序号"> </el-table-column>
        </template>
        <template #info-card="{ row }">
          <ul>
            <li>
              <label for="name">名称：</label><span id="name">{{ row.name }}</span>
            </li>
          </ul>
        </template>
      </wlGantt>
    </div>
  </div>
</template>

<script>
import wlGantt from "@/components/gantt/gantt";
// import wlGantt from "@/pages/wl-gantt";
import "@/assets/css/clear.css";

export default {
  name: "Gantt",
  data() {
    return {
      data: [
        {
          id: "1",
          pid: "0",
          name: "概念",
          startDate: "2020-09-07",
          realStartDate: "2020-09-10",
          endDate: "2020-10-31",
          realEndDate: "2020-10-19",
          children: [
            {
              id: "1-1",
              pid: "1",
              name: "评价当前系统",
              startDate: "2020-09-10",
              endDate: "2020-09-13",
            },
            {
              id: "1-2",
              pid: "1",
              name: "定义需求",
              startDate: "2020-09-10",
              endDate: "2020-09-13",
              children: [
                {
                  id: "1-1-1",
                  pid: "1-1",
                  name: "定义用户需求",
                  startDate: "2020-09-10",
                  endDate: "2020-09-13",
                },
                {
                  id: "1-1-2",
                  pid: "1-1",
                  name: "定义内容需求",
                  startDate: "2020-09-10",
                  endDate: "2020-09-13",
                },
                {
                  id: "1-1-3",
                  pid: "1-1",
                  name: "定义系统需求",
                  startDate: "2020-09-10",
                  endDate: "2020-09-13",
                },
                {
                  id: "1-1-4",
                  pid: "1-1",
                  name: "定义服务器所有者需求",
                  startDate: "2020-09-10",
                  endDate: "2020-09-13",
                }
              ]
            },
            {
              id: "1-3",
              pid: "1",
              name: "定义特定功能",
              startDate: "2020-09-10",
              endDate: "2020-09-13",
            },
            {
              id: "1-4",
              pid: "1",
              name: "定义风险及风险管理方法",
              startDate: "2020-09-10",
              endDate: "2020-09-13",
            },
            {
              id: "1-5",
              pid: "1",
              name: "制订项目计划",
              startDate: "2020-09-17",
              endDate: "2020-09-22",
            },
            {
              id: "1-6",
              name: "简要介绍网站开发团队",
              pid: "1",
              startDate: "2020-09-25",
              endDate: "2020-09-30",
            }
          ],
        },
        {
          id: "2",
          name: "网站设计",
          startDate: "2020-09-20",
          endDate: "2020-10-31",
        },
        {
          id: "3",
          name: "网站开发",
          startDate: "2020-09-20",
          endDate: "2020-10-31",
        },
        {
          id: "4",
          name: "正式上线",
          startDate: "2020-09-20",
          endDate: "2020-10-31",
        },
        {
          id: "5",
          name: "技术支持",
          startDate: "2020-09-20",
          endDate: "2020-10-31",
        }
      ], // 数据
      selected: [], // 选中数据
      contextMenuOptions: [
        { label: "任务名称", prop: "name" },
        { label: "开始时间", prop: "startDate" },
        { label: "结束时间", prop: "endDate" },
      ],
      columns: [{ type: "name", minWidth: 150, colType: "expand" }], // 可通过此参数配置列。其中内置有名称name、开始日期startDate、结束日期endDate、前置任务preTask，如果cloumns中有type等于这四个且slot为false时，将使用内置代码，当然除了内容使用内置代码，其他字段你还拥有配置权。另外如果不是为了配置内置列参数，slot中的prv和default仍可以用来自定义列
    };
  },
  methods: {
    aa(row) {
      console.log(row, 99);
    },
    /**
     * 时间发生更改
     * row: Object 当前行数据c
     */
    timeChange(row) {
      console.log("时间修改:", row);
    },
    //
    /**
     * 前置任务发生更改
     * row: Object 当前行数据
     * oldval: [String, Array] 前置修改前的旧数据
     * handle: Boolean 是否用户编辑产生的改变
     */
    preChange(row, oldval, handle) {
      console.log("前置修改:", row, oldval, handle);
    },
    // 数表展开行
    expandChange(row, expanded) {
      console.log("展开行:", row, expanded);
    },
    // 多选选择
    selectionChange(/* val */) {
      // console.log("多选：", val);
    },
    // 删除任务
    taskRemove(item) {
      console.log("删除任务：", item);
    },
    // 添加任务
    taskAdd(item) {
      console.log("添加任务：", item);
      // 非懒加载方式直接设置子数据
     /*  this.$set(
        item,
        "children",
        item.children.concat([
          {
            pid: item.id,
            id: "###",
            name: "一轮新月",
            startDate: "2020-10-11",
            endDate: "2020-10-19"
          }
        ])
      );*/
      this.$refs["wl-gantt-demo"].loadTreeAdd(item.id, [
        {
          pid: item.id,
          id: "###",
          name: "一轮新月",
          startDate: "2020-10-11",
          endDate: "2020-10-19",
          children: []
        },
      ]);
    },
    // 懒加载
    lazyLoad(tree, treeNode, resolve) {
      setTimeout(() => {
        resolve([
          {
            id: "1-1-1",
            pid: tree.id,
            name: "日落云巅",
            startDate: "2020-09-10",
            endDate: "2020-09-13",
          },
        ]);
      }, 1000);
    },
  },
  components: {
    wlGantt,
  },
};
</script>

<style lang="scss">
#app {
  font-family: "Avenir", Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  padding: 10px 10px 0;
  width: 100%
}

.wl-gantt-demo {
  width: 100%;
}
</style>
