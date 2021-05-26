package com.management.article;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SpringBootApplication
public class ArticleManagementApplication {
    private final Logger logger = LoggerFactory.getLogger(ArticleManagementApplication.class);
    private final String driver = "com.mysql.cj.jdbc.Driver";
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    //是否刷新数据库
    @Value("${refresh_database}")
    private boolean refreshDatabase;
    //刷新数据库并添加初始数据
    private final String refresh_database_sql_path = "database_init/article_manage_refresh_data.sql";
    //创建表结构，不添加数据
    private final String create_database_struct = "database_init/article_manage_create_struct.sql";

    @PostConstruct
    private void mybatisExec() throws ClassNotFoundException, SQLException, IOException {
        logger.info("database:" + url);
        Class.forName(driver);

        File file = new ClassPathResource(refreshDatabase ? refresh_database_sql_path : create_database_struct).getFile();
        Connection conn = DriverManager.getConnection(url, username, password);
        ScriptRunner runner = new ScriptRunner(conn);
        try {
            runner.setStopOnError(true);
            runner.runScript(new FileReader(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        conn.close();
    }


    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        SpringApplication.run(ArticleManagementApplication.class, args);
    }

}
