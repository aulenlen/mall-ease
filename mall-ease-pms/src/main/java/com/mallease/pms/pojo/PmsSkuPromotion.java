package com.mallease.pms.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SKU促销信息表
 * 说明：促销价格统一在此表管理，避免多处存储导致不一致
 *
 * @author: Aulen
 * @create: 2025-12-10
 */
@Data
public class PmsSkuPromotion {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * SKU ID（关联 pms_sku.id）
     */
    private Long skuId;

    /**
     * 促销类型: 0-无促销, 1-促销价, 2-会员价, 3-阶梯价, 4-满减价, 5-限时购
     */
    private Integer promotionType;

    /**
     * 是否为预告商品: 0-否, 1-是
     */
    private Integer previewStatus;

    /**
     * 促销价格
     */
    private BigDecimal promotionPrice;

    /**
     * 促销开始时间
     */
    private LocalDateTime promotionStartTime;

    /**
     * 促销结束时间
     */
    private LocalDateTime promotionEndTime;

    /**
     * 活动限购数量（0表示不限购）
     */
    private Integer promotionPerLimit;

    /**
     * 赠送的成长值
     */
    private Integer giftGrowth;

    /**
     * 赠送的积分
     */
    private Integer giftPoint;

    /**
     * 限制使用的积分数
     */
    private Integer usePointLimit;

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
