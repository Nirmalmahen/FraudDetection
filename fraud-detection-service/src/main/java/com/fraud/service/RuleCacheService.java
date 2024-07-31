package com.fraud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RuleCacheService {

    private static final String RULES_CACHE_KEY = "fraudRules";
    @Autowired
    private StringRedisTemplate redisTemplate;

    public void loadRulesIntoCache(Map<String, String> rules) {
        redisTemplate.opsForHash().putAll(RULES_CACHE_KEY, rules);
    }

    public <T> T getRule(String ruleName) {
        return (T) redisTemplate.opsForHash().get(RULES_CACHE_KEY, ruleName);
    }

}
