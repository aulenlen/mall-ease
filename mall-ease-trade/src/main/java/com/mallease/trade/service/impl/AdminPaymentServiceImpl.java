package com.mallease.trade.service.impl;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.exception.ApiException;
import com.mallease.trade.converter.PaymentConverter;
import com.mallease.trade.model.client.query.PaymentQuery;
import com.mallease.trade.model.client.vo.PaymentVO;
import com.mallease.trade.model.data.entity.PaymentOrder;
import com.mallease.trade.service.AdminPaymentService;
import com.mallease.trade.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 管理端支付服务实现
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminPaymentServiceImpl implements AdminPaymentService {

    private final PaymentService paymentService;
    private final PaymentConverter paymentConverter;

    @Override
    public Page<PaymentVO> list(PaymentQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<PaymentOrder> payments = paymentService.adminList(query);

        if (payments.isEmpty()) {
            return PageUtils.buildPage(payments, Collections.emptyList());
        }

        List<PaymentVO> voList = payments.stream()
                .map(paymentConverter::entityToVO)
                .toList();

        return PageUtils.buildPage(payments, voList);
    }

    @Override
    public PaymentVO detail(String paymentNo) {
        PaymentOrder payment = paymentService.getByPaymentNo(paymentNo);
        if (payment == null) {
            throw new ApiException("支付单不存在");
        }
        return paymentConverter.entityToVO(payment);
    }
}
