package com.security.everywhere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class EverywhereApplication {

    public static void main(String[] args) {
        SpringApplication.run(EverywhereApplication.class, args);
    }
}
