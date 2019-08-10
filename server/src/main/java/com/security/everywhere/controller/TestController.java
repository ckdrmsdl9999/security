package com.security.everywhere.controller;

import com.security.everywhere.domain.TestParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/test")
public class TestController {

    @GetMapping("/")
    public String gettesting(@RequestParam(name="name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "test";  // html name
    }

    @PostMapping("/")
    public String postTesting(@RequestBody TestParam testParam, Model model) {
        model.addAttribute("name", testParam.getName());
        return "test";
    }
}
