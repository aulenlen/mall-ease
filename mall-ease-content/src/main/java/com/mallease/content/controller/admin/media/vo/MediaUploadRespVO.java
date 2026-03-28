package com.mallease.content.controller.admin.media.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "素材上传响应")
public class MediaUploadRespVO {

    @Schema(description = "对象名称")
    private String objectName;

    @Schema(description = "访问地址")
    private String url;
}
