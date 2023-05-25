package com.migmeninfo.cipservice.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CustomerJobNotificationListener extends JobExecutionListenerSupport {
    @Override
    public void afterJob(JobExecution jobExecution) {
        List<String> errors = jobExecution.getAllFailureExceptions()
                .stream()
                .map(Throwable::getMessage).collect(Collectors.toList());
        log.info("!!! JOB FAILED! Check errors: {}", errors);
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");
        }
        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.info("!!! JOB FAILED! Check errors: {}", errors);
        }
    }
}
