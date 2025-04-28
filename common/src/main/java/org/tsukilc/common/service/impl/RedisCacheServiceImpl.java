package org.tsukilc.common.service.impl;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.tsukilc.common.service.CacheService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis缓存服务实现
 * 平台化设计：提供标准化的Redis缓存操作，屏蔽底层实现细节
 */
@Service
public class RedisCacheServiceImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisCacheServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        return (T) value;
    }

    @Override
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    @Override
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public Boolean zAdd(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Set<T> zRange(String key, long start, long end, Class<T> clazz) {
        Set<Object> objects = redisTemplate.opsForZSet().range(key, start, end);
        if (objects == null) {
            return Set.of();
        }
        return objects.stream()
                .map(obj -> (T) obj)
                .collect(Collectors.toSet());
    }

    @Override
    public Long zCard(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    @Override
    public Long zRemove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    @Override
    public void multiSet(Map<String, Object> map) {
        redisTemplate.opsForValue().multiSet(map);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> multiGet(List<String> keys, Class<T> clazz) {
        List<Object> objects = redisTemplate.opsForValue().multiGet(keys);
        if (objects == null) {
            return new ArrayList<>();
        }
        return objects.stream()
                .map(obj -> obj != null ? (T) obj : null)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, String> GetAll(String key) {
        return redisTemplate.opsForHash().entries(key)
                .entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> entry.getValue() != null ? entry.getValue().toString() : null
                ));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> lRange(String key, long start, long end) {
        List<Object> objects = redisTemplate.opsForList().range(key, start, end);
        if (objects == null) {
            return new ArrayList<>();
        }
        return objects.stream()
                .map(obj -> obj != null ? obj.toString() : null)
                .collect(Collectors.toList());
    }

    @Override
    public Long lPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    @Override
    public Long hIncrBy(String key, String field, long delta) {
        return redisTemplate.opsForHash().increment(key, field, delta);
    }
}
