package com.mallease.trade.service;

import com.mallease.common.api.Page;
import com.mallease.trade.model.client.query.PaymentQuery;
import com.mallease.trade.model.client.vo.PaymentVO;

/**
 * 管理端支付服务接口
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
public interface AdminPaymentService {

    /**
     * 管理端支付单分页列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    Page<PaymentVO> list(PaymentQuery query);

    /**
     * 支付单详情
     *
     * @param paymentNo 支付单号
     * @return 支付单详情
     */
    PaymentVO detail(String paymentNo);
}
