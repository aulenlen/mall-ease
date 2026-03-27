package com.mallease.trade.convert.order;

import com.mallease.common.enums.OrderStatus;
import com.mallease.trade.controller.admin.order.vo.OrderAdminRespVO;
import com.mallease.trade.controller.portal.order.vo.OrderItemRespVO;
import com.mallease.trade.controller.admin.payment.vo.PaymentRespVO;
import com.mallease.trade.dal.entity.Order;
import com.mallease.trade.dal.entity.OrderItem;
import org.mapstruct.Mapper;

import java.util.Collections;
import java.util.List;

/**
 * 管理端订单对象转换器
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
@Mapper(componentModel = "spring")
public interface OrderAdminConvert {

    /**
     * OrderItem -> OrderItemRespVO
     */
    OrderItemRespVO itemToItemVO(OrderItem item);

    /**
     * OrderItem List -> OrderItemRespVO List
     */
    List<OrderItemRespVO> itemsToItemVOs(List<OrderItem> items);

    /**
     * Order -> OrderAdminRespVO（列表场景，含商品列表）
     */
    default OrderAdminRespVO toListVO(Order order, List<OrderItem> items) {
        if (order == null) {
            return null;
        }
        List<OrderItemRespVO> itemVOs = items == null
                ? Collections.emptyList()
                : itemsToItemVOs(items);

        return OrderAdminRespVO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .userId(order.getUserId())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .receiverProvince(order.getReceiverProvince())
                .receiverCity(order.getReceiverCity())
                .receiverDistrict(order.getReceiverDistrict())
                .receiverAddress(order.getReceiverAddress())
                .totalAmount(order.getTotalAmount())
                .freightAmount(order.getFreightAmount())
                .discountAmount(order.getDiscountAmount())
                .payAmount(order.getPayAmount())
                .status(order.getStatus())
                .statusDesc(OrderStatus.getDescriptionByCode(order.getStatus()))
                .remark(order.getRemark())
                .createTime(order.getCreateTime())
                .updateTime(order.getUpdateTime())
                .items(itemVOs)
                .build();
    }

    /**
     * Order -> OrderAdminRespVO（详情场景，含商品列表 + 支付信息）
     */
    default OrderAdminRespVO toDetailVO(Order order, List<OrderItem> items, PaymentRespVO payment) {
        OrderAdminRespVO vo = toListVO(order, items);
        if (vo != null) {
            vo.setPayment(payment);
        }
        return vo;
    }
}
