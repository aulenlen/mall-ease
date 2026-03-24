package com.mallease.product.controller.admin.storage;

import com.mallease.common.api.R;
import com.mallease.product.controller.admin.storage.vo.ObjectUploadRespVO;
import com.mallease.product.service.storage.ObjectStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * 后台对象存储文件管理
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Tag(name = "后台对象存储文件管理", description = "文件上传、下载、删除等操作")
@Slf4j
@RestController
@RequestMapping("/product/admin/object-storage")
public class ObjectStorageAdminController {

    @Autowired
    private ObjectStorageService objectStorageService;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public R<ObjectUploadRespVO> uploadFile(@Parameter(description = "文件") @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.failed("文件不能为空");
        }
        try {
            ObjectUploadRespVO vo = objectStorageService.uploadFile(file);
            return R.success(vo);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return R.failed("文件上传失败: " + e.getMessage());
        }
    }

    @Operation(summary = "下载文件")
    @GetMapping("/download")
    public void downloadFile(
            @Parameter(description = "对象名称(文件路径)") @RequestParam String objectName,
            HttpServletResponse response) {
        try {
            InputStream inputStream = objectStorageService.downloadFile(objectName);
            String fileName = objectName.substring(objectName.lastIndexOf("/") + 1);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
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

    @Operation(summary = "获取文件访问URL")
    @GetMapping("/url")
    public R<String> getFileUrl(
            @Parameter(description = "对象名称(文件路径)") @RequestParam String objectName,
            @Parameter(description = "过期时间(秒)") @RequestParam(required = false) Integer expiry) {
        try {
            String url = objectStorageService.getFileUrl(objectName, expiry);
            return R.success(url);
        } catch (Exception e) {
            log.error("获取文件URL失败", e);
            return R.failed("获取文件URL失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/delete")
    public R<Void> deleteFile(@Parameter(description = "对象名称(文件路径)") @RequestParam String objectName) {
        try {
            objectStorageService.deleteFile(objectName);
            return R.success(null, "文件删除成功");
        } catch (Exception e) {
            log.error("文件删除失败", e);
            return R.failed("文件删除失败: " + e.getMessage());
        }
    }

    @Operation(summary = "检查文件是否存在")
    @GetMapping("/exists")
    public R<Boolean> fileExists(@Parameter(description = "对象名称(文件路径)") @RequestParam String objectName) {
        try {
            boolean exists = objectStorageService.fileExists(objectName);
            return R.success(exists);
        } catch (Exception e) {
            log.error("检查文件是否存在失败", e);
            return R.failed("检查文件是否存在失败: " + e.getMessage());
        }
    }
}

