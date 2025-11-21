package com.mallease.common.service.impl;

import com.mallease.common.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

        try {
            // 获取序列化器
            RedisSerializer<String> keySerializer = redisTemplate.getStringSerializer();
            RedisSerializer<Object> valueSerializer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();

            // 统计变量
            final int[] successCount = {0};
            final int[] failCount = {0};

            // 使用 Pipeline 批量设置并设置过期时间（原子操作）
            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                map.forEach((key, value) -> {
                    try {
                        // 序列化 key 和 value
                        byte[] keyBytes = keySerializer.serialize(key);
                        byte[] valueBytes = valueSerializer.serialize(value);

                        if (keyBytes != null && valueBytes != null) {
                            // 使用 SETEX 命令保证原子性：一次性设置值和过期时间
                            // 避免 SET 成功但 EXPIRE 失败导致的内存泄漏
                            connection.setEx(keyBytes, time, valueBytes);
                            successCount[0]++;
                        } else {
                            failCount[0]++;
                        }
                    } catch (Exception e) {
                        failCount[0]++;
                    }
                });
                return null;
            });
        } catch (Exception e) {
            throw new RuntimeException("Redis批量操作失败", e);
        }
    }
}
