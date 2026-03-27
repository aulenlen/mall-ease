package com.mallease.trade.service.order.cache;

import com.mallease.common.service.RedisService;
import com.mallease.trade.constant.OrderCacheKeys;
import com.mallease.trade.controller.portal.order.vo.OrderConfirmRespVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 订单确认快照缓存实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderSnapshotCacheService {

    private final RedisService redisService;

    public void save(String requestId, OrderConfirmRespVO snapshot) {
        if (requestId == null || requestId.isBlank() || snapshot == null) {
            return;
        }
        redisService.set(OrderCacheKeys.snapshotKey(requestId), snapshot, OrderCacheKeys.snapshotTtlSeconds());
    }

    public OrderConfirmRespVO get(String requestId) {
        if (requestId == null || requestId.isBlank()) {
            return null;
        }
        Object cached = redisService.get(OrderCacheKeys.snapshotKey(requestId));
        if (cached == null) {
            return null;
        }
        if (cached instanceof OrderConfirmRespVO orderConfirmVO) {
            return orderConfirmVO;
        }

        log.warn("订单快照缓存类型不匹配，requestId: {}, actualType: {}", requestId, cached.getClass().getName());
        return null;
    }

    public void delete(String requestId) {
        if (requestId == null || requestId.isBlank()) {
            return;
        }
        redisService.del(OrderCacheKeys.snapshotKey(requestId));
    }
}
