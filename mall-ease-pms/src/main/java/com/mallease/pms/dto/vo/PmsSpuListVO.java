package com.mallease.pms.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SPU列表视图对象
 * <p>
 * 用于列表页展示，字段精简，包含核心信息和状态字段
 *
 * @author: Aulen
 * @create: 2025-12-12
 */
@Schema(description = "SPU列表视图对象")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsSpuListVO {

    @Schema(description = "SPU ID")
    private Long id;

    @Schema(description = "SPU编码")
    private String spuCode;

    @Schema(description = "品牌ID")
    private Long brandId;

    @Schema(description = "品牌名称")
    private String brandName;

    @Schema(description = "商品分类ID")
    private Long categoryId;

    @Schema(description = "商品分类名称")
    private String categoryName;

    @Schema(description = "SPU名称")
    private String name;

    @Schema(description = "副标题")
    private String subTitle;

    @Schema(description = "SPU主图URL")
    private String pic;

    @Schema(description = "上架状态: 0-下架, 1-上架")
    private Integer publishStatus;

    @Schema(description = "上架状态名称")
    private String publishStatusName;

    @Schema(description = "新品状态: 0-不是新品, 1-新品")
    private Integer newStatus;

    @Schema(description = "推荐状态: 0-不推荐, 1-推荐")
    private Integer recommendStatus;

    @Schema(description = "审核状态: 0-未审核, 1-审核通过")
    private Integer verifyStatus;

    @Schema(description = "审核状态名称")
    private String verifyStatusName;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "总销量")
    private Integer sale;

    @Schema(description = "价格区间")
    private String priceRange;

    @Schema(description = "最低价格")
    private BigDecimal minPrice;

    @Schema(description = "最高价格")
    private BigDecimal maxPrice;

    @Schema(description = "总库存")
    private Integer stock;

    @Schema(description = "SKU数量")
    private Integer skuCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}