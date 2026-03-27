package com.mallease.trade.convert.order;

import com.mallease.common.enums.OrderStatus;
import com.mallease.trade.controller.portal.order.vo.OrderSubmitReqVO;
import com.mallease.trade.service.order.model.OrderAggregate;
import com.mallease.trade.controller.portal.order.vo.OrderItemRespVO;
import com.mallease.trade.controller.portal.order.vo.OrderRespVO;
import com.mallease.trade.dal.entity.Order;
import com.mallease.trade.dal.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.mallease.trade.constant.OrderConstant.PAYMENT_TIMEOUT_MINUTES;

/**
 * 订单对象转换器
 *
 * @author: Aulen
 * @create: 2026-01-30
 */
@Mapper(componentModel = "spring")
public interface OrderConvert {

    /**
     * OrderSubmitReqVO -> Order Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderNo", ignore = true)
    @Mapping(target = "requestId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "freightAmount", ignore = true)
    @Mapping(target = "discountAmount", ignore = true)
    @Mapping(target = "payAmount", ignore = true)
    @Mapping(target = "payExpireTime", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "stockProcessStatus", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    Order reqVOToEntity(OrderSubmitReqVO reqVO);

    /**
     * OrderItem -> OrderItemRespVO
     */
    OrderItemRespVO itemToItemVO(OrderItem item);

    /**
     * OrderItem List -> OrderItemRespVO List
     */
    List<OrderItemRespVO> itemsToItemVOs(List<OrderItem> items);

    /**
     * OrderAggregate -> OrderRespVO
     */
    default OrderRespVO aggregateToVO(OrderAggregate aggregate) {
        if (aggregate == null || aggregate.getOrder() == null) {
            return null;
        }

        Order order = aggregate.getOrder();
        Integer totalQuantity = resolveTotalQuantity(aggregate);
        List<OrderItemRespVO> itemVOs = aggregate.getItems() == null
                ? Collections.emptyList()
                : itemsToItemVOs(aggregate.getItems());

        return OrderRespVO.builder()
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
                .payExpireTime(resolvePayExpireTime(order))
                .status(order.getStatus())
                .statusDesc(OrderStatus.getDescriptionByCode(order.getStatus()))
                .remark(order.getRemark())
                .createTime(order.getCreateTime())
                .totalQuantity(totalQuantity)
                .items(itemVOs)
                .build();
    }

    /**
     * OrderAggregate List -> OrderRespVO List
     */
    default List<OrderRespVO> aggregatesToVOs(List<OrderAggregate> aggregates) {
        if (aggregates == null) {
            return Collections.emptyList();
        }
        return aggregates.stream().map(this::aggregateToVO).toList();
    }

    private Integer resolveTotalQuantity(OrderAggregate aggregate) {
        if (aggregate.getTotalQuantity() != null) {
            return aggregate.getTotalQuantity();
        }
        if (aggregate.getItems() == null || aggregate.getItems().isEmpty()) {
            return 0;
        }
        return aggregate.getItems().stream()
                .map(OrderItem::getQuantity)
                .filter(quantity -> quantity != null)
                .reduce(0, Integer::sum);
    }

    private LocalDateTime resolvePayExpireTime(Order order) {
        if (order.getPayExpireTime() != null) {
            return order.getPayExpireTime();
        }
        if (order.getCreateTime() != null) {
            return order.getCreateTime().plusMinutes(PAYMENT_TIMEOUT_MINUTES);
        }
        return null;
    }
}
