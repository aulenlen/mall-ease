package com.mallease.pms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * 商品管理服务启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.mallease.pms", "com.mallease.common"})
@EnableDiscoveryClient
public class MallEasePmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallEasePmsApplication.class, args);
    }
}

