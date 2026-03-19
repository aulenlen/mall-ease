package com.mallease.product.task;

import com.mallease.common.service.RedisService;
import com.mallease.product.constant.ProductCacheKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * PMS 缓存清理任务
 * 扫描并修复未设置过期时间的异常缓存
 *
 * @author: Aulen
 * @create: 2025-11-25
 */
@Slf4j
@Component
public class CacheCleanupTask {

    @Autowired
    private RedisService redisService;

    /**
     * 每天凌晨3点扫描清理异常缓存
     * 检测规则：TTL = -1（永久有效）
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupOrphanedCache() {
        log.info("开始清理PMS异常缓存任务");
        try {
            // 扫描所有 sku:stock:* 的 key
            Set<String> keys = redisService.scan(ProductCacheKeys.spuStockPattern());
            int cleaned = 0;
            for (String key : keys) {
                Long ttl = redisService.getExpire(key);

                // 发现未设置过期时间的异常数据
                if (ttl != null && ttl == -1) {
                    // 补救设置过期时间（而非直接删除，避免误伤）
                    redisService.expire(key, ProductCacheKeys.spuStockTtlSeconds());
                    log.warn("修复未设置过期时间的缓存: {}", key);
                    cleaned++;
                }
            }

            if (cleaned > 0) {
                log.info("清理任务完成，修复 {} 个异常缓存", cleaned);
            } else {
                log.info("清理任务完成，无异常缓存");
            }

        } catch (Exception e) {
            log.error("清理任务执行失败", e);
        }
    }
}
