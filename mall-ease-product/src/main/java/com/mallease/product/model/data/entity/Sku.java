package com.mallease.product.model.data.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SKU基础信息
 *
 * @author: Aulen
 * @create: 2025-12-10
 */
@Data
public class Sku {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * SPU ID
     */
    private Long spuId;

    /**
     * SKU编码
     */
    private String skuCode;

    /**
     * SKU规格值（JSON格式）
     */
    private String specValues;

    /**
     * SKU图片URL
     */
    private String pic;

    /**
     * SKU价格
     */
    private BigDecimal price;

    /**
     * 市场价
     */
    private BigDecimal originalPrice;

    /**
     * 逻辑删除: 0-未删除, 1-已删除
     */
    private Integer deleted;

    /**
     * 启用状态: 0-禁用, 1-启用
     */
    private Integer enableStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 更新人
     */
    private String updater;

    /**
     * 乐观锁版本号
     */
    private Integer version;
}