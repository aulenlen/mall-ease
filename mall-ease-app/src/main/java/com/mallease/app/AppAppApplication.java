package com.mallease.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 移动端 App 服务启动类
 */
@SpringBootApplication
@EnableFeignClients
public class AppAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppAppApplication.class, args);
    }
}