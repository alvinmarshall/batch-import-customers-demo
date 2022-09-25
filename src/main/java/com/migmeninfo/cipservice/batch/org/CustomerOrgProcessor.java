package com.migmeninfo.cipservice.batch.org;

import com.migmeninfo.cipservice.common.CustomerType;
import com.migmeninfo.cipservice.domain.entity.Customer;
import com.migmeninfo.cipservice.repository.CustomerRepository;
import com.migmeninfo.cipservice.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class CustomerOrgProcessor implements ItemProcessor<CustomerOrgInput, Customer> {
    private final CustomerRepository customerRepository;

    public CustomerOrgProcessor(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer process(CustomerOrgInput customerInput) {
        Optional<Customer> optionalCustomer = customerRepository
                .findByCorrelationId(customerInput.getCorrelationId());
        if (optionalCustomer.isPresent()) return optionalCustomer.get();
        Customer customer = Customer.builder().build();
        BeanUtils.copyProperties(customerInput, customer);
        customer.setTinType(customerInput.getTinType());
        customer.setCustomerType(CustomerType.fromString(customerInput.getCustomerType()));
        customer.setDateOfIncorporation(DateUtils.parseLocalDate(customerInput.getDateOfIncorporation()));
        return customer;
    }


}
