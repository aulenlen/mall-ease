package com.mallease.trade.service.impl;

import com.mallease.common.enums.OrderStatus;
import com.mallease.common.exception.ApiException;
import com.mallease.trade.dao.OrderDao;
import com.mallease.trade.dao.OrderItemDao;
import com.mallease.trade.model.aggregate.OrderAggregate;
import com.mallease.trade.model.data.entity.Order;
import com.mallease.trade.model.data.entity.OrderItem;
import com.mallease.trade.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String create(OrderAggregate aggregate) {

        Order order = aggregate.getOrder();
        List<OrderItem> orderItems = aggregate.getItems();

        if (orderItems == null || orderItems.isEmpty()) {
            throw new ApiException("未选择商品！");
        }

        orderDao.insert(order);

        orderItems.forEach(item -> {
            item.setOrderId(order.getId());
            item.setOrderNo(order.getOrderNo());
        });

        orderItemDao.insertBatch(orderItems);

        return order.getOrderNo();
    }

    @Override
    public OrderAggregate getByOrderNo(String orderNo) {
        Order order = orderDao.selectByOrderNo(orderNo);
        List<OrderItem> orderItems = orderItemDao.selectByOrderNo(orderNo);
        return OrderAggregate.builder().order(order).items(orderItems).build();
    }

    @Override
    public List<OrderAggregate> listByUserId(Long userId, Integer status) {
        List<Order> orders = orderDao.listByUserId(userId, status);
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> orderIds = orders.stream().map(Order::getId).toList();
        List<OrderItem> orderItems = orderItemDao.selectByOrderIds(orderIds);
        Map<Long, List<OrderItem>> itemsMap = orderItems.stream().collect(Collectors.groupingBy(OrderItem::getOrderId));

        ArrayList<OrderAggregate> orderAggregates = new ArrayList<>();
        orders.forEach(order -> {
            OrderAggregate orderAggregate = new OrderAggregate();
            orderAggregate.setOrder(order);
            List<OrderItem> orderItemList = itemsMap.get(order.getId());
            orderAggregate.setItems(orderItemList);
            orderAggregates.add(orderAggregate);
        });

        return orderAggregates;
    }

    @Override
    public boolean cancel(String orderNo, Long userId) {
        Order order = orderDao.selectByOrderNo(orderNo);
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new ApiException("无权操作此订单");
        }
        return orderDao.updateStatusByOrderNo(orderNo, OrderStatus.CANCELLED.getCode()) > 0;
    }

    private String orderNumberGenerator(Long userId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String timestamp = LocalDateTime.now().format(formatter);
        String userIdSuffix = String.format("%04d", userId % 10000);
        String randomSuffix = String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
        return timestamp + userIdSuffix + randomSuffix;
    }
}
