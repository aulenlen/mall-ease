package com.mallease.marketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 营销服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.mallease.marketing.feign")
@ComponentScan(basePackages = {"com.mallease.marketing", "com.mallease.common"})
public class MarketingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketingApplication.class, args);
    }
}

