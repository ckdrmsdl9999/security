package com.everywhere.server.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/mainPage")
class MainPageController {

    @GetMapping("/")
    fun hello() = "hello world"

    // 위의 함수랑 같음
//    @GetMapping("/")
//    fun hello(): String {
//        return "hello world"
//    }
}