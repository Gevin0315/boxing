package com.boxinggym.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 */
@Component
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ============================= Key 操作 =============================

    /**
     * 判断 key 是否存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 删除 key
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 批量删除 key
     */
    public Long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 设置过期时间
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 设置过期时间（秒）
     */
    public Boolean expire(String key, long seconds) {
        return redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    /**
     * 获取过期时间
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    // ============================= String 操作 =============================

    /**
     * 获取缓存
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 设置缓存
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置缓存并设置过期时间
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 设置缓存并设置过期时间（秒）
     */
    public void set(String key, Object value, long seconds) {
        redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    /**
     * 递增
     */
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 递增指定值
     */
    public Long increment(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     */
    public Long decrement(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    /**
     * 递减指定值
     */
    public Long decrement(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    // ============================= Hash 操作 =============================

    /**
     * 获取 Hash 中某个 key 的值
     */
    public Object hGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 获取 Hash 中所有键值对
     */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 设置 Hash 中某个 key 的值
     */
    public void hSet(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 批量设置 Hash
     */
    public void hSetAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 删除 Hash 中某个 key
     */
    public Long hDelete(String key, Object... hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * 判断 Hash 中是否存在某个 key
     */
    public Boolean hHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    // ============================= Set 操作 =============================

    /**
     * 获取 Set 中所有元素
     */
    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 向 Set 中添加元素
     */
    public Long sAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 判断 Set 中是否包含某个元素
     */
    public Boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 获取 Set 的长度
     */
    public Long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 删除 Set 中的元素
     */
    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    // ============================= List 操作 =============================

    /**
     * 获取 List 缓存的内容
     */
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取 List 的长度
     */
    public Long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 根据 index 获取 List 中的元素
     */
    public Object lIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 向 List 左侧添加元素
     */
    public Long lLeftPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 向 List 右侧添加元素
     */
    public Long lRightPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 批量向 List 右侧添加元素
     */
    public Long lRightPushAll(String key, Object... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 从 List 左侧弹出元素
     */
    public Object lLeftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 从 List 右侧弹出元素
     */
    public Object lRightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 删除 List 中的元素
     */
    public Long lRemove(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    // ============================= ZSet 操作 =============================

    /**
     * 向 ZSet 中添加元素
     */
    public Boolean zAdd(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 获取 ZSet 中指定范围的元素（按分数升序）
     */
    public Set<Object> zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 获取 ZSet 中指定范围的元素（按分数降序）
     */
    public Set<Object> zReverseRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key == null ? "" : key, start, end);
    }

    /**
     * 获取 ZSet 中元素的分数
     */
    public Double zScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 获取 ZSet 中指定分数范围内的元素
     */
    public Set<Object> zRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 获取 ZSet 的长度
     */
    public Long zSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    /**
     * 删除 ZSet 中的元素
     */
    public Long zRemove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }
}
