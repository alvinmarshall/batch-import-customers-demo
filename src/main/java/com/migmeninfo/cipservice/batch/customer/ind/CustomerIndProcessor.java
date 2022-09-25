package com.migmeninfo.cipservice.batch.customer.ind;

import com.migmeninfo.cipservice.common.Gender;
import com.migmeninfo.cipservice.common.MaritalStatus;
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
public class CustomerIndProcessor implements ItemProcessor<CustomerIndInput, Customer> {
    private final CustomerRepository customerRepository;

    public CustomerIndProcessor(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer process(CustomerIndInput customerInput) {
        Optional<Customer> optionalCustomer = customerRepository.findByCorrelationId(customerInput.getCorrelationId());
        if (optionalCustomer.isPresent()) return optionalCustomer.get();
        Customer customer = Customer.builder().build();
        BeanUtils.copyProperties(customerInput, customer);
        customer.setMaritalStatus(MaritalStatus.fromString(customerInput.getMaritalStatus()));
        customer.setDob(DateUtils.parseLocalDate(customerInput.getDob()));
        customer.setGender(Gender.fromString(customerInput.getGender()));
        customer.setTinType(customerInput.getTinType());
        return customer;
    }


}
