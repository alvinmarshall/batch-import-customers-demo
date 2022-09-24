package com.migmeninfo.cipservice.batch.org;

import com.migmeninfo.cipservice.common.CustomerType;
import com.migmeninfo.cipservice.domain.entity.Customer;
import com.migmeninfo.cipservice.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;

@Slf4j
public class CustomerOrgProcessor implements ItemProcessor<CustomerOrgInput, Customer> {
    @Override
    public Customer process(CustomerOrgInput customerInput) {
        Customer customer = Customer.builder().build();
        BeanUtils.copyProperties(customerInput, customer);
        customer.setTinType(customerInput.getTinType());
        customer.setCustomerType(CustomerType.fromString(customerInput.getCustomerType()));
        customer.setDateOfIncorporation(DateUtils.parseLocalDate(customerInput.getDateOfIncorporation()));
        return customer;
    }


}
