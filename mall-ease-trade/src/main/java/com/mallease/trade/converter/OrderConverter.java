package com.mallease.trade.converter;

import com.mallease.common.dto.remote.OrderDTO;
import com.mallease.trade.model.aggregate.OrderAggregate;
import com.mallease.trade.model.data.entity.Order;
import com.mallease.trade.model.data.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 订单对象转换器
 *
 * @author: Aulen
 * @create: 2026-01-30
 */
@Mapper(componentModel = "spring")
public interface OrderConverter {

    // ==================== DTO → Entity ====================

    /**
     * OrderDTO → Order Entity
     */
    Order dtoToOrder(OrderDTO dto);

    /**
     * OrderItemDTO → OrderItem Entity
     */
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "orderNo", ignore = true)
    OrderItem itemDtoToEntity(OrderDTO.OrderItemDTO dto);

    /**
     * OrderItemDTO List → OrderItem Entity List
     */
    List<OrderItem> itemDtoListToEntityList(List<OrderDTO.OrderItemDTO> dtoList);

    // ==================== Entity → DTO ====================

    /**
     * Order Entity → OrderDTO
     */
    OrderDTO orderToDto(Order order);

    /**
     * OrderItem Entity → OrderItemDTO
     */
    OrderDTO.OrderItemDTO itemEntityToDto(OrderItem item);

    /**
     * OrderItem Entity List → OrderItemDTO List
     */
    List<OrderDTO.OrderItemDTO> itemEntityListToDtoList(List<OrderItem> items);

    // ==================== Aggregate ↔ DTO ====================

    /**
     * OrderDTO → OrderAggregate
     */
    default OrderAggregate dtoToAggregate(OrderDTO dto) {
        if (dto == null) {
            return null;
        }
        return OrderAggregate.builder()
                .order(dtoToOrder(dto))
                .items(itemDtoListToEntityList(dto.getItems()))
                .build();
    }

    /**
     * OrderAggregate → OrderDTO
     */
    default OrderDTO aggregateToDto(OrderAggregate aggregate) {
        if (aggregate == null) {
            return null;
        }
        OrderDTO dto = orderToDto(aggregate.getOrder());
        dto.setItems(itemEntityListToDtoList(aggregate.getItems()));
        return dto;
    }

    /**
     * OrderAggregate List → OrderDTO List
     */
    default List<OrderDTO> aggregateListToDtoList(List<OrderAggregate> aggregates) {
        if (aggregates == null) {
            return null;
        }
        return aggregates.stream()
                .map(this::aggregateToDto)
                .toList();
    }
}
