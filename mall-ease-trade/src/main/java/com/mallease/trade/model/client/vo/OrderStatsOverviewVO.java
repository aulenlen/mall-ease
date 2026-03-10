package com.mallease.trade.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单统计总览视图对象
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
@Schema(description = "订单统计总览")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatsOverviewVO {

    @Schema(description = "今日订单数")
    private Long todayOrderCount;

    @Schema(description = "今日订单金额")
    private BigDecimal todayOrderAmount;

    @Schema(description = "待支付订单数")
    private Long pendingPaymentCount;

    @Schema(description = "待发货订单数")
    private Long pendingShipmentCount;

    @Schema(description = "待收货订单数")
    private Long pendingReceiptCount;

    @Schema(description = "总订单数")
    private Long totalOrderCount;

    @Schema(description = "总订单金额")
    private BigDecimal totalOrderAmount;
}
