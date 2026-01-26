package com.mallease.user.model.client.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 资源查询参数
 *
 * @author: Aulen
 * @create: 2026-01-25
 */
@Data
@Schema(description = "资源查询参数")
public class ResourceQuery {

    @Schema(description = "资源名称（模糊查询）")
    private String name;

    @Schema(description = "资源URL（模糊查询）")
    private String url;

    @Schema(description = "资源分类ID")
    private Long categoryId;
}
