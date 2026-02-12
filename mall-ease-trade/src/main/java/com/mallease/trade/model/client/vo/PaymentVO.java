package com.mallease.trade.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付单视图对象
 *
 * @author: Aulen
 * @create: 2026-02-06
 */
@Schema(description = "支付单信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentVO {

    @Schema(description = "支付单号")
    private String paymentNo;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "支付金额")
    private BigDecimal payAmount;

    @Schema(description = "支付渠道：1-支付宝 2-微信支付 9-模拟支付")
    private Integer payChannel;

    @Schema(description = "支付状态：1-待支付 2-支付成功 3-已关闭 4-支付失败")
    private Integer status;

    @Schema(description = "支付单过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "实际支付时间")
    private LocalDateTime paidTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "支付表单 HTML（支付宝 WAP 支付时返回）")
    private String payForm;
}
