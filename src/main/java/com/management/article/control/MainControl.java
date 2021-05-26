package com.management.article.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainControl {
    @RequestMapping("/index")
    private String index() {
        System.out.println("index!");
        return "index.html";
    }
}
