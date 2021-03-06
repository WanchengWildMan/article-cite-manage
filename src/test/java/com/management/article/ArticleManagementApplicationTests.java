package com.management.article;

import com.management.article.control.ArticleControl;
import com.management.article.dao.ArticleDAO;
import com.management.article.dataobject.ArticleDO;
import com.management.article.utils.ArticleUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootTest
class ArticleManagementApplicationTests {
    @Autowired
    ArticleDAO articleDAO;
    @Autowired
    ArticleControl articleControl;

    @Test
    void contextLoads() {
        List<ArticleDO> list = new ArrayList();
        ArticleDO articleDO = new ArticleDO();
        list = articleDAO.findById(0);
        ArticleDO newA = list.get(0);
        newA.setArticleName(RandomStringUtils.random(5));
        newA.setId(new Random().nextInt(1231124));
        newA.setArticleName(RandomStringUtils.random(12));
        ArticleDO newB = list.get(1);
        newB.setArticleName(RandomStringUtils.random(12));
        System.out.println(newA.getArticleName());
        list.set(0, newA);
        int sucessNum = articleDAO.batchAdd(list.subList(0, 2));
        System.out.println(sucessNum);
    }

    @Test
    void addExcel() {
        FileInputStream fileInputStream = null;
        ClassPathResource classpathResourceXlsx = new ClassPathResource("testdata.xlsx");
        try {
            File testxlsx = classpathResourceXlsx.getFile();
            fileInputStream = new FileInputStream(testxlsx);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fileInputStream);
            //??????sheet
            List<ArticleDO> articleDOS = ArticleUtil.parseArticlesFromExcel(xssfWorkbook);
            articleDAO.batchAdd(articleDOS);
            System.out.println("ok");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void testBatchAddExcelController() {
        FileInputStream fileInputStream = null;
        //???resource??????
        ClassPathResource classpathResourceXlsx = new ClassPathResource("testdata.xlsx");
        try {
            fileInputStream = new FileInputStream(classpathResourceXlsx.getFile());
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fileInputStream);
            //??????sheet
            MultipartFile multipartFile = new MockMultipartFile("test.xlsx", fileInputStream);
            articleControl.batchAddExcel(multipartFile);

            System.out.println("ok");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void testParseArticleFromString() {
        String s = "[2] ?????????????????????.??????????????????????????????[M].?????????????????????????????????2001???42.";
//        [1]?????????????????????????????????. ???????????????[M]. ?????????????????????????????????1957.15-18.
        try {
            FileInputStream txtStream = new FileInputStream(new ClassPathResource("????????????.txt").getFile());
            BufferedReader bfr = new BufferedReader(new InputStreamReader(txtStream));
            String line = null;
            while ((line = bfr.readLine()) != null) {
                ArticleDO articleDO = ArticleUtil.parseArticleFromString(line);
                if (articleDO == null) continue;
                try {
                    articleDAO.add(articleDO);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    if (e.getMessage().contains("Duplicate")) {
                        try {
                            articleDAO.update(articleDO);
                        }catch (Exception e2){
                            e2.printStackTrace();
                        }
                        System.out.println("??????????????????");
                    }
                    continue;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();

        }


    }

}
