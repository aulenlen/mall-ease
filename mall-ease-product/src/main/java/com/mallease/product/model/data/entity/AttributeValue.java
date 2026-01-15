package com.mallease.product.model.data.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品属性值表
 *
 * @author: Aulen
 * @create: 2026-01-11
 */
@Data
public class AttributeValue {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * SPU ID
     */
    private Long spuId;

    /**
     * SKU ID（NULL=SPU级参数，非NULL=SKU级规格）
     */
    private Long skuId;

    /**
     * 属性ID
     */
    private Long attrId;

    /**
     * 属性名称
     */
    private String attrName;

    /**
     * 属性值
     */
    private String attrValue;

    /**
     * 逻辑删除：0-未删除 1-已删除
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
