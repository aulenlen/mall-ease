package com.mallease.pms.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MinIO文件上传结果视图对象
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Schema(description = "MinIO文件上传结果")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MinioUploadVO {

    @Schema(description = "文件访问URL")
    private String url;

    @Schema(description = "文件名称")
    private String name;
}
