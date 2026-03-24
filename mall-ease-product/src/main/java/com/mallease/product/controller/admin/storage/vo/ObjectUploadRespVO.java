package com.mallease.product.controller.admin.storage.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对象存储上传响应
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Schema(description = "对象存储上传响应")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObjectUploadRespVO {

    @Schema(description = "文件访问URL")
    private String url;

    @Schema(description = "文件名称")
    private String name;
}
