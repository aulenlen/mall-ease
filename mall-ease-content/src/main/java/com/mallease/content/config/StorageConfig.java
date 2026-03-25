package com.mallease.content.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/**
 * R2 存储客户端配置
 */
@Configuration
public class StorageConfig {

    @Bean
    public AwsCredentialsProvider storageCredentialsProvider(StorageProperties storageProperties) {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(storageProperties.getAccessKey(), storageProperties.getSecretKey())
        );
    }

    @Bean
    public S3Client storageClient(StorageProperties storageProperties, AwsCredentialsProvider storageCredentialsProvider) {
        return S3Client.builder()
                .endpointOverride(URI.create(storageProperties.getEndpoint()))
                .credentialsProvider(storageCredentialsProvider)
                .region(Region.of(storageProperties.getRegion()))
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .build()
                )
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .build();
    }

    @Bean
    public S3Presigner storagePresigner(StorageProperties storageProperties,
                                        AwsCredentialsProvider storageCredentialsProvider) {
        return S3Presigner.builder()
                .endpointOverride(URI.create(storageProperties.getEndpoint()))
                .credentialsProvider(storageCredentialsProvider)
                .region(Region.of(storageProperties.getRegion()))
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .build()
                )
                .build();
    }
}
