package com.management.article.utils;

import com.management.article.control.ArticleControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.*;
import java.util.*;

public class ClassFieldUtil {
    private static final Logger LOG = LogManager.getLogger(ClassFieldUtil.class);

    /**
     * 根据属性名获取属性值
     */
    public static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(o, new Object[]{});
            return value;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取属性名数组
     */
    public static List<String> getFieldsNames(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        List<String> fieldNames = new ArrayList<>();
        for (int i = 0; i < fields.length; i++) {
            System.out.println(fields[i].getType());
            fieldNames.add(fields[i].getName());
        }
        return fieldNames;
    }

    /**
     * 获取属性类型(type)，属性名(name)，属性值(value)的map组成的list
     */
    public static List getFieldsInfo(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        List list = new ArrayList();
        Map infoMap = null;
        for (int i = 0; i < fields.length; i++) {
            infoMap = new HashMap();
            infoMap.put("type", fields[i].getType().toString());
            infoMap.put("name", fields[i].getName());
            infoMap.put("value", getFieldValueByName(fields[i].getName(), o));
            list.add(infoMap);
        }
        return list;
    }

    /**
     * 获取对象的所有属性值，返回一个对象数组
     */
    public static List<Object> getFieldValues(Object o) {
        List<String> fieldNames = getFieldsNames(o);
        List<Object> value = new ArrayList<>();
        for (int i = 0; i < fieldNames.size(); i++) {
            value.add(getFieldValueByName(fieldNames.get(i), o));
        }
        return value;
    }
}
