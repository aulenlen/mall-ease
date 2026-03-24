package com.mallease.product.controller.admin.spu.vo;

import com.mallease.product.controller.admin.sku.vo.SkuRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 后台商品分页项
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "后台商品分页项")
public class SpuPageRespVO {

    @Schema(description = "SPU ID")
    private Long id;

    @Schema(description = "SPU编码")
    private String spuCode;

    @Schema(description = "品牌ID")
    private Long brandId;

    @Schema(description = "品牌名称")
    private String brandName;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "商品主图")
    private String pic;

    @Schema(description = "上架状态: 0-下架, 1-上架")
    private Integer publishStatus;

    @Schema(description = "新品状态: 0-不是新品, 1-新品")
    private Integer newStatus;

    @Schema(description = "推荐状态: 0-不推荐, 1-推荐")
    private Integer recommendStatus;

    @Schema(description = "审核状态: 0-未审核, 1-审核通过")
    private Integer verifyStatus;

    @Schema(description = "排序值")
    private Integer sort;

    @Schema(description = "销量")
    private Integer sale;

    @Schema(description = "最低价")
    private BigDecimal minPrice;

    @Schema(description = "最高价")
    private BigDecimal maxPrice;

    @Schema(description = "是否有货")
    private Boolean inStock;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "SKU 列表")
    private List<SkuRespVO> skuVOList;
}
