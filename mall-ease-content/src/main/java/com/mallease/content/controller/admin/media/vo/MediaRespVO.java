package com.mallease.content.controller.admin.media.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "素材响应")
public class MediaRespVO {

    @Schema(description = "媒体资源ID")
    private Long id;

    @Schema(description = "分组ID")
    private Long groupId;

    @Schema(description = "文件 hash 值")
    private String hash;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "访问地址")
    private String url;

    @Schema(description = "缩略图地址")
    private String thumbnailUrl;

    @Schema(description = "媒体类型：IMAGE/VIDEO/OTHER")
    private String mediaType;

    @Schema(description = "文件字节数")
    private Long fileSize;

    @Schema(description = "扩展名")
    private String extension;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "更新人")
    private String updater;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
