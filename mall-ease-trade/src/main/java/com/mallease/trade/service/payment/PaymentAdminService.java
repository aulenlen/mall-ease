package com.mallease.trade.service.payment;

import com.mallease.common.api.Page;
import com.mallease.trade.controller.admin.payment.vo.PaymentPageReqVO;
import com.mallease.trade.controller.admin.payment.vo.PaymentRespVO;

/**
 * 管理端支付服务接口
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
public interface PaymentAdminService {

    /**
     * 管理端支付单分页列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    Page<PaymentRespVO> list(PaymentPageReqVO query);

    /**
     * 支付单详情
     *
     * @param paymentNo 支付单号
     * @return 支付单详情
     */
    PaymentRespVO detail(String paymentNo);
}
