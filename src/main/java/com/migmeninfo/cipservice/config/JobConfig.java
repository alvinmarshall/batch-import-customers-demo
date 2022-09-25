package com.migmeninfo.cipservice.config;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobConfig {

    @Bean
    public ExecutionContext executionContext() {
        return new ExecutionContext();
    }
}
