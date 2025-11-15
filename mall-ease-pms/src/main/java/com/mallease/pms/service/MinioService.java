package com.mallease.pms.service;

import com.mallease.pms.dto.vo.MinioUploadVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * MinIO 文件服务接口
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
public interface MinioService {

    /**
     * 上传文件（自动生成文件路径）
     *
     * @param file 文件
     * @return 文件上传结果（包含URL和文件名）
     */
    MinioUploadVO uploadFile(MultipartFile file);

    /**
     * 下载文件
     *
     * @param objectName 对象名称（文件路径）
     * @return 文件输入流
     */
    InputStream downloadFile(String objectName);

    /**
     * 删除文件
     *
     * @param objectName 对象名称（文件路径）
     */
    void deleteFile(String objectName);

    /**
     * 获取文件访问URL（预签名URL，临时访问）
     *
     * @param objectName 对象名称（文件路径）
     * @param expiry     过期时间（秒），默认7天
     * @return 文件访问URL
     */
    String getFileUrl(String objectName, Integer expiry);

    /**
     * 获取文件访问URL（默认7天有效期）
     *
     * @param objectName 对象名称（文件路径）
     * @return 文件访问URL
     */
    String getFileUrl(String objectName);

    /**
     * 检查文件是否存在
     *
     * @param objectName 对象名称（文件路径）
     * @return 是否存在
     */
    boolean fileExists(String objectName);
}

