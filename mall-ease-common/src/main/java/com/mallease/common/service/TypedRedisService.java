package com.mallease.common.service;

import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;
import java.util.Map;

/**
 * 强类型 Redis 基础服务。
 * 这里仅提供通用缓存能力，例如字符串、JSON 对象、批量读写和 Lua 脚本执行。
 * 业务模块负责自己的 key 规则和 TTL 语义，不应把订单、商品等领域逻辑下沉到这里。
 */
public interface TypedRedisService {

    /**
     * 写入普通字符串值，并设置过期时间。
     */
    void setString(String key, String value, long ttlSeconds);

    /**
     * 读取普通字符串值，不存在时返回 null。
     */
    String getString(String key);

    /**
     * 把对象序列化为 JSON 后写入 Redis，并设置过期时间。
     */
    <T> void setJson(String key, T value, long ttlSeconds);

    /**
     * 读取 JSON 并反序列化为指定类型，不存在时返回 null。
     */
    <T> T getJson(String key, Class<T> clazz);

    /**
     * 按 key 列表批量读取字符串值，返回结果中的 key 顺序与输入无关，但会一一对应。
     */
    Map<String, String> multiGetString(List<String> keys);

    /**
     * 批量写入字符串值，并统一设置过期时间。
     */
    void multiSetStringWithExpire(Map<String, String> map, long ttlSeconds);

    /**
     * 删除单个 key。
     */
    void delete(String key);

    /**
     * 批量删除多个 key。
     */
    void deleteBatch(List<String> keys);

    /**
     * 执行 Redis Lua 脚本
     *
     * @param script 预加载的 Lua 脚本对象
     * @param keys   参与脚本执行的 Redis Keys (对应 Lua 中的 KEYS)
     * @param args   传入脚本的参数 (对应 Lua 中的 ARGV)
     * @param <T>    返回值类型 (通常是 Long, Boolean 或 List)
     * @return 脚本执行结果
     */
    <T> T executeScript(RedisScript<T> script, List<String> keys, Object... args);
}
