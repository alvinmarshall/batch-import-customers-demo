package com.migmeninfo.cipservice.batch.customer.countryoperation;

import com.migmeninfo.cipservice.domain.entity.Customer;
import com.migmeninfo.cipservice.domain.entity.CustomerCountry;
import com.migmeninfo.cipservice.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class CountryOperationProcessor implements ItemProcessor<CountryOperationInput, CustomerCountry> {
    private final CustomerRepository customerRepository;

    public CountryOperationProcessor(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerCountry process(CountryOperationInput countryOperationInput) {
        Optional<Customer> optionalCustomer = customerRepository
                .findByCorrelationId(countryOperationInput.getCorrelationId());

        if (optionalCustomer.isEmpty()) return null;
        Customer customer = optionalCustomer.get();
        return CustomerCountry.builder()
                .iso2Code(countryOperationInput.getCountry())
                .customer(customer)
                .build();
    }


}
