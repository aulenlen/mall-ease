package com.mallease.content.service.storage;

import com.mallease.content.controller.admin.media.vo.MediaUploadRespVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 对象存储服务接口
 */
public interface ObjectStorageService {

    /**
     * 上传文件
     */
    MediaUploadRespVO uploadFile(MultipartFile file);

    /**
     * 下载文件
     */
    InputStream downloadFile(String objectName);

    /**
     * 删除文件
     */
    void deleteFile(String objectName);

    /**
     * 获取临时访问 URL
     */
    String getFileUrl(String objectName, Integer expiry);

    /**
     * 获取默认有效期访问 URL
     */
    String getFileUrl(String objectName);

    /**
     * 检查文件是否存在
     */
    boolean fileExists(String objectName);
}