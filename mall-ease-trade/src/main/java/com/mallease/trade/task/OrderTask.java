package com.mallease.trade.task;

import com.mallease.trade.model.data.entity.Order;
import com.mallease.trade.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTask {

    private final OrderService orderService;

    /**
     * 每分钟扫描需要处理的订单：
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void processOrders() {
        log.info("进入Order定时任务...");

        List<Order> orders = orderService.listNeedProcess();
        if (orders == null || orders.isEmpty()) {
            return;
        }

        List<String> orderNos = orders.stream().map(Order::getOrderNo).toList();

        orderService.cancelByOrderNos(orderNos);

        log.info("定时任务处理完成，共处理 {} 个订单", orderNos.size());
    }
}
