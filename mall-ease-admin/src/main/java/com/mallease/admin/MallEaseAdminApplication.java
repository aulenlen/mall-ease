package com.mallease.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 管理后台启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@ComponentScan(basePackages = {"com.mallease.admin", "com.mallease.common"})
public class MallEaseAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallEaseAdminApplication.class, args);
    }
}

