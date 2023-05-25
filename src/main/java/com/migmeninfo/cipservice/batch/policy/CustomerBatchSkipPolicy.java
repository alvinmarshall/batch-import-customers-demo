package com.migmeninfo.cipservice.batch.policy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

@Slf4j
public class CustomerBatchSkipPolicy implements SkipPolicy {
    @Override
    public boolean shouldSkip(Throwable throwable, int skipCount) throws SkipLimitExceededException {
        log.error(throwable.getMessage(), throwable);
        return throwable instanceof Exception;
    }
}
