package com.mallease.product.controller.admin.stock.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 后台库存日志响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "后台库存日志响应")
public class SkuStockLogRespVO {

    @Schema(description = "日志 ID")
    private Long id;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "SPU ID")
    private Long spuId;

    @Schema(description = "SPU 名称")
    private String spuName;

    @Schema(description = "SKU 编码")
    private String skuCode;

    @Schema(description = "变更类型")
    private String changeType;

    @Schema(description = "变更前可用库存")
    private Integer beforeStock;

    @Schema(description = "变更后可用库存")
    private Integer afterStock;

    @Schema(description = "变更前锁定库存")
    private Integer beforeLockStock;

    @Schema(description = "变更后锁定库存")
    private Integer afterLockStock;

    @Schema(description = "本次变更的可用库存增量")
    private Integer changeQuantity;

    @Schema(description = "来源类型")
    private String sourceType;

    @Schema(description = "来源编号，例如订单号")
    private String sourceNo;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
