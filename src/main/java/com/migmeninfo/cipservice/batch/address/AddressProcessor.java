package com.migmeninfo.cipservice.batch.address;

import com.migmeninfo.cipservice.domain.entity.Address;
import com.migmeninfo.cipservice.domain.entity.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;

@Slf4j
public class AddressProcessor implements ItemProcessor<AddressInput, Address> {
    @Override
    public Address process(AddressInput addressInput) {
        Address address = Address.builder().build();
        BeanUtils.copyProperties(addressInput, address);
        address.setCustomer(Customer.builder().correlationId(addressInput.getCorrelationId()).build());
        return address;
    }


}
