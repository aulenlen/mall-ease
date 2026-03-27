package com.mallease.trade.service.payment.handler;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.mallease.common.exception.ApiException;
import com.mallease.trade.config.AlipayConfiguration;
import com.mallease.trade.dal.entity.PaymentOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlipayHandler implements PayChannelHandler {

    private static final DateTimeFormatter ALIPAY_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AlipayConfig alipayConfig;
    private final AlipayConfiguration alipayConfiguration;

    @Override
    public String prepay(PaymentOrder payment) {
        validateConfig();

        try {
            AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);

            AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();

            request.setNotifyUrl(alipayConfiguration.getNotifyUrl());
            request.setReturnUrl(alipayConfiguration.getReturnUrl());

            AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();

            model.setOutTradeNo(payment.getPaymentNo());
            model.setTotalAmount(String.valueOf(payment.getPayAmount()));
            model.setSubject("商城订单:" + payment.getOrderNo());
            model.setTimeExpire(payment.getExpireTime().format(ALIPAY_TIME_FORMATTER));
            model.setProductCode("QUICK_WAP_WAY");

            request.setBizModel(model);

            AlipayTradeWapPayResponse response = alipayClient.pageExecute(request, "POST");
            String payForm = response == null ? null : response.getBody();
            if (payForm == null || payForm.isBlank()) {
                throw new ApiException("支付宝预下单失败，请稍后重试");
            }
            return payForm;

        } catch (AlipayApiException e) {
            log.error("支付宝预下单失败: paymentNo={}, error={}", payment.getPaymentNo(), e.getErrMsg());
            throw new ApiException("支付失败，请稍后重试");
        }
    }

    @Override
    public Map<String, String> handleNotify(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return Map.of();
        }

        try {
            boolean verified = AlipaySignature.rsaCheckV1(params, alipayConfiguration.getAlipayPublicKey(), "UTF-8", "RSA2");
            if (!verified) {
                return Map.of();
            }
            return params;
        } catch (AlipayApiException e) {
            log.error("支付宝回调验签异常", e);
            return Map.of();
        }
    }

    private void validateConfig() {
        if (isBlank(alipayConfig.getServerUrl())) {
            throw new ApiException("支付宝网关未配置");
        }
        if (isBlank(alipayConfiguration.getNotifyUrl()) || isBlank(alipayConfiguration.getReturnUrl())) {
            throw new ApiException("支付宝回调地址未配置");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
