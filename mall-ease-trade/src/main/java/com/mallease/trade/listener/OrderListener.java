package com.mallease.trade.listener;

import com.mallease.common.enums.StockReleaseStatus;
import com.mallease.trade.evnent.OrderCancelledEvent;
import com.mallease.trade.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OrderListener {
    private final OrderService orderService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCancelled(OrderCancelledEvent event) {

        List<String> orderNos = event.getOrderNos();
        Map<String, StockReleaseStatus> resultMap = orderService.tryReleaseStock(orderNos);

        List<String> released = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        resultMap.forEach((orderNo, status) -> {
            if (status == StockReleaseStatus.RELEASED) {
                released.add(orderNo);
            } else {
                failed.add(orderNo);
            }
        });

        if (!released.isEmpty()) {
            orderService.orderReleaseSuccess(released);
        }
        if (!failed.isEmpty()) {
            orderService.orderReleaseFailed(failed);
        }
    }
}
