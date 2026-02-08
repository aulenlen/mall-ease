package com.mallease.trade.service.impl;

import com.mallease.common.enums.OrderStatus;
import com.mallease.common.enums.PaymentStatus;
import com.mallease.common.exception.ApiException;
import com.mallease.common.util.NoGeneratorUtil;
import com.mallease.trade.dao.PaymentOrderDao;
import com.mallease.trade.model.client.cmd.PaymentCmd;
import com.mallease.trade.model.data.entity.Order;
import com.mallease.trade.model.data.entity.PaymentOrder;
import com.mallease.trade.service.OrderService;
import com.mallease.trade.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentOrderDao paymentOrderDao;
    private final OrderService orderService;

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
    public PaymentOrder pay(Long userId, String paymentNo, Integer payChannel) {
        PaymentOrder existPayment = paymentOrderDao.findPending(userId, paymentNo);

        if (existPayment == null) {
            throw new ApiException("订单错误");
        }
        paymentOrderDao.updateStatus(existPayment.getId(), PaymentStatus.SUCCESS.getCode(), payChannel, LocalDateTime.now(), "mock");
        orderService.updateStatus(existPayment.getOrderNo(), OrderStatus.PAID.getCode());
        return existPayment;
    }

    @Override
    public PaymentOrder getByOrderNo(Long userId, String orderNo) {
        return paymentOrderDao.selectByUserIdAndOrderNo(userId, orderNo);
    }

    @Override
    public void close(Long userId, String orderNo) {
        paymentOrderDao.closeByOrderNo(userId, orderNo, PaymentStatus.CLOSED.getCode());
    }
}
