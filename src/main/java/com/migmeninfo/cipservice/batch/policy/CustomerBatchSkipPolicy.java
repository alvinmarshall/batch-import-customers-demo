package com.migmeninfo.cipservice.batch.policy;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

public class CustomerBatchSkipPolicy implements SkipPolicy {
    @Override
    public boolean shouldSkip(Throwable throwable, int skipCount) throws SkipLimitExceededException {
        return throwable instanceof Exception;
    }
}
