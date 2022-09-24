package com.migmeninfo.cipservice.batch.account;

import com.migmeninfo.cipservice.domain.entity.Account;
import com.migmeninfo.cipservice.domain.entity.Customer;
import com.migmeninfo.cipservice.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

@Configuration
@Slf4j
public class AccountBatchConfig {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private CustomerRepository customerRepository;
    @Value("${batch-data.accounts}")
    private Resource resource;

    public ItemReader<? extends AccountInput> accountReader(Resource file) {
        FlatFileItemReader<AccountInput> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(file);
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(accountLineMapper());
        return itemReader;
    }

    private LineMapper<AccountInput> accountLineMapper() {
        String[] props = new ArrayList<>(AccountInput.getAccountHeaders().values()).toArray(new String[0]);
        DefaultLineMapper<AccountInput> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(props);
        BeanWrapperFieldSetMapper<AccountInput> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(AccountInput.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public AccountProcessor accountProcessor() {
        return new AccountProcessor();
    }

    @Bean
    public ItemWriter<Account> accountWriter() {
        return list -> list.forEach(account -> {
            Optional<Customer> optionalCustomer = customerRepository
                    .findByCorrelationId(account.getCustomer().getCorrelationId());
            if (optionalCustomer.isPresent()) {
                Customer customer = optionalCustomer.get();
                account.setCustomer(customer);
                HashSet<Account> accounts = new HashSet<>();
                accounts.add(account);
                customer.setAccounts(accounts);
                customerRepository.save(customer);
            }
            log.info("account: {}", account);
        });
    }

    @Bean
    public Step accountStep() {
        return stepBuilderFactory.get("accounts-csv").<AccountInput, Account>chunk(10)
                .reader(accountReader(resource))
                .processor(accountProcessor())
                .writer(accountWriter())
                .build();
    }


}
