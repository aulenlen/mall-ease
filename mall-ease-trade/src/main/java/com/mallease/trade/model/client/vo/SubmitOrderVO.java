package com.mallease.trade.model.client.vo;

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
public class SubmitOrderVO {

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "支付金额")
    private BigDecimal payAmount;

    @Schema(description = "订单支付截止时间")
    private LocalDateTime payExpireTime;

    @Schema(description = "订单状态：1-待发货 2-待收货 3-已完成 4-已取消 5-待支付 6-已支付")
    private Integer orderStatus;

    @Schema(description = "服务端当前时间")
    private LocalDateTime serverTime;
}
