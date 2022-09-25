package com.migmeninfo.cipservice.batch.account;

import com.migmeninfo.cipservice.common.AccountType;
import com.migmeninfo.cipservice.domain.entity.Account;
import com.migmeninfo.cipservice.domain.entity.Customer;
import com.migmeninfo.cipservice.repository.CustomerRepository;
import com.migmeninfo.cipservice.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class AccountProcessor implements ItemProcessor<AccountInput, Account> {
    private final CustomerRepository customerRepository;

    public AccountProcessor(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Account process(AccountInput accountInput) {
        Account account = Account.builder().build();
        BeanUtils.copyProperties(accountInput, account);
        Optional<Customer> optionalCustomer = customerRepository.findByCorrelationId(accountInput.getCorrelationId());
        if (optionalCustomer.isEmpty()) return null;
        Customer customer = optionalCustomer.get();
        if (!ObjectUtils.isEmpty(accountInput.getExpectedYearlyActivityValue())) {
            account.setExpectedYearlyActivityValue(NumberUtils.createBigDecimal(accountInput.getExpectedYearlyActivityValue()));
        }
        account.setAccountType(AccountType.fromString(accountInput.getAccountType()));
        account.setOpeningDate(DateUtils.parseLocalDate(accountInput.getOpeningDate()));
        account.setCustomer(customer);
        return account;
    }


}
