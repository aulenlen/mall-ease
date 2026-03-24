package com.mallease.product.dal.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类-品牌关联表
 *
 * @author: Aulen
 * @create: 2026-01-12
 */
@Data
public class CategoryBrandRelation {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 品牌ID
     */
    private Long brandId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
