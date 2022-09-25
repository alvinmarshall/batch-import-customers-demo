package com.migmeninfo.cipservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadConfig {
    @Bean
    public TaskExecutor customerTaskExecutor() {
//        new SimpleAsyncTaskExecutor("cus_batch");
//        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("cus_batch");
//        executor.setConcurrencyLimit(10);
//        return executor;

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(5);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("MultiThreaded-");
        return executor;
    }
}
