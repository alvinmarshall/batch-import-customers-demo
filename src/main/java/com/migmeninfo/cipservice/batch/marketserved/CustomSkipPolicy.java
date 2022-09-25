package com.migmeninfo.cipservice.batch.marketserved;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemStreamException;

public class CustomSkipPolicy implements SkipPolicy {


    @Override
    public boolean shouldSkip(Throwable throwable, int skipCount)
            throws SkipLimitExceededException {

        if (throwable instanceof Exception) return true;
        if (throwable instanceof ItemStreamException) return true;
        return false;
    }
}
