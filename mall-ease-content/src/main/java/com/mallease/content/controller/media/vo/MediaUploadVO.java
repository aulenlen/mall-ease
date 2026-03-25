package com.mallease.content.controller.media.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 素材上传响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "素材上传响应")
public class MediaUploadVO {

    @Schema(description = "文件访问 URL")
    private String url;

    @Schema(description = "对象名称")
    private String name;
}
