package com.mallease.pms.dto.query;

import com.mallease.common.dto.query.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * SKU查询对象
 * <p>
 * 支持按SPU、价格区间、库存状态、启用状态等维度查询SKU
 *
 * @author: Aulen
 * @create: 2025-12-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "SKU查询对象")
public class PmsSkuQuery extends BaseQuery {

    @Schema(description = "SPU ID（查询某个SPU下的所有SKU）")
    private Long spuId;

    @Schema(description = "SKU编码（精确查询）")
    private String skuCode;

    @Schema(description = "启用状态: 0-禁用, 1-启用")
    private Integer enableStatus;

    @Schema(description = "价格下限（区间查询）")
    private BigDecimal priceStart;

    @Schema(description = "价格上限（区间查询）")
    private BigDecimal priceEnd;

    @Schema(description = "库存状态: 0-无货, 1-有货, 2-预售")
    private Integer stockStatus;

    @Schema(description = "最小库存数量（查询库存大于此值的SKU）")
    private Integer minStock;

    @Schema(description = "规格值关键字（JSON字段模糊查询，如：红色、XL）")
    private String specKeyword;
}