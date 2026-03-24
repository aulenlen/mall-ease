package com.mallease.common.service.impl;

import com.mallease.common.service.TypedRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 基于 StringRedisTemplate 的强类型 Redis 实现。
 */
@Component
@RequiredArgsConstructor
public class TypedRedisServiceImpl implements TypedRedisService {

    private static final int PIPELINE_BATCH_SIZE = 100;

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void setString(String key, String value, long ttlSeconds) {
        String validKey = requireKey(key);
        String validValue = requireValue(value);
        long validTtlSeconds = requireTtl(ttlSeconds);
        stringRedisTemplate.opsForValue().set(validKey, validValue, validTtlSeconds, TimeUnit.SECONDS);
    }

    @Override
    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(requireKey(key));
    }

    @Override
    public Map<String, String> multiGetString(List<String> keys) {
        List<String> normalizedKeys = normalizeKeys(keys);
        if (normalizedKeys.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> values = stringRedisTemplate.opsForValue().multiGet(normalizedKeys);
        Map<String, String> result = new HashMap<>(normalizedKeys.size());
        for (int i = 0; i < normalizedKeys.size(); i++) {
            String value = values == null || i >= values.size() ? null : values.get(i);
            result.put(normalizedKeys.get(i), value);
        }
        return result;
    }

    @Override
    public void multiSetStringWithExpire(Map<String, String> map, long ttlSeconds) {
        if (map == null || map.isEmpty()) {
            return;
        }

        long validTtlSeconds = requireTtl(ttlSeconds);
        List<Map.Entry<String, String>> entries = normalizeEntries(map);
        if (entries.isEmpty()) {
            return;
        }

        RedisSerializer<String> serializer = stringRedisTemplate.getStringSerializer();
        for (int i = 0; i < entries.size(); i += PIPELINE_BATCH_SIZE) {
            List<Map.Entry<String, String>> batch = entries.subList(i, Math.min(i + PIPELINE_BATCH_SIZE, entries.size()));
            stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                for (Map.Entry<String, String> entry : batch) {
                    byte[] keyBytes = serializer.serialize(entry.getKey());
                    byte[] valueBytes = serializer.serialize(entry.getValue());
                    if (keyBytes != null && valueBytes != null) {
                        connection.stringCommands().set(
                                keyBytes,
                                valueBytes,
                                Expiration.seconds(validTtlSeconds),
                                RedisStringCommands.SetOption.UPSERT
                        );
                    }
                }
                return null;
            });
        }
    }

    @Override
    public void delete(String key) {
        stringRedisTemplate.delete(requireKey(key));
    }

    @Override
    public void deleteBatch(List<String> keys) {
        List<String> normalizedKeys = normalizeKeys(keys);
        if (normalizedKeys.isEmpty()) {
            return;
        }
        stringRedisTemplate.delete(normalizedKeys);
    }

    @Override
    public <T> T executeScript(RedisScript<T> script, List<String> keys, Object... args) {
        if (script == null) {
            throw new IllegalArgumentException("Lua 脚本对象不能为空");
        }

        List<String> validKeys = normalizeKeys(keys);

        Object[] stringArgs = new Object[0];
        if (args != null && args.length > 0) {
            stringArgs = Arrays.stream(args)
                    .map(arg -> arg == null ? "" : String.valueOf(arg))
                    .toArray();
        }

        return stringRedisTemplate.execute(script, validKeys, stringArgs);
    }

    private String requireKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Redis key 不能为空");
        }
        return key;
    }

    private String requireValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Redis value 不能为空");
        }
        return value;
    }

    private long requireTtl(long ttlSeconds) {
        if (ttlSeconds <= 0) {
            throw new IllegalArgumentException("Redis TTL 必须大于 0");
        }
        return ttlSeconds;
    }

    private List<String> normalizeKeys(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        return keys.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(key -> !key.isEmpty())
                .distinct()
                .toList();
    }

    private List<Map.Entry<String, String>> normalizeEntries(Map<String, String> map) {
        return new ArrayList<>(map.entrySet().stream().collect(Collectors.toMap(
                        entry -> requireKey(entry.getKey()),
                        entry -> requireValue(entry.getValue()),
                        (left, right) -> right, HashMap::new))
                .entrySet());
    }
}
