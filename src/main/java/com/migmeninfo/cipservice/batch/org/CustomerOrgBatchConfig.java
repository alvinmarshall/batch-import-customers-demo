package com.migmeninfo.cipservice.batch.org;

import com.migmeninfo.cipservice.domain.entity.Customer;
import com.migmeninfo.cipservice.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
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

@Configuration
@Slf4j
public class CustomerOrgBatchConfig {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private CustomerRepository customerRepository;
    @Value("${batch-data.org}")
    private Resource resource;

    public ItemReader<? extends CustomerOrgInput> customerOrgReader(Resource file) {
        FlatFileItemReader<CustomerOrgInput> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(file);
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(customerOrgLineMapper());
        return itemReader;
    }

    private LineMapper<CustomerOrgInput> customerOrgLineMapper() {
        String[] props = new ArrayList<>(CustomerOrgInput.getCustomerHeaders().values()).toArray(new String[0]);
        DefaultLineMapper<CustomerOrgInput> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(props);
        BeanWrapperFieldSetMapper<CustomerOrgInput> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(CustomerOrgInput.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public CustomerOrgProcessor customerOrgProcessor() {
        return new CustomerOrgProcessor();
    }

    @Bean
    public RepositoryItemWriter<Customer> customerOrgWriter() {
        RepositoryItemWriter<Customer> itemWriter = new RepositoryItemWriter<>();
        itemWriter.setRepository(customerRepository);
        itemWriter.setMethodName("save");
        return itemWriter;
    }

    @Bean
    public Step customerOrgStep() {
        return stepBuilderFactory.get("customer-org-csv").<CustomerOrgInput, Customer>chunk(10)
                .reader(customerOrgReader(resource))
                .processor(customerOrgProcessor())
                .writer(customerOrgWriter())
                .build();
    }


}
