package com.mallease.product.dal.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * SKU库存信息（读写分离，高频更新）
 *
 * @author: Aulen
 * @create: 2025-12-10
 */
@Data
public class SkuStock {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * SKU ID（关联 pms_sku.id）
     */
    private Long skuId;

    /**
     * SPU ID（冗余字段，便于按商品查询库存）
     */
    private Long spuId;

    /**
     * 可用库存
     */
    private Integer stock;

    /**
     * 锁定库存（下单未支付）
     */
    private Integer lockStock;

    /**
     * 累计销量
     */
    private Integer sale;

    /**
     * 库存预警值
     */
    private Integer lowStock;

    /**
     * 库存状态: 0-无货, 1-有货, 2-预售
     */
    private Integer stockStatus;

    /**
     * 乐观锁版本号（库存扣减必备）
     */
    private Integer version;

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
}

