package com.mallease.trade.controller.portal.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSubmitRespVO {

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "支付金额")
    private BigDecimal payAmount;

    @Schema(description = "订单支付截止时间")
    private LocalDateTime payExpireTime;

    @Schema(description = "订单状态编码，详见 OrderStatus")
    private Integer orderStatus;

    @Schema(description = "服务端当前时间")
    private LocalDateTime serverTime;
}
