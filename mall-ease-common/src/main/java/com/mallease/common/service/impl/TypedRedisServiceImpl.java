package com.mallease.common.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final ObjectMapper objectMapper;
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
    public <T> void setJson(String key, T value, long ttlSeconds) {
        try {
            setString(requireKey(key), objectMapper.writeValueAsString(requireValue(value)), ttlSeconds);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Redis JSON 序列化失败", ex);
        }
    }

    @Override
    public <T> T getJson(String key, Class<T> clazz) {
        String json = getString(key);
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, requireClass(clazz));
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Redis JSON 反序列化失败", ex);
        }
    }

    @Override
    public <T> Map<String, T> multiGetJson(List<String> keys, Class<T> clazz) {
        Map<String, String> jsonMap = multiGetString(keys);
        if (jsonMap.isEmpty()) {
            return Collections.emptyMap();
        }

        Class<T> validClass = requireClass(clazz);
        Map<String, T> result = new HashMap<>(jsonMap.size());
        for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
            String json = entry.getValue();
            if (json == null || json.isBlank()) {
                result.put(entry.getKey(), null);
                continue;
            }
            try {
                result.put(entry.getKey(), objectMapper.readValue(json, validClass));
            } catch (JsonProcessingException ex) {
                throw new RuntimeException("Redis JSON 反序列化失败", ex);
            }
        }
        return result;
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
    public <T> void multiSetJsonWithExpire(Map<String, T> map, long ttlSeconds) {
        if (map == null || map.isEmpty()) {
            return;
        }

        Map<String, String> jsonMap = new HashMap<>(map.size());
        for (Map.Entry<String, T> entry : map.entrySet()) {
            String validKey = requireKey(entry.getKey());
            T validValue = requireValue(entry.getValue());
            try {
                jsonMap.put(validKey, objectMapper.writeValueAsString(validValue));
            } catch (JsonProcessingException ex) {
                throw new RuntimeException("Redis JSON 序列化失败", ex);
            }
        }
        multiSetStringWithExpire(jsonMap, ttlSeconds);
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

    private <T> T requireValue(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Redis value 不能为空");
        }
        return value;
    }

    private <T> Class<T> requireClass(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Redis value 类型不能为空");
        }
        return clazz;
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
