package com.mallease.trade.model.client.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentVO {
    private String paymentNo;
    private String orderNo;
    private BigDecimal payAmount;
    private Integer payChannel;
    private Integer status;
    private LocalDateTime expireTime;
    private LocalDateTime paidTime;
    private LocalDateTime createTime;
}
