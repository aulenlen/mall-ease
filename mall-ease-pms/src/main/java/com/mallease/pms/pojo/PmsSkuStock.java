package com.mallease.pms.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SKU库存信息（读写分离，高频更新）
 *
 * @author: Aulen
 * @create: 2025-12-10
 */
@Data
public class PmsSkuStock {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * SKU ID（关联 pms_sku.id）
     */
    private Long skuId;

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

    // ========================================================================
    // 遗留字段（兼容旧表结构）- 待迁移后删除
    // TODO: 完成 SKU 重构后删除以下字段
    // ========================================================================

    /**
     * 产品ID
     * @deprecated 新架构中使用 skuId 关联，通过 pms_sku.spu_id 获取商品信息
     */
    @Deprecated
    private Long productId;

    /**
     * SKU编码
     * @deprecated 新架构中 sku_code 已移至 pms_sku 表
     */
    @Deprecated
    private String skuCode;

    /**
     * 价格
     * @deprecated 新架构中价格在 pms_sku.price 字段
     */
    @Deprecated
    private BigDecimal price;

    /**
     * 促销价格
     * @deprecated 新架构中促销价通过 pms_sku_price 表管理
     */
    @Deprecated
    private BigDecimal promotionPrice;

    /**
     * 销售属性值（JSON格式）
     * @deprecated 新架构中属性通过 pms_sku_spec_value 关联表管理
     */
    @Deprecated
    private String spData;

    /**
     * 图片
     * @deprecated 新架构中图片在 pms_sku.pic 字段
     */
    @Deprecated
    private String pic;
}

