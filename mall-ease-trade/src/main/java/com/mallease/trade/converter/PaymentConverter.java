package com.mallease.trade.converter;

import com.mallease.trade.model.client.vo.PaymentVO;
import com.mallease.trade.model.data.entity.PaymentOrder;
import org.mapstruct.Mapper;

/**
 * 支付单对象转换器
 *
 * @author: Aulen
 * @create: 2026-02-07
 */
@Mapper(componentModel = "spring")
public interface PaymentConverter {

    PaymentVO entityToVO(PaymentOrder entity);
}
