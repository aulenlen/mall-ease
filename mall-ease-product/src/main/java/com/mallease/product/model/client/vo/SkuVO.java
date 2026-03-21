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
 * 包含SKU基础信息和库存数据
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

    @Schema(description = "SKU属性值（JSON格式）")
    private String attrValues;

    @Schema(description = "属性值列表（前端展示用）")
    private List<AttrValueVO> attrValuesObj;

    /**
     * SKU属性值VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SKU属性值")
    public static class AttrValueVO {

        @Schema(description = "属性ID")
        private Long attrId;

        @Schema(description = "属性名称")
        private String attrName;

        @Schema(description = "属性值")
        private String attrValue;
    }

    @Schema(description = "SKU图片URL")
    private String pic;

    @Schema(description = "SKU基础成交价")
    private BigDecimal basePrice;

    @Schema(description = "SKU划线参考价")
    private BigDecimal compareAtPrice;

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

}
