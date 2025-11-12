package com.mallease.pms.pojo;

import lombok.Data;

/**
 * 产品属性分类表
 *
 * @author: Aulen
 * @create: 2025-11-09
 */
@Data
public class PmsProductAttributeCategory {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 属性数量
     */
    private Integer attributeCount;

    /**
     * 参数数量
     */
    private Integer paramCount;
}






