package com.mallease.pms.pojo;

import lombok.Data;

/**
 * 产品的分类和属性的关系表，用于设置分类筛选条件（只支持一级分类）
 *
 * @author: Aulen
 * @create: 2025-11-09
 */
@Data
public class PmsProductCategoryAttributeRelation {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 产品分类ID
     */
    private Long productCategoryId;

    /**
     * 产品属性ID
     */
    private Long productAttributeId;
}






