package com.mallease.product.controller.admin.stock.vo;

import com.mallease.common.dto.client.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 后台库存分页查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "后台库存分页查询参数")
public class SkuStockPageReqVO extends BaseQuery {

    @Schema(description = "关键字，匹配 SPU 名称或 SKU 编码")
    private String keyword;

    @Schema(description = "SPU ID")
    private Long spuId;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "品牌 ID")
    private Long brandId;

    @Schema(description = "分类 ID")
    private Long categoryId;

    @Schema(description = "库存状态: 0-无货, 1-有货, 2-预售")
    private Integer stockStatus;

    @Schema(description = "是否只看低库存")
    private Boolean lowStockWarning;

    @Schema(description = "页签类型：all / warning / empty / presale")
    private String tab = "all";
}
