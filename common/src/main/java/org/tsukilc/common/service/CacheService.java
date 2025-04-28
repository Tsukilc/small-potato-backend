package org.tsukilc.common.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存服务接口
 * 平台化设计：提供统一的缓存操作接口，屏蔽底层实现细节
 */
public interface CacheService {
    
    /**
     * 设置缓存
     */
    void set(String key, Object value);
    
    /**
     * 设置缓存并设置过期时间
     */
    void set(String key, Object value, long timeout, TimeUnit unit);
    
    /**
     * 获取缓存
     */
    <T> T get(String key, Class<T> clazz);
    
    /**
     * 删除缓存
     */
    Boolean delete(String key);
    
    /**
     * 判断缓存是否存在
     */
    Boolean hasKey(String key);
    
    /**
     * 设置过期时间
     */
    Boolean expire(String key, long timeout, TimeUnit unit);
    
    /**
     * 自增操作
     */
    Long increment(String key, long delta);
    
    /**
     * 向有序集合添加元素
     */
    Boolean zAdd(String key, Object value, double score);
    
    /**
     * 获取有序集合的成员
     */
    <T> Set<T> zRange(String key, long start, long end, Class<T> clazz);
    
    /**
     * 获取有序集合的成员数量
     */
    Long zCard(String key);
    
    /**
     * 移除有序集合中的成员
     */
    Long zRemove(String key, Object... values);
    
    /**
     * 批量设置缓存
     */
    void multiSet(Map<String, Object> map);
    
    /**
     * 批量获取缓存
     */
    <T> List<T> multiGet(List<String> keys, Class<T> clazz);
} 