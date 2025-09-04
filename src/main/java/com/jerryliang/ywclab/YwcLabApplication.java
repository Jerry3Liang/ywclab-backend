package com.jerryliang.ywclab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class YwcLabApplication {

    public static void main(String[] args) {
        SpringApplication.run(YwcLabApplication.class, args);
    }

}
