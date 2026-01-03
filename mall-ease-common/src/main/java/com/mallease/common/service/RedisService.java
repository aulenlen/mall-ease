package com.mallease.common.service;

import jakarta.annotation.Nullable;
import org.springframework.data.redis.core.RedisCallback;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis服务接口
 * 提供Redis常用操作，包括字符串、Hash、Set、List等数据结构
 * 注意：标记为@Nullable的方法可能返回null，调用时需要进行null检查
 *
 * @author: Aulen
 * @create: 2025-11-08
 */
public interface RedisService {
    /**
     * 保存属性（带过期时间）
     *
     * @param key   键
     * @param value 值
     * @param time  过期时间（秒）
     */
    void set(String key, Object value, long time);

    /**
     * 保存属性（永久有效）
     *
     * @param key   键
     * @param value 值
     */
    void set(String key, Object value);

    /**
     * 获取属性
     *
     * @param key 键
     * @return 值，如果key不存在则返回null
     */
    @Nullable
    Object get(String key);

    /**
     * 删除属性
     *
     * @param key 键
     * @return 是否删除成功，失败返回false
     */
    Boolean del(String key);

    /**
     * 批量删除属性
     *
     * @param keys 键列表
     * @return 成功删除的数量
     */
    Long del(List<String> keys);

    /**
     * 设置过期时间
     *
     * @param key  键
     * @param time 过期时间（秒）
     * @return 是否设置成功
     */
    Boolean expire(String key, long time);

    /**
     * 获取过期时间
     *
     * @param key 键
     * @return 剩余秒数，-1表示永久有效，-2表示key不存在
     */
    Long getExpire(String key);

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return 是否存在
     */
    Boolean hasKey(String key);

    /**
     * 获取key的数据类型
     *
     * @param key 键
     * @return 数据类型：string, list, set, hash, zset, none
     */
    String type(String key);

    /**
     * 按delta递增
     *
     * @param key   键
     * @param delta 增量
     * @return 递增后的值
     */
    Long incr(String key, long delta);

    /**
     * 按delta递减
     *
     * @param key   键
     * @param delta 减量
     * @return 递减后的值
     */
    Long decr(String key, long delta);

    /**
     * 获取Hash结构中的属性
     *
     * @param key     键
     * @param hashKey Hash中的字段
     * @return 字段值，如果不存在则返回null
     */
    @Nullable
    Object hGet(String key, String hashKey);

    /**
     * 向Hash结构中放入一个属性
     */
    Boolean hSet(String key, String hashKey, Object value, long time);

    /**
     * 向Hash结构中放入一个属性
     */
    void hSet(String key, String hashKey, Object value);

    /**
     * 直接获取整个Hash结构
     */
    Map<Object, Object> hGetAll(String key);

    /**
     * 直接设置整个Hash结构
     */
    Boolean hSetAll(String key, Map<String, Object> map, long time);

    /**
     * 直接设置整个Hash结构
     */
    void hSetAll(String key, Map<String, ?> map);

    /**
     * 删除Hash结构中的属性
     */
    void hDel(String key, Object... hashKey);

    /**
     * 判断Hash结构中是否有该属性
     */
    Boolean hHasKey(String key, String hashKey);

    /**
     * Hash结构中属性递增
     */
    Long hIncr(String key, String hashKey, Long delta);

    /**
     * Hash结构中属性递减
     */
    Long hDecr(String key, String hashKey, Long delta);

    /**
     * 获取Set结构
     */
    Set<Object> sMembers(String key);

    /**
     * 向Set结构中添加属性
     */
    Long sAdd(String key, Object... values);

    /**
     * 向Set结构中添加属性
     */
    Long sAdd(String key, long time, Object... values);

    /**
     * 是否为Set中的属性
     */
    Boolean sIsMember(String key, Object value);

    /**
     * 获取Set结构的长度
     */
    Long sSize(String key);

    /**
     * 删除Set结构中的属性
     */
    Long sRemove(String key, Object... values);

    /**
     * 获取List结构中的属性
     */
    List<Object> lRange(String key, long start, long end);

    /**
     * 获取List结构的长度
     */
    Long lSize(String key);

    /**
     * 根据索引获取List中的属性
     *
     * @param key   键
     * @param index 索引
     * @return 元素值，如果不存在则返回null
     */
    @Nullable
    Object lIndex(String key, long index);

    /**
     * 向List结构中添加属性
     */
    Long lPush(String key, Object value);

    /**
     * 向List结构中添加属性
     */
    Long lPush(String key, Object value, long time);

    /**
     * 向List结构中批量添加属性
     */
    Long lPushAll(String key, Object... values);

    /**
     * 向List结构中批量添加属性
     */
    Long lPushAll(String key, Long time, Object... values);

    /**
     * 从List结构中移除属性
     */
    Long lRemove(String key, long count, Object value);

    /**
     * 批量获取（使用 Redis MGET 命令）
     *
     * @param keys 键列表
     * @return 值列表（顺序与keys对应，不存在的key对应null）
     */
    List<Object> multiGet(List<String> keys);

    /**
     * 批量设置（使用 Redis MSET 命令）
     * 注意：不设置过期时间，永久有效
     *
     * @param map 键值对
     */
    void multiSet(Map<String, Object> map);

    /**
     * 批量设置（使用 Pipeline + SETEX 原子操作）
     * 使用 SETEX 命令保证设置值和过期时间的原子性，避免内存泄漏
     *
     * @param map  键值对
     * @param time 过期时间（秒）
     */
    void multiSetWithExpire(Map<String, Object> map, long time);

    /**
     * 执行Lua脚本
     *
     * @param script Lua脚本
     * @param keys   Key列表
     * @param args   参数列表
     * @return 执行结果
     */
    Long execute(String script, List<String> keys, List<String> args);

    /**
     * 原子化设置 Hash + 过期时间（使用 Lua 脚本）
     *
     * @param key           Redis key
     * @param map           Hash 字段映射
     * @param expireSeconds 过期时间（秒），0表示永久
     */
    void hSetAllWithExpire(String key, Map<String, Object> map, long expireSeconds);

    /**
     * Pipeline 批量执行器
     *
     * @param action 批量操作回调
     * @return 执行结果列表
     */
    <T> List<T> executePipelined(RedisCallback<T> action);

    /**
     * 扫描匹配的 key（用于清理任务）
     *
     * @param pattern key 匹配模式（如 "sku:stock:*"）
     * @return 匹配的 key 集合
     */
    Set<String> scan(String pattern);

    /**
     * 批量设置 Hash 结构（使用 Pipeline + HMSET）
     *
     * @param dataMap       数据映射（完整key -> Hash字段映射）
     * @param expireSeconds 过期时间（秒），0表示永久有效
     */
    void multiSetHashWithExpire(Map<String, Map<String, Object>> dataMap, long expireSeconds);

    /**
     * 获取缓存的列表数据并反序列化为指定类型
     * @param key   Redis 键
     * @param clazz 列表元素的类型
     * @param <T>   元素类型泛型
     * @return 反序列化后的列表，如果 key 不存在或类型不匹配则返回 null
     */
    @Nullable
    <T> List<T> getList(String key, Class<T> clazz);
}
