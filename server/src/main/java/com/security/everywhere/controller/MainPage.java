package com.security.everywhere.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class MainPage {
    @GetMapping("/main")
    public String getTesting(
            @RequestParam(value="month",required = false) String month, @RequestParam(value="area",required = false) String area, Model model) {

        model.addAttribute("month", month);
        model.addAttribute("area", area);

        return "main";
    }

    @GetMapping("/content/{contentId}")
    public String Contentpage(@PathVariable String contentId, Model model) {

        return "content";  // html name
    }

//    @PostMapping("/")
//    public String postTesting(TestParam testParam, Model model) {
////        model.addAttribute("name", testParam.getClass().getName());
//        model.addAttribute("name", testParam.toString());
//        return "test";
//    }
}
