package com.fraud.config;

import com.fraud.service.RuleCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    @Autowired
    private RuleCacheService ruleCacheService;

    @Bean
    public void initializeCache() {
        Map<String, String> rules = new HashMap<>();
        rules.put("amountLimit", "500.00");
        rules.put("restrictedMerchant", "MerchantX");
        rules.put("businessStartHour", "9");
        rules.put("businessEndHour", "17");

        ruleCacheService.loadRulesIntoCache(rules);
    }


    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // Cache TTL
                .disableCachingNullValues();
        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory))
                .cacheDefaults(config)
                .build();
    }

}
