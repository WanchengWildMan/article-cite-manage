package com.management.article.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;

import com.management.article.dataobject.ArticleDO;

public class ArticleDOFactory {
    public static ArticleDO getArticleDO(Map<String, Object> map) {
        if (map == null)
            return null;
        Object obj = new Object();
        try {
            Class<?> beanClass = ArticleDO.class;
            obj = beanClass.newInstance();
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                Method setter = property.getWriteMethod();
                if (map.get(property.getName()) == null) continue;

                if (setter != null) {
                    System.out.println(property.getName());
                    //getName之后取的才是字符串
                    if (property.getPropertyType().getName().toLowerCase().contains("short")) {
                        int x = Integer.parseInt(map.get(property.getName()).toString());
                        setter.invoke(obj, (short) x);
                    } else {
                        setter.invoke(obj, map.get(property.getName()));
                    }
                    System.out.println(map.get(property.getName()));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return (ArticleDO) obj;
    }
}
