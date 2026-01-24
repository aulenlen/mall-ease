package com.mallease.user.model.data;

import lombok.Data;

/**
 * 后台用户和角色关系表
 *
 * @author: Aulen
 * @create: 2025-11-08
 */
@Data
public class AdminRoleRelation {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 管理员ID
     */
    private Long adminId;

    /**
     * 角色ID
     */
    private Long roleId;
}

