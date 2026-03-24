package com.mallease.common.service;

import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;
import java.util.Map;

/**
 * 面向新链路的强类型 Redis 基础服务。
 * 当前只收敛商品详情快照需要的字符串读写能力，避免继续扩展旧 RedisService 的 Object 接口。
 */
public interface TypedRedisService {

    void setString(String key, String value, long ttlSeconds);

    String getString(String key);

    Map<String, String> multiGetString(List<String> keys);

    void multiSetStringWithExpire(Map<String, String> map, long ttlSeconds);

    void delete(String key);

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
