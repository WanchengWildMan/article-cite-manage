<template>
  <div>
    <el-table :data="tableData" border style="width: 70%">
      <el-table-column fixed prop="id" label="" width="150"> </el-table-column>
      <el-table-column fixed sort prop="seqId" label="" width="150">
      </el-table-column>
      <el-table-column prop="articleName" label="论文题目" width="150">
      </el-table-column>
      <el-table-column prop="author" label="作者" width="120">
      </el-table-column>
      <el-table-column prop="articleType" label="论文类型" width="150">
      </el-table-column>

      <el-table-column fixed sortable="right" label="操作" width="100">
        <template slot-scope="scope">
          <el-button @click="edit(scope.row)" type="text" size="small"
            >修改</el-button
          >
          <el-button @click="deleteArticle(scope.row)" type="text" size="small"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    //分页操作
    <!--    <el-pagination-->
    <!--        background-->
    <!--        layout="prev, pager, next"-->
    <!--        :page-size="pageSize"-->
    <!--        :total="total"-->
    <!--        @current-change="page">-->
    <!--    </el-pagination>-->
  </div>
</template>
<script src="https://unpkg.com/axios/dist/axios.min.js"></script>
<script>
export default {
  methods: {
    deleteArticle(row) {
      const _this = this;
      axios
        .delete(`http://localhost:${this.port}/admin/deleteOneById/` + row.id)
        .then(function (resp) {
          if (!resp.data || resp.data.errors.length > 0) {
            _this.$alert(
              `论文  + ${row.articleName} +  删除失败！原因：${resp.data.errors}`,
              "消息",
              {
                confirmButtonText: "确定",
                callback: (action) => {
                  window.location.reload();
                },
              }
            );
          }
          _this.$alert("论文 " + row.articleName + " 删除成功！", "消息", {
            confirmButtonText: "确定",
            callback: (action) => {
              window.location.reload();
            },
          });
        });
    },
    edit(row) {
      this.$router.push({
        path: "admin/updateOne",
        query: {
          id: row.id,
        },
        data: {
          body: row,
        },
      });
    },
    page(currentPage) {
      const _this = this;
      axios.get(`http://localhost:${this.port}/admin/find`).then(function (resp) {
        console.log(resp);
        _this.tableData = resp.data.content;
        // _this.pageSize = resp.data.size
        // _this.total = resp.data.totalElements
      });
    },
    // handleCellClick:function(row, column, cell, event){
    //   emptransfer.addClass(cell,'current-cell');
    //   if(emptransfer.getChildElement(cell,3) !== 0){
    //     var _inputParentNode =emptransfer.getChildElement(cell,3);
    //     if(_inputParentNode.hasChildNodes()&& _inputParentNode.childNodes.length > 2) {
    //       var _inputNode = _inputParentNode.childNodes[2];
    //       if(_inputNode.tagName === 'INPUT'){
    //         _inputNode.focus();
    //       }
    //     }
    //   }
    // },
//input框失去焦点事件
//     handleInputBlur:function(event){   //当 input 失去焦点 时,input 切换为 span，并且让下方 表格消失（注意，与点击表格事件的执行顺序）
//       var _event = event;
//       setTimeout(function(){
//         var _inputNode = _event.target;
//         if(emptransfer.getParentElement(_inputNode,4)!==0){
//           var _cellNode = emptransfer.getParentElement(_inputNode,4);
//           emptransfer.removeClass(_cellNode,'current-cell');
//           emptransfer.removeClass(_cellNode,'current-cell2');
//         }
//       },200);
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
  // 防止全局组件污染，故data用函数
  data() {
    return {
      port: 8080,
      pageSize: "1",
      total: "11",
      tableData: [
        {
          id: 1,
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
    };
  },
  mounted() {
    // 这里比较重要，在表格dom渲染完成后，再进行事件的添加操作
    this.tableShow = true;
    this.$nextTick(() => {
      // 表格添加列宽变化
      this.tableInit();
    });
  },
  created() {
    const _this = this;
    axios.get(`http://localhost:${this.port}/admin/find`).then(function (resp) {
      console.log(resp);
      _this.tableData = resp.data.content;
      // _this.pageSize = resp.data.size
      // _this.total = resp.data.totalElements
    });
  },
};
</script>
