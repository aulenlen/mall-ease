package com.mallease.trade.service.payment.handler;

import com.mallease.trade.dal.entity.PaymentOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 模拟支付处理器（用于本地开发和测试）
 *
 * @author: Aulen
 * @create: 2026-02-09
 */
@Slf4j
@Component
public class MockPayHandler implements PayChannelHandler {

    @Override
    public String prepay(PaymentOrder payment) {
        log.info("模拟支付预下单: paymentNo={}", payment.getPaymentNo());
        return null;
    }

    @Override
    public Map<String, String> handleNotify(Map<String, String> params) {
        return Map.of();
    }

}
