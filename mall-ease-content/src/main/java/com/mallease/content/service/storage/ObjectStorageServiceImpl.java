package com.mallease.content.service.storage;

import cn.hutool.core.util.StrUtil;
import com.mallease.common.exception.ApiException;
import com.mallease.content.config.StorageProperties;
import com.mallease.content.controller.admin.media.vo.MediaUploadRespVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * R2 素材存储服务实现
 */
@Slf4j
@Service
public class ObjectStorageServiceImpl implements ObjectStorageService {

    private static final int DEFAULT_URL_EXPIRE_SECONDS = 7 * 24 * 60 * 60;

    private final S3Client storageClient;

    private final S3Presigner storagePresigner;

    private final StorageProperties storageProperties;

    public ObjectStorageServiceImpl(S3Client storageClient,
                                    S3Presigner storagePresigner,
                                    StorageProperties storageProperties) {
        this.storageClient = storageClient;
        this.storagePresigner = storagePresigner;
        this.storageProperties = storageProperties;
    }

    @Override
    public MediaUploadRespVO uploadFile(MultipartFile file) {
        try {
            ensureBucketExists();
            String objectName = generateObjectName(file.getOriginalFilename());
            try (InputStream inputStream = file.getInputStream()) {
                storageClient.putObject(
                        PutObjectRequest.builder()
                                .bucket(storageProperties.getBucketName())
                                .key(objectName)
                                .contentType(file.getContentType())
                                .build(),
                        RequestBody.fromInputStream(inputStream, file.getSize())
                );
            }
            return MediaUploadRespVO.builder()
                    .url(buildFileUrl(objectName))
                    .objectName(objectName)
                    .build();
        } catch (Exception ex) {
            log.error("上传素材失败", ex);
            throw new ApiException("上传素材失败: " + ex.getMessage());
        }
    }

    @Override
    public InputStream downloadFile(String objectName) {
        try {
            return storageClient.getObject(
                    GetObjectRequest.builder()
                            .bucket(storageProperties.getBucketName())
                            .key(objectName)
                            .build()
            );
        } catch (Exception ex) {
            log.error("下载素材失败", ex);
            throw new ApiException("下载素材失败: " + ex.getMessage());
        }
    }

    @Override
    public void deleteFile(String objectName) {
        try {
            storageClient.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(storageProperties.getBucketName())
                            .key(objectName)
                            .build()
            );
            log.info("删除素材成功，objectName={}", objectName);
        } catch (Exception ex) {
            log.error("删除素材失败", ex);
            throw new ApiException("删除素材失败: " + ex.getMessage());
        }
    }

    @Override
    public String getFileUrl(String objectName, Integer expiry) {
        try {
            int safeExpiry = expiry == null || expiry <= 0 ? DEFAULT_URL_EXPIRE_SECONDS : expiry;
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(storageProperties.getBucketName())
                    .key(objectName)
                    .build();
            PresignedGetObjectRequest presignedRequest = storagePresigner.presignGetObject(
                    GetObjectPresignRequest.builder()
                            .signatureDuration(Duration.ofSeconds(safeExpiry))
                            .getObjectRequest(getObjectRequest)
                            .build()
            );
            return presignedRequest.url().toString();
        } catch (Exception ex) {
            log.error("获取素材访问 URL 失败", ex);
            throw new ApiException("获取素材访问URL失败: " + ex.getMessage());
        }
    }

    @Override
    public String getFileUrl(String objectName) {
        return getFileUrl(objectName, null);
    }

    @Override
    public boolean fileExists(String objectName) {
        try {
            storageClient.headObject(
                    HeadObjectRequest.builder()
                            .bucket(storageProperties.getBucketName())
                            .key(objectName)
                            .build()
            );
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private void ensureBucketExists() {
        try {
            storageClient.headBucket(
                    HeadBucketRequest.builder()
                            .bucket(storageProperties.getBucketName())
                            .build()
            );
        } catch (S3Exception ex) {
            if (!isBucketMissing(ex)) {
                log.error("检查素材 bucket 失败", ex);
                throw new ApiException("检查素材 bucket 失败: " + ex.getMessage());
            }
            createBucket();
        } catch (Exception ex) {
            log.error("检查素材 bucket 失败", ex);
            throw new ApiException("检查素材 bucket 失败: " + ex.getMessage());
        }
    }

    private void createBucket() {
        try {
            storageClient.createBucket(
                    CreateBucketRequest.builder()
                            .bucket(storageProperties.getBucketName())
                            .build()
            );
            log.info("创建素材 bucket 成功，bucket={}", storageProperties.getBucketName());
        } catch (Exception ex) {
            log.error("创建素材 bucket 失败", ex);
            throw new ApiException("创建素材 bucket 失败: " + ex.getMessage());
        }
    }

    private boolean isBucketMissing(S3Exception ex) {
        String errorCode = ex.awsErrorDetails() == null ? null : ex.awsErrorDetails().errorCode();
        return ex.statusCode() == 404
                || StrUtil.equalsAnyIgnoreCase(errorCode, "NotFound", "NoSuchBucket");
    }

    private String buildFileUrl(String objectName) {
        if (StrUtil.isNotBlank(storageProperties.getPublicUrl())) {
            return StrUtil.removeSuffix(storageProperties.getPublicUrl(), "/") + "/" + objectName;
        }
        return StrUtil.removeSuffix(storageProperties.getEndpoint(), "/")
                + "/"
                + storageProperties.getBucketName()
                + "/"
                + objectName;
    }

    private String generateObjectName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        return "media/" + datePath + "/" + fileName;
    }
}