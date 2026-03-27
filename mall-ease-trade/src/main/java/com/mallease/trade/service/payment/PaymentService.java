package com.mallease.trade.service.payment;

import com.mallease.common.api.Page;
import com.mallease.trade.controller.admin.payment.vo.PaymentRespVO;
import com.mallease.trade.controller.portal.payment.vo.PaymentCreateReqVO;
import com.mallease.trade.controller.admin.payment.vo.PaymentPageReqVO;
import com.mallease.trade.dal.entity.PaymentOrder;

import java.util.List;
import java.util.Map;

public interface PaymentService {

    PaymentOrder create(Long userId, PaymentCreateReqVO reqVO);

    /**
     * 执行支付
     *
     * @return 支付表单 HTML（支付宝 WAP），模拟支付返回 null
     */
    String pay(Long userId, String paymentNo, Integer payChannel);

    PaymentOrder getByOrderNo(Long userId, String orderNo);

    PaymentOrder getByPaymentNo(String paymentNo);

    /**
     * 管理端支付单分页列表。
     */
    Page<PaymentRespVO> pageAdminPayments(PaymentPageReqVO reqVO);

    /**
     * 管理端支付单详情。
     */
    PaymentRespVO getAdminPaymentDetail(String paymentNo);

    void close(Long userId, String orderNo);

    /**
     * 处理支付宝异步回调
     *
     * @param params 回调请求参数
     * @return true=处理成功
     */
    boolean handleAlipayNotify(Map<String, String> params);

}
