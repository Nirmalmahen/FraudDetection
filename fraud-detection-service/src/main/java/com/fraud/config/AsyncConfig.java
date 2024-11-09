package com.fraud.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.context.annotation.Bean;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // Set the core number of threads
        executor.setMaxPoolSize(20); // Set the maximum number of threads
        executor.setQueueCapacity(500); // Set the capacity of the queue
        executor.setThreadNamePrefix("FraudCheck-");
        executor.initialize();
        return executor;
    }
}
