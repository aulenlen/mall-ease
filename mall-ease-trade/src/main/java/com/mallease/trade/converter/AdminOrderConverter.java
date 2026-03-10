package com.mallease.trade.converter;

import com.mallease.common.enums.OrderStatus;
import com.mallease.trade.model.client.vo.AdminOrderVO;
import com.mallease.trade.model.client.vo.OrderItemVO;
import com.mallease.trade.model.client.vo.PaymentVO;
import com.mallease.trade.model.data.entity.Order;
import com.mallease.trade.model.data.entity.OrderItem;
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
public interface AdminOrderConverter {

    /**
     * OrderItem -> OrderItemVO
     */
    OrderItemVO itemToItemVO(OrderItem item);

    /**
     * OrderItem List -> OrderItemVO List
     */
    List<OrderItemVO> itemsToItemVOs(List<OrderItem> items);

    /**
     * Order -> AdminOrderVO（列表场景，含商品列表）
     */
    default AdminOrderVO toListVO(Order order, List<OrderItem> items) {
        if (order == null) {
            return null;
        }
        List<OrderItemVO> itemVOs = items == null
                ? Collections.emptyList()
                : itemsToItemVOs(items);

        return AdminOrderVO.builder()
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
     * Order -> AdminOrderVO（详情场景，含商品列表 + 支付信息）
     */
    default AdminOrderVO toDetailVO(Order order, List<OrderItem> items, PaymentVO payment) {
        AdminOrderVO vo = toListVO(order, items);
        if (vo != null) {
            vo.setPayment(payment);
        }
        return vo;
    }
}
