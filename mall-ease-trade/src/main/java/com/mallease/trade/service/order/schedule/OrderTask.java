package com.mallease.trade.service.order.schedule;

import com.mallease.trade.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTask {

    private final OrderService orderService;

    @Scheduled(cron = "0/10 * * * * ?")
    public void recoverLockingOrders() {
        orderService.recoverLockingOrders(100);
    }

    @Scheduled(cron = "0/15 * * * * ?")
    public void closeExpiredPendingOrders() {
        orderService.closeExpiredPendingOrders(100);
    }

    @Scheduled(cron = "0/18 * * * * ?")
    public void retryPayConfirmingOrders() {
        orderService.retryPayConfirmingOrders(100);
    }

    @Scheduled(cron = "0/20 * * * * ?")
    public void retryReleasingOrders() {
        orderService.retryReleasingOrders(100);
    }
}
