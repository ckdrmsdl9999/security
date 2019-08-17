package com.security.everywhere.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class MainPage {
    @GetMapping("/main")
    public String getTesting() {
        return "main";  // html name
    }

    @GetMapping("/content/{contentId}")
    public String Contentpage(@PathVariable String contentId) {

        return "content";  // html name
    }

//    @PostMapping("/")
//    public String postTesting(TestParam testParam, Model model) {
////        model.addAttribute("name", testParam.getClass().getName());
//        model.addAttribute("name", testParam.toString());
//        return "test";
//    }
}
