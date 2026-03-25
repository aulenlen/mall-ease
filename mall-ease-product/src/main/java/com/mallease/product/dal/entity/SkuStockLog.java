package com.mallease.product.dal.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * SKU 库存变更日志
 */
@Data
public class SkuStockLog {

    /**
     * 主键 ID
     */
    private Long id;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * SPU ID
     */
    private Long spuId;

    /**
     * 变更类型
     */
    private String changeType;

    /**
     * 变更前可用库存
     */
    private Integer beforeStock;

    /**
     * 变更后可用库存
     */
    private Integer afterStock;

    /**
     * 变更前锁定库存
     */
    private Integer beforeLockStock;

    /**
     * 变更后锁定库存
     */
    private Integer afterLockStock;

    /**
     * 可用库存变化量
     */
    private Integer changeQuantity;

    /**
     * 来源类型
     */
    private String sourceType;

    /**
     * 来源编号，例如订单号
     */
    private String sourceNo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
