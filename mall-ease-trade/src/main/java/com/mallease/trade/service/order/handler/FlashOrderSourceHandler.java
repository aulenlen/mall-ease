package com.mallease.trade.service.order.handler;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.FlashRestoreReqDTO;
import com.mallease.common.enums.OrderSource;
import com.mallease.common.exception.ApiException;
import com.mallease.trade.dal.entity.Order;
import com.mallease.trade.dal.entity.OrderItem;
import com.mallease.trade.feign.product.MarketingFlashFeignClient;
import com.mallease.trade.service.order.support.OrderCancelSupport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
@Slf4j
public class FlashOrderSourceHandler implements OrderSourceHandler {
    private final MarketingFlashFeignClient flashFeignClient;
    private final OrderCancelSupport orderCancelSupport;

    @Override
    public OrderSource source() {
        return OrderSource.FLASH;
    }

    @Override
    public boolean cancelPending(Order order, boolean restoreCart) {
        restoreFlashOrderAndMarkCancelled(order, "取消秒杀订单失败");
        return true;
    }

    @Override
    public void confirmPaid(Order order) {

    }

    @Override
    public void closeExpired(List<Order> orders) {
        for (Order order : orders) {
            try {
                restoreFlashOrderAndMarkCancelled(order, "关闭超时秒杀订单失败");
            } catch (ApiException ex) {
                log.warn("关闭超时秒杀订单失败，orderNo={}", order.getOrderNo(), ex);
            }
        }
    }

    @Override
    public void retryConfirm(List<Order> orders) {

    }

    @Override
    public void retryRelease(List<Order> orders, boolean restoreCart, boolean needTransition) {

    }

    private void restoreFlashOrderAndMarkCancelled(Order order, String errorMessage) {
        if (order == null || order.getId() == null) {
            throw new ApiException("订单不存在");
        }

        OrderItem orderItem = orderCancelSupport.loadSingleOrderItem(order);

        try {
            R<Long> result = flashFeignClient.restoreStock(
                    FlashRestoreReqDTO.builder()
                            .sessionId(order.getFlashSessionId())
                            .skuId(orderItem.getSkuId())
                            .userId(order.getUserId())
                            .quantity(orderItem.getQuantity())
                            .build());

            if (result != null && result.isSuccess() && Objects.equals(result.getData(), 1L)) {
                markFlashOrderCancelled(order);
                return;
            }
        } catch (Exception ex) {
            log.warn("秒杀库存回补失败，orderNo={}", order.getOrderNo(), ex);
        }

        throw new ApiException(errorMessage);
    }

    private void markFlashOrderCancelled(Order order) {
        orderCancelSupport.markCancelled(order, com.mallease.common.enums.OrderStockStatus.RELEASED.getCode());
    }
}
