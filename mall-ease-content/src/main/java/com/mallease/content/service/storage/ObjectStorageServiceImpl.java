package com.mallease.content.service.storage;

import cn.hutool.core.util.StrUtil;
import com.mallease.common.exception.ApiException;
import com.mallease.content.config.StorageProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;

/**
 * R2 素材存储服务实现
 */
@Slf4j
@Service
@AllArgsConstructor
public class ObjectStorageServiceImpl implements ObjectStorageService {

    private final S3Client storageClient;

    private final StorageProperties storageProperties;

    @Override
    public String uploadFile(byte[] bytes, String objectName, String contentType) {
        try {
            if (bytes == null || bytes.length == 0) {
                throw new ApiException("文件不能为空");
            }
            if (StrUtil.isBlank(objectName)) {
                throw new ApiException("对象名称不能为空");
            }
            ensureBucketExists();
            PutObjectRequest.Builder requestBuilder = PutObjectRequest.builder()
                    .bucket(storageProperties.getBucketName())
                    .key(objectName);
            if (StrUtil.isNotBlank(contentType)) {
                requestBuilder.contentType(contentType);
            }
            storageClient.putObject(requestBuilder.build(), RequestBody.fromBytes(bytes));
            return buildFileUrl(objectName);
        } catch (ApiException ex) {
            throw ex;
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

}
