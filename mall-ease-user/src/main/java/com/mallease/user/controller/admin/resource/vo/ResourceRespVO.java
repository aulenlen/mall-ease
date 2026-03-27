package com.mallease.user.controller.admin.resource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 资源视图对象
 *
 * @author: Aulen
 * @create: 2026-01-23
 */
@Schema(description = "资源视图对象")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceRespVO {

    @Schema(description = "资源ID")
    private Long id;

    @Schema(description = "资源名称")
    private String name;

    @Schema(description = "资源URL")
    private String url;

    @Schema(description = "资源描述")
    private String description;

    @Schema(description = "资源分类ID")
    private Long categoryId;

    @Schema(description = "创建时间")
    private Date createTime;
}