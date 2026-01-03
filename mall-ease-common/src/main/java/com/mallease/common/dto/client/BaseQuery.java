package com.mallease.common.dto.client;

import lombok.Data;

/**
 * 基础查询对象
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Data
public class BaseQuery {

    /**
     * 页码（从1开始）
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 排序方向（ASC/DESC）
     */
    private String sort = "DESC";
}
