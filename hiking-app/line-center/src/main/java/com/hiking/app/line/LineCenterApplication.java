package com.hiking.app.line;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.hiking.app")
@EnableFeignClients(basePackages = "com.hiking.app.common.feign")
public class LineCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(LineCenterApplication.class, args);
    }
}