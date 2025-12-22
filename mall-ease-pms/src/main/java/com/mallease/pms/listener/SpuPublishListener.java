package com.mallease.pms.listener;

import com.mallease.pms.event.SpuPublishEvent;
import com.mallease.pms.service.SpuCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * SPU 上下架缓存监听器
 */
@Slf4j
@Component
public class SpuPublishListener {
    @Autowired
    private SpuCacheService spuCacheService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishEvent(SpuPublishEvent event) {

        // 处理缓存（缓存失败不影响业务结果）
        try {
            if (event.getPublishStatus() == 1) {
                // 上架：预热缓存
                log.info("开始预热SPU缓存，商品数量: {}", event.getSpuIds().size());
                spuCacheService.warmUpBatch(event.getSpuIds());
                log.info("SPU缓存预热完成");
            } else {
                // 下架：清除缓存
                spuCacheService.evictBatch(event.getSpuIds());
                log.info("清除下架SPU缓存，数量: {}", event.getSpuIds().size());
            }
        } catch (Exception e) {
            log.error("缓存操作失败，spuIds: {}", event.getSpuIds(), e);
        }
    }
}
