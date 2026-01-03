package com.mallease.user.dto.query;

import lombok.Data;

/**
 * 管理员查询条件
 *
 * @author: Aulen
 * @create: 2025-11-16
 */
@Data
public class AdminQuery {

    /**
     * 用户名（模糊查询）
     */
    private String username;

    /**
     * 状态（0:禁用 1:启用）
     */
    private Integer status;
}
