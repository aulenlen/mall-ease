package com.mallease.product.listener;

import com.mallease.product.event.StockLockRollbackEvent;
import com.mallease.product.event.StockReleaseEvent;
import com.mallease.product.model.data.entity.StockReservation;
import com.mallease.product.service.SpuCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 库存事件监听器（事务提交后更新 Redis 缓存）
 *
 * @author: Aulen
 * @create: 2026-02-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockListener {

    private final SpuCacheService spuCacheService;

    /**
     * 监听库存释放事件，更新 Redis 缓存
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleStockRelease(StockReleaseEvent event) {
        for (StockReservation reservation : event.getReservations()) {
            Long spuId = reservation.getSpuId();
            Long skuId = reservation.getSkuId();
            Integer quantity = reservation.getQuantity();
            spuCacheService.increaseStock(spuId, skuId, quantity);
            log.info("Redis 库存释放成功: spuId={}, skuId={}, quantity={}", spuId, skuId, quantity);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleLockStockRollback(StockLockRollbackEvent event) {
        for (StockReservation reservation : event.getDeductedList()) {
            Long spuId = reservation.getSpuId();
            Long skuId = reservation.getSkuId();
            Integer quantity = reservation.getQuantity();
            spuCacheService.increaseStock(spuId, skuId, quantity);
            log.info("回滚 Redis 库存成功，spuId={}, skuId={}, quantity={}", spuId, skuId, quantity);
        }
    }
}
