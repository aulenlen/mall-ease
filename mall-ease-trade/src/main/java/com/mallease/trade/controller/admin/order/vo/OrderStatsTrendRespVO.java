package com.mallease.trade.controller.admin.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单趋势数据视图对象
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
@Schema(description = "订单趋势数据")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatsTrendRespVO {

    @Schema(description = "日期")
    private String date;

    @Schema(description = "订单数量")
    private Long orderCount;

    @Schema(description = "订单金额")
    private BigDecimal orderAmount;
}
