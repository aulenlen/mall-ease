package com.mallease.pms.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.mallease.common.exception.ApiException;
import com.mallease.pms.dto.BucketPolicyConfig;
import com.mallease.pms.dto.vo.MinioUploadVO;
import com.mallease.pms.service.MinioService;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * MinIO 文件服务实现类
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Slf4j
@Service
public class MinioServiceImpl implements MinioService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Override
    public MinioUploadVO uploadFile(MultipartFile file) {
        try {
            ensureBucketExists();

            String objectName = generateObjectName(file.getOriginalFilename());

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            String url = endpoint + "/" + bucketName + "/" + objectName;

            return MinioUploadVO.builder()
                    .url(url)
                    .name(objectName)
                    .build();
        } catch (Exception e) {
            log.error("上传文件失败: {}", e.getMessage(), e);
            throw new ApiException("上传文件失败: " + e.getMessage());
        }
    }

    @Override
    public InputStream downloadFile(String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("下载文件失败: {}", e.getMessage(), e);
            throw new ApiException("下载文件失败: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            log.info("删除文件成功: {}", objectName);
        } catch (Exception e) {
            log.error("删除文件失败: {}", e.getMessage(), e);
            throw new ApiException("删除文件失败: " + e.getMessage());
        }
    }

    @Override
    public String getFileUrl(String objectName, Integer expiry) {
        try {
            if (expiry == null || expiry <= 0) {
                expiry = 7 * 24 * 60 * 60; // 默认7天
            }
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(expiry)
                            .build()
            );
        } catch (Exception e) {
            log.error("获取文件URL失败: {}", e.getMessage(), e);
            throw new ApiException("获取文件URL失败: " + e.getMessage());
        }
    }

    @Override
    public String getFileUrl(String objectName) {
        return getFileUrl(objectName, null);
    }

    @Override
    public boolean fileExists(String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 确保 bucket 存在，不存在则创建
     */
    private void ensureBucketExists() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                BucketPolicyConfig bucketPolicyConfig = createBucketPolicyConfig(bucketName);

                SetBucketPolicyArgs setBucketPolicyArgs = SetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .config(JSONUtil.toJsonStr(bucketPolicyConfig))
                        .build();
                minioClient.setBucketPolicy(setBucketPolicyArgs);

                log.info("创建 bucket 成功: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("检查或创建 bucket 失败: {}", e.getMessage(), e);
            throw new ApiException("检查或创建 bucket 失败: " + e.getMessage());
        }
    }

    private BucketPolicyConfig createBucketPolicyConfig(String bucketName) {
        BucketPolicyConfig.Statement statement = BucketPolicyConfig.Statement.builder()
                .Effect("Allow")
                .Principal("*")
                .Action("s3:GetObject")
                .Resource("arn:aws:s3:::"+bucketName+"/*")
                .build();
        return BucketPolicyConfig.builder()
                .Version("2012-10-17")
                .Statement(CollUtil.toList(statement))
                .build();
    }

    /**
     * 生成对象名称（文件路径）
     * 格式: yyyy/MM/dd/uuid-原文件名
     *
     * @param originalFilename 原始文件名
     * @return 对象名称
     */
    private String generateObjectName(String originalFilename) {
        // 获取文件扩展名
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 生成日期路径: yyyy/MM/dd
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        // 生成唯一文件名
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String fileName = uuid + extension;

        // 组合完整路径
        return datePath + "/" + fileName;
    }
}

