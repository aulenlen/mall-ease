package com.mallease.pms;

import io.minio.*;
import io.minio.messages.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;

/**
 * MinIO 连接和功能验证测试类
 *
 * @author: Aulen
 * @description: 验证 MinIO 的连接、上传、下载、删除等基本功能
 * @create: 2025-11-12
 */
@SpringBootTest
public class MinioTest {

    private MinioClient minioClient;
    private String endpoint = "http://192.168.50.148:19000";
    private String accessKey = "minioadmin";
    private String secretKey = "minioadmin";
    private String bucketName = "mall-ease";
    private String testObjectName = "test/test-file.txt";
    private String testContent = "这是一个 MinIO 测试文件内容";

    @BeforeEach
    public void setUp() {
        // 初始化 MinIO 客户端
        minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    /**
     * 测试 MinIO 连接
     */
    @Test
    public void testConnection() {
        try {
            // 测试连接 - 列出所有 buckets
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            System.out.println("MinIO 连接成功！");
            System.out.println("Bucket '" + bucketName + "' 是否存在: " + found);
        } catch (Exception e) {
            System.err.println("MinIO 连接失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试创建 Bucket（如果不存在）
     */
    @Test
    public void testCreateBucket() {
        try {
            // 检查 bucket 是否存在
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());

            if (!found) {
                // 创建 bucket
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                System.out.println("Bucket '" + bucketName + "' 创建成功！");
            } else {
                System.out.println("Bucket '" + bucketName + "' 已存在");
            }
        } catch (Exception e) {
            System.err.println("创建 Bucket 失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试上传文件到 MinIO
     */
    @Test
    public void testUploadFile() {
        try {
            // 确保 bucket 存在
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
            }

            // 创建测试文件内容
            ByteArrayInputStream inputStream = new ByteArrayInputStream(testContent.getBytes("UTF-8"));

            // 上传文件
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(testObjectName)
                    .stream(inputStream, testContent.getBytes("UTF-8").length, -1)
                    .contentType("text/plain")
                    .build());

            System.out.println("文件上传成功！");
            System.out.println("Bucket: " + bucketName);
            System.out.println("Object: " + testObjectName);
            System.out.println("文件大小: " + testContent.getBytes("UTF-8").length + " 字节");

        } catch (Exception e) {
            System.err.println("上传文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试从 MinIO 下载文件
     */
    @Test
    public void testDownloadFile() {
        try {
            // 下载文件
            GetObjectResponse response = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(testObjectName)
                    .build());

            // 读取文件内容
            byte[] buffer = new byte[1024];
            StringBuilder content = new StringBuilder();
            int bytesRead;
            while ((bytesRead = response.read(buffer)) != -1) {
                content.append(new String(buffer, 0, bytesRead, "UTF-8"));
            }
            response.close();

            System.out.println("文件下载成功！");
            System.out.println("文件内容: " + content.toString());
            System.out.println("文件大小: " + content.length() + " 字符");

        } catch (Exception e) {
            System.err.println("下载文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试列出 Bucket 中的所有对象
     */
    @Test
    public void testListObjects() {
        try {
            System.out.println("列出 Bucket '" + bucketName + "' 中的所有对象:");
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .build());

            int count = 0;
            for (Result<Item> result : results) {
                Item item = result.get();
                count++;
                System.out.println("对象 " + count + ":");
                System.out.println("  名称: " + item.objectName());
                System.out.println("  大小: " + item.size() + " 字节");
                System.out.println("  最后修改时间: " + item.lastModified());
                System.out.println("---");
            }

            if (count == 0) {
                System.out.println("Bucket 为空，没有对象");
            } else {
                System.out.println("总共找到 " + count + " 个对象");
            }

        } catch (Exception e) {
            System.err.println("列出对象失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试获取文件信息（元数据）
     */
    @Test
    public void testGetObjectInfo() {
        try {
            StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(testObjectName)
                    .build());

            System.out.println("文件信息:");
            System.out.println("  名称: " + testObjectName);
            System.out.println("  大小: " + stat.size() + " 字节");
            System.out.println("  内容类型: " + stat.contentType());
            System.out.println("  最后修改时间: " + stat.lastModified());
            System.out.println("  ETag: " + stat.etag());

        } catch (Exception e) {
            System.err.println("获取文件信息失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试删除文件
     */
    @Test
    public void testDeleteFile() {
        try {
            // 删除文件
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(testObjectName)
                    .build());

            System.out.println("文件删除成功！");
            System.out.println("已删除对象: " + testObjectName);

        } catch (Exception e) {
            System.err.println("删除文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试删除 Bucket（先删除所有文件，再删除 bucket）
     */
    @Test
    public void testDeleteBucket() {
        try {
            // 检查 bucket 是否存在
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());

            if (!found) {
                System.out.println("Bucket '" + bucketName + "' 不存在，无需删除");
                return;
            }

            System.out.println("开始删除 Bucket '" + bucketName + "'...");

            // 列出 bucket 中的所有对象
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .recursive(true) // 递归列出所有文件（包括子目录中的文件）
                            .build()
            );

            // 删除所有对象
            int deletedCount = 0;
            for (Result<Item> result : results) {
                Item item = result.get();
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(item.objectName())
                                .build()
                );
                deletedCount++;
                System.out.println("已删除文件: " + item.objectName());
            }

            if (deletedCount > 0) {
                System.out.println("共删除 " + deletedCount + " 个文件");
            } else {
                System.out.println("Bucket 为空，没有文件需要删除");
            }

            // 删除 bucket
            minioClient.removeBucket(RemoveBucketArgs.builder()
                    .bucket(bucketName)
                    .build());

            System.out.println("Bucket '" + bucketName + "' 删除成功！");

        } catch (Exception e) {
            System.err.println("删除 Bucket 失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 综合测试：完整流程验证
     */
    @Test
    public void testCompleteWorkflow() {
        System.out.println("========== MinIO 完整流程测试 ==========");
        
        try {
            // 1. 测试连接
            System.out.println("\n1. 测试连接...");
            testConnection();

            // 2. 创建 Bucket（如果不存在）
            System.out.println("\n2. 创建/检查 Bucket...");
            testCreateBucket();

            // 3. 上传文件
            System.out.println("\n3. 上传测试文件...");
            testUploadFile();

            // 4. 获取文件信息
            System.out.println("\n4. 获取文件信息...");
            testGetObjectInfo();

            // 5. 列出所有对象
            System.out.println("\n5. 列出所有对象...");
            testListObjects();

            // 6. 下载文件
            System.out.println("\n6. 下载测试文件...");
            testDownloadFile();

            // 7. 删除文件（可选，取消注释以执行）
            // System.out.println("\n7. 删除测试文件...");
            // testDeleteFile();

            System.out.println("\n========== 测试完成 ==========");

        } catch (Exception e) {
            System.err.println("测试过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

