package com.mallease.pms.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * SKU库存视图对象
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Schema(description = "SKU库存")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsSkuStockVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "产品ID")
    private Long productId;

    @Schema(description = "SKU编码")
    private String skuCode;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "库存")
    private Integer stock;

    @Schema(description = "预警库存")
    private Integer lowStock;

    @Schema(description = "展示图片")
    private String pic;

    @Schema(description = "销量")
    private Integer sale;

    @Schema(description = "单品促销价格")
    private BigDecimal promotionPrice;

    @Schema(description = "锁定库存")
    private Integer lockStock;

    @Schema(description = "商品销售属性，json格式")
    private String spData;
}
