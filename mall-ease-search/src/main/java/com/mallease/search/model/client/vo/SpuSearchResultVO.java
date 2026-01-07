package com.mallease.search.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 商品搜索结果 VO
 *
 * @author: Aulen
 * @create: 2025-12-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "商品搜索结果")
public class SpuSearchResultVO {

    @Schema(description = "SPU ID")
    private Long spuId;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "商品名称（高亮）")
    private String highlightName;

    @Schema(description = "副标题")
    private String subTitle;

    @Schema(description = "主图")
    private String pic;

    @Schema(description = "品牌ID")
    private Long brandId;

    @Schema(description = "品牌名称")
    private String brandName;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "最低价")
    private BigDecimal minPrice;

    @Schema(description = "最高价")
    private BigDecimal maxPrice;

    @Schema(description = "销量")
    private Integer sale;

    @Schema(description = "是否有货")
    private Boolean inStock;

    @Schema(description = "是否新品")
    private Boolean isNew;

    @Schema(description = "搜索得分")
    private Float score;
}
