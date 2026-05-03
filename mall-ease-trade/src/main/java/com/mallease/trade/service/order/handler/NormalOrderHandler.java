package com.mallease.trade.service.order.handler;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.StockLockDTO;
import com.mallease.common.enums.OrderSource;
import com.mallease.common.exception.ApiException;
import com.mallease.trade.dal.entity.OrderItem;
import com.mallease.trade.feign.product.ProductFeignClient;
import com.mallease.trade.service.order.handler.bo.OrderContext;
import com.mallease.trade.service.order.handler.enums.OrderEvent;
import com.mallease.trade.service.order.handler.exception.StockLockFailedException;
import com.mallease.trade.service.order.handler.exception.StockLockUnknownException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NormalOrderHandler implements OrderHandler {

    private final ProductFeignClient productFeignClient;

    private static final Set<OrderEvent> SUPPORTED_EVENTS = Set.of(
            OrderEvent.BEFORE_CREATE,
            OrderEvent.AFTER_PERSISTED,
            OrderEvent.AFTER_PAID,
            OrderEvent.AFTER_CANCELLED,
            OrderEvent.AFTER_EXPIRED,
            OrderEvent.RETRY_CONFIRM,
            OrderEvent.RETRY_RELEASE
    );

    @Override
    public boolean supports(OrderContext context) {
        return context != null
                && context.getOrderSource() == OrderSource.NORMAL
                && SUPPORTED_EVENTS.contains(context.getEvent());
    }

    @Override
    public int order() {
        return 100;
    }

    @Override
    public void beforeOrderCreate(OrderContext context) {
        if (context.getOrderItems() == null || context.getOrderItems().isEmpty()) {
            throw new ApiException("未选择任何商品");
        }

        for (OrderItem item : context.getOrderItems()) {
            if (item.getSkuId() == null) {
                throw new ApiException("商品 SKU 不能为空");
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new ApiException("商品数量不正确");
            }
        }
    }

    @Override
    public void afterOrderPersisted(OrderContext context) {

        Map<Long, Integer> lockStocks = context.getOrderItems().stream().collect(
                Collectors.toMap(
                        OrderItem::getSkuId,
                        OrderItem::getQuantity,
                        Integer::sum,
                        LinkedHashMap::new
                ));

        StockLockDTO lockDTO = StockLockDTO.builder()
                .requestId(context.getRequestId())
                .orderNo(context.getOrder().getOrderNo())
                .lockStocks(lockStocks)
                .expireTime(context.getPayExpireTime())
                .build();

        R<Void> lockResult;
        try {
            lockResult = productFeignClient.lockStock(lockDTO);
        } catch (Exception ex) {
            context.setResourceReservation(lockDTO);
            throw new StockLockUnknownException("库存锁定结果未知", ex);
        }

        if (lockResult == null || !lockResult.isSuccess()) {
            context.setResourceReservation(lockDTO);
            String message = lockResult != null && lockResult.getMessage() != null
                    ? lockResult.getMessage()
                    : "库存锁定失败";
            throw new StockLockFailedException(message);
        }

        context.setResourceReservation(lockDTO);
    }

    @Override
    public void afterOrderPaid(OrderContext context) {
        R<Void> result = productFeignClient.confirmStock(context.getOrder().getOrderNo());
        if (result == null || !result.isSuccess()) {
            throw new ApiException(result != null ? result.getMessage() : "确认扣减库存失败");
        }
    }

    @Override
    public void afterOrderCancelled(OrderContext context) {
        unlock(context);
    }

    @Override
    public void afterOrderExpired(OrderContext context) {
        unlock(context);
    }

    @Override
    public void retryConfirm(OrderContext context) {
        afterOrderPaid(context);
    }

    @Override
    public void retryRelease(OrderContext context) {
        unlock(context);
    }

    private void unlock(OrderContext context) {
        R<List<String>> result = productFeignClient.unlock(List.of(context.getOrder().getOrderNo()));
        if (result == null || !result.isSuccess()) {
            throw new ApiException(result != null ? result.getMessage() : "释放库存失败");
        }
        if (result.getData() != null && result.getData().contains(context.getOrder().getOrderNo())) {
            throw new ApiException("释放库存失败");
        }
    }
}
