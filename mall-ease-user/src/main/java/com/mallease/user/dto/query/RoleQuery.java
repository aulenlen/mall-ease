package com.mallease.user.dto.query;

import lombok.Data;

/**
 * 角色查询条件
 */
@Data
public class RoleQuery {
    private String name;
    private Integer status;
}
