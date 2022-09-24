package com.migmeninfo.cipservice.batch.businessunit;

import com.migmeninfo.cipservice.domain.entity.BusinessUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class BuinsessUnitProcessor implements ItemProcessor<BusinessUnitInput, BusinessUnit> {
    @Override
    public BusinessUnit process(BusinessUnitInput businessUnitInput) {
        return BusinessUnit.builder()
                .correlationId(businessUnitInput.getCorrelationId())
                .businessUnit(businessUnitInput.getName())
                .isDefault(true)
                .build();
    }


}
