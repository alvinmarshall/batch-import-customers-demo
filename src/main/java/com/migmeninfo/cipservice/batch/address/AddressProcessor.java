package com.migmeninfo.cipservice.batch.address;

import com.migmeninfo.cipservice.domain.entity.Address;
import com.migmeninfo.cipservice.domain.entity.Customer;
import com.migmeninfo.cipservice.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class AddressProcessor implements ItemProcessor<AddressInput, Address> {
    private final CustomerRepository customerRepository;

    public AddressProcessor(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Address process(AddressInput addressInput) {
        Optional<Customer> customerOptional = customerRepository
                .findByCorrelationId(addressInput.getCorrelationId());
        if (customerOptional.isEmpty()) return null;
        Customer customer = customerOptional.get();
        Address address = Address.builder().build();
        BeanUtils.copyProperties(addressInput, address);
        address.setCustomer(customer);
        return address;
    }


}
