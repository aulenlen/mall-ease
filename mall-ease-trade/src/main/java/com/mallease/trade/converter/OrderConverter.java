package com.mallease.trade.converter;

import com.mallease.common.enums.OrderStatus;
import com.mallease.trade.model.aggregate.OrderAggregate;
import com.mallease.trade.model.client.cmd.SubmitOrderCmd;
import com.mallease.trade.model.client.vo.OrderItemVO;
import com.mallease.trade.model.client.vo.OrderVO;
import com.mallease.trade.model.data.entity.Order;
import com.mallease.trade.model.data.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;

/**
 * 订单对象转换器
 *
 * @author: Aulen
 * @create: 2026-01-30
 */
@Mapper(componentModel = "spring")
public interface OrderConverter {

    /**
     * SubmitOrderCmd -> Order Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderNo", ignore = true)
    @Mapping(target = "requestId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "freightAmount", ignore = true)
    @Mapping(target = "discountAmount", ignore = true)
    @Mapping(target = "payAmount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "stockReleaseStatus", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    Order cmdToEntity(SubmitOrderCmd cmd);

    /**
     * OrderItem -> OrderItemVO
     */
    OrderItemVO itemToItemVO(OrderItem item);

    /**
     * OrderItem List -> OrderItemVO List
     */
    List<OrderItemVO> itemsToItemVOs(List<OrderItem> items);

    /**
     * OrderAggregate -> OrderVO
     */
    default OrderVO aggregateToVO(OrderAggregate aggregate) {
        if (aggregate == null || aggregate.getOrder() == null) {
            return null;
        }
        Order order = aggregate.getOrder();
        List<OrderItemVO> itemVOs = aggregate.getItems() == null
                ? Collections.emptyList()
                : itemsToItemVOs(aggregate.getItems());

        return OrderVO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
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
                .items(itemVOs)
                .build();
    }

    /**
     * OrderAggregate List -> OrderVO List
     */
    default List<OrderVO> aggregatesToVOs(List<OrderAggregate> aggregates) {
        if (aggregates == null) {
            return Collections.emptyList();
        }
        return aggregates.stream().map(this::aggregateToVO).toList();
    }
}
