package com.mallease.trade.service.payment;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.exception.ApiException;
import com.mallease.trade.convert.payment.PaymentConvert;
import com.mallease.trade.controller.admin.payment.vo.PaymentPageReqVO;
import com.mallease.trade.controller.admin.payment.vo.PaymentRespVO;
import com.mallease.trade.dal.entity.PaymentOrder;
import com.mallease.trade.service.payment.PaymentAdminService;
import com.mallease.trade.service.payment.PaymentService;
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
public class PaymentAdminServiceImpl implements PaymentAdminService {

    private final PaymentService paymentService;
    private final PaymentConvert paymentConverter;

    @Override
    public Page<PaymentRespVO> list(PaymentPageReqVO query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<PaymentOrder> payments = paymentService.adminList(query);

        if (payments.isEmpty()) {
            return PageUtils.buildPage(payments, Collections.emptyList());
        }

        List<PaymentRespVO> voList = payments.stream()
                .map(paymentConverter::entityToVO)
                .toList();

        return PageUtils.buildPage(payments, voList);
    }

    @Override
    public PaymentRespVO detail(String paymentNo) {
        PaymentOrder payment = paymentService.getByPaymentNo(paymentNo);
        if (payment == null) {
            throw new ApiException("支付单不存在");
        }
        return paymentConverter.entityToVO(payment);
    }
}
