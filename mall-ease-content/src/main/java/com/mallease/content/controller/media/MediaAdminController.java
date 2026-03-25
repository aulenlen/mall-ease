package com.mallease.content.controller.media;

import com.mallease.common.api.R;
import com.mallease.content.controller.media.vo.MediaUploadVO;
import com.mallease.content.service.storage.ObjectStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 后台素材文件管理
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/media/files")
@Tag(name = "后台素材管理", description = "素材上传、下载、删除和访问地址管理")
public class MediaAdminController {

    private final ObjectStorageService objectStorageService;

    @Operation(summary = "上传素材")
    @PostMapping
    public R<MediaUploadVO> upload(@Parameter(description = "文件") @RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return R.failed("文件不能为空");
        }
        try {
            return R.success(objectStorageService.uploadFile(file));
        } catch (Exception ex) {
            log.error("上传素材失败", ex);
            return R.failed("上传素材失败: " + ex.getMessage());
        }
    }

    @Operation(summary = "下载素材")
    @GetMapping("/download")
    public void download(@Parameter(description = "对象名称(文件路径)") @RequestParam String objectName,
                         HttpServletResponse response) {
        try (InputStream inputStream = objectStorageService.downloadFile(objectName);
             OutputStream outputStream = response.getOutputStream()) {
            String fileName = objectName.substring(objectName.lastIndexOf("/") + 1);
            response.setContentType("application/octet-stream");
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8)
            );
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } catch (Exception ex) {
            log.error("下载素材失败", ex);
            throw new RuntimeException("下载素材失败: " + ex.getMessage());
        }
    }

    @Operation(summary = "获取素材访问 URL")
    @GetMapping("/url")
    public R<String> getUrl(@Parameter(description = "对象名称(文件路径)") @RequestParam String objectName,
                            @Parameter(description = "过期时间(秒)") @RequestParam(required = false) Integer expiry) {
        try {
            return R.success(objectStorageService.getFileUrl(objectName, expiry));
        } catch (Exception ex) {
            log.error("获取素材访问 URL 失败", ex);
            return R.failed("获取素材访问URL失败: " + ex.getMessage());
        }
    }

    @Operation(summary = "删除素材")
    @DeleteMapping
    public R<Void> delete(@Parameter(description = "对象名称(文件路径)") @RequestParam String objectName) {
        try {
            objectStorageService.deleteFile(objectName);
            return R.success(null, "删除素材成功");
        } catch (Exception ex) {
            log.error("删除素材失败", ex);
            return R.failed("删除素材失败: " + ex.getMessage());
        }
    }

    @Operation(summary = "检查素材是否存在")
    @GetMapping("/exists")
    public R<Boolean> exists(@Parameter(description = "对象名称(文件路径)") @RequestParam String objectName) {
        try {
            return R.success(objectStorageService.fileExists(objectName));
        } catch (Exception ex) {
            log.error("检查素材是否存在失败", ex);
            return R.failed("检查素材是否存在失败: " + ex.getMessage());
        }
    }
}
