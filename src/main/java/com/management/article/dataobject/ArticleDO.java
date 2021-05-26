package com.management.article.dataobject;

import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

@Alias("articleDO")
public class ArticleDO {
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
     * 文献题目
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
     * 起始页码或出版月
     * ！！！如果是报纸则为出版月，因为报纸必须写出版月日，但不用写页码，默认或空则为0
     */
    private short startPage;
    /**
     * 结束页码或出版日
     * ！！！如果是报纸则为出版日
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