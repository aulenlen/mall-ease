package com.mallease.product.controller.admin.sku.vo;

import com.mallease.common.dto.client.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * SKU 分页查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "SKU 分页查询请求")
public class SkuPageReqVO extends BaseQuery {

    @Schema(description = "SPU ID（查询某个 SPU 下的所有 SKU）")
    private Long spuId;

    @Schema(description = "SKU 编码（精确查询）")
    private String skuCode;

    @Schema(description = "启用状态: 0-禁用, 1-启用")
    private Integer enableStatus;

    @Schema(description = "基础成交价下限（区间查询）")
    private BigDecimal basePriceStart;

    @Schema(description = "基础成交价上限（区间查询）")
    private BigDecimal basePriceEnd;

    @Schema(description = "库存状态: 0-无货, 1-有货, 2-预售")
    private Integer stockStatus;

    @Schema(description = "最小库存数量（查询库存大于此值的 SKU）")
    private Integer minStock;

    @Schema(description = "规格值关键字（JSON 字段模糊查询，如：红色、XL）")
    private String specKeyword;
}