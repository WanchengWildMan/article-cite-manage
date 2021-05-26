package com.management.article.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;

import com.management.article.dataobject.ArticleDO;

public class ArticleDOFactory {
    /**
     *
     * @param map 解析字符串或excel得到的map
     * @return ArticleDO
     */
    public static ArticleDO getArticleDO(Map<String, Object> map) {
        if (map == null)
            return null;
        Object obj = new Object();
        try {
            //用bean获取
            Class<?> beanClass = ArticleDO.class;
            obj = beanClass.newInstance();
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                //获取某个属性的set方法
                Method setter = property.getWriteMethod();
                if (map.get(property.getName()) == null) continue;

                if (setter != null) {
                    System.out.println(property.getName());
                    //getName之后取的才是字符串
                    //不能直接将Integer转换为short类型
                    if (property.getPropertyType().getName().toLowerCase().contains("short")) {
                        int x = Integer.parseInt(map.get(property.getName()).toString());
                        setter.invoke(obj, (short) x);
                    } else {
                        setter.invoke(obj, map.get(property.getName()));
                    }
//                    System.out.println(map.get(property.getName()));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return (ArticleDO) obj;
    }
}
