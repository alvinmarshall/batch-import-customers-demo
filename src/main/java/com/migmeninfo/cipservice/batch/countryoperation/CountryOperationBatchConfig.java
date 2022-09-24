package com.migmeninfo.cipservice.batch.countryoperation;

import com.migmeninfo.cipservice.domain.entity.Customer;
import com.migmeninfo.cipservice.domain.entity.CustomerCountry;
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
public class CountryOperationBatchConfig {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private CustomerRepository customerRepository;
    @Value("${batch-data.countries_of_operation}")
    private Resource resource;

    public ItemReader<? extends CountryOperationInput> countryOperationReader(Resource file) {
        FlatFileItemReader<CountryOperationInput> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(file);
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(countryOperationLineMapper());
        return itemReader;
    }

    private LineMapper<CountryOperationInput> countryOperationLineMapper() {
        String[] props = new ArrayList<>(CountryOperationInput.getCountryOperationHeaders().values()).toArray(new String[0]);
        DefaultLineMapper<CountryOperationInput> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(props);
        BeanWrapperFieldSetMapper<CountryOperationInput> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(CountryOperationInput.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public CountryOperationProcessor countryOperationProcessor() {
        return new CountryOperationProcessor();
    }

    @Bean
    public ItemWriter<CustomerCountry> countryOperationWriter() {
        return list -> list.forEach(customerCountry -> {
            Optional<Customer> optionalCustomer = customerRepository
                    .findByCorrelationId(customerCountry.getCustomer().getCorrelationId());
            if (optionalCustomer.isPresent()) {
                Customer customer = optionalCustomer.get();
                customerCountry.setCustomer(customer);
                HashSet<CustomerCountry> customerCountries = new HashSet<>();
                customerCountries.add(customerCountry);
                customer.setCustomerCountries(customerCountries);
                customerRepository.save(customer);
            }
            log.info("customerCountry: {}", customerCountry);
        });
    }

    @Bean
    public Step countryOperationStep() {
        return stepBuilderFactory.get("country_of_operation-csv").<CountryOperationInput, CustomerCountry>chunk(10)
                .reader(countryOperationReader(resource))
                .processor(countryOperationProcessor())
                .writer(countryOperationWriter())
                .build();
    }


}
