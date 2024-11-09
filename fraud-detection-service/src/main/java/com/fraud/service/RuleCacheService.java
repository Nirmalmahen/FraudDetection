package com.fraud.service;

import com.fraud.exception.RuleCacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class RuleCacheService {

    private static final Logger logger = LoggerFactory.getLogger(RuleCacheService.class);
    private static final String RULES_CACHE_KEY = "fraudRules";
    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Async
    public ListenableFuture<Void> loadRulesIntoCache(Map<String, String> rules) {
        ListenableFutureTask<Void> task = new ListenableFutureTask<>(() -> {
            logger.debug("Loading rules into cache with key: {}", RULES_CACHE_KEY);
            redisTemplate.opsForHash().putAll(RULES_CACHE_KEY, rules);
            logger.info("Successfully loaded rules into cache.");
        }, null);
        executorService.submit(task);
        return task;
    }

    @Cacheable("ruleSetCache")
    @Async
    public <T> ListenableFuture<T> getRule(String ruleName) {
        ListenableFutureTask<T> task = new ListenableFutureTask<>(() -> {
            logger.debug("Fetching rule from cache: {}", ruleName);
            T rule = (T) redisTemplate.opsForHash().get(RULES_CACHE_KEY, ruleName);
            if (rule == null) {
                logger.warn("Rule not found in cache: {}", ruleName);
            } else {
                logger.info("Successfully fetched rule from cache: {}", ruleName);
            }
            return rule;
        });
        executorService.submit(task);
        return task;
    }
}
