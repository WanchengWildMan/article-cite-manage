# article-cite-manage
## 前端
Vue+Element UI
###功能
一个增删查改的表格
- 行内编辑保存
- 多个过滤条件
- 对话框添加
- 单元格缩放
#后端
SpringBoot
## 认证
直接采用SpringSecurity，登录需输入用户名密码，在application.properties中配置
## 接口 /admin：
### 增 /add___By__
### 删 /delete___By__
### 查 /find___By__ /find
### 改 update___By__
___ : 一个或多个One/Mul

__: 参数类型Id/Object
- Object：请求体body中的json
- Id：请求参数query中的值
### Excel与txt的上传与Excel的导出
- ####txt
   - 导入：从标准引用字格式中解析 分别对中英文引用格式进行正则表达式匹配
- ####Excel
    格式：xlsx
   - 导入：点击导出按钮下载导入模版填写信息后
   - 导出：点击导出按钮对筛选后的数据进行导出