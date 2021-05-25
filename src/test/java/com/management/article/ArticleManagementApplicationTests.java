package com.management.article;

import ch.qos.logback.core.joran.action.ActionUtil;
import com.management.article.control.ArticleControl;
import com.management.article.dao.ArticleDAO;
import com.management.article.dataobject.ArticleDO;
import com.management.article.utils.ArticleUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlgraphics.util.ClasspathResource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
//        String filePath = "/Volumes/杂项/学习/java/课设/article/src/main/resources/testdata.xlsx";
        ClassPathResource classpathResourceXlsx = new ClassPathResource("testdata.xlsx");
        try {
            File testxlsx = classpathResourceXlsx.getFile();
            fileInputStream = new FileInputStream(testxlsx);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fileInputStream);
            //获取sheet
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
        //找resource下的
        ClassPathResource classpathResourceXlsx = new ClassPathResource("testdata.xlsx");
        try {
            fileInputStream = new FileInputStream(classpathResourceXlsx.getFile());
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fileInputStream);
            //获取sheet
            MultipartFile multipartFile = new MockMultipartFile("test.xlsx", fileInputStream);
            articleControl.batchAddExcel(multipartFile);

            System.out.println("ok");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void testParseArticleFromString() {
//        String s = "[2] 葛家澍，林志军.现代西方财务会计理论[M].厦门：厦门大学出版社，2001：42.";
//        [1]刘国钧，陈绍业，王凤翥. 图书馆目录[M]. 北京：高等教育出版社，1957.15-18.
        try {
            FileInputStream txtStream = new FileInputStream(new ClassPathResource("参考文献.txt").getFile());
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
                        System.out.println("已存在！！！");
                    }
                    continue;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();

        }


    }

}
