package com.migmeninfo.cipservice.batch.account;

import com.migmeninfo.cipservice.common.AccountType;
import com.migmeninfo.cipservice.domain.entity.Account;
import com.migmeninfo.cipservice.domain.entity.Customer;
import com.migmeninfo.cipservice.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;

@Slf4j
public class AccountProcessor implements ItemProcessor<AccountInput, Account> {
    @Override
    public Account process(AccountInput accountInput) {
        Account account = Account.builder().build();
        BeanUtils.copyProperties(accountInput, account);
        if (!ObjectUtils.isEmpty(accountInput.getExpectedYearlyActivityValue())){
            account.setExpectedYearlyActivityValue(NumberUtils.createBigDecimal(accountInput.getExpectedYearlyActivityValue()));
        }
        account.setAccountType(AccountType.fromString(accountInput.getAccountType()));
        account.setOpeningDate(DateUtils.parseLocalDate(accountInput.getOpeningDate()));
        account.setCustomer(Customer.builder().correlationId(accountInput.getCorrelationId()).build());
        return account;
    }


}
