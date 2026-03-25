package com.mallease.content.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 素材存储配置
 */
@Data
@Validated
@Component
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

    @NotBlank(message = "storage.endpoint 不能为空")
    private String endpoint;

    @NotBlank(message = "storage.access-key 不能为空")
    private String accessKey;

    @NotBlank(message = "storage.secret-key 不能为空")
    private String secretKey;

    @NotBlank(message = "storage.bucket-name 不能为空")
    private String bucketName = "mall-ease";

    @NotBlank(message = "storage.region 不能为空")
    private String region = "auto";

    private String publicUrl;
}
