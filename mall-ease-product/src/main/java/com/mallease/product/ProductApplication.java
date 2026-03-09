package com.mallease.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 商品管理服务启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.mallease.product", "com.mallease.common"})
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}

