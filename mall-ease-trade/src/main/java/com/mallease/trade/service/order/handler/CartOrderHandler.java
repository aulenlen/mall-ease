package com.mallease.trade.service.order.handler;

import com.mallease.common.enums.OrderSource;
import com.mallease.common.service.TypedRedisService;
import com.mallease.trade.constant.OrderCacheKeys;
import com.mallease.trade.controller.portal.order.vo.OrderConfirmRespVO;
import com.mallease.trade.controller.portal.order.vo.OrderItemRespVO;
import com.mallease.trade.dal.entity.CartItem;
import com.mallease.trade.service.cart.CartService;
import com.mallease.trade.service.order.handler.bo.OrderContext;
import com.mallease.trade.service.order.handler.enums.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CartOrderHandler implements OrderHandler {
    private final CartService cartService;
    private final TypedRedisService typedRedisService;

    @Override
    public boolean supports(OrderContext context) {
        return context != null
                && context.getOrderSource() == OrderSource.NORMAL
                && context.getEvent() == OrderEvent.AFTER_CREATED;
    }

    @Override
    public int order() {
        return 900;
    }

    @Override
    public void afterOrderCreated(OrderContext context) {
        OrderConfirmRespVO snapshot = (OrderConfirmRespVO) context.getSourceSnapshot();

        List<Long> deleteIds = snapshot.getItems().stream()
                .map(OrderItemRespVO::getSkuId)
                .filter(Objects::nonNull)
                .distinct()
                .flatMap(skuId -> cartService.listByUserId(context.getUserId()).stream()
                        .filter(cart -> Objects.equals(cart.getSkuId(), skuId))
                        .map(CartItem::getId))
                .filter(Objects::nonNull)
                .toList();

        if (!deleteIds.isEmpty()) {
            cartService.deleteBatch(deleteIds);
        }

        typedRedisService.delete(OrderCacheKeys.snapshotKey(context.getUserId(), context.getRequestId()));
    }
}
