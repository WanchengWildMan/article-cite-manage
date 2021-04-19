package com.management.article;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
//@MapperScan("com.management.article.dao")
public class ArticleManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArticleManagementApplication.class, args);
    }

}
