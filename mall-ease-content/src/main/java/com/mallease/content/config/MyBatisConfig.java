package com.mallease.content.config;

import com.mallease.common.mybatis.AuditFieldInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisConfig {
    @Bean
    public AuditFieldInterceptor auditFieldInterceptor() {
        return new AuditFieldInterceptor();
    }
}
