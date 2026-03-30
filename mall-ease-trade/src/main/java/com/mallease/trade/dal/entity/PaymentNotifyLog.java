package com.mallease.trade.dal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 支付回调日志实体（映射 trade_payment_notify_log 表）
 *
 * @author: Aulen
 * @create: 2026-03-31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentNotifyLog {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 支付渠道: 1-支付宝, 2-微信, 9-模拟
     */
    private Integer channel;

    /**
     * 渠道回调唯一ID
     */
    private String notifyId;

    /**
     * 支付单号
     */
    private String paymentNo;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 第三方交易号
     */
    private String thirdTradeNo;

    /**
     * 签名校验: 0-未通过, 1-通过
     */
    private Integer signVerified;

    /**
     * 处理状态: 1-待处理, 2-处理成功, 3-重复忽略, 4-处理失败
     */
    private Integer processStatus;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 回调原文
     */
    private String rawBody;

    /**
     * 回调触发时间
     */
    private LocalDateTime notifyTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}