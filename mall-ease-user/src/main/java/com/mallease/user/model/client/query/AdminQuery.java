package com.mallease.user.model.client.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理员查询条件
 *
 * @author: Aulen
 * @create: 2025-11-16
 */
@Data
@Schema(description = "管理员查询条件")
public class AdminQuery {

    @Schema(description = "用户名（模糊查询）")
    private String username;

    @Schema(description = "状态（0:禁用 1:启用）")
    private Integer status;
}