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
        List<String> keys = ids.stream()
                .map(id -> keyPrefix + id)
                .toList();

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
    public <T> void batchSet(List<T> items,
                             String keyPrefix,
                             Function<T, Long> idExtractor,
                             long expireSeconds) {
        if (items == null || items.isEmpty()) {
            return;
        }

        // 构建 key-value map
        Map<String, Object> cacheMap = items.stream()
                .collect(Collectors.toMap(
                        item -> keyPrefix + idExtractor.apply(item),
                        item -> item
                ));

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

        List<String> keys = ids.stream()
                .map(id -> keyPrefix + id)
                .toList();

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
}
