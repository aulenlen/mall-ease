package com.mallease.trade.service.impl;

import com.mallease.common.enums.OrderStatus;
import com.mallease.common.enums.PayChannel;
import com.mallease.common.enums.PaymentStatus;
import com.mallease.common.exception.ApiException;
import com.mallease.common.util.NoGeneratorUtil;
import com.mallease.trade.dao.PaymentOrderDao;
import com.mallease.trade.model.client.cmd.PaymentCmd;
import com.mallease.trade.model.data.entity.Order;
import com.mallease.trade.model.data.entity.PaymentOrder;
import com.mallease.trade.service.OrderService;
import com.mallease.trade.service.PaymentService;
import com.mallease.trade.service.pay.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 支付服务实现
 *
 * @author: Aulen
 * @create: 2026-02-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentOrderDao paymentOrderDao;
    private final OrderService orderService;
    private final AlipayHandler alipayHandler;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentOrder create(Long userId, PaymentCmd cmd) {

        Order pendingOrder = orderService.findPending(userId, cmd.getOrderNo());

        if (pendingOrder == null) {
            throw new ApiException("订单错误");
        }
        PaymentOrder existPayment = paymentOrderDao.selectByOrderNo(cmd.getOrderNo());

        if (existPayment == null) {
            PaymentOrder payment = PaymentOrder.builder()
                    .paymentNo(NoGeneratorUtil.generate(userId))
                    .orderNo(cmd.getOrderNo())
                    .userId(userId)
                    .payAmount(pendingOrder.getPayAmount())
                    .status(PaymentStatus.PENDING.getCode())
                    .expireTime(LocalDateTime.now().plusMinutes(30))
                    .build();

            paymentOrderDao.insert(payment);
            return paymentOrderDao.selectByOrderNo(cmd.getOrderNo());
        }

        if (existPayment.getStatus().equals(PaymentStatus.PENDING.getCode())) {
            return existPayment;
        }

        if (existPayment.getStatus().equals(PaymentStatus.FAILED.getCode())) {
            paymentOrderDao.resetForRetry(
                    existPayment.getId(),
                    NoGeneratorUtil.generate(userId),
                    LocalDateTime.now().plusMinutes(30)
            );
            return paymentOrderDao.selectByOrderNo(cmd.getOrderNo());
        }

        if (existPayment.getStatus().equals(PaymentStatus.SUCCESS.getCode())) {
            throw new ApiException("订单已支付");
        }

        if (existPayment.getStatus().equals(PaymentStatus.CLOSED.getCode())) {
            throw new ApiException("订单已关闭");
        }

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String pay(Long userId, String paymentNo, Integer payChannel) {
        PaymentOrder payment = paymentOrderDao.findPending(userId, paymentNo);
        if (payment == null) {
            throw new ApiException("支付单不存在或已过期");
        }

        PayChannelHandler handler = getHandler(payChannel);

        if (PayChannel.MOCK.getCode().equals(payChannel)) {
            paymentOrderDao.updateStatus(
                    payment.getId(),
                    PaymentStatus.SUCCESS.getCode(),
                    payChannel,
                    LocalDateTime.now(),
                    "mock"
            );
            orderService.updateStatus(payment.getOrderNo(), OrderStatus.PAID.getCode());
            return null;
        }

        return handler.prepay(payment);
    }

    @Override
    public PaymentOrder getByOrderNo(Long userId, String orderNo) {
        return paymentOrderDao.selectByUserIdAndOrderNo(userId, orderNo);
    }

    @Override
    public PaymentOrder getByPaymentNo(String paymentNo) {
        return paymentOrderDao.selectByPaymentNo(paymentNo);
    }

    @Override
    public void close(Long userId, String orderNo) {
        paymentOrderDao.closeByOrderNo(userId, orderNo, PaymentStatus.CLOSED.getCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAlipayNotify(Map<String, String> params) {

        Map<String, String> notifyMap = alipayHandler.handleNotify(params);

        if (notifyMap == null || notifyMap.isEmpty()) {
            log.warn("支付宝回调验签失败");
            return false;
        }

        String outTradeNo = notifyMap.get("out_trade_no");
        String tradeNo = notifyMap.get("trade_no");
        String tradeStatus = notifyMap.get("trade_status");
        String buyerId = notifyMap.get("buyer_id");
        String gmtPayment = notifyMap.get("gmt_payment");

        if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)) {
            log.info("支付宝回调非成功状态: tradeStatus={}", tradeStatus);
            return true;
        }

        PaymentOrder payment = paymentOrderDao.selectByPaymentNo(outTradeNo);
        if (payment == null) {
            log.warn("回调对应的支付单不存在: outTradeNo={}", outTradeNo);
            return false;
        }

        if (PaymentStatus.SUCCESS.getCode().equals(payment.getStatus())) {
            log.info("支付单已处理，忽略重复回调: paymentNo={}", payment.getPaymentNo());
            return true;
        }

        LocalDateTime paidTime = parsePaidTime(gmtPayment);

        int rows = paymentOrderDao.updateStatus(
                payment.getId(),
                PaymentStatus.SUCCESS.getCode(),
                PayChannel.ALIPAY.getCode(),
                paidTime,
                tradeNo
        );

        if (rows == 0) {
            log.info("支付单状态已变更，跳过本次回调: paymentNo={}", payment.getPaymentNo());
            return true;
        }

        PaymentOrder notifyUpdate = new PaymentOrder();
        notifyUpdate.setId(payment.getId());
        notifyUpdate.setThirdBuyerId(buyerId);
        notifyUpdate.setNotifyTime(LocalDateTime.now());
        notifyUpdate.setNotifyCount(payment.getNotifyCount() == null ? 1 : payment.getNotifyCount() + 1);
        paymentOrderDao.updateByPrimaryKeySelective(notifyUpdate);

        orderService.updateStatus(payment.getOrderNo(), OrderStatus.PAID.getCode());

        log.info("支付宝回调处理成功: paymentNo={}, tradeNo={}, buyerId={}", payment.getPaymentNo(), tradeNo, buyerId);

        return true;
    }

    /**
     * 解析支付宝回传的付款时间
     */
    private LocalDateTime parsePaidTime(String gmtPayment) {
        if (gmtPayment == null || gmtPayment.isBlank()) {
            return LocalDateTime.now();
        }
        try {
            return LocalDateTime.parse(gmtPayment, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            log.warn("解析支付宝付款时间失败: {}, 使用当前时间", gmtPayment);
            return LocalDateTime.now();
        }
    }

    /**
     * 根据支付渠道获取对应的 Handler
     */
    private PayChannelHandler getHandler(Integer payChannel) {
        PayChannel channel = PayChannel.of(payChannel);
        if (channel == null) {
            throw new ApiException("不支持的支付渠道: " + payChannel);
        }
        return switch (channel) {
            case ALIPAY -> alipayHandler;
            default -> throw new ApiException("不支持的支付渠道: " + channel.getDesc());
        };
    }
}
