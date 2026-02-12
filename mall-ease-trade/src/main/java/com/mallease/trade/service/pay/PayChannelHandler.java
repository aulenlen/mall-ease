package com.mallease.trade.service.pay;

import com.mallease.trade.model.data.entity.PaymentOrder;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface PayChannelHandler {
    /**
     * 预下单，返回支付表单/签名字符串
     *
     * @param payment 支付单
     * @return 支付表单 HTML
     */
    String prepay(PaymentOrder payment);

    /**
     * 处理异步回调，返回解析结果
     *
     */
    Map<String, String> handleNotify(Map<String, String> params);
}
