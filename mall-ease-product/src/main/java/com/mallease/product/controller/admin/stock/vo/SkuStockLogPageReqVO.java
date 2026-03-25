package com.mallease.product.controller.admin.stock.vo;

import com.mallease.common.dto.client.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 后台库存日志分页查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "后台库存日志分页查询参数")
public class SkuStockLogPageReqVO extends BaseQuery {

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "变更类型")
    private String changeType;

    @Schema(description = "来源类型")
    private String sourceType;

    @Schema(description = "来源编号，例如订单号")
    private String sourceNo;
}
