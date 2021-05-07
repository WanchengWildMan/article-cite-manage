package com.management.article;

import ch.qos.logback.core.joran.action.ActionUtil;
import com.management.article.control.ArticleControl;
import com.management.article.dao.ArticleDAO;
import com.management.article.dataobject.ArticleDO;
import com.management.article.utils.ArticleUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
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
        String filePath = "/Volumes/杂项/学习/java/课设/article/src/main/resources/testdata.xlsx";
        try {
            fileInputStream = new FileInputStream(filePath);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fileInputStream);
            //获取sheet
            List<ArticleDO> articleDOS = ArticleUtil.parseArticlesFromExcel(xssfWorkbook);

            System.out.println("ok");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void testBatchAddExcelController() {
        FileInputStream fileInputStream = null;
        String filePath = "/Volumes/杂项/学习/java/课设/article/src/main/resources/testdata.xlsx";
        try {
            fileInputStream = new FileInputStream(filePath);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fileInputStream);
            //获取sheet

            articleControl.batchAddExcel(xssfWorkbook);

            System.out.println("ok");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void testParseArticleFromString() {
        String s = "[2] 葛家澍，林志军.现代西方财务会计理论[M].厦门：厦门大学出版社，2001：42.";

        try {
            ArticleDO articleDO = ArticleUtil.parseArticleFromString(s);
            articleDAO.add(articleDO);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
