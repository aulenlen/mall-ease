package com.mallease.trade.service;

import com.mallease.trade.model.client.cmd.PaymentCmd;
import com.mallease.trade.model.data.entity.PaymentOrder;

public interface PaymentService {
    PaymentOrder create(Long userId, PaymentCmd cmd);

    PaymentOrder pay(Long userId, String paymentNo, Integer payChannel);

    PaymentOrder getByOrderNo(Long userId, String orderNo);

    void close(Long userId, String orderNo);
}
