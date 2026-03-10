package com.mallease.trade.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单状态分布视图对象
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
@Schema(description = "订单状态分布")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusDistributionVO {

    @Schema(description = "状态码")
    private Integer status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "订单数量")
    private Long count;
}
