package com.management.article.control;

import com.management.article.dao.ArticleDAO;
import com.management.article.dataobject.ArticleDO;
import com.management.article.utils.ArticleDOFactory;
import com.management.article.utils.ArticleUtil;
import com.management.article.utils.ClassFieldUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.net.http.HttpResponse;
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
                     @RequestParam(value = "articleType", defaultValue = "") String articleType,
                     @RequestParam(value = "publishYear", defaultValue = "0") String publishYear

    ) {
        ArticleDO articleQueryParam = new ArticleDO();
        articleQueryParam.setId(id);
        articleQueryParam.setSeqId(seqId);
        articleQueryParam.setArticleName(articleName);
        articleQueryParam.setAuthor(author);
        articleQueryParam.setArticleType(articleType);
        articleQueryParam.setPublishYear(publishYear);
        System.out.println(articleQueryParam.getId());
        System.out.println(articleQueryParam.getSeqId());
        LOG.debug(articleQueryParam.getArticleName());
        LOG.debug(articleQueryParam.getPublishHouse());
        List<ArticleDO> articleDOs = new ArrayList<>();
        Map resultMap = new HashMap();
        resultMap.put("timestamp", ArticleUtil.LOG_TIME_FORMAT.format(new Date()));
        resultMap.put("status", 200);

        List<String> errors = new ArrayList<>();
        try {
            articleDOs = articleDAO.findByObject(articleQueryParam);

            for (ArticleDO articleDO : articleDOs) {
                System.out.println(articleDO.getArticleName());

            }
        } catch (Exception e) {
            errors.add(e.getMessage());
            resultMap.put("status", 400);

        }
        resultMap.put("errors", errors);
        if (articleDOs.size() > 0)
            resultMap.put("status", 401);
        resultMap.put("content", articleDOs);
        return resultMap;
    }

    @RequestMapping("addOne")
    @ResponseBody
    private Map addOne(@RequestBody ArticleDO articleDO) {

        LOG.debug(articleDO.getAuthor());
        Map resultMap = new HashMap();
        resultMap.put("timestamp", ArticleUtil.LOG_TIME_FORMAT.format(new Date()));
        resultMap.put("status", 200);
        List<String> errors = new ArrayList<>();
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
        resultMap.put("timestamp", ArticleUtil.LOG_TIME_FORMAT.format(new Date()));
        resultMap.put("status", 200);
        List<String> errors = new ArrayList<>();
        for (ArticleDO articleDO : articleDOList) {
            try {
                if (articleDAO.add(articleDO) > 0)
                    successNum++;
            } catch (Exception e) {
                errors.add(e.getMessage());
                resultMap.put("status", 400);
            }
        }
        resultMap.put("errors", errors);
        if (successNum > 0)
            resultMap.put("status", 401);
        resultMap.put("totalNum", articleDOList.size());
        resultMap.put("successNum", successNum);
        return resultMap;

    }

    @RequestMapping("updateOne")
    @ResponseBody
    private Map updateOne(@RequestBody ArticleDO articleDO) {
        Map resultMap = new HashMap();
        resultMap.put("timestamp", ArticleUtil.LOG_TIME_FORMAT.format(new Date()));
        resultMap.put("status", 200);
        Map logMap = new HashMap();
        logMap.put("body", articleDO);
        LOG.info(articleDO.getArticleName());
        System.out.println(articleDO.getPublishYear());
        List<String> errors = new ArrayList<>();

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
        resultMap.put("timestamp", ArticleUtil.LOG_TIME_FORMAT.format(new Date()));
        resultMap.put("status", 200);
        List<String> errors = new ArrayList<>();
        try {
            successNum += articleDAO.deleteById(id);
        } catch (Exception e) {
            errors.add(e.getMessage());
            resultMap.put("status", 400);
        }
        resultMap.put("errors", errors);
        if (successNum > 0)
            resultMap.put("status", 401);
        resultMap.put("successNum", successNum);
        return resultMap;
    }

    @DeleteMapping("deleteMulById")
    @ResponseBody
    private Map deleteMulById(@RequestBody List<Integer> idList) {
        int successNum = 0;
        Map resultMap = new HashMap();
        resultMap.put("timestamp", ArticleUtil.LOG_TIME_FORMAT.format(new Date()));
        resultMap.put("status", 200);
        List<String> errors = new ArrayList<>();
        for (int id : idList) {
            try {
                successNum += articleDAO.deleteById(id);
            } catch (Exception e) {
                errors.add(e.getMessage());
                resultMap.put("status", 400);
            }
        }
        resultMap.put("errors", errors);
        if (successNum > 0)
            resultMap.put("status", 401);
        resultMap.put("successNum", successNum);
        return resultMap;
    }

    // DEPRECIATE
    @DeleteMapping("deleteMulByObject")
    @ResponseBody
    private int deleteMulByObject(@RequestBody List<ArticleDO> articleDOList) {
        int successNum = 0;
        LOG.debug(articleDOList.get(0).getArticleName());
        Map resultMap = new HashMap();
        resultMap.put("timestamp", ArticleUtil.LOG_TIME_FORMAT.format(new Date()));
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
        if (successNum > 0)
            resultMap.put("status", 401);
        resultMap.put("successNum", successNum);
        return successNum;
    }

    @PostMapping("uploadExcel")
    @ResponseBody
    //要加file参数！！！
    public Map batchAddExcel(@RequestParam("file") MultipartFile multipartFile) throws Exception {
        Map resultMap = new HashMap();
        if (multipartFile == null || multipartFile.isEmpty()) {

            resultMap.put("errors", "文件不能为空！");
            return resultMap;
        }
        InputStream in = null;

        try {
            in = multipartFile.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        XSSFWorkbook excel = new XSSFWorkbook(in);
        List articleDOList = ArticleUtil.parseArticlesFromExcel(excel);
        resultMap = this.addMul(articleDOList);
        for (int i = 0; i < articleDOList.size(); i++)
            LOG.debug(articleDOList.get(i));
        return resultMap;
    }

    @PostMapping("/uploadTxt")
    @ResponseBody
    public Map batchAddFormatted(@RequestParam("file") MultipartFile file) {
        Map resultMap = new HashMap();
        resultMap.put("timestamp", ArticleUtil.LOG_TIME_FORMAT.format(new Date()));
        resultMap.put("status", 200);

        List<String> errors = new ArrayList<>();
        try {
            InputStream is = file.getInputStream();
            String line = null;
            List<ArticleDO> articleDOList = new ArrayList<>();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(is, "UTF-8"));
            while ((line = br.readLine()) != null) {
                articleDOList.add(ArticleUtil.parseArticleFromString(line));
            }
            resultMap = this.addMul(articleDOList);
        } catch (Exception e) {
            errors.add(e.getMessage());
            resultMap.put("status", 400);
        }
        return resultMap;
    }

    @PostMapping("/downloadExcel")
    @ResponseBody
    public Map downloadExcel(@RequestBody List<ArticleDO> articleDOList) {
        //文档对象
        Map resultMap = new HashMap();
        for (ArticleDO articleDO : articleDOList)
            System.out.println(articleDO.getArticleName());
        resultMap.put("timestamp", ArticleUtil.LOG_TIME_FORMAT.format(new Date()));
        resultMap.put("status", 200);HSSFWorkbook wb = new HSSFWorkbook();
        int rowNum = 0;
        Sheet sheet = wb.createSheet("excel的标题");
        Row row0 = sheet.createRow(rowNum++);
        row0.createCell(0).setCellValue("文献编号");
        row0.createCell(1).setCellValue("引用次序");
        row0.createCell(2).setCellValue("作者");
        row0.createCell(3).setCellValue("文献题目");
        row0.createCell(4).setCellValue("文献类型");
        row0.createCell(5).setCellValue("出版机构");
        row0.createCell(6).setCellValue("出版年份");
        row0.createCell(7).setCellValue("卷号（期号）");
        row0.createCell(8).setCellValue("起始页码或出版月");
        row0.createCell(9).setCellValue("结束页码或出版日");
        if (articleDOList != null && articleDOList.size() > 0) {
            for (ArticleDO articleDO : articleDOList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(articleDO.getId());
                row.createCell(1).setCellValue("[" + articleDO.getSeqId() + "]");
                row.createCell(2).setCellValue(articleDO.getAuthor());
                row.createCell(3).setCellValue(articleDO.getArticleName());
                row.createCell(4).setCellValue("[" + articleDO.getArticleType() + "]");
                row.createCell(5).setCellValue(articleDO.getPublishHouse());
                row.createCell(5).setCellValue(articleDO.getPublishYear());
                row.createCell(6).setCellValue(articleDO.getPublishYear());
                row.createCell(7).setCellValue(articleDO.getNum());
                row.createCell(8).setCellValue(articleDO.getStartPage());
                row.createCell(9).setCellValue(articleDO.getEndPage());
            }
        }
        if (rowNum - 1 == 0) resultMap.put("status", 400);
//        resultMap.put("content", wb);
        return resultMap;
    }
}
