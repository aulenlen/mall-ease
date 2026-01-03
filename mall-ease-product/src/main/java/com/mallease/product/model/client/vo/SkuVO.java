package com.mallease.product.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;

import lombok.Builder;

import lombok.Data;

import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import java.time.LocalDateTime;

import java.util.List;

/**
 * 
 * SKU视图对象
 * 包含SKU基础信息、库存、促销、价格策略等完整数据
 * @author: Aulen
 * @create: 2025-12-12
 */
@Schema(description = "SKU视图对象")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuVO {

    // SKU 基础信息

    @Schema(description = "SKU ID")
    private Long id;

    @Schema(description = "SPU ID")
    private Long spuId;

    @Schema(description = "SPU名称")
    private String spuName;

    @Schema(description = "SKU编码")
    private String skuCode;

    @Schema(description = "SKU规格值（JSON格式）")
    private String specValues;

    @Schema(description = "规格值列表（前端展示用）")
    private List<SkuSpecValue> specValuesObj;

    /**
     * SKU规格值VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SKU规格值")
    public static class SkuSpecValue {

        @Schema(description = "规格ID")
        private Long specId;

        @Schema(description = "规格名称")
        private String specName;

        @Schema(description = "规格值")
        private String value;
    }

    @Schema(description = "SKU图片URL")
    private String pic;

    @Schema(description = "SKU价格")
    private BigDecimal price;

    @Schema(description = "市场价")
    private BigDecimal originalPrice;

    @Schema(description = "启用状态: 0-禁用, 1-启用")
    private Integer enableStatus;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "更新人")
    private String updater;

    @Schema(description = "版本号")
    private Integer version;

    // 库存信息

    @Schema(description = "可用库存")
    private Integer stock;

    @Schema(description = "锁定库存")
    private Integer lockStock;

    @Schema(description = "累计销量")
    private Integer sale;

    @Schema(description = "库存预警值")
    private Integer lowStock;

    @Schema(description = "库存状态: 0-无货, 1-有货, 2-预售")
    private Integer stockStatus;

    // 促销信息

    @Schema(description = "促销类型: 0-无促销, 1-促销价, 2-会员价, 3-阶梯价, 4-满减价, 5-限时购")
    private Integer promotionType;

    @Schema(description = "促销价格")
    private BigDecimal promotionPrice;

    @Schema(description = "促销开始时间")
    private LocalDateTime promotionStartTime;

    @Schema(description = "促销结束时间")
    private LocalDateTime promotionEndTime;

    @Schema(description = "活动限购数量")
    private Integer promotionPerLimit;

    @Schema(description = "赠送成长值")
    private Integer giftGrowth;

    @Schema(description = "赠送积分")
    private Integer giftPoint;

    @Schema(description = "积分使用上限")
    private Integer usePointLimit;

    // 价格策略

    @Schema(description = "会员价格列表")
    private List<SkuMemberPriceVO> memberPriceList;

    @Schema(description = "阶梯价格列表")
    private List<SkuLadderVO> ladderList;

    // 嵌套VO对象

    /**
     * SKU会员价格VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SKU会员价格")
    public static class SkuMemberPriceVO {

        @Schema(description = "主键ID")
        private Long id;

        @Schema(description = "会员等级ID")
        private Long memberLevelId;

        @Schema(description = "会员等级名称")
        private String memberLevelName;

        @Schema(description = "会员价格")
        private BigDecimal memberPrice;
    }

    /**
     * SKU阶梯价格VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SKU阶梯价格")
    public static class SkuLadderVO {

        @Schema(description = "主键ID")
        private Long id;

        @Schema(description = "满足数量")
        private Integer count;

        @Schema(description = "折扣")
        private BigDecimal discount;

        @Schema(description = "折后价格")
        private BigDecimal price;

        @Schema(description = "优惠描述")
        private String description;
    }
}