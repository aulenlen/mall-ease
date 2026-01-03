package com.mallease.product.component;

import com.mallease.common.service.RedisService;
import com.mallease.product.constant.RedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
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

    /**
     * 获取单个缓存对象
     *
     * @param redisKey Redis Key 枚举
     * @param id       业务ID
     * @param clazz    目标类型
     * @param <T>      泛型类型
     * @return 缓存对象，不存在或类型不匹配返回 null
     */
    @Nullable
    public <T> T get(RedisKey redisKey, Long id, Class<T> clazz) {
        if (id == null) {
            return null;
        }

        try {
            String key = redisKey.key(id);
            Object value = redisService.get(key);
            if (value == null) {
                return null;
            }

            if (clazz.isInstance(value)) {
                return clazz.cast(value);
            }

            log.warn("缓存类型不匹配，key: {}, 期望: {}, 实际: {}",
                    key, clazz.getName(), value.getClass().getName());
            return null;

        } catch (Exception e) {
            log.error("获取缓存失败，redisKey: {}, id: {}", redisKey.name(), id, e);
            return null;
        }
    }

    /**
     * 获取 Hash 结构中的单个字段值
     *
     * @param redisKey Redis Key 枚举
     * @param id       业务ID（用于构建 key）
     * @param hashKey  Hash 字段名
     * @return 字段值，不存在返回 null
     */
    @Nullable
    public Object hGet(RedisKey redisKey, Long id, String hashKey) {
        if (id == null || hashKey == null) {
            return null;
        }

        try {
            String key = redisKey.key(id);
            return redisService.hGet(key, hashKey);

        } catch (Exception e) {
            log.error("获取Hash字段失败，redisKey: {}, id: {}, hashKey: {}",
                    redisKey.name(), id, hashKey, e);
            return null;
        }
    }

    /**
     * 获取 Hash 结构的所有字段
     *
     * @param redisKey Redis Key 枚举
     * @param id       业务ID
     * @return 所有字段的映射，不存在返回空 Map
     */
    public Map<Object, Object> hGetAll(RedisKey redisKey, Long id) {
        if (id == null) {
            return Collections.emptyMap();
        }

        try {
            String key = redisKey.key(id);
            Map<Object, Object> result = redisService.hGetAll(key);
            return result != null ? result : Collections.emptyMap();

        } catch (Exception e) {
            log.error("获取Hash全部字段失败，redisKey: {}, id: {}", redisKey.name(), id, e);
            return Collections.emptyMap();
        }
    }

    /**
     * Hash 字段原子自增/自减
     *
     * @param redisKey Redis Key 枚举
     * @param id       业务ID
     * @param hashKey  Hash 字段名
     * @param delta    增量（负数为减）
     * @return 操作后的新值，失败返回 null
     */
    @Nullable
    public Long hIncr(RedisKey redisKey, Long id, String hashKey, long delta) {
        if (id == null || hashKey == null) {
            return null;
        }

        try {
            String key = redisKey.key(id);
            return redisService.hIncr(key, hashKey, delta);

        } catch (Exception e) {
            log.error("Hash字段自增失败，redisKey: {}, id: {}, hashKey: {}, delta: {}",
                    redisKey.name(), id, hashKey, delta, e);
            return null;
        }
    }

    /**
     * 获取 List 类型缓存（固定 Key，无参数）
     *
     * @param redisKey Redis Key 枚举
     * @param clazz    List 元素类型
     * @param <T>      泛型类型
     * @return List 对象，不存在返回 null
     */
    @Nullable
    public <T> List<T> getList(RedisKey redisKey, Class<T> clazz) {
        try {
            return redisService.getList(redisKey.key(), clazz);
        } catch (Exception e) {
            log.error("获取List缓存失败，redisKey: {}", redisKey.name(), e);
            return null;
        }
    }

    /**
     * 设置缓存（固定 Key，使用枚举自带的 TTL）
     *
     * @param redisKey Redis Key 枚举
     * @param value    缓存值
     */
    public void set(RedisKey redisKey, Object value) {
        try {
            redisService.set(redisKey.key(), value, redisKey.getTtl());
            log.debug("设置缓存成功，redisKey: {}", redisKey.name());
        } catch (Exception e) {
            log.error("设置缓存失败，redisKey: {}", redisKey.name(), e);
        }
    }

    /**
     * 原子扣减库存（Lua 脚本保证原子性）
     *
     * @param redisKey Redis Key 枚举
     * @param id       业务ID（如 spuId）
     * @param hashKey  Hash 字段名（如 skuId）
     * @param quantity 扣减数量（正数）
     * @return 扣减后的库存值，库存不足返回 null
     */
    @Nullable
    public Long deductStockAtomic(RedisKey redisKey, Long id, String hashKey, int quantity) {
        if (id == null || hashKey == null || quantity <= 0) {
            return null;
        }

        try {
            String key = redisKey.key(id);
            String luaScript =
                    "local stock = redis.call('HGET', KEYS[1], ARGV[1]) " +
                            "if stock and tonumber(stock) >= tonumber(ARGV[2]) then " +
                            "  return redis.call('HINCRBY', KEYS[1], ARGV[1], -ARGV[2]) " +
                            "else " +
                            "  return nil " +
                            "end";
            Object result = redisService.execute(
                    luaScript,
                    Collections.singletonList(key),
                    List.of(hashKey, String.valueOf(quantity))
            );
            if (result != null) {
                Long newStock = ((Number) result).longValue();
                log.debug("扣减库存成功，key: {}, hashKey: {}, 扣减: {}, 剩余: {}",
                        key, hashKey, quantity, newStock);
                return newStock;
            }

            log.warn("库存不足，key: {}, hashKey: {}, 尝试扣减: {}", key, hashKey, quantity);
            return null;

        } catch (Exception e) {
            log.error("扣减库存失败，redisKey: {}, id: {}, hashKey: {}, quantity: {}",
                    redisKey.name(), id, hashKey, quantity, e);
            return null;
        }
    }
}
