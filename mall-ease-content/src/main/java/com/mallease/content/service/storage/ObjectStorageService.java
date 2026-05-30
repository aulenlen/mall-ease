package com.mallease.content.service.storage;

import java.io.InputStream;

/**
 * 对象存储服务接口
 */
public interface ObjectStorageService {

    /**
     * 上传文件
     */
    String uploadFile(byte[] bytes, String objectName, String contentType);

    /**
     * 下载文件
     */
    InputStream downloadFile(String objectName);

    /**
     * 删除文件
     */
    void deleteFile(String objectName);

}
