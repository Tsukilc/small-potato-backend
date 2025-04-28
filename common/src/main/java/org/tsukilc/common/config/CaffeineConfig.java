package org.tsukilc.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine本地缓存配置
 * 平台化设计：提供标准化的本地缓存组件，减轻Redis压力
 */
@Configuration
public class CaffeineConfig {

    /**
     * 配置Caffeine缓存管理器，作为主缓存管理器
     * 本地缓存作为一级缓存，优先于Redis进行查询，减轻Redis负担
     */
    @Bean
    @Primary
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 初始容量
                .initialCapacity(100)
                // 最大容量，超过后会自动清理
                .maximumSize(1000)
                // 过期时间，30分钟后过期
                .expireAfterWrite(30, TimeUnit.MINUTES)
                // 统计缓存命中率
                .recordStats());
        return cacheManager;
    }
} 