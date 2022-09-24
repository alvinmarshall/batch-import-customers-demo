package com.migmeninfo.cipservice.batch.address;

import com.migmeninfo.cipservice.domain.entity.Address;
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
public class AddressBatchConfig {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private CustomerRepository customerRepository;
    @Value("${batch-data.addresses}")
    private Resource resource;


    public ItemReader<? extends AddressInput> addressReader(Resource file) {
        FlatFileItemReader<AddressInput> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(file);
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(addressLineMapper());
        return itemReader;
    }

    private LineMapper<AddressInput> addressLineMapper() {
        String[] props = new ArrayList<>(AddressInput.getAddressHeaders().values()).toArray(new String[0]);
        DefaultLineMapper<AddressInput> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(props);
        BeanWrapperFieldSetMapper<AddressInput> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(AddressInput.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public AddressProcessor addressProcessor() {
        return new AddressProcessor();
    }

    @Bean
    public ItemWriter<Address> addressWriter() {
        return list -> list.forEach(address -> {
            Optional<Customer> optionalCustomer = customerRepository
                    .findByCorrelationId(address.getCustomer().getCorrelationId());
            if (optionalCustomer.isPresent()) {
                Customer customer = optionalCustomer.get();
                address.setCustomer(customer);
                HashSet<Address> addresses = new HashSet<>();
                addresses.add(address);
                customer.setAddresses(addresses);
                customerRepository.save(customer);
            }

            log.info("address: {}", address);
        });
    }

    @Bean
    public Step addressStep() {
        return stepBuilderFactory.get("addresses-csv").<AddressInput, Address>chunk(10)
                .reader(addressReader(resource))
                .processor(addressProcessor())
                .writer(addressWriter())
                .build();
    }


}
