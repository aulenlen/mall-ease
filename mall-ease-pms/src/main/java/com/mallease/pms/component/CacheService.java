package com.mallease.pms.component;

import com.mallease.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 通用缓存工具类
 *
 * @author: Aulen
 * @create: 2025-11-21
 */
@Component
@Slf4j
public class CacheService {

    @Autowired
    private RedisService redisService;

    /**
     * 批量获取缓存
     *
     * @param ids       ID列表
     * @param keyPrefix 缓存key前缀
     * @param clazz     目标类型
     * @param <T>       泛型类型
     * @return ID到对象的映射
     */
    public <T> Map<Long, T> batchGet(List<Long> ids, String keyPrefix, Class<T> clazz) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyMap();
        }

        // 构建 key 列表
        List<String> keys = ids.stream().map(id -> keyPrefix + id).toList();

        List<Object> values = redisService.multiGet(keys);

        Map<Long, T> result = new HashMap<>();
        if (values != null) {
            for (int i = 0; i < ids.size(); i++) {
                Object value = values.get(i);
                if (value != null && clazz.isInstance(value)) {
                    result.put(ids.get(i), clazz.cast(value));
                }
            }
        }

        log.debug("批量获取缓存，总数: {}, 命中: {}", ids.size(), result.size());
        return result;
    }

    /**
     * 批量设置缓存
     *
     * @param items         数据列表
     * @param keyPrefix     缓存key前缀
     * @param idExtractor   ID提取函数
     * @param expireSeconds 过期时间（秒）
     * @param <T>           泛型类型
     */
    public <T> void batchSetList(List<T> items, String keyPrefix, Function<T, Long> idExtractor, long expireSeconds) {
        if (items == null || items.isEmpty()) {
            return;
        }

        // 构建 key-value map
        Map<String, Object> cacheMap = items.stream().collect(Collectors.toMap(item -> keyPrefix + idExtractor.apply(item), item -> item));

        // 批量写入
        redisService.multiSetWithExpire(cacheMap, expireSeconds);
        log.info("批量写入缓存 {} 条", items.size());
    }

    /**
     * 删除单个缓存
     *
     * @param id        ID
     * @param keyPrefix 缓存key前缀
     */
    public void delete(Long id, String keyPrefix) {
        if (id == null) {
            return;
        }
        String key = keyPrefix + id;
        redisService.del(key);
        log.debug("删除缓存，key: {}", key);
    }

    /**
     * 批量删除缓存
     *
     * @param ids       ID列表
     * @param keyPrefix 缓存key前缀
     */
    public void deleteBatch(List<Long> ids, String keyPrefix) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        List<String> keys = ids.stream().map(id -> keyPrefix + id).toList();

        redisService.del(keys);
        log.info("批量删除缓存 {} 条", ids.size());
    }

    /**
     * 检查缓存是否存在
     *
     * @param id        ID
     * @param keyPrefix 缓存key前缀
     * @return 是否存在
     */
    public boolean exists(Long id, String keyPrefix) {
        if (id == null) {
            return false;
        }
        String key = keyPrefix + id;
        return redisService.hasKey(key);
    }

    /**
     * 获取缓存剩余过期时间
     *
     * @param id        ID
     * @param keyPrefix 缓存key前缀
     * @return 剩余秒数，-1表示永久有效，-2表示不存在
     */
    public Long getExpire(Long id, String keyPrefix) {
        if (id == null) {
            return -2L;
        }
        String key = keyPrefix + id;
        return redisService.getExpire(key);
    }

    /**
     * 批量设置简单键值对缓存
     *
     * @param dataMap       数据映射（ID -> 值）
     * @param keyPrefix     缓存key前缀
     * @param expireSeconds 过期时间（秒），0表示永久有效
     * @param <V>           值类型
     */
    public <V> void batchSetMap(String keyPrefix, Map<Long, V> dataMap, long expireSeconds) {
        if (dataMap == null || dataMap.isEmpty()) {
            return;
        }

        // 构建 key-value map
        Map<String, Object> cacheMap = dataMap.entrySet().stream().collect(Collectors.toMap(entry -> keyPrefix + entry.getKey(), Map.Entry::getValue));

        // 批量写入
        if (expireSeconds > 0) {
            redisService.multiSetWithExpire(cacheMap, expireSeconds);
        } else {
            redisService.multiSet(cacheMap);
        }

        log.info("批量写入简单KV缓存 {} 条", dataMap.size());
    }

    /**
     * 批量设置 Hash 结构缓存（用于商品多 SKU 库存等场景）
     *
     * @param keyPrefix     缓存 key 前缀（如 "sku:stock:"）
     * @param dataMap       数据映射（商品ID -> Hash字段映射）
     * @param expireSeconds 过期时间（秒），0表示永久有效
     * @param <V>           Hash 值类型
     */
    public <V> void batchSetHashMap(String keyPrefix, Map<Long, Map<String, V>> dataMap, long expireSeconds) {
        if (dataMap == null || dataMap.isEmpty()) {
            return;
        }

        // 转换为 RedisService 需要的格式：完整key -> Hash字段映射
        Map<String, Map<String, Object>> cacheMap = new HashMap<>();
        dataMap.forEach((id, hashValue) -> {
            if (hashValue != null && !hashValue.isEmpty()) {
                String key = keyPrefix + id;
                Map<String, Object> objectMap = new HashMap<>(hashValue);
                cacheMap.put(key, objectMap);
            }
        });

        redisService.multiSetHashWithExpire(cacheMap, expireSeconds);

        log.info("批量写入Hash缓存 {} 条，共 {} 个字段，过期时间: {}秒",
                cacheMap.size(),
                cacheMap.values().stream().mapToInt(Map::size).sum(),
                expireSeconds == 0 ? "永久" : expireSeconds);
    }

    /**
     * 默认使用 Pipeline 模式即可满足大多数缓存场景需求。
     *
     * @param keyPrefix     缓存 key 前缀
     * @param dataMap       数据映射（商品ID -> Hash字段映射）
     * @param expireSeconds 过期时间（秒），0表示永久有效
     * @param <V>           Hash 值类型
     */
    private <V> void batchSetHashMapByLua(String keyPrefix, Map<Long, Map<String, V>> dataMap, long expireSeconds) {
        dataMap.forEach((id, value) -> {
            String key = keyPrefix + id;
            Map<String, Object> hashValue = new HashMap<>(value);

            if (expireSeconds > 0) {
                redisService.hSetAllWithExpire(key, hashValue, expireSeconds);
            } else {
                redisService.hSetAll(key, hashValue);
            }
        });

        log.info("批量写入Hash缓存(Lua模式) {} 条，共 {} 个SKU",
                dataMap.size(),
                dataMap.values().stream().mapToInt(Map::size).sum());
    }
}
