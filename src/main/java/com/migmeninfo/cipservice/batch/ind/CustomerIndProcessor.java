package com.migmeninfo.cipservice.batch.ind;

import com.migmeninfo.cipservice.common.Gender;
import com.migmeninfo.cipservice.common.MaritalStatus;
import com.migmeninfo.cipservice.domain.entity.Customer;
import com.migmeninfo.cipservice.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;

@Slf4j
public class CustomerIndProcessor implements ItemProcessor<CustomerIndInput, Customer> {
    @Override
    public Customer process(CustomerIndInput customerInput) {
        Customer customer = Customer.builder().build();
        BeanUtils.copyProperties(customerInput, customer);
        customer.setMaritalStatus(MaritalStatus.fromString(customerInput.getMaritalStatus()));
        customer.setDob(DateUtils.parseLocalDate(customerInput.getDob()));
        customer.setGender(Gender.fromString(customerInput.getGender()));
        customer.setTinType(customerInput.getTinType());
        return customer;
    }


}
