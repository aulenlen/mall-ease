package com.mallease.ums;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 用户管理服务启动类
 */
@EnableDiscoveryClient
@SpringBootApplication
public class MallEaseUmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallEaseUmsApplication.class, args);
    }
}

