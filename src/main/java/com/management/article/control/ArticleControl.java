package com.management.article.control;

import com.management.article.dao.ArticleDAO;
import com.management.article.dataobject.ArticleDO;
import com.management.article.utils.ArticleUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class ArticleControl {
    @Autowired
    private ArticleDAO articleDAO;
    private static final Logger LOG = LogManager.getLogger(ArticleControl.class);

    @RequestMapping("find")
    @ResponseBody
    private Map find(@RequestParam(value = "id", defaultValue = "0") long id,
                     @RequestParam(value = "author", defaultValue = "") String author,
                     @RequestParam(value = "articleName", defaultValue = "") String articleName,
                     @RequestParam(value = "articleType", defaultValue = "") String articleType,
                     @RequestParam(value = "publishYear", defaultValue = "0") String publishYear,
                     @RequestParam(value = "publishYearStart", defaultValue = "0") String publishYearStart,
                     @RequestParam(value = "publishYearEnd", defaultValue = "9999") String publishYearEnd
    ) {
        ArticleDO articleQueryParam = new ArticleDO();
        articleQueryParam.setId(id);
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
            articleDOs = articleDOs.stream().filter(e -> e.getPublishYear().compareTo(publishYearEnd) < 0 && e.getPublishYear().compareTo(publishYearStart) > 0).collect(Collectors.toList());
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
            boolean ok = false;
            //?????????????????????
            if (e.getMessage().contains("Duplicate")) {
                if (articleDAO.update(articleDO) > 0) {
                    successNum++;
                    ok = true;
                }
                if (!ok) {
                    errors.add(articleDO.getAuthor() + articleDO.getArticleName() + "????????????");
                    resultMap.put("status", 400);
                }
            }
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
        for (int i = 0; i < articleDOList.size(); i++) {
            ArticleDO articleDO = articleDOList.get(i);
            boolean ok = false;
            try {
                if (articleDAO.add(articleDO) > 0) {
                    successNum++;
                    ok = true;
                }
            } catch (Exception e) {
                if (articleDAO.update(articleDO) > 0) {
                    successNum++;
                    ok = true;
                }
                if (!ok) {
                    //????????????????????????????????????????????????
                    if (e.getMessage().contains("Duplicate"))
                        errors.add(articleDO.getAuthor() + articleDO.getArticleName() + "????????????");
                    else if (articleDO == null) errors.add("???" + (i + 1) + "??????????????????" + "??????????????????????????????????????????");
                    resultMap.put("status", 400);
                }

            }
        }
        resultMap.put("errors", errors);
        if (articleDOList.size() - successNum > 0)
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
    //??????file???????????????
    public Map batchAddExcel(@RequestParam("file") MultipartFile multipartFile) throws Exception {
        Map resultMap = new HashMap();
        if (multipartFile == null || multipartFile.isEmpty()) {

            resultMap.put("errors", "?????????????????????");
            return resultMap;
        }
        InputStream in = null;
        List articleDOList = null;
        try {
            in = multipartFile.getInputStream();
            XSSFWorkbook excel = new XSSFWorkbook(in);
            articleDOList = ArticleUtil.parseArticlesFromExcel(excel);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        resultMap.put("successNum", 0);
        List<String> errors = new ArrayList<>();
        try {
            InputStream is = file.getInputStream();
            String line = null;
            List<ArticleDO> articleDOList = new ArrayList<>();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(is, "UTF-8"));
            while ((line = br.readLine()) != null) {
                try {
                    ArticleDO articleDO = ArticleUtil.parseArticleFromString(line);
                    articleDOList.add(articleDO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //??????addMul????????????????????????
            resultMap.put("totalNum", articleDOList.size());
            resultMap = this.addMul(articleDOList);
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("status", 400);
        }
        return resultMap;
    }

    @PostMapping(value = "/downloadExcel")//, produces = "application/vnd.ms-excel;charset=utf-8")
    public ResponseEntity<byte[]> downloadExcel(@RequestParam("isTemplate") boolean isTemplate, @RequestBody List<ArticleDO> articleDOList, HttpServletResponse response) throws IOException {
        //????????????


        Map resultMap = new HashMap();
        for (ArticleDO articleDO : articleDOList)
            System.out.println(articleDO.getArticleName());
        resultMap.put("timestamp", ArticleUtil.LOG_TIME_FORMAT.format(new Date()));
        resultMap.put("status", 200);
        XSSFWorkbook wb = new XSSFWorkbook();
        int rowNum = 0;
        Sheet sheet = wb.createSheet("??????????????????");
        String[] headerNames = {"????????????", "??????", "????????????", "????????????", "????????????", "????????????", "??????????????????", "????????????????????????", "????????????????????????"};
        Row row0 = sheet.createRow(rowNum++);
        for (int i = 0; i < headerNames.length; i++) {
            int idx = (isTemplate ? i - 1 : i);
            if (idx < 0) continue;
            row0.createCell(idx).setCellValue(headerNames[i]);
        }
        if (!isTemplate) {
            if (articleDOList != null && articleDOList.size() > 0) {
                for (ArticleDO articleDO : articleDOList) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(articleDO.getId());
//                row.createCell(1).setCellValue("[" + articleDO.getSeqId() + "]");
                    row.createCell(1).setCellValue(articleDO.getAuthor());
                    row.createCell(2).setCellValue(articleDO.getArticleName());
                    row.createCell(3).setCellValue("[" + articleDO.getArticleType() + "]");
                    row.createCell(4).setCellValue(articleDO.getPublishHouse());
                    row.createCell(5).setCellValue(articleDO.getPublishYear());
                    row.createCell(6).setCellValue(articleDO.getNum());
                    row.createCell(7).setCellValue(articleDO.getStartPage());
                    row.createCell(8).setCellValue(articleDO.getEndPage());
                }
            }
            if (rowNum - 1 == 0) resultMap.put("status", 400);
        }
        resultMap.put("content", wb);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ResponseEntity<byte[]> filebyte = null;
        try {
            wb.write(outputStream);
            outputStream.close();
            String fileName = new String("??????????????????.xlsx".getBytes("UTF-8"), "iso-8859-1");
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentDispositionFormData("attachment", fileName);
            httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            filebyte = new ResponseEntity<byte[]>(outputStream.toByteArray(), httpHeaders, HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filebyte;
    }

    @PostMapping(value = "/downloadTxt")//, produces = "application/vnd.ms-excel;charset=utf-8")
    public ResponseEntity<byte[]> downloadExcel(@RequestBody List<String> lineList, HttpServletResponse response) throws IOException {
        Map resultMap = new HashMap();
        for (String articleDOString : lineList)
            System.out.println(articleDOString);
        resultMap.put("timestamp", ArticleUtil.LOG_TIME_FORMAT.format(new Date()));
        resultMap.put("status", 200);
        // ????????????
        String fileName = "??????????????????";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StringBuffer stringBuffer = new StringBuffer();
        ResponseEntity<byte[]> filebyte = null;
        String enter = "\n";
        try {
            // ?????????????????????
            if (lineList.size() > 0) {
                for (int i = 0; i < lineList.size(); i++) {
                    stringBuffer.append(lineList.get(i));
                    stringBuffer.append(enter);
                }
            }
            outputStream.write(stringBuffer.toString().getBytes("UTF-8"));
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentDispositionFormData("attachment", fileName);
            httpHeaders.setContentType(MediaType.TEXT_PLAIN);
            filebyte = new ResponseEntity<byte[]>(outputStream.toByteArray(), httpHeaders, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return filebyte;
    }
}
