package com.security.everywhere.controller;

import com.security.everywhere.model.Festival;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class MainPage {

    @GetMapping("/main")
    public String getTesting(
            @RequestParam(value="startmonth",defaultValue = "0") String startmonth,
            @RequestParam(value="endmonth",defaultValue = "0") String endmonth,
            @RequestParam(value="area",defaultValue = "0") String area,
            @RequestParam(value="search",defaultValue = "0") String search, Model model) {

            if (startmonth.length() == 1)
                startmonth = "0" + startmonth;
            if (endmonth.length() == 1)
                endmonth = "0" + endmonth;

        model.addAttribute("endmonth", endmonth);
        model.addAttribute("startmonth", startmonth);
        model.addAttribute("area", area);
        model.addAttribute("search",search);

        return "main";
    }

//    @GetMapping("/content/{contentId}")
//    public String Contentpage(@PathVariable String contentId, Model model) {
//        System.out.println("content");
//        return "content";  // html name
//    }

//    @GetMapping("/content")
//    public String content(){
//        System.out.println("content");
//        return "content";
//    }

    @PostMapping("/content")
    public String goContent(@RequestBody String festival, Model model){
        System.out.println(festival);
        model.addAttribute("festival", festival);

        return "content";
    }


//    @PostMapping("/")
//    public String postTesting(TestParam testParam, Model model) {
////        model.addAttribute("name", testParam.getClass().getName());
//        model.addAttribute("name", testParam.toString());
//        return "test";
//    }
}
