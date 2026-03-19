package com.mallease.product.cache;

import com.mallease.common.dto.remote.SkuStockQueryDTO;
import com.mallease.common.service.RedisService;
import com.mallease.product.constant.ProductCacheKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 商品库存缓存实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductStockCacheService {

    public static final long STOCK_NOT_ENOUGH = -1L;
    public static final long STOCK_CACHE_MISS = -2L;


    private final RedisService redisService;

    public Integer getStock(Long spuId, Long skuId) {
        if (spuId == null || skuId == null) {
            return null;
        }
        return toInteger(redisService.hGet(ProductCacheKeys.spuStockKey(spuId), String.valueOf(skuId)));
    }

    public Map<Long, Integer> getStockBySpu(Long spuId) {
        if (spuId == null) {
            return Collections.emptyMap();
        }

        Map<Object, Object> hashMap = redisService.hGetAll(ProductCacheKeys.spuStockKey(spuId));
        if (hashMap == null || hashMap.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, Integer> result = new LinkedHashMap<>();
        hashMap.forEach((key, value) -> {
            Long skuId = toLong(key);
            Integer stock = toInteger(value);
            if (skuId != null && stock != null) {
                result.put(skuId, stock);
            }
        });
        return result;
    }

    public Map<Long, Integer> batchGet(List<SkuStockQueryDTO> skuQueries) {
        if (skuQueries == null || skuQueries.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, List<String>> groupedHashKeys = skuQueries.stream()
                .filter(query -> query.getSpuId() != null && query.getSkuId() != null)
                .collect(Collectors.groupingBy(
                        SkuStockQueryDTO::getSpuId,
                        Collectors.mapping(query -> String.valueOf(query.getSkuId()), Collectors.toList())
                ));
        if (groupedHashKeys.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, Integer> result = new HashMap<>();
        groupedHashKeys.forEach((spuId, hashKeys) -> {
            List<Object> values = redisService.hMultiGet(ProductCacheKeys.spuStockKey(spuId), new ArrayList<>(hashKeys));
            for (int i = 0; i < hashKeys.size(); i++) {
                Object value = values != null && i < values.size() ? values.get(i) : null;
                Integer stock = toInteger(value);
                if (stock != null) {
                    result.put(Long.valueOf(hashKeys.get(i)), stock);
                }
            }
        });
        return result;
    }

    public void batchSet(Map<Long, Map<Long, Integer>> skuStockMap) {
        if (skuStockMap == null || skuStockMap.isEmpty()) {
            return;
        }

        Map<String, Map<String, Object>> cacheMap = new HashMap<>();
        skuStockMap.forEach((spuId, skuStocks) -> {
            if (spuId == null || skuStocks == null || skuStocks.isEmpty()) {
                return;
            }
            Map<String, Object> hashValue = skuStocks.entrySet().stream()
                    .filter(entry -> entry.getKey() != null && entry.getValue() != null)
                    .collect(Collectors.toMap(
                            entry -> String.valueOf(entry.getKey()),
                            Map.Entry::getValue,
                            (left, right) -> left
                    ));
            if (!hashValue.isEmpty()) {
                cacheMap.put(ProductCacheKeys.spuStockKey(spuId), hashValue);
            }
        });
        if (cacheMap.isEmpty()) {
            return;
        }

        redisService.multiSetHashWithExpire(cacheMap, ProductCacheKeys.spuStockTtlSeconds());
        log.info("批量写入商品库存缓存 {} 条", cacheMap.size());
    }

    public void delete(Long spuId) {
        if (spuId == null) {
            return;
        }
        redisService.del(ProductCacheKeys.spuStockKey(spuId));
    }

    public void deleteBatch(List<Long> spuIds) {
        if (spuIds == null || spuIds.isEmpty()) {
            return;
        }
        List<String> keys = spuIds.stream()
                .filter(Objects::nonNull)
                .map(ProductCacheKeys::spuStockKey)
                .toList();
        if (!keys.isEmpty()) {
            redisService.del(keys);
        }
    }

    public Long deduct(Long spuId, Long skuId, int quantity) {
        if (spuId == null || skuId == null || quantity <= 0) {
            return null;
        }

        String luaScript =
                "local stock = redis.call('HGET', KEYS[1], ARGV[1]) " +
                        "if not stock then " +
                        "  return -2 " +
                        "elseif tonumber(stock) < tonumber(ARGV[2]) then " +
                        "  return -1 " +
                        "else " +
                        "  return redis.call('HINCRBY', KEYS[1], ARGV[1], -ARGV[2]) " +
                        "end";

        return redisService.execute(
                luaScript,
                Collections.singletonList(ProductCacheKeys.spuStockKey(spuId)),
                List.of(String.valueOf(skuId), String.valueOf(quantity))
        );
    }

    public Long release(Long spuId, Long skuId, int quantity) {
        if (spuId == null || skuId == null || quantity <= 0) {
            return null;
        }

        String luaScript =
                "if redis.call('EXISTS', KEYS[1]) == 1 then " +
                        "  return redis.call('HINCRBY', KEYS[1], ARGV[1], ARGV[2]) " +
                        "else " +
                        "  return nil " +
                        "end";

        return redisService.execute(
                luaScript,
                Collections.singletonList(ProductCacheKeys.spuStockKey(spuId)),
                List.of(String.valueOf(skuId), String.valueOf(quantity))
        );
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer integerValue) {
            return integerValue;
        }
        if (value instanceof Number numberValue) {
            return numberValue.intValue();
        }
        if (value instanceof String stringValue) {
            try {
                return Integer.parseInt(stringValue);
            } catch (NumberFormatException e) {
                log.warn("库存缓存值无法解析为整数，value: {}", value);
                return null;
            }
        }
        log.warn("库存缓存值类型不支持，actualType: {}", value.getClass().getName());
        return null;
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long longValue) {
            return longValue;
        }
        if (value instanceof Number numberValue) {
            return numberValue.longValue();
        }
        if (value instanceof String stringValue) {
            try {
                return Long.parseLong(stringValue);
            } catch (NumberFormatException e) {
                log.warn("库存缓存字段无法解析为 Long，value: {}", value);
                return null;
            }
        }
        log.warn("库存缓存字段类型不支持，actualType: {}", value.getClass().getName());
        return null;
    }
}
