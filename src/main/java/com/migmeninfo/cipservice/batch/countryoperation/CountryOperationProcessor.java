package com.migmeninfo.cipservice.batch.countryoperation;

import com.migmeninfo.cipservice.domain.entity.Customer;
import com.migmeninfo.cipservice.domain.entity.CustomerCountry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class CountryOperationProcessor implements ItemProcessor<CountryOperationInput, CustomerCountry> {
    @Override
    public CustomerCountry process(CountryOperationInput countryOperationInput) {
        CustomerCountry productsOffered = CustomerCountry.builder()
                .iso2Code(countryOperationInput.getCountry())
                .customer(Customer.builder().correlationId(countryOperationInput.getCorrelationId()).build())
                .build();
        return productsOffered;
    }


}
