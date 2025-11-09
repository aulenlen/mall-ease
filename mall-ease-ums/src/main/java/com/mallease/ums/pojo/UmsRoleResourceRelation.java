package com.mallease.ums.pojo;

import lombok.Data;

/**
 * 后台角色资源关系表
 *
 * @author: Aulen
 * @create: 2025-11-08
 */
@Data
public class UmsRoleResourceRelation {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 资源ID
     */
    private Long resourceId;
}

