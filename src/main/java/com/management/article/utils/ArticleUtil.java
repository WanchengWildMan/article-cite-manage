package com.management.article.utils;

import com.management.article.dataobject.ArticleDO;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArticleUtil {
    // TODO:正则表达式从标准格式获取

    public static final String MONOGRAPH = "M";
    public static final String ARTICLE_COLLECTION = "C";
    public static final String NEWSPAPER_ARTICLE = "N";
    public static final String JOURNAL = "J";
    public static final String DISSERTATION = "D";
    public static final String REPORT = "R";
    public static final String NULL = "null";

    public static final String ARTICLE_NAME = "articleName";
    public static final String ARTICLE_TYPE = "articleType";
    public static final String PUBLISH_HOUSE = "publishHouse";
    public static final String PUBLISH_Year = "publishYear";
    //卷号（期号）
    public static final String JOURNAL_NUMBER_OR_PAPER_NUMBER = "num";
    //起始页码
    public static final String START_PAGE = "startPage";
    //结束页码
    public static final String END_PAGE = "endPage";

    public static final SimpleDateFormat LOG_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public boolean isDate(String s) {
        DateTimeFormatter fomatter = DateTimeFormatter.ofPattern("yyyy");
        try {
            LocalDateTime year = LocalDateTime.parse(s, fomatter);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean isInteger(String str) {
        Pattern pat = Pattern.compile("[-\\+]?[\\d]*$");
        return pat.matcher(str).matches();
    }

    public static ArticleDO parseArticleFromString(String s) throws Exception {
        String pat_seqId = "\\[(\\d*)\\]";
        String pat = "\\[(\\d*)\\]\\s*([^。.]*)[.｜。]\\s*([^\\[\\s]*)\\s*\\[([^\\]]*)\\]\\s*.\\s*([^,，]*)[,|，]\\s*(\\d{4})[\\(|（]?(\\d*)[\\)|）]?[\\:|：|,|，|\\D]*(\\d*)[\\-]*(\\d*)\\.*";
        String pat_eng = "\\[(\\d*)\\]\\s*([[\\w*|,|\\s]|[\\.]*(?=[\\S])&*]*)\\.\\s*([[^\\[]*\\s*]*)\\[([^\\]]*)\\]\\s*\\.\\s*([^\\d|,]*),(\\d{4})[\\(|（]?(\\d*)[\\)|）]?\\.?[\\:|：|,|，|\\D]*(\\d*)[\\-]*(\\d*)\\.*";

        Pattern rpat = Pattern.compile(pat);
        Pattern rpat_eng = Pattern.compile(pat_eng);
        Matcher mat = rpat.matcher(s);

        Matcher matcher_eng = rpat_eng.matcher(s.toString());
        ArticleDO articleDO = new ArticleDO();
        List<Map> props = ClassFieldUtil.getFieldsInfo(new ArticleDO());
        Map map = new HashMap<>();
        boolean ok=mat.find();
        if (!ok) {
            mat = matcher_eng;
            ok=matcher_eng.find();
        }
        if (ok) {
            //不同类型要分别判断！！
            for (int i = 1; i <= mat.groupCount(); i++) {
                String propName = props.get(i).get("name").toString();
                String propType = props.get(i).get("type").toString().toLowerCase();
                String content = mat.group(i);
                System.out.println(content);
                if (content == "") content = "0";
                if (propType.contains("short")) {
                    int x = 0;
                    x = Integer.parseInt(content);
                    map.put(propName, (short) x);
                } else if (propType.contains("int") || propType.contains("long")) {
                    map.put(propName, Integer.parseInt((content)));
                } else {
                    map.put(propName, content);
                }

            }
            if (map.get(END_PAGE).equals(0)) {
                map.put(END_PAGE, map.get(START_PAGE));
            }
            if (map.get(JOURNAL_NUMBER_OR_PAPER_NUMBER).equals(0)) {
                map.put(JOURNAL_NUMBER_OR_PAPER_NUMBER, 0);
            }
            return ArticleDOFactory.getArticleDO(map);

        }

        return null;
    }

    public static List<ArticleDO> parseArticlesFromExcel(XSSFWorkbook xssfWorkbook) throws Exception {
        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
        if (xssfSheet == null)
            return null;
        List<Map> props = ClassFieldUtil.getFieldsInfo(new ArticleDO());
        List<ArticleDO> articleDOList = new ArrayList();
        for (int rowNum = 1, i = 0; rowNum <= xssfSheet.getLastRowNum(); rowNum++, i++) {
            XSSFRow xssfRow = xssfSheet.getRow(rowNum);
            int minColIx = xssfRow.getFirstCellNum();
            int maxColIx = xssfRow.getLastCellNum();
            Map rowMap = new HashMap();
            for (int colIx = minColIx; colIx < maxColIx; colIx++) {
                int j = (colIx - minColIx);
                XSSFCell cell = xssfRow.getCell(colIx);
                if (cell == null) {
                    continue;
                }
                System.out.println(cell.getCellType());
                String propName = props.get(j + 1).get("name").toString();
                String propType = props.get(j + 1).get("type").toString();
                if (cell.getCellType() == CellType.NUMERIC) {
                    double x = cell.getNumericCellValue();
                    if (propName.equals("publishYear")) {
                        rowMap.put(propName, Integer.toString((int) x));
                    } else if (propType.toLowerCase().equals("long")) {
                        rowMap.put(propName, (long) x);
                    } else if (propType.toLowerCase().contains("short")) {
                        rowMap.put(propName, (short) x);
                    } else if (propType.equals("int")) {
                        rowMap.put(propName, (int) x);
                    } else rowMap.put(propName, x);
                } else rowMap.put(propName, cell.getStringCellValue());
                System.out.println(propName + " " + rowMap.get(propName));
            }

            try {
                articleDOList.add(ArticleDOFactory.getArticleDO(rowMap));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return articleDOList;
    }
}
