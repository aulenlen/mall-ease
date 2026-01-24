package com.mallease.user.model.client.query;

import lombok.Data;

/**
 * 角色查询条件
 */
@Data
public class RoleQuery {
    private String name;
    private Integer status;
}
