package com.todo.business.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.todo.business.constant.TaskConstants;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    private static final long MAX_CACHE_SIZE = 5000;
    private static final long CACHE_TTL_MINUTES = 30;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(TaskConstants.CACHE_TASKS);
        manager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(MAX_CACHE_SIZE)
                        .expireAfterWrite(CACHE_TTL_MINUTES, TimeUnit.MINUTES)
        );
        return manager;
    }
}
