package com.migmeninfo.cipservice.batch.customer.businessunit;

import com.migmeninfo.cipservice.domain.entity.BusinessUnit;
import com.migmeninfo.cipservice.domain.entity.Customer;
import com.migmeninfo.cipservice.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;

@Component
@Slf4j
public class BusinessUnitProcessor implements ItemProcessor<BusinessUnitInput, Customer> {
    private final CustomerRepository customerRepository;

    public BusinessUnitProcessor(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer process(BusinessUnitInput businessUnitInput) {
        Optional<Customer> optionalCustomer = customerRepository.findByCorrelationId(businessUnitInput.getCorrelationId());
        if (optionalCustomer.isEmpty()) return null;
        Customer customer = optionalCustomer.get();
        BusinessUnit businessUnit = BusinessUnit.builder()
                .correlationId(businessUnitInput.getCorrelationId())
                .businessUnit(businessUnitInput.getName())
                .isDefault(true)
                .build();
        HashSet<BusinessUnit> businessUnits = new HashSet<>();
        businessUnits.add(businessUnit);
        customer.setBusinessUnits(businessUnits);
        return customer;
    }


}
