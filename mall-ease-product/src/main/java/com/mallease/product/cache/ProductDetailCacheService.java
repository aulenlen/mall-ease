package com.mallease.product.cache;

import com.mallease.common.service.RedisService;
import com.mallease.product.constant.ProductCacheKeys;
import com.mallease.product.model.data.cache.SpuCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 商品详情缓存实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductDetailCacheService {

    private final RedisService redisService;

    public SpuCache get(Long spuId) {
        if (spuId == null) {
            return null;
        }

        Object value = redisService.get(ProductCacheKeys.spuDetailKey(spuId));
        if (value == null) {
            return null;
        }
        if (value instanceof SpuCache spuCache) {
            return spuCache;
        }

        log.warn("商品详情缓存类型不匹配，spuId: {}, actualType: {}", spuId, value.getClass().getName());
        return null;
    }

    public Map<Long, SpuCache> batchGet(List<Long> spuIds) {
        if (spuIds == null || spuIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> keys = spuIds.stream()
                .map(ProductCacheKeys::spuDetailKey)
                .toList();
        List<Object> values = redisService.multiGet(keys);
        Map<Long, SpuCache> result = new HashMap<>();
        for (int i = 0; i < spuIds.size(); i++) {
            Object value = values != null && i < values.size() ? values.get(i) : null;
            if (value instanceof SpuCache spuCache) {
                result.put(spuIds.get(i), spuCache);
            }
        }
        return result;
    }

    public void batchSet(List<SpuCache> spuCaches) {
        if (spuCaches == null || spuCaches.isEmpty()) {
            return;
        }

        Map<String, Object> cacheMap = spuCaches.stream()
                .filter(spuCache -> spuCache != null && spuCache.getId() != null)
                .collect(Collectors.toMap(
                        spuCache -> ProductCacheKeys.spuDetailKey(spuCache.getId()),
                        Function.identity(),
                        (left, right) -> left
                ));
        if (cacheMap.isEmpty()) {
            return;
        }

        redisService.multiSetWithExpire(cacheMap, ProductCacheKeys.spuDetailTtlSeconds());
        log.info("批量写入商品详情缓存 {} 条", cacheMap.size());
    }

    public void delete(Long spuId) {
        if (spuId == null) {
            return;
        }
        redisService.del(ProductCacheKeys.spuDetailKey(spuId));
    }

    public void deleteBatch(List<Long> spuIds) {
        if (spuIds == null || spuIds.isEmpty()) {
            return;
        }
        List<String> keys = spuIds.stream()
                .map(ProductCacheKeys::spuDetailKey)
                .toList();
        redisService.del(keys);
    }
}
