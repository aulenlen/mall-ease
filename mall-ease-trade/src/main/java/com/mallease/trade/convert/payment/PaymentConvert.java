package com.mallease.trade.convert.payment;

import com.mallease.trade.controller.admin.payment.vo.PaymentRespVO;
import com.mallease.trade.dal.entity.PaymentOrder;
import org.mapstruct.Mapper;

/**
 * 支付单对象转换器
 *
 * @author: Aulen
 * @create: 2026-02-07
 */
@Mapper(componentModel = "spring")
public interface PaymentConvert {

    PaymentRespVO entityToVO(PaymentOrder entity);
}
