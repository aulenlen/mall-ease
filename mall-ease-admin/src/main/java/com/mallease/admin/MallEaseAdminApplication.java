package com.mallease.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 管理后台启动类
 */
@SpringBootApplication
@MapperScan("com.mallease.admin.dao")
public class MallEaseAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallEaseAdminApplication.class, args);
    }
}

