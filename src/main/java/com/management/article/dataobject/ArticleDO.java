package com.management.article.dataobject;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

@Mapper
@Alias("articleDO")
public class ArticleDO {
    //基本信息：文献编号、文献作者、文献篇名、文献类型（M：专著、C：论文集、N：报纸文章、J：期刊文章、D：学位论文、R：报告）、出版机构、出版时间、卷号（期号），
    //[1] 夏鲁惠.高等学校毕业论文教学情况调研报告[J].高等理科教育，2004(1):46-52.
    //[2] 葛家澍，林志军.现代西方财务会计理论[M].厦门：厦门大学出版社，2001：42.
    //https://wenku.baidu.com/view/978dff906bd97f192379e915.html
    /**
     * 文献编号
     */
    private long id;
    /**
     * 文中的顺序
     */
    private long seqId;
    /**
     * 文献作者
     */
    private String author;
    /**
     * 文献篇名
     */
    private String articleName;
    /**
     * 文献类型
     */
    private String articleType;
    /**
     * 出版机构
     */
    private String publishHouse;


    /**
     * 出版时间
     */
    private String publishYear;
    /**
     * 卷号(期号)
     */
    private short num;
    /**
     * 起页码
     */
    private short startPage;
    /**
     * 止页码
     */
    private short endPage;
    /**
     * 创建和修改时间
     */
    private LocalDateTime gmtCreated;
    private LocalDateTime gmtModified;

    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }

    public long getSeqId() {
        return seqId;
    }

    public void setSeqId(long seqId) {
        this.seqId = seqId;
    }


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public String getArticleType() {
        return articleType;
    }

    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }

    public String getPublishHouse() {
        return publishHouse;
    }

    public void setPublishHouse(String publishHouse) {
        this.publishHouse = publishHouse;
    }

    public String getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(String publishYear) {
        this.publishYear = publishYear;
    }

    public short getNum() {
        return num;
    }

    public void setNum(short num) {
        this.num = num;
    }

    public short getStartPage() {
        return startPage;
    }

    public void setStartPage(short startPage) {
        this.startPage = startPage;
    }

    public short getEndPage() {
        return endPage;
    }

    public void setEndPage(short endPage) {
        this.endPage = endPage;
    }

    public LocalDateTime getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(LocalDateTime gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }
}