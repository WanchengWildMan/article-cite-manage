<template>
  <view-page>
    <!-- 左按钮区 -->
    <template slot="left-field">
      <el-button
        type="danger"
        icon="el-icon-circle-plus-outline"
        @click="handleAdd"
      >添加
      </el-button>
    </template>
    <!-- TODO -->
    <!--    搜索框-->
    <template slot="search-field">
      <el-input
        v-model="searchStr"
        suffix-icon="el-icon-search"
        placeholder="请输入搜索内容"
        style="min-width: 50px"
      ></el-input>
    </template>
    <!-- 过滤条件区 -->
    <template slot="filter-field" style="display: flex">
      <el-input-number
        v-model="filterId"
        :min="0"
        :step="1"
        placeholder="文献编号"
        style="min-width: 50px; max-width: 150px"
      ></el-input-number>
      <!--      文献题目过滤框-->
      <el-input
        autosize
        v-model="filterName"
        suffix-icon="el-icon-search"
        placeholder="搜索文献题目"
        style="min-width: 50px; max-width: 150px"
        @input="change($event)"
      ></el-input>
      <!-- 文献类型过滤框 -->
      <el-select
        v-model="filterType"
        placeholder="选择类型"
        style="min-width: 50px; max-width: 150px"
      >
        <el-option label="全部" value=""></el-option>
        <el-option
          v-for="type in articleTypes"
          :key="type"
          :label="type.label"
          :value="type.value"
        ></el-option>
      </el-select>
      <!--作者过滤框-->
      <el-input
        v-model="filterAuthor"
        suffix-icon="el-icon-search"
        placeholder="文献作者"
        style="min-width: 50px; max-width: 150px"
      ></el-input>
      <!-- 出版时间过滤框 -->
      <el-date-picker
        v-model="filterYear_start"
        type="year"
        format="yyyy"
        placeholder="起始年份"
        style="min-width: 50px; max-width: 150px"
      ></el-date-picker>

      <el-date-picker
        v-model="filterYear_end"
        type="year"
        placeholder="结束年份"
        style="min-width: 50px; max-width: 150px"
      ></el-date-picker>
      <el-button
        type="primary"
        icon="el-icon-search"
        @click="searchArticle"
      ></el-button>
    </template>
    <!-- 右按钮区 -->
    <template slot="right-field" style="display: flex">
      <el-button type="primary" icon="el-icon-refresh" @click="refresh">
        刷新
      </el-button>
      <el-button
        type="warning"
        icon="el-icon-upload2"
        @click="uploadShow = true"
      >
        导入
      </el-button>
      <el-button
        type="success"
        icon="el-icon-download"
        @click="downloadshow = true"
      >
        导出
      </el-button>
    </template>
    <!-- 表格区 -->
    <el-table
      :data="
        filtedData.slice(
          (currentPage - 1) * pageEachSize,
          currentPage * pageEachSize
        )
      "
      border
      id="articleTable"
      ref="articleTable"
    >
      <el-table-column
        v-for="item of tableHeadData"
        :key="item.key"
        :label="item.label"
        :width="item.width"
        :prop="item.name"
        sortable
        :sort-orders="[`ascending`, `descending`]"
        :min-width="item.minWidth == null ? '100px' : item.minWidth"
      >
        <template slot-scope="scope">
          <span v-if="item.name == 'index'">{{ scope.$index + 1 }}</span>
          <template v-else>
            <!--            不可修改或不在编辑状态展示表格本身-->
            <span v-show="item.unModifiable || !scope.row.show">{{
                scope.row[item.name] == 0 ? "" : scope.row[item.name]
              }}</span>
            <el-form :rules="formRules" style="overflow: scroll">
              <el-form-item v-show="!scope.row.unModifiable && scope.row.show">
                <!-- 判断是展示列表还是新增
            判断编辑状态下是input还是select -->
                <el-input
                  v-if="item.isInput"
                  v-model="scope.row[item.name]"
                ></el-input>
                <el-input-number
                  v-if="item.isNumber"
                  v-model="scope.row[item.name]"
                >
                </el-input-number>
                <el-select
                  v-if="item.isSelect && item.selectList"
                  v-model="scope.row[item.name]"
                >
                  <el-option
                    v-for="childItem in item.selectList"
                    :key="childItem.value"
                    :label="childItem.label"
                    :value="childItem.value"
                  >
                    <!-- FIXME: 不明原因childItem undefined -->
                    <!--                  TODO 初始化时js加初选-->
                  </el-option>
                </el-select>
                <el-date-picker
                  v-model="scope.row[item.name]"
                  v-if="item.isYear"
                  type="year"
                  format="yyyy"
                  value-format="yyyy"
                  placeholder="请选择出版年份"
                >
                </el-date-picker>
              </el-form-item>
            </el-form>
          </template>
        </template>
      </el-table-column>

      <el-table-column
        sortable="right"
        label="操作"
        min-width="300px"
        style="height: auto"
      >
        <template slot-scope="scope">
          <el-button
            @click="updateData(scope.row)"
            type="success"
            size="mini"
            icon="el-icon-success"
          >保存
          </el-button>
          <el-button
            @click="handleEdit(scope.row)"
            type="primary"
            size="mini"
            icon="el-icon-edit"
          >编辑
          </el-button>
          <el-button
            @click="deleteArticle(scope.row)"
            type="danger"
            size="mini"
            class="btn-text-red"
            icon="el-icon-delete"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      background
      layout="prev, pager, next, sizes, total, jumper"
      :page-sizes="[5, 10, 15, 20]"
      :page-size="pageEachSize"
      :total="total"
      :current-page="currentPage"
      @current-change="page"
      @size-change="handleSizeChange"
    >
    </el-pagination>
    <!-- 添加文献对话框 -->
    <edit-dialog
      :show="editShow"
      title="新建文献条目"
      @close="closeEditDialog"
      @save="saveNewRecord"
    >
      <!-- 文献信息表单 -->
      <el-form :model="articleModel" ref="articleAddForm" :rules="formRules">
        <el-form-item
          v-for="item of filterHeadDataToForm()"
          :key="item.key"
          :label="item.label"
          :prop="item.name"
          :required="!item.notRequired && !item.autoAdd"
        >
          <!-- 特判-作者 -->
          <div v-if="item.name == 'author'">
            <el-tag
              v-for="author in currentAuthors"
              :key="author"
              closable
              @close="removeCurrentAuthor(author)"
            >{{ author }}
            </el-tag>

            <span @keyup.enter="addCurrentAuthor"
            ><el-input v-model="inputAuthor"></el-input
            ></span>

            <el-button
              type="primary"
              size="small"
              icon="el-icon-plus"
              @click="addCurrentAuthor"
            >添加文献作者
            </el-button>
          </div>
          <!-- 特判-选项 -->
          <el-select
            v-else-if="item.isSelect && item.selectList"
            v-model="articleModel[item.name]"
          >
            <el-option
              v-for="childItem in item.selectList"
              :key="childItem.value"
              :label="childItem.label"
              :value="childItem.value"
            >
              <!--                  TODO 初始化时js加初选-->
            </el-option>
          </el-select>
          <!-- 特判-日期 -->
          <el-date-picker
            v-model="articleModel[item.name]"
            v-else-if="item.isYear"
            type="year"
            format="yyyy"
            value-format="yyyy"
            placeholder="请选择出版年份"
          >
          </el-date-picker>
          <!-- 特判-数字 -->
          <el-input-number
            v-else-if="item.isNumber == true"
            :min="0"
            :step="1"
            v-model="articleModel[item.name]"
          ></el-input-number>
          <!-- 其他 -->
          <el-input v-else v-model="articleModel[item.name]"></el-input>
          <!-- model的意义 -->
        </el-form-item>
      </el-form>
    </edit-dialog>
    <!-- 上传文件对话框 -->
    <el-dialog title="从文件中导入" :visible.sync="uploadShow">
      <h4>从Excel中导入</h4>
      <el-upload
        title="Excel"
        ref="uploadexcel"
        accept=".xlsx"
        :action="uploadExcelURL()"
        list-type="text"
        :on-success="uploadSuccess"
        :on-error="uploadError"
        :file-list="currentAddFileList"
      >
        <el-button type="primary" icon="el-icon-upload">立即上传</el-button>
      </el-upload>

      <h4>从txt文本中导入</h4>
      <el-upload
        title="txt文本"
        ref="uploadtxt"
        accept=".txt"
        :action="uploadTxtURL()"
        list-type="text"
        :on-success="uploadSuccess"
        :on-error="uploadError"
        :file-list="currentAddFileList"
      >
        <el-button type="primary" icon="el-icon-upload">立即上传</el-button>
      </el-upload>
      <span slot="footer">
        <el-button
          type="danger"
          icon="el-icon-close"
          @click="uploadShow = false"
        >关闭</el-button
        >
      </span>
    </el-dialog>
    <el-dialog title="导出模版或数据" :visible.sync="downloadshow">
      <el-button
        type="primary"
        icon="el-icon-download"
        @click="downloadExcel(true)"
      >导出xlsx模版
      </el-button
      >
      <el-button
        type="success"
        icon="el-icon-download"
        @click="downloadExcel(false)"
      >导出xlsx数据
      </el-button
      >
      <el-button type="success" icon="el-icon-download" @click="downloadTxt()"
      >导出txt数据
      </el-button
      >
    </el-dialog>
  </view-page>
</template>

<script>
import ViewPage from "./ViewPage";
import EditDialog from "./EditDialog";
import axios from "axios";

export default {
  components: {
    ViewPage,
    EditDialog,
  },
  data() {
    return {
      port: 8080,
      pageEachSize: 20,
      pageNum: 1, //no use
      displayLogic: ["none", "block"],
      articleTypes: [
        {value: "J", label: "[J]期刊"},
        {value: "M", label: "[M]专著"},
        {value: "C", label: "[C]文献集"},
        {value: "D", label: "[D]学位文献"},
        {value: "R", label: "[R]报告"},
        {value: "S", label: "[S]标准"},
        {value: "N", label: "[N]报纸"},
        {value: "P", label: "[P]专利"},
        {value: "DB/CD", label: "[DB/CD]数据库（CD）"},
        {value: "DB/OL", label: "[DB/CD]数据库（在线）"},
      ],
      inputAuthor: "",
      formRules: {
        //要在el-form里面加
        articleName: [{required: true, message: "请填写文献题目"}],
        author: [{required: true, message: "请输入作者", trigger: "blur"}],
        publishYear: [{required: true, message: "请选择日期"}],
        publishHouse: [{required: true, message: "请输入出版机构"}],
        articleType: [{required: true, message: "请选择文献类型"}],
        startPage: [{required: true, message: "请输入起始页码或出版月"}],
        endPage: [{required: true, message: "请输入结束页码或出版日"}],
      },
      articleModel: {
        id: "",
        articleName: "",
        author: "",
        articleType: "",
        publishHouse: "",
        publishYear: "",
        num: "",
        startPage: "",
        endPage: "",
      },
      tableHeadData: [
        {
          name: "index",
          label: "行",
          minWidth: "60px",
          unModifiable: true,
          autoAdd: true,
        },
        {
          name: "id",
          label: "文献编号",
          minWidth: "60px",
          unModifiable: true,
          autoAdd: true,
        },
        {
          name: "articleName",
          minWidth: "180px",
          label: "文献题目",
          isInput: true,
        },
        {
          name: "author",
          label: "文献作者",
          minWidth: "140px",
          isInput: true,
          notRequired: true,
        },
        {
          name: "articleType",
          label: "文献类型",
          minWidth: "130px",
          isSelect: true,

          selectList: [
            {value: "J", label: "[J]期刊"},
            {value: "M", label: "[M]专著"},
            {value: "C", label: "[C]文献集"},
            {value: "D", label: "[D]学位文献"},
            {value: "R", label: "[R]报告"},
            {value: "S", label: "[S]标准"},
            {value: "P", label: "[P]专利"},
            {value: "DB/CD", label: "[DB/CD]数据库（CD）"},
            {value: "DB/OL", label: "[DB/CD]数据库（在线）"},
          ],
        },
        {
          name: "publishHouse",
          label: "出版机构",
          isInput: true,
        },
        {
          name: "publishYear",
          label: "出版年份",
          isYear: true,
        },
        {
          name: "num",
          label: "卷号(期号)",
          isNumber: true,
          notRequired: true,
          minNumber: 1,
        },
        {
          name: "startPage",
          label: "起始页码或出版月",
          isNumber: true,
          minNumber: 1,
        },
        {
          name: "endPage",
          label: "结束页码或出版日",
          isNumber: true,
          minNumber: 1,
        },
        {
          name: "gmtCreated",
          label: "创建时间",
          minWidth: "170px",
          unModifiable: true,
          autoAdd: true,
        },
        {
          name: "gmtModified",
          label: "修改时间",
          minWidth: "170px",
          unModifiable: true,
          autoAdd: true,
        },
      ],

      tableDataAll: [],
      data: [],
      searchStr: "",
      filterId: undefined,
      filterAuthor: "",
      filterName: "",
      filterType: "",
      filterYear_start: 0,
      filterYear_end: 0,
      statusColors: ["info", "primary", "warning", "success"],
      sortProp: "",
      sortOrder: "",
      currentPage: 1,
      currentPageSize: 3,
      editShow: false,
      uploadShow: false,
      downloadshow: false,
      currentAddFileList: [],
      currentAuthors: [],
    };
  },
  created() {
    console.log("CREATED");
  },
  mounted() {
    console.log("MOUNTED");
    const self = this;
    this.getTableData().then(() => {
      console.log(self.tableHeadData);
      console.log(self.tableDataAll);
      console.log(self.filtedData);
      // this.page(1, self.filtedData)
      this.$nextTick(() => {
        // 表格添加列宽变化
        this.tableResizeInit();
      });
    });

    for (let i in this.tableHeadData) {
      this.tableHeadData[i]["modelName"] =
        "tableHeadData." + this.tableHeadData[i].name;
    }
  },
  methods: {
    handleCellClick: function (row, column, cell, event) {
      let isEdit = cell.getAttribute("isEdit");
      if (isEdit == undefined || isEdit == "false" || isEdit == "null")
        isEdit = false;
      isEdit = !isEdit;

      cell.querySelector(".el-form-item").style.display = this.displayLogic[
        isEdit ? 1 : 0
        ];
      cell.querySelector("span").style.display = this.displayLogic[
        isEdit ? 0 : 1
        ];
      cell.setAttribute("isEdit", isEdit);
    },
    hasError(resp) {
      return !resp.data || resp.data.errors.length > 0;
    },
    showAlert(_this, resp, row, opr) {
      // const _this=this;
      if (this.hasError(resp)) {
        _this.$alert(
          `文献  + ${row.articleName} +  ${opr}失败！原因：${resp.data.errors}`,
          "消息",
          {
            confirmButtonText: "确定",
            callback: (action) => {
              _this.location.reload();
            },
          }
        );
        return false;
      }
      _this.$alert(`文献 ${row.articleName} ${opr}成功！`, "消息", {
        confirmButtonText: "确定",
        callback: (action) => {
          window.location.reload();
        },
      });
      return true;
    },
    deleteArticle(row) {
      const _this = this;
      const index = row.$index;
      this.$confirm("此操作将永久删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          _this.tableDataAll.splice(index, 1);
          axios
            .delete(`/admin/deleteOneById?id=` + row.id)
            .then(function (resp) {
              // console.log(_this)
              _this.showAlert(_this, resp, row, "删除");
            });
          _this.$message({
            type: "success",
            message: "删除成功!",
          });
        })
        .catch(() => {
          _this.$message({
            type: "info",
            message: "已取消删除",
          });
        });
    },
    updateData(row) {
      const _this = this;
      this.$set(row, "show", false);
      console.log(row);
      axios.post(`/admin/updateOne`, row).then(function (resp) {
        let ok = !_this.hasError(resp);
        _this.$message({
          type: ok ? "success" : "error",
          message: "保存" + (ok ? "成功" : "失败"),
        });
      });
    },
    handleEdit(row) {
      // console.log(scope.row); scope.row.$set(scope.row,'show',true)
      this.$set(row, "show", true);
      let tds = document.querySelectorAll("#articleTable td");
      let ths = document.querySelectorAll("#articleTable th");
    },
    handleAdd() {
      this.newArticleRecord = this.articleModel;
      this.editShow = true;
    },
    addCurrentAuthor() {
      const _this = this;
      //   console.log(this);
      //   console.log(this.inputAuthor);
      //如果用循环找且在回调中修改_this.ok，，出循环后，_this.author值还是会不变
      _this.ok = true;
      if (this.inputAuthor == "") {
        this.$alert("不合法的作者名！", "错误", {
          confirmButtonText: "确定",
          callback: (action) => {
          },
        });
      } else if (this.currentAuthors.indexOf(this.inputAuthor) != -1) {
        this.$alert(`${this.inputAuthor}已存在！`, "错误", {
          confirmButtonText: "确定",
          callback: (action) => {
          },
        });
      } else {
        this.currentAuthors.push(this.inputAuthor);
        this.inputAuthor = "";
        this.formRules.author[0].required = false;
      }
    },
    removeCurrentAuthor(tag) {
      this.currentAuthors.splice(this.currentAuthors.indexOf(tag), 1);
    },
    //对话框添加
    saveNewRecord() {
      const _this = this;

      this.$refs.articleAddForm.validate((valid) => {
        if (valid) {
          this.articleModel.author = this.currentAuthors.join(",");
          axios.post(`/admin/addOne`, this.articleModel).then(function (resp) {
            let ok =
              !_this.hasError(resp) &&
              resp.data.errors.indexOf("Duplicate") == -1;
            _this.$message({
              type: ok ? "success" : "error",
              message: "添加" + (ok ? "成功" : `失败}`),
            });
            _this.refresh();
          });
        }
      });
    },
    handleSpanClickDebug() {
      console.log(this.formRules);
    },
    page(currentPage) {
      this.currentPage = currentPage;
      //避免再次请求，直接取缓存
    },
    handleSizeChange(psize) {
      this.pageEachSize = psize;
    },
    //请求数据不分页
    getTableData(url = null) {
      const _this = this;
      console.log(url);
      if (!url) url = `/admin/find`;
      return axios.get(url).then(function (resp) {
        // console.log(resp);
        // console.log(resp.data.content.length);
        _this.tableDataAll = resp.data.content;
        console.log(_this.tableDataAll);
      });
    },
    filterHeadDataToForm() {
      return this.tableHeadData.filter((item) => !item.autoAdd);
    },

    addListener(element, type, listener, useCapture) {
      //这是两种写法，对应不同浏览器
      element.addEventListener
        ? element.addEventListener(type, listener, useCapture)
        : element.attachEvent("on" + type, listener);
    },
    closeEditDialog() {
      this.newArticleRecord = {};
      this.currentAuthors = [];
      this.inputAuthor = "";
      this.$refs.articleAddForm.resetFields();
      this.editShow = false;
    },
    refresh() {
      window.location.reload();
    },
    // 下载excel
    downloadExcel(isTemplate = false) {
      let _this = this;
      // console.log(JSON.stringify(this.filtedData))
      axios
        .post(
          `/admin/downloadExcel?isTemplate=${isTemplate}`,
          _this.filtedData,
          {
            responseType: "blob",
          }
        )
        .then((res) => {
          var blob = new Blob([res.data], {
            type: "application/vnd.ms-excel",
          });
          // console.log(res.data)
          var downloadElement = document.createElement("a");
          var href = window.URL.createObjectURL(blob); //创建下载的链接
          downloadElement.href = href;
          downloadElement.download = "文献导出列表.xlsx"; //下载后文件名
          document.body.appendChild(downloadElement);
          downloadElement.click(); //点击下载
          document.body.removeChild(downloadElement); //下载完成移除元素
          window.URL.revokeObjectURL(href); //释放掉blob对象
        })
        .catch((err) =>
          this.$notify({
            type: "error",
            message: "导出失败，网络错误！",
          })
        );
    },
    // 上传
    uploadExcelURL() {
      return `/admin/uploadExcel`;
    },
    downloadTxt() {
      let _this = this;
      let lineList = [];
      for (let i in _this.filtedData) {
        const line = _this.filtedData[i];
        let lineStr = `[${i}]`;
        for (let key of Object.keys(line)) {
          let val = line[key];
          if (
            val == "" ||
            val == 0 ||
            val == "0" ||
            key == "id" ||
            key == "seqId" ||
            key.substr(0, 3) == "gmt" ||
            key == "show"
          )
            continue;
          if (key == "articleType") lineStr += `[${val}].`;
          else if (key == "num") lineStr += `(${val}):`;
          else if (
            key == "startPage" ||
            (line["articleType"] == "N" && key == "publishYear")
          )
            lineStr += `${val}-`;
          else lineStr += `${val}` + `${key == "articleName" ? "" : "."}`;
        }
        lineList.push(lineStr);
      }
      axios
        .post(`/admin/downloadTxt`, lineList, {
          responseType: "blob",
        })
        .then((res) => {
          var blob = new Blob([res.data]);
          // console.log(res.data)
          var downloadElement = document.createElement("a");
          var href = window.URL.createObjectURL(blob); //创建下载的链接
          downloadElement.href = href;
          downloadElement.download = "文献导出列表.txt"; //下载后文件名
          document.body.appendChild(downloadElement);
          downloadElement.click(); //点击下载
          document.body.removeChild(downloadElement); //下载完成移除元素
          window.URL.revokeObjectURL(href); //释放掉blob对象
        })
        .catch((err) =>
          this.$notify({
            type: "error",
            message: "导出失败，网络错误！",
          })
        );
    },
    uploadTxtURL() {
      return `/admin/uploadTxt`;
    },
    uploadExcelNow() {
      console.log("uploadExcel!!!!!");
      this.$refs.uploadexcel.submit();
    },
    uploadTxtNow() {
      this.$refs.uploadtxt.submit();
    },
    uploadSuccess(res) {
      if (res.status != 200) {
        console.log(res.successNum);
        this.$notify.error({
          title: `${res.successNum}个导入成功，${
            res.totalNum - res.successNum
          }个导入失败，引用格式不正确或引用条目已存在！`,
          message: `${res.errors.join("\n")}`,
        });
      } else
        this.$notify.success({
          title: `${res.successNum}个导入成功！`,
        });
      this.uploadShow = false;
    },
    //上传失败时提示
    uploadError(error) {
      this.$message({
        message: "上传失败，请检查网络状态！",
        type: "error",
        showClose: true,
      });
    },
    searchArticle() {
      let _this = this;
      this.getTableData(
        `/admin/find?id=${!this.filterId ? 0 : this.filterId}&author=${
          this.filterAuthor ? this.filterAuthor : ""
        }&articleType=${this.articleType}&articleName=${
          this.filterName ? this.filterName : ""
        }&publishYearStart=${this.filterYear_start ? this.filterYear_start.getFullYear() : ""}
        &publishYearEnd=${this.filterYear_end ? this.filterYear_end.getFullYear() : ""}
        `
      );
    },
    change(e) {
      this.$forceUpdate();
    },
  },
  computed: {
    filtedData() {
      let ansFilter = this.tableDataAll
        .filter((item) => {
          var reg = new RegExp(this.searchStr, "i");
          console.log(item);
          return (
            !this.searchStr ||
            reg.test(item.articleName) ||
            reg.test(item.author) || //.join(",")) ||
              reg.test(item.publishYear)||
              reg.test(item.id)||
              reg.test(item.publishHouse)||
              reg.test(item.articleType)||
            // reg.test(item.author.join(" ")) ||
            reg.test(item.articleType)
          );
        })
        .filter((item) => {
          return (
            this.filterId == 0 || !this.filterId || item.id == this.filterId
          );
        })
        .filter((item) => {
          return this.filterType == "" || item.articleType == this.filterType;
        })
        .filter((item) => {
          var reg = new RegExp(this.filterName, "i");
          return this.filterName == "" || reg.test(item.articleName);
        })
        .filter((item) => {
          // console.log(
          //   this.filterYear_start,
          //   new Date(item.publishYear),
          //   this.filterYear_end == new Date(item.publishYear)
          // );
          return (
            (!this.filterYear_start ||
              this.filterYear_start == 0 ||
              this.filterYear_start.getFullYear() <=
              new Date(item.publishYear).getFullYear()) &&
            (!this.filterYear_end ||
              this.filterYear_end == 0 ||
              this.filterYear_end.getFullYear() >=
              new Date(item.publishYear).getFullYear())
          );
        })
        .filter((item) => {
          return (
            this.filterAuthor == "" ||
            item.author.indexOf(this.filterAuthor) != -1
          );
        });
      // 计算属性会在mounted之前加载！！！
      // console.log(this.tableDataAll);
      if (this.tableDataAll.length > 0) this.page(1);
      return ansFilter;
    },
    sortedData() {
      if (
        !this.sortOrder ||
        !this.sortProp ||
        !this.filtedData ||
        !this.filtedData.length
      )
        return this.filtedData;
      var reverse = this.sortOrder == "descending" ? -1 : 1;
      switch (typeof this.filtedData[0][this.sortProp]) {
        case "number":
          return this.filtedData.sort((a, b) => {
            return reverse * (a[this.sortProp] - b[this.sortProp]);
          });
        case "string":
          if (
            JSON.stringify(new Date(this.filtedData[0][this.sortProp])) !==
            "null"
          ) {
            return this.filtedData.sort((a, b) => {
              return (
                reverse *
                (new Date(a[this.sortProp]) - new Date(b[this.sortProp]))
              );
            });
          } else {
            return this.filtedData.sort((a, b) => {
              var cmp = 0;
              if (a[this.sortProp] > b[this.sortProp]) cmp = 1;
              else if (a[this.sortProp] < b[this.sortProp]) cmp = -1;
              return reverse * cmp;
            });
          }
      }
    },
    total() {
      return this.filtedData.length;
    },
    pagedData() {
      return this.sortedData.slice(
        (this.currentPage - 1) * this.currentPageSize,
        this.currentPage * this.currentPageSize
      );
    },
  },
};
</script>
