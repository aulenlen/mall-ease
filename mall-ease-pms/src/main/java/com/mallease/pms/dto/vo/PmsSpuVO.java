package com.mallease.pms.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SPU基础视图对象
 * <p>
 * 用于通用的SPU信息展示，不包含详情和关联数据
 *
 * @author: Aulen
 * @create: 2025-12-12
 */
@Schema(description = "SPU基础视图对象")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsSpuVO {

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

    @Schema(description = "分类路径(逗号分隔)")
    private String categoryIds;

    @Schema(description = "SPU名称")
    private String name;

    @Schema(description = "副标题")
    private String subTitle;

    @Schema(description = "SPU描述")
    private String description;

    @Schema(description = "关键字")
    private String keywords;

    @Schema(description = "SPU主图URL")
    private String pic;

    @Schema(description = "画册图片（逗号分割）")
    private String albumPics;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "商品重量（克）")
    private BigDecimal weight;

    @Schema(description = "上架状态: 0-下架, 1-上架")
    private Integer publishStatus;

    @Schema(description = "新品状态: 0-不是新品, 1-新品")
    private Integer newStatus;

    @Schema(description = "推荐状态: 0-不推荐, 1-推荐")
    private Integer recommendStatus;

    @Schema(description = "审核状态: 0-未审核, 1-审核通过")
    private Integer verifyStatus;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "总销量")
    private Integer sale;

    @Schema(description = "最低价格")
    private BigDecimal minPrice;

    @Schema(description = "最高价格")
    private BigDecimal maxPrice;

    @Schema(description = "总库存")
    private Integer stock;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}