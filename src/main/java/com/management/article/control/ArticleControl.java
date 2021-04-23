package com.management.article.control;

import com.management.article.dao.ArticleDAO;
import com.management.article.dataobject.ArticleDO;
import com.management.article.utils.ArticleUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class ArticleControl {
    @Autowired
    private ArticleDAO articleDAO;
    private static final Logger LOG = LogManager.getLogger(ArticleControl.class);

    @RequestMapping("find")
    @ResponseBody
    private Map find(@RequestParam(value = "id", defaultValue = "0") long id,
                     @RequestParam(value = "seqId", defaultValue = "0") long seqId,
                     @RequestParam(value = "author", defaultValue = "") String author,
                     @RequestParam(value = "articleName", defaultValue = "") String articleName,
                     @RequestParam(value = "articleType", defaultValue = "") String articleType) {
        ArticleDO articleQueryParam = new ArticleDO();
        articleQueryParam.setId(id);
        articleQueryParam.setSeqId(seqId);
        articleQueryParam.setArticleName(articleName);
        articleQueryParam.setAuthor(author);
        articleQueryParam.setArticleType(articleType);
        System.out.println(articleQueryParam.getId());
        System.out.println(articleQueryParam.getSeqId());
        LOG.debug(articleQueryParam.getArticleName());
        LOG.debug(articleQueryParam.getPublishHouse());
        List<ArticleDO> articleDOs = new ArrayList<>();
        Map resultMap = new HashMap();
        resultMap.put("status", 200);

        List<String> errors = new ArrayList<>();
        resultMap.put("timestamp", ArticleUtil.LOG_TIME_FORMAT.format(new Date()));
        try {
            articleDOs = articleDAO.findByObject(articleQueryParam);


            for (ArticleDO articleDO : articleDOs) {
                System.out.println(articleDO.getArticleName()
                );

            }
        } catch (Exception e) {
            errors.add(e.getMessage());
            resultMap.put("status", 400);

        }
        resultMap.put("errors", errors);
        if (articleDOs.size() > 0) resultMap.put("status", 401);
        resultMap.put("content", articleDOs);
        return resultMap;
    }


    @RequestMapping("addOne")
    @ResponseBody
    private Map addOne(@RequestBody ArticleDO articleDO) {

        LOG.debug(articleDO.getAuthor());
        Map resultMap = new HashMap();
        resultMap.put("status", 200);

        List<String> errors = new ArrayList<>();
        resultMap.put("timestamp", ArticleUtil.LOG_TIME_FORMAT.format(new Date()));
        int successNum = 0;
        try {
            successNum = articleDAO.add(articleDO);

        } catch (Exception e) {
            errors.add(e.getMessage());
            resultMap.put("status", 400);
        }
        resultMap.put("errors", errors);
        resultMap.put("successNum", successNum);
        return resultMap;
    }

    @RequestMapping("addMul")
    @ResponseBody
    private Map addMul(@RequestBody List<ArticleDO> articleDOList) {
        int successNum = 0;
        Map resultMap = new HashMap();
        resultMap.put("status", 200);
        List<String> errors = new ArrayList<>();
        resultMap.put("timestamp", ArticleUtil.LOG_TIME_FORMAT.format(new Date()));
        for (ArticleDO articleDO : articleDOList) {
            try {
                if (articleDAO.add(articleDO) > 0) successNum++;
            } catch (Exception e) {
                errors.add(e.getMessage());
                resultMap.put("status", 400);
            }
        }
        resultMap.put("errors", errors);
        if (successNum > 0) resultMap.put("status", 401);

        resultMap.put("successNum", successNum);
        return resultMap;

    }

    @RequestMapping("updateOne")
    @ResponseBody
    private Map updateOne(@RequestBody ArticleDO articleDO) {
        Map resultMap = new HashMap();
        resultMap.put("status", 200);

        System.out.println(articleDO.getPublishYear());
        List<String> errors = new ArrayList<>();
        resultMap.put("timestamp", ArticleUtil.LOG_TIME_FORMAT.format(new Date()));

        int successNum = 0;
        try {
            successNum = articleDAO.update(articleDO);
        } catch (Exception e) {
            e.printStackTrace();
            errors.add(e.getMessage());
            resultMap.put("status", 400);

        }
        resultMap.put("errors", errors);
        resultMap.put("successNum", successNum);
        return resultMap;

    }


    @DeleteMapping("deleteOneById")
    @ResponseBody
    private Map deleteOneById(@RequestParam int id) {
        int successNum = 0;
        Map resultMap = new HashMap();
        resultMap.put("status", 200);

        List<String> errors = new ArrayList<>();
        resultMap.put("timestamp", ArticleUtil.LOG_TIME_FORMAT.format(new Date()));
        try {
            successNum += articleDAO.deleteById(id);
        } catch (Exception e) {
            errors.add(e.getMessage());
            resultMap.put("status", 400);
        }
        resultMap.put("errors", errors);
        if (successNum > 0) resultMap.put("status", 401);
        resultMap.put("successNum", successNum);
        return resultMap;
    }

    @DeleteMapping("deleteMulById")
    @ResponseBody
    private Map deleteMulById(@RequestBody List<Integer> idList) {
        int successNum = 0;
        Map resultMap = new HashMap();
        resultMap.put("status", 200);

        List<String> errors = new ArrayList<>();
        resultMap.put("timestamp", ArticleUtil.LOG_TIME_FORMAT.format(new Date()));
        for (int id : idList) {
            try {
                successNum += articleDAO.deleteById(id);
            } catch (Exception e) {
                errors.add(e.getMessage());
                resultMap.put("status", 400);
            }
        }
        resultMap.put("errors", errors);
        if (successNum > 0) resultMap.put("status", 401);
        resultMap.put("successNum", successNum);
        return resultMap;
    }

    //DEPRECIATE
    @DeleteMapping("deleteMulByObject")
    @ResponseBody
    private int deleteMulByObject(@RequestBody List<ArticleDO> articleDOList) {
        int successNum = 0;
        LOG.debug(articleDOList.get(0).getArticleName());
        Map resultMap = new HashMap();
        resultMap.put("status", 200);

        List<String> errors = new ArrayList<>();
        for (ArticleDO articleDO : articleDOList) {
            try {
                successNum += articleDAO.deleteByObject(articleDO);

            } catch (Exception e) {
                errors.add(e.getMessage());
                resultMap.put("status", 400);
            }
        }
        resultMap.put("errors", errors);
        if (successNum > 0) resultMap.put("status", 401);
        resultMap.put("successNum", successNum);
        return successNum;
    }
}
