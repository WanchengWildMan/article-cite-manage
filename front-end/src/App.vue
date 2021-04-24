<template>
  <div>
    <!-- 左按钮区 -->
    <template slot="left-field">
      <el-button
        type="danger"
        icon="el-icon-circle-plus-outline"
        @click="addTodo"
        >添加</el-button
      >
    </template>
    <!-- 搜索框 -->
    <template slot="search-field">
      <el-input
        v-model="searchNameStr"
        suffix-icon="el-icon-search"
        placeholder="搜索文章条目"
      ></el-input>
    </template>
    <template slot="search-field">
      <el-input
        v-model="searchAuthorStr"
        suffix-icon="el-icon-search"
        placeholder="搜索作者"
      ></el-input>
    </template>
    <!-- 过滤条件区 -->
    <template slot="filter-field">
      <!-- 类型过滤框 -->
      <el-select v-model="articleTypes" placeholder="选择文献类型">
        <!-- <el-option label="全部" value=""></el-option> -->
        <el-option
          v-for="articleType in articleTypes"
          :key="articleType"
          :label="articleType"
          :value="articleType"
        ></el-option>
      </el-select>
      <!-- 时间过滤框 -->
      <el-date-picker
        v-model="filterDates"
        type="daterange"
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
    <!--    TODO-->

    <el-table :data="tableData" border style="width: 100%; border-radius: 20px">
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
          <span v-if="item.name == 'index'">{{scope.$index + 1}}</span>
          <template v-else>
            <span v-show="scope.row.unModifiable || !scope.row.show">{{
              scope.row[item.name]
            }}</span>
            <el-form
              :rules="formRules[item.name]"
              @click="alert(formRules[item.name])"
            >
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
            @click="saveData(scope.row)"
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
    <!--    //分页操作-->
    <el-pagination
      background
      layout="prev, pager, next"
      :page-size="pageEachSize"
      :total="total"
      @current-change="page"
    >
    </el-pagination>
  </div>
</template>
<style type="text/css" src="./assets/css/el_table.css" scoped></style>

<script src="https://unpkg.com/axios/dist/axios.min.js"></script>
<script>
import "@/assets/css/el_table.css";
//TODO 表单验证
export default {
  methods: {
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
          // window.location.reload();
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
    saveData(row) {
      const _this = this;
      this.$set(row, "show", false);
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
          console.log(_this.tableData);
          _this.pageNum = Math.ceil(
            resp.data.content.length / _this.pageEachSize
          );
          console.log(_this.pageNum);
          _this.total = resp.data.content.length;
        });
    },
    //单元格点击后，显示input，并让input 获取焦点
    // handleCellClick: function (row, column, cell, event) {
    //   emptransfer.addClass(cell, 'current-cell');
    //   if (emptransfer.getChildElement(cell, 3) !== 0) {
    //     var _inputParentNode = emptransfer.getChildElement(cell, 3);
    //     if (_inputParentNode.hasChildNodes() && _inputParentNode.childNodes.length > 2) {
    //       var _inputNode = _inputParentNode.childNodes[2];
    //       if (_inputNode.tagName === 'INPUT') {
    //         _inputNode.focus();
    //       }
    //     }
    //   }
    // },
    //input框失去焦点事件
    //     handleInputBlur: function (event) {   //当 input 失去焦点 时,input 切换为 span，并且让下方 表格消失（注意，与点击表格事件的执行顺序）
    //       var _event = event;
    //       setTimeout(function () {
    //         var _inputNode = _event.target;
    //         if (emptransfer.getParentElement(_inputNode, 4) !== 0) {
    //           var _cellNode = emptransfer.getParentElement(_inputNode, 4);
    //           emptransfer.removeClass(_cellNode, 'current-cell');
    //           emptransfer.removeClass(_cellNode, 'current-cell2');
    //         }
    //       }, 200);
    //     },

    tableInit() {
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
        //rowIndex是未定义！！！cellIndex是好用的。我应该获取的是tr的rowindex
        //获取偏移 这里是鼠标的偏移
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
  },
  watch: {},
  computed: {
    filtedData() {
      return this.data
        .filter((item) => {
          var reg = new RegExp(this.searchNameStr, "i");
          return (
            !this.searchNameStr ||
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
  },

  // 防止全局组件污染，故data用函数
  data() {
    return {
      port: 8080,
      pageEachSize: 10,
      pageNum: 1, //no use
      total: 10,
      displayLogic: ["display:none;", "display:block;"],
      // isSelect: ['articleType'],
      articleTypes: ["M", "A"],
      filterType: "",
      searchAuthorStr: "",
      tableHeadData: [
        {
          name: "index",
          label: "行",
          minWidth: "50px",
          unModifiable: true,
        },

        {
          name: "articleName",
          minWidth: "180px",
          label: "文献篇名",

          isInput: true,
        },
        {
          name: "author",
          label: "文献作者",
          minWidth: "140px",
          isInput: true,
        },
        {
          name: "articleType",
          label: "文献类型",
          minWidth: "130px",
          isSelect: true,
          list: ["a", "ab"],
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
        },
        {
          name: "gmtModified",
          label: "修改时间",
          minWidth: "170px",
          unModifiable: true,
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
          author: "卡勒德·胡赛尼",
        },
        {
          id: 3,
          articleName: "人间失格",
          author: "太宰治",
        },
      ],
      formRules: {
        articleName: [
          { required: true, message: "请输入论文题目", trigger: "blur" },
        ],
        seqId: [{ required: true, message: "请输入论文的引用次序" }],
      },
    };
  },
  mounted() {
    // 这里比较重要，在表格dom渲染完成后，再进行事件的添加操作
    this.tableShow = true;
    this.$nextTick(() => {
      // 表格添加列宽变化
      this.tableInit();
      // console.log(this.tableHeadData)
    });
  },
  created() {
    const _this = this;
    this.page(1);
  },
};
</script>
