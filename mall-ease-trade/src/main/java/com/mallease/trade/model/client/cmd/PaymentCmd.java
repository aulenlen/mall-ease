package com.mallease.trade.model.client.cmd;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentCmd {
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @NotNull(message = "支付渠道不能为空")
    private Integer payChannel;
}
