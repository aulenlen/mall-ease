package com.mallease.product.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 对象存储配置类
 *
 * @author: Aulen
 * @create: 2025-11-12
 */
@Configuration
public class ObjectStorageConfig {

    @Value("${storage.endpoint}")
    private String endpoint;

    @Value("${storage.access-key}")
    private String accessKey;

    @Value("${storage.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient storageClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .region("auto")  // R2 需要指定 region 为 auto，MinIO 会忽略此参数
                .build();
    }
}

