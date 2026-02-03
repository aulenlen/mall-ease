package com.mallease.product.task;

import com.mallease.product.service.SkuStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 库存释放定时任务（处理过期未支付的预占库存）
 *
 * @author: Aulen
 * @create: 2026-02-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockReleaseTask {

    private final SkuStockService skuStockService;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void releaseExpiredStock() {
        log.info("释放过期库存定时任务开始");
        int releasedCount = skuStockService.releaseExpiredReservations(1000);
        log.info("释放过期库存定时任务结束，释放记录数={}", releasedCount);
    }
}
