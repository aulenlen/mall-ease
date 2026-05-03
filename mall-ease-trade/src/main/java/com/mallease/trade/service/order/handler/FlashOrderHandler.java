package com.mallease.trade.service.order.handler;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.FlashRestoreReqDTO;
import com.mallease.common.enums.OrderSource;
import com.mallease.common.exception.ApiException;
import com.mallease.trade.dal.entity.Order;
import com.mallease.trade.dal.entity.OrderItem;
import com.mallease.trade.feign.product.MarketingFlashFeignClient;
import com.mallease.trade.service.order.handler.bo.OrderContext;
import com.mallease.trade.service.order.handler.enums.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class FlashOrderHandler implements OrderHandler {

    private static final Set<OrderEvent> SUPPORTED_EVENTS = Set.of(
            OrderEvent.BEFORE_CREATE,
            OrderEvent.AFTER_CANCELLED,
            OrderEvent.AFTER_EXPIRED,
            OrderEvent.RETRY_RELEASE
    );

    private final MarketingFlashFeignClient flashFeignClient;

    @Override
    public boolean supports(OrderContext context) {
        return context != null
                && context.getOrderSource() == OrderSource.FLASH
                && SUPPORTED_EVENTS.contains(context.getEvent());
    }

    @Override
    public int order() {
        return 100;
    }

    @Override
    public void beforeOrderCreate(OrderContext context) {
        Order order = context.getOrder();
        if (order == null) {
            throw new ApiException("秒杀订单不能为空");
        }
        if (context.getFlashSessionId() == null || order.getFlashSessionId() == null) {
            throw new ApiException("秒杀场次不能为空");
        }
        if (context.getFlashProductId() == null || order.getFlashProductId() == null) {
            throw new ApiException("秒杀商品不能为空");
        }
        if (context.getOrderItems() == null || context.getOrderItems().isEmpty()) {
            throw new ApiException("秒杀订单明细不能为空");
        }
        OrderItem orderItem = context.getOrderItems().get(0);
        if (orderItem.getSkuId() == null) {
            throw new ApiException("秒杀 SKU 不能为空");
        }
        if (orderItem.getQuantity() == null || orderItem.getQuantity() <= 0) {
            throw new ApiException("秒杀商品数量不正确");
        }
    }

    @Override
    public void afterOrderCancelled(OrderContext context) {
        restoreFlashStock(context, "取消秒杀订单失败");
    }

    @Override
    public void afterOrderExpired(OrderContext context) {
        restoreFlashStock(context, "关闭超时秒杀订单失败");
    }

    @Override
    public void retryRelease(OrderContext context) {
        restoreFlashStock(context, "重试回补秒杀库存失败");
    }

    private void restoreFlashStock(OrderContext context, String errorMessage) {
        Order order = context.getOrder();
        if (order == null || order.getId() == null) {
            throw new ApiException("订单不存在");
        }

        OrderItem orderItem = resolveSingleOrderItem(context);

        try {
            R<Long> result = flashFeignClient.restoreStock(
                    FlashRestoreReqDTO.builder()
                            .sessionId(order.getFlashSessionId())
                            .skuId(orderItem.getSkuId())
                            .userId(order.getUserId())
                            .quantity(orderItem.getQuantity())
                            .build());

            if (result != null && result.isSuccess() && Objects.equals(result.getData(), 1L)) {
                return;
            }
        } catch (Exception ex) {
            log.warn("秒杀库存回补失败，orderNo={}", order.getOrderNo(), ex);
        }

        throw new ApiException(errorMessage);
    }

    private OrderItem resolveSingleOrderItem(OrderContext context) {
        if (context.getOrderItems() == null || context.getOrderItems().isEmpty()) {
            throw new ApiException("订单明细不存在");
        }
        return context.getOrderItems().get(0);
    }
}
