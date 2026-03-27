package com.mallease.user.dal.entity;

import lombok.Data;

/**
 * 后台角色菜单关系表
 *
 * @author: Aulen
 * @create: 2026-01-23
 */
@Data
public class RoleMenuRelation {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 菜单ID
     */
    private Long menuId;
}