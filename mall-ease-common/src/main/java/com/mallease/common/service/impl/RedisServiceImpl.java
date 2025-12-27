package com.mallease.common.service.impl;

import com.mallease.common.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具实现类
 * 提供Redis常用操作的封装，包括字符串、Hash、Set、List等数据结构的操作
 * 所有方法都包含异常处理和日志记录，确保系统稳定性
 *
 * @author: Aulen
 * @create: 2025-11-08
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void set(String key, Object value, long time) {
        try {
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Redis操作失败: set with expiration", e);
        }
    }

    @Override
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            throw new RuntimeException("Redis操作失败: set", e);
        }
    }

    @Override
    public Object get(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            return value;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Boolean del(String key) {
        try {
            Boolean result = redisTemplate.delete(key);
            return result;
        } catch (Exception e) {
            return false; // 缓存降级
        }
    }

    @Override
    public Long del(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return 0L;
        }
        try {
            Long count = redisTemplate.delete(keys);
            return count != null ? count : 0L;
        } catch (Exception e) {
            return 0L; // 缓存降级
        }
    }

    @Override
    public Boolean expire(String key, long time) {
        try {
            Boolean result = redisTemplate.expire(key, time, TimeUnit.SECONDS);
            return result;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Long getExpire(String key) {
        try {
            Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return expire;
        } catch (Exception e) {
            return -2L; // Redis约定：-2表示key不存在
        }
    }

    @Override
    public Boolean hasKey(String key) {
        try {
            Boolean exists = redisTemplate.hasKey(key);
            return exists != null ? exists : false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String type(String key) {
        try {
            DataType dataType = redisTemplate.type(key);
            String typeName = dataType != null ? dataType.name().toLowerCase() : "none";
            return typeName;
        } catch (Exception e) {
            return "none";
        }
    }

    @Override
    public Long incr(String key, long delta) {
        try {
            Long result = redisTemplate.opsForValue().increment(key, delta);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Redis操作失败: increment", e);
        }
    }

    @Override
    public Long decr(String key, long delta) {
        try {
            Long result = redisTemplate.opsForValue().increment(key, -delta);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Redis操作失败: decrement", e);
        }
    }

    @Override
    public Object hGet(String key, String hashKey) {
        try {
            Object value = redisTemplate.opsForHash().get(key, hashKey);
            return value;
        } catch (Exception e) {
            return null; // 缓存降级
        }
    }

    @Override
    public Boolean hSet(String key, String hashKey, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            Boolean result = expire(key, time);
            return result;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void hSet(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
        } catch (Exception e) {
            throw new RuntimeException("Redis操作失败: hash set", e);
        }
    }

    @Override
    public Map<Object, Object> hGetAll(String key) {
        try {
            Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
            return map != null ? map : Collections.emptyMap();
        } catch (Exception e) {
            return Collections.emptyMap(); // 缓存降级，返回空Map
        }
    }

    @Override
    public Boolean hSetAll(String key, Map<String, Object> map, long time) {
        if (map == null || map.isEmpty()) {
            return false;
        }
        try {
            redisTemplate.opsForHash().putAll(key, map);
            Boolean result = expire(key, time);
            return result;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void hSetAll(String key, Map<String, ?> map) {
        if (map == null || map.isEmpty()) {
            return;
        }
        try {
            redisTemplate.opsForHash().putAll(key, map);
        } catch (Exception e) {
            throw new RuntimeException("Redis操作失败: hash set all", e);
        }
    }

    @Override
    public void hDel(String key, Object... hashKey) {
        Long count = redisTemplate.opsForHash().delete(key, hashKey);
    }

    @Override
    public Boolean hHasKey(String key, String hashKey) {
        try {
            Boolean exists = redisTemplate.opsForHash().hasKey(key, hashKey);
            return exists != null ? exists : false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Long hIncr(String key, String hashKey, Long delta) {
        try {
            Long result = redisTemplate.opsForHash().increment(key, hashKey, delta);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Redis操作失败: hash increment", e);
        }
    }

    @Override
    public Long hDecr(String key, String hashKey, Long delta) {
        try {
            Long result = redisTemplate.opsForHash().increment(key, hashKey, -delta);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Redis操作失败: hash decrement", e);
        }
    }

    @Override
    public Set<Object> sMembers(String key) {
        try {
            Set<Object> members = redisTemplate.opsForSet().members(key);
            return members != null ? members : Collections.emptySet();
        } catch (Exception e) {
            return Collections.emptySet(); // 缓存降级
        }
    }

    @Override
    public Long sAdd(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            return count != null ? count : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public Long sAdd(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            expire(key, time);
            return count != null ? count : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public Boolean sIsMember(String key, Object value) {
        try {
            Boolean isMember = redisTemplate.opsForSet().isMember(key, value);
            return isMember != null ? isMember : false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Long sSize(String key) {
        try {
            Long size = redisTemplate.opsForSet().size(key);
            return size != null ? size : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public Long sRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count != null ? count : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public List<Object> lRange(String key, long start, long end) {
        try {
            List<Object> list = redisTemplate.opsForList().range(key, start, end);
            return list != null ? list : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList(); // 缓存降级
        }
    }

    @Override
    public Long lSize(String key) {
        try {
            Long size = redisTemplate.opsForList().size(key);
            return size != null ? size : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public Object lIndex(String key, long index) {
        try {
            Object value = redisTemplate.opsForList().index(key, index);
            return value;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Long lPush(String key, Object value) {
        try {
            Long count = redisTemplate.opsForList().rightPush(key, value);
            return count;
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public Long lPush(String key, Object value, long time) {
        try {
            Long index = redisTemplate.opsForList().rightPush(key, value);
            expire(key, time);
            return index;
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public Long lPushAll(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForList().rightPushAll(key, values);
            return count;
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public Long lPushAll(String key, Long time, Object... values) {
        try {
            Long count = redisTemplate.opsForList().rightPushAll(key, values);
            expire(key, time);
            return count;
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public Long lRemove(String key, long count, Object value) {
        try {
            Long removed = redisTemplate.opsForList().remove(key, count, value);
            return removed != null ? removed : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public List<Object> multiGet(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            List<Object> values = redisTemplate.opsForValue().multiGet(keys);
            return values != null ? values : Collections.emptyList();
        } catch (Exception e) {
            // 返回与keys等长的null列表，保持顺序对应
            return Collections.nCopies(keys.size(), null);
        }
    }

    @Override
    public void multiSet(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return;
        }
        try {
            redisTemplate.opsForValue().multiSet(map);
        } catch (Exception e) {
            throw new RuntimeException("Redis操作失败: multi set", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void multiSetWithExpire(Map<String, Object> map, long time) {
        if (map == null || map.isEmpty()) {
            return;
        }

        RedisSerializer<String> keySerializer = redisTemplate.getStringSerializer();
        RedisSerializer<Object> valueSerializer =
                (RedisSerializer<Object>) redisTemplate.getValueSerializer();

        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            map.forEach((key, value) -> {
                byte[] keyBytes = keySerializer.serialize(key);
                byte[] valueBytes = valueSerializer.serialize(value);
                if (keyBytes != null && valueBytes != null) {
                    // 使用新 API：set() + Expiration 替代已废弃的 setEx()
                    connection.stringCommands().set(
                            keyBytes,
                            valueBytes,
                            Expiration.seconds(time),
                            RedisStringCommands.SetOption.UPSERT
                    );
                }
            });
            return null;
        });
    }

    @Override
    public Long execute(String script, List<String> keys, List<String> args) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);

        return redisTemplate.execute(redisScript, keys, args.toArray());
    }

    @Override
    public void hSetAllWithExpire(String key, Map<String, Object> map, long expireSeconds) {
        if (map == null || map.isEmpty()) {
            return;
        }

        // 修复 Lua 脚本语法：使用换行符和分号分隔语句
        String luaScript =
                "redis.call('HMSET', KEYS[1], unpack(ARGV, 1, #ARGV-1)); " +
                "local ttl = tonumber(ARGV[#ARGV]); " +
                "if ttl and ttl > 0 then " +
                "  redis.call('EXPIRE', KEYS[1], ttl); " +
                "end; " +
                "return 1";

        List<String> args = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            args.add(entry.getKey());
            args.add(String.valueOf(entry.getValue()));
        }
        args.add(String.valueOf(expireSeconds));

        try {
            execute(luaScript, Collections.singletonList(key), args);
        } catch (Exception e) {
            log.error("Redis Lua脚本执行失败，key: {}, expireSeconds: {}", key, expireSeconds, e);
            throw new RuntimeException("Redis操作失败: hash set all with expire", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> executePipelined(RedisCallback<T> action) {
        if (action == null) {
            return Collections.emptyList();
        }
        try {
            List<Object> results = redisTemplate.executePipelined(action);
            return (List<T>) results;
        } catch (Exception e) {
            log.error("Redis Pipeline操作失败", e);
            return Collections.emptyList();
        }
    }

    @Override
    public Set<String> scan(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return Collections.emptySet();
        }
        try {
            Set<String> keys = new HashSet<>();
            redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
                Cursor<byte[]> cursor = connection.keyCommands().scan(
                        ScanOptions.scanOptions()
                                .match(pattern)
                                .count(1000)
                                .build()
                );

                RedisSerializer<String> keySerializer = redisTemplate.getStringSerializer();
                while (cursor.hasNext()) {
                    byte[] keyBytes = cursor.next();
                    String key = keySerializer.deserialize(keyBytes);
                    if (key != null) {
                        keys.add(key);
                    }
                }

                // 关闭游标
                try {
                    cursor.close();
                } catch (Exception e) {
                    log.warn("关闭SCAN游标失败", e);
                }

                return keys;
            });

            return keys;
        } catch (Exception e) {
            log.error("Redis SCAN操作失败，pattern: {}", pattern, e);
            return Collections.emptySet();
        }
    }

    @Override
    public void multiSetHashWithExpire(Map<String, Map<String, Object>> dataMap, long expireSeconds) {
        if (dataMap == null || dataMap.isEmpty()) {
            return;
        }

        try {
            // 获取序列化器（复用全局配置）
            RedisSerializer<String> keySerializer = redisTemplate.getStringSerializer();

            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                dataMap.forEach((key, hashValue) -> {
                    if (hashValue != null && !hashValue.isEmpty()) {
                        // 序列化 Hash 字段
                        Map<byte[], byte[]> byteMap = new HashMap<>();
                        hashValue.forEach((hashKey, value) -> {
                            byte[] hashKeyBytes = hashKey.getBytes(StandardCharsets.UTF_8);
                            byte[] valueBytes = String.valueOf(value).getBytes(StandardCharsets.UTF_8);
                            byteMap.put(hashKeyBytes, valueBytes);
                        });

                        byte[] keyBytes = keySerializer.serialize(key);
                        if (keyBytes != null) {
                            connection.hashCommands().hMSet(keyBytes, byteMap);

                            if (expireSeconds > 0) {
                                connection.keyCommands().expire(keyBytes, expireSeconds);
                            }
                        }
                    }
                });
                return null;
            });

            log.debug("批量写入Hash缓存 {} 条，过期时间: {}秒", dataMap.size(), expireSeconds == 0 ? "永久" : expireSeconds);
        } catch (Exception e) {
            log.error("Redis批量Hash操作失败，size: {}", dataMap.size(), e);
            throw new RuntimeException("Redis操作失败: multi set hash with expire", e);
        }
    }

}
