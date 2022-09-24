package com.migmeninfo.cipservice.batch.businessunit;

import com.migmeninfo.cipservice.domain.entity.BusinessUnit;
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
public class BusinessUnitBatchConfig {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private CustomerRepository customerRepository;
    @Value("${batch-data.business_units}")
    private Resource resource;

    public ItemReader<? extends BusinessUnitInput> businessUnitReader(Resource file) {
        FlatFileItemReader<BusinessUnitInput> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(file);
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(businessUnitLineMapper());
        return itemReader;
    }

    private LineMapper<BusinessUnitInput> businessUnitLineMapper() {
        String[] props = new ArrayList<>(BusinessUnitInput.getBusinessUnitHeaders().values()).toArray(new String[0]);
        DefaultLineMapper<BusinessUnitInput> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(props);
        BeanWrapperFieldSetMapper<BusinessUnitInput> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(BusinessUnitInput.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public BuinsessUnitProcessor buinsessUnitProcessor() {
        return new BuinsessUnitProcessor();
    }

    @Bean
    public ItemWriter<BusinessUnit> businessUnitWriter() {
        return list -> list.forEach(businessUnit -> {
            Optional<Customer> optionalCustomer = customerRepository
                    .findByCorrelationId(businessUnit.getCorrelationId());
            if (optionalCustomer.isPresent()) {
                Customer customer = optionalCustomer.get();
                HashSet<BusinessUnit> businessUnits = new HashSet<>();
                businessUnits.add(businessUnit);
                customer.setBusinessUnits(businessUnits);
                customerRepository.save(customer);
            }
            log.info("business-unit: {}", businessUnit);
        });
    }

    @Bean
    public Step businessUnitStep() {
        return stepBuilderFactory.get("business_units-csv").<BusinessUnitInput, BusinessUnit>chunk(10)
                .reader(businessUnitReader(resource))
                .processor(buinsessUnitProcessor())
                .writer(businessUnitWriter())
                .build();
    }


}
