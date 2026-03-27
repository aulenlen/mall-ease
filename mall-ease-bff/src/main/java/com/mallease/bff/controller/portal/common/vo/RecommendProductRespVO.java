package com.mallease.bff.controller.portal.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 推荐商品卡片返回值。
 *
 * @author: Aulen
 * @create: 2026-01-08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "推荐商品")
public class RecommendProductRespVO {

    @Schema(description = "SPU ID")
    private Long spuId;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "副标题")
    private String subTitle;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类路径")
    private String categoryPath;

    @Schema(description = "主图URL")
    private String pic;

    @Schema(description = "品牌名称")
    private String brandName;

    @Schema(description = "最低价")
    private BigDecimal minPrice;

    @Schema(description = "最高价")
    private BigDecimal maxPrice;

    @Schema(description = "销量")
    private Integer sale;

    @Schema(description = "是否新品")
    private Boolean isNew;
}
