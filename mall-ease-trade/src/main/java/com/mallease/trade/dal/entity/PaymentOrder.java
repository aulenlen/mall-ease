package com.mallease.trade.dal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付单实体（映射 trade_payment_order 表）
 *
 * @author: Aulen
 * @create: 2026-02-06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentOrder {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 支付单号
     */
    private String paymentNo;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 支付渠道: 1-支付宝, 2-微信, 9-模拟
     *
     * @see com.mallease.common.enums.PayChannel
     */
    private Integer payChannel;

    /**
     * 支付状态: 1-待支付, 2-支付成功, 3-已关闭, 4-支付失败
     *
     * @see com.mallease.common.enums.PaymentStatus
     */
    private Integer status;

    /**
     * 第三方交易号
     */
    private String thirdTradeNo;

    /**
     * 第三方买家ID
     */
    private String thirdBuyerId;

    /**
     * 支付过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 支付成功时间
     */
    private LocalDateTime paidTime;

    /**
     * 最新回调时间
     */
    private LocalDateTime notifyTime;

    /**
     * 回调次数
     */
    private Integer notifyCount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
