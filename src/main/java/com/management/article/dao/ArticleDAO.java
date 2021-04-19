package com.management.article.dao;

import com.management.article.dataobject.ArticleDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.*;

@Mapper
public interface ArticleDAO {
    int add(ArticleDO articleDO);

    int update(ArticleDO articleDO);

    int deleteByObject(ArticleDO articleDO);

    int deleteById(@Param("id") String id);

    List<ArticleDO> findByObject(ArticleDO articleDO);

    List<ArticleDO> findById(@Param("id") String id);

}
