package com.mallease.pms.controller;

import com.mallease.common.api.R;
import com.mallease.pms.dto.response.MinioUploadResponse;
import com.mallease.pms.service.MinioService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * MinIO 文件管理控制器
 *
 * @author: Aulen
 * @create: 2025-11-12
 */
@Slf4j
@RestController
@RequestMapping("/minio/file")
public class MinioController {

    @Autowired
    private MinioService minioService;

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件上传响应（包含URL和文件名）
     */
    @PostMapping("/upload")
    public R<MinioUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.failed("文件不能为空");
        }
        try {
            MinioUploadResponse response = minioService.uploadFile(file);
            return R.success(response);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return R.failed("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 下载文件
     *
     * @param objectName 对象名称（文件路径）
     * @param response   HTTP响应
     */
    @GetMapping("/download")
    public void downloadFile(@RequestParam("objectName") String objectName,
                             HttpServletResponse response) {
        try {
            InputStream inputStream = minioService.downloadFile(objectName);

            // 设置响应头
            String fileName = objectName.substring(objectName.lastIndexOf("/") + 1);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));

            // 将文件流写入响应
            OutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();
        } catch (Exception e) {
            log.error("文件下载失败", e);
            throw new RuntimeException("文件下载失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件访问URL（预签名URL）
     *
     * @param objectName 对象名称（文件路径）
     * @param expiry     过期时间（秒），可选，默认7天
     * @return 文件访问URL
     */
    @GetMapping("/url")
    public R<String> getFileUrl(@RequestParam("objectName") String objectName,
                                @RequestParam(value = "expiry", required = false) Integer expiry) {
        try {
            String url = minioService.getFileUrl(objectName, expiry);
            return R.success(url);
        } catch (Exception e) {
            log.error("获取文件URL失败", e);
            return R.failed("获取文件URL失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     *
     * @param objectName 对象名称（文件路径）
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public R<Void> deleteFile(@RequestParam("objectName") String objectName) {
        try {
            minioService.deleteFile(objectName);
            return R.success(null, "文件删除成功");
        } catch (Exception e) {
            log.error("文件删除失败", e);
            return R.failed("文件删除失败: " + e.getMessage());
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param objectName 对象名称（文件路径）
     * @return 是否存在
     */
    @GetMapping("/exists")
    public R<Boolean> fileExists(@RequestParam("objectName") String objectName) {
        try {
            boolean exists = minioService.fileExists(objectName);
            return R.success(exists);
        } catch (Exception e) {
            log.error("检查文件是否存在失败", e);
            return R.failed("检查文件是否存在失败: " + e.getMessage());
        }
    }
}

