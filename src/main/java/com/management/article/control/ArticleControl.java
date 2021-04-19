package com.management.article.control;

import com.management.article.dao.ArticleDAO;
import com.management.article.dataobject.ArticleDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class ArticleControl {
    @Autowired
    private ArticleDAO articleDAO;

    @RequestMapping("searchContent")
    @ResponseBody
    private List<ArticleDO> search(@RequestParam(value = "id", defaultValue = "-1") long id, @RequestParam(value = "seqId", defaultValue = "-1") long seqId, @RequestParam(value = "author", defaultValue = "null") String author, @RequestParam(value = "articleName", defaultValue = "null") String articleName, @RequestParam(value = "articleType", defaultValue = "null") String articleType) {
        ArticleDO articleQueryParam = new ArticleDO();
        articleQueryParam.setId(id);
        articleQueryParam.setSeqId(seqId);
        articleQueryParam.setArticleName(articleName == "null" ? null : articleName);
        articleQueryParam.setAuthor(author == "null" ? null : author);
        articleQueryParam.setArticleType(articleType == "null" ? null : articleType);
        System.out.println(articleName == "null");
        List<ArticleDO> articleDOs = articleDAO.findByObject(articleQueryParam);
        return articleDOs;
    }

    @RequestMapping("addContent")
    @ResponseBody
    private int addContent(@RequestBody ArticleDO articleDO){
        return articleDAO.add(articleDO);
    }
}
