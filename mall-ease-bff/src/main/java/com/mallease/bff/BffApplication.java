package com.mallease.bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * BFF 聚合层启动类（Backend for Frontend）
 *
 * @author: Aulen
 * @create: 2026-01-08
 */
@SpringBootApplication
@EnableFeignClients
public class BffApplication {

    public static void main(String[] args) {
        SpringApplication.run(BffApplication.class, args);
    }
}
