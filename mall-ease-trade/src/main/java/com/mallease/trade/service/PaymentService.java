package com.mallease.trade.service;

import com.mallease.trade.model.client.cmd.PaymentCmd;
import com.mallease.trade.model.data.entity.PaymentOrder;

import java.util.Map;

public interface PaymentService {

    PaymentOrder create(Long userId, PaymentCmd cmd);

    /**
     * 执行支付
     *
     * @return 支付表单 HTML（支付宝 WAP），模拟支付返回 null
     */
    String pay(Long userId, String paymentNo, Integer payChannel);

    PaymentOrder getByOrderNo(Long userId, String orderNo);

    PaymentOrder getByPaymentNo(String paymentNo);

    void close(Long userId, String orderNo);

    /**
     * 处理支付宝异步回调
     *
     * @param params 回调请求参数
     * @return true=处理成功
     */
    boolean handleAlipayNotify(Map<String, String> params);
}
