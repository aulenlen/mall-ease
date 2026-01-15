package com.mallease.product.model.data.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类-属性关联表
 *
 * @author: Aulen
 * @create: 2026-01-12
 */
@Data
public class CategoryAttributeRelation {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 属性ID
     */
    private Long attrId;

    /**
     * 在该分类下的分组名称
     */
    private String groupName;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 在该分类下是否必填：0-否 1-是
     */
    private Integer required;

    /**
     * 分类特定选项（JSON格式，覆盖全局选项）
     */
    private String options;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
