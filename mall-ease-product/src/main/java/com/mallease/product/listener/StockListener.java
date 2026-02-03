package com.mallease.product.listener;

import com.mallease.product.model.data.entity.StockReservation;
import com.mallease.product.service.SkuStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SkuListener {
    private final SkuStockService skuStockService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void lockStockEvent(List<StockReservation> stockReservations) {
        for (StockReservation stockReservation : stockReservations) {
            Long spuId = stockReservation.getSpuId();
            Long skuId = stockReservation.getSkuId();
            Integer quantity = stockReservation.getQuantity();
            skuStockService.deductStock(spuId, skuId, quantity);
            log.info("redis锁定库存: spuId {} skuId {} quantity {}", spuId, skuId, quantity);
        }
    }
}
