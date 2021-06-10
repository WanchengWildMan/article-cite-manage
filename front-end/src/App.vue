<template>
  <el-container>
    <!-- 头部区域 -->
    <el-header class="head" height="100px" align="center">
      <h2>参 考 文 献 管 理 系 统</h2>
    </el-header>
    <!-- 主区域 -->
    <el-main>
      <data-table></data-table>
    </el-main>
  </el-container>
</template>

<script>
import DataTable from "./components/DataTable";

export default {
  name: "App",
  components: {
    DataTable,
  },
};
</script>
<style type="text/css" src="./assets/css/el_table.css" ></style>
<style>
.head {
  background-color: #409eff;
  color: white;
}
</style>
