<template>
  <view-page>
    <!-- 左按钮区 -->
    <template slot="left-field">
      <el-button
        type="danger"
        icon="el-icon-circle-plus-outline"
        @click="handleAdd"
        >添加</el-button
      >
    </template>
    <!-- TODO -->
    <!-- 搜索框 -->
    <template slot="search-field">
      <el-input
        v-model="searchStr"
        suffix-icon="el-icon-search"
        placeholder="请输入搜索内容"
      ></el-input>
    </template>
    <!-- 过滤条件区 -->
    <template slot="filter-field">
      <!-- 状态过滤框 -->
      <el-select v-model="filterType" placeholder="选择类型">
        <el-option label="全部" value=""></el-option>
        <el-option
          v-for="type in articleTypes"
          :key="type"
          :label="type.label"
          :value="type.value"
        ></el-option>
      </el-select>
      <!-- 时间过滤框 -->
      <el-date-picker
        v-model="filterDates"
        type="year"
        start-placeholder="起始时间"
        end-placeholder="结束时间"
      ></el-date-picker>
    </template>
    <!-- 右按钮区 -->
    <template slot="right-field">
      <el-button type="primary" icon="el-icon-refresh" @click="update"
        >刷新</el-button
      >
      <el-button
        type="warning"
        icon="el-icon-upload2"
        @click="uploadShow = true"
        >导入</el-button
      >
      <el-button type="success" icon="el-icon-download" @click="downloadTodos"
        >导出</el-button
      >
    </template>
    <!-- 表格区 -->
    <el-table :data="tableData" border id="articleTable" ref="articleTable">
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
            <span v-show="item.unModifiable || !scope.row.show">{{
              scope.row[item.name]
            }}</span>
            <el-form :rules="formRules">
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

      <el-table-column sortable="right" label="操作" min-width="300px">
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
      layout="prev, pager, next"
      :page-size="pageEachSize"
      :total="total"
      @current-change="page"
      @size-change="pageSizeChange"
    >
    </el-pagination>
    <!-- 对话框 -->
    <edit-dialog
      :show="editShow"
      title="新建论文条目"
      @close="closeEditDialog"
      @save="saveNewRecord"
    >
      <!-- 论文信息表单 -->
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
              >添加文献作者</el-button
            >
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
              <!-- FIXME: 不明原因childItem undefined -->
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
    <el-dialog title="上传文件" :visible.sync="uploadShow">
      <el-upload :action="uploadUrl" :on-success="uploadSuccess">
        <el-button type="primary" icon="el-icon-upload">上传</el-button>
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
      pageEachSize: 10,
      pageNum: 1, //no use
      total: 10,
      displayLogic: ["none", "block"],
      articleTypes: [
        { value: "J", label: "[J]期刊" },
        { value: "M", label: "[M]专著" },
        { value: "C", label: "[C]论文集" },
        { value: "D", label: "[D]学位论文" },
        { value: "R", label: "[R]报告" },
        { value: "S", label: "[S]标准" },
        { value: "P", label: "[P]专利" },
      ],

      filterType: "",
      searchAuthorStr: "",
      searchNameStr: "",
      inputAuthor: "",
      formRules: {
        //要在el-form里面加
        articleName: [
          { required: true, message: "请填写论文题目", trigger: "blur" },
        ],
        // author: [{ required: true, message: "请输入作者", trigger: "blur" }],
        publishYear: [
          {
            required: true,
            message: "请选择日期",
            trigger: "change",
          },
        ],
        num: [{ required: true, message: "请填写卷号(期号)", trigger: "blur" }],

        seqId: [{ required: true, message: "请输入论文的引用次序" }],
      },
      articleModel: {
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
            { value: "J", label: "[J]期刊" },
            { value: "M", label: "[M]专著" },
            { value: "C", label: "[C]论文集" },
            { value: "D", label: "[D]学位论文" },
            { value: "R", label: "[R]报告" },
            { value: "S", label: "[S]标准" },
            { value: "P", label: "[P]专利" },
          ],
        },
        {
          name: "publishHouse",
          label: "出版机构",
          isInput: true,
        },
        {
          name: "publishYear",
          label: "出版时间",
          isYear: true,
        },
        {
          name: "num",
          label: "卷号(期号)",
          isNumber: true,
          minNumber: 1,
        },
        {
          name: "startPage",
          label: "起始页码",
          isNumber: true,
          minNumber: 1,
        },
        {
          name: "endPage",
          label: "结束页码",
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
      tableData: [
        {
          id: 1,
          seqId: 2,
          articleName: "解忧杂货店",
          author: "东野圭吾",
        },
        {
          id: 2,
          articleName: "追风筝的人",
          publishYear: "2004",
          author: "卡勒德·胡赛尼",
        },
        {
          id: 3,
          publishYear: "2004",
          articleName: "人间失格",
          author: "太宰治",
        },
      ],
      data: [],
      searchStr: "",
      filterType: "",
      filterDates: null,
      statusColors: ["info", "primary", "warning", "success"],
      sortProp: "",
      sortOrder: "",
      currentPage: 1,
      currentPageSize: 3,
      editShow: false,
      uploadShow: false,

      currentAuthors: [],
    };
  },
  created() {
    setTimeout(() => {
      this.page(1);
    }, 300);
  },
  mounted() {
    console.log(this.tableHeadData);

    this.$nextTick(() => {
      // 表格添加列宽变化
      this.tableResizeInit();
      // console.log(this.tableHeadData)
    });
    console.log(this.tableData);
    this.page(1);
    for (let i in this.tableHeadData) {
      this.tableHeadData[i]["modelName"] =
        "tableHeadData." + this.tableHeadData[i].name;
    }
  },
  methods: {
    handleCellClick: function (row, column, cell, event) {
      console.log(cell);
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
          `论文  + ${row.articleName} +  ${opr}失败！原因：${resp.data.errors}`,
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
      _this.$alert(`论文 ${row.articleName} ${opr}成功！`, "消息", {
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
          _this.tableData.splice(index, 1);
          axios
            .delete(
              `http://localhost:${this.port}/admin/deleteOneById?id=` + row.id
            )
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
      axios
        .post(`http://localhost:${this.port}/admin/updateOne`, row)
        .then(function (resp) {
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
          callback: (action) => {},
        });
      } else if (this.currentAuthors.indexOf(this.inputAuthor) != -1) {
        this.$alert(`${this.inputAuthor}已存在！`, "错误", {
          confirmButtonText: "确定",
          callback: (action) => {},
        });
      } else {
        this.currentAuthors.push(this.inputAuthor);
        this.inputAuthor = "";
      }
    },
    removeCurrentAuthor(tag) {
      this.currentAuthors.splice(this.currentAuthors.indexOf(tag), 1);
    },
    saveNewRecord() {
      const _this = this;

      this.$refs.articleAddForm.validate((valid) => {
        if (valid) {
          this.articleModel.author = this.currentAuthors.join(",");
          //   this.$alert(this.$refs.articleAddForm.author);
          axios
            .post(
              `http://localhost:${this.port}/admin/addOne`,
              this.articleModel
            )
            .then(function (resp) {
              let ok = !_this.hasError(resp);
              _this.$message({
                type: ok ? "success" : "error",
                message: "添加" + (ok ? "成功" : "失败"),
              });
            });
        }
      });
    },
    closeEditDialog() {
      this.newArticleRecord = {};
      this.currentAuthors = [];
      this.inputAuthor = "";
      this.$refs.articleAddForm.resetFields();
      this.editShow = false;
    },
    page(currentPage) {
      const _this = this;
      axios
        .get(`http://localhost:${this.port}/admin/find`)
        .then(function (resp) {
          console.log(resp);
          console.log(resp.data.content.length);
          _this.tableData = resp.data.content.slice(
            (currentPage - 1) * _this.pageEachSize,
            currentPage * _this.pageEachSize
          );
          console.log(_this.tableData[1].publishYear);
          _this.pageNum = Math.ceil(
            resp.data.content.length / _this.pageEachSize
          );
          console.log(_this.pageNum);
          _this.total = resp.data.content.length;
          for (let i in _this.tableData) {
            this.tableData[i].publishYear = this.tableData[
              i
            ].publishYear.toString();
            // console.log(_this.tableData[i].publishYear);
          }
        });
    },
    handleSpanClickDebug() {
      console.log(this.formRules);
    },
    //input框失去焦点事件
    //     handleInputBlur: function (event) {   //当 input 失去焦点 时,input 切换为 span，并且让下方 表格消失（注意，与点击表格事件的执行顺序）
    //       var _event = event;
    //       setTimeout(function () {
    //         var _inputNode = _event.target;
    //         if (this.getParentElement(_inputNode, 4) !== 0) {
    //           var _cellNode = this.getParentElement(_inputNode, 4);
    //           this.removeClass(_cellNode, 'current-cell');
    //           this.removeClass(_cellNode, 'current-cell2');
    //         }
    //       }, 200);
    //     },

    page(currentPage) {
      const _this = this;
      axios
        .get(`http://localhost:${this.port}/admin/find`)
        .then(function (resp) {
          console.log(resp);
          console.log(resp.data.content.length);
          _this.tableData = resp.data.content.slice(
            (currentPage - 1) * _this.pageEachSize,
            currentPage * _this.pageEachSize
          );
          console.log(_this.tableData[1].publishYear);
          _this.pageNum = Math.ceil(
            resp.data.content.length / _this.pageEachSize
          );
          console.log(_this.pageNum);
          _this.total = resp.data.content.length;
          for (let i in _this.tableData) {
            this.tableData[i].publishYear = this.tableData[
              i
            ].publishYear.toString();
            // console.log(_this.tableData[i].publishYear);
          }
        });
    },
    filterHeadDataToForm() {
      return this.tableHeadData.filter((item) => !item.autoAdd);
    },
    tableResizeInit() {
      let self = this;
      /* 获取头部td集合,这边是测试表格，只有一个所以直接el-table__body 的0，后续可以在<el-table> 加class，
      再用querySelector
      */
      let tblObj = document.getElementsByClassName("el-table__body")[0];
      //如果不用数组，最后遍历的时候不能有【】这个来选取元素
      let headerRows = new Array();
      for (let i = 0; i < tblObj.rows.length; i++) {
        //只有rows这个能选，col要先选rows，然后用cells
        headerRows[i] = tblObj.rows[i].cells[0];
      }
      // 去头部的位置
      let headerTds = document.getElementsByClassName("el-table__body")[0]
        .rows[0].cells;
      let screenYStart = 0;
      let tdHeight = 0;
      let headerHeight = 0;

      for (let i = 0; i < headerRows.length; i++) {
        //添加头部单元格事件
        this.addListener(headerRows[i], "mousedown", onmousedown);
        this.addListener(headerRows[i], "mousemove", onmousemove);
      }

      document.onmousedown = function (event) {
        if (this.resizeable) {
          let evt = event || window.event;
          this.mousedown = true;
          screenYStart = evt.screenY;
          tdHeight = this.targetTd.offsetHeight;
          headerHeight = tblObj.offsetHeight;
        }
      };
      document.onmousemove = function (event) {
        let evt = event || window.event;
        let srcObj = self.getTarget(evt);
        let offsetY = evt.offsetY;
        if (this.mousedown) {
          let height = tdHeight + (evt.screenY - screenYStart) + "px"; //计算后的新的宽度，原始td+偏移
          this.targetTd.style.height = height;
          tblObj.style.height =
            headerHeight + (evt.screenY - screenYStart) + "px";
        } else {
          if (
            srcObj.offsetHeight - evt.offsetY <= 8 &&
            srcObj.offsetWidth - evt.offsetX > 8
          ) {
            this.targetTd = srcObj;
            this.resizeable = true;
            srcObj.style.cursor = "row-resize"; //修改光标样式
          } else if (evt.offsetY <= 8 && evt.offsetX > 8) {
            if (srcObj.parentNode.rowIndex) {
              this.targetTd =
                tblObj.rows[srcObj.parentNode.rowIndex - 1].cells[0];
              this.resizeable = true;
              srcObj.style.cursor = "row-resize";
            }
          } else if (
            srcObj.offsetHeight - evt.offsetY > 8 &&
            srcObj.offsetWidth - evt.offsetX <= 8
          ) {
            srcObj.style.cursor = "column-resize"; //修改光标样式
          } else if (evt.offsetY > 8 && evt.offsetX <= 8) {
            if (srcObj.parentNode.rowIndex) {
              srcObj.style.cursor = "column-resize";
            }
          } else {
            this.resizeable = false;
            srcObj.style.cursor = "default";
          }
        }
      };
      //放开鼠标恢复原位
      document.onmouseup = function (event) {
        this.tartgetTd = null;
        this.coltargetTd = null;
        this.resizeable = false;
        this.mousedown = false;
        document.body.style.cursor = "default";
      };
    },
    // 得到目标值事件
    getTarget(evt) {
      return evt.target || evt.srcElement;
    },
    // 添加监听
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

    downloadTodos() {
      this.$ajax({
        method: "post",
        url: "todos/download",
        responseType: "blob",
        data: this.filtedData,
      })
        .then((res) => {
          var blob = new Blob([res.data], {
            type:
              "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8",
          });
          var downloadElement = document.createElement("a");
          var href = window.URL.createObjectURL(blob); //创建下载的链接
          downloadElement.href = href;
          downloadElement.download = "导出列表.xlsx"; //下载后文件名
          document.body.appendChild(downloadElement);
          downloadElement.click(); //点击下载
          document.body.removeChild(downloadElement); //下载完成移除元素
          window.URL.revokeObjectURL(href); //释放掉blob对象
        })
        .catch((err) =>
          this.$notify({
            type: "error",
            message: err,
          })
        );
    },
    uploadSuccess(res) {
      this.$notify({
        type: "success",
        message: res,
      });
      this.update();
      this.uploadShow = false;
    },
  },
  computed: {
    filtedData() {
      return this.tableData
        .filter((item) => {
          var reg = new RegExp(this.searchStr, "i");
          return (
            !this.searchStr ||
            reg.test(item.name) ||
            reg.test(item.author.join(" "))
          );
        })
        .filter((item) => {
          return this.filterType === "" || item.status === this.filterType;
        })
        .filter((item) => {
          return (
            !this.filterDates ||
            (this.filterDates[0] <= new Date(item.completeDate) &&
              this.filterDates[1] >= new Date(item.completeDate))
          );
        });
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
    uploadUrl() {
      return `${this.$ajax.defaults.baseURL}todos/upload`;
    },
  },
};
</script>