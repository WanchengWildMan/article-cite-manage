package com.management.article.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(WebSecurity web) throws Exception {
        this.getHttp().csrf().disable();
        web.ignoring().antMatchers("/login", "index.html", "favicon.ico", "/static/css", "/static/js", "/static/fonts", "/database_init/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //super.configure(http);(此处不需要对父类进行引用)
        //配置不需要登录验证
        http.authorizeRequests().anyRequest().permitAll().and().logout().permitAll();
    }

}
