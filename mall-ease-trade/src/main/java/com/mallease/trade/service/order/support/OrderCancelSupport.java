package com.mallease.trade.service.order.support;

import com.mallease.common.enums.OrderStatus;
import com.mallease.common.enums.PaymentStatus;
import com.mallease.common.exception.ApiException;
import com.mallease.trade.dal.entity.Order;
import com.mallease.trade.dal.entity.OrderItem;
import com.mallease.trade.dal.mapper.OrderDao;
import com.mallease.trade.dal.mapper.OrderItemDao;
import com.mallease.trade.dal.mapper.PaymentOrderDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OrderCancelSupport {

    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;
    private final PaymentOrderDao paymentOrderDao;
    private final TransactionTemplate transactionTemplate;

    public OrderItem loadSingleOrderItem(Order order) {
        if (order == null || order.getId() == null) {
            throw new ApiException("订单不存在");
        }

        List<OrderItem> orderItems = orderItemDao.selectByOrderId(order.getId());
        if (orderItems == null || orderItems.isEmpty()) {
            throw new ApiException("订单明细不存在");
        }
        return orderItems.get(0);
    }

    public void markCancelled(Order order, Integer stockProcessStatus) {
        if (order == null || order.getOrderNo() == null) {
            throw new ApiException("订单不存在");
        }
        markCancelled(List.of(order.getOrderNo()), stockProcessStatus, null);
    }

    public void markCancelled(List<String> orderNos, Integer stockProcessStatus, Runnable afterCancelled) {
        List<String> validOrderNos = normalizeOrderNos(orderNos);
        if (validOrderNos.isEmpty()) {
            return;
        }

        transactionTemplate.executeWithoutResult(status -> {
            orderDao.batchUpdateStatusByOrderNos(
                    validOrderNos,
                    OrderStatus.CANCELLED.getCode(),
                    stockProcessStatus
            );
            closePendingPaymentOrders(validOrderNos);
            if (afterCancelled != null) {
                afterCancelled.run();
            }
        });
    }

    public void closePendingPaymentOrders(List<String> orderNos) {
        List<String> validOrderNos = normalizeOrderNos(orderNos);
        if (validOrderNos.isEmpty()) {
            return;
        }
        paymentOrderDao.closeByOrderNos(validOrderNos, PaymentStatus.CLOSED.getCode());
    }

    private List<String> normalizeOrderNos(List<String> orderNos) {
        if (orderNos == null || orderNos.isEmpty()) {
            return List.of();
        }
        return orderNos.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }
}
