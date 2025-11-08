package com.mallease.admin.pojo;

import lombok.Data;

/**
 * 后台用户和角色关系表
 *
 * @author: Aulen
 * @create: 2025-11-08
 */
@Data
public class UmsAdminRoleRelation {
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

