package com.migmeninfo.cipservice.batch.ind;

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
public class CustomerIndBatchConfig {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private CustomerRepository customerRepository;
    @Value("${batch-data.ind}")
    private Resource resource;


    public ItemReader<? extends CustomerIndInput> customerIndReader(Resource file) {
        FlatFileItemReader<CustomerIndInput> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(file);
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(customerIndLineMapper());
        return itemReader;
    }

    private LineMapper<CustomerIndInput> customerIndLineMapper() {
        String[] props = new ArrayList<>(CustomerIndInput.getCustomerHeaders().values()).toArray(new String[0]);
        DefaultLineMapper<CustomerIndInput> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(props);
        BeanWrapperFieldSetMapper<CustomerIndInput> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(CustomerIndInput.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public CustomerIndProcessor customerIndProcessor() {
        return new CustomerIndProcessor();
    }

    @Bean
    public RepositoryItemWriter<Customer> customerIndWriter() {
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step customerIndStep() {
        return stepBuilderFactory.get("customer-ind-csv").<CustomerIndInput, Customer>chunk(10)
                .reader(customerIndReader(resource))
                .processor(customerIndProcessor())
                .writer(customerIndWriter())
                .build();
    }


}
