package com.mallease.content;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 内容管理服务启动类
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}
