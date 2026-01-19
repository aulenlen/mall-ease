package com.mallease.app.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 商品列表项
 *
 * @author: Aulen
 * @create: 2026-01-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "商品列表项")
public class ProductItemVO {

    @Schema(description = "商品ID")
    private Long spuId;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "副标题")
    private String subTitle;

    @Schema(description = "商品主图")
    private String pic;

    @Schema(description = "品牌ID")
    private Long brandId;

    @Schema(description = "品牌名称")
    private String brandName;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类路径")
    private String categoryPath;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "最低价格")
    private BigDecimal minPrice;

    @Schema(description = "最高价格")
    private BigDecimal maxPrice;

    @Schema(description = "销量")
    private Integer sale;

    @Schema(description = "是否有货")
    private Boolean inStock;

    @Schema(description = "是否新品")
    private Boolean isNew;
}