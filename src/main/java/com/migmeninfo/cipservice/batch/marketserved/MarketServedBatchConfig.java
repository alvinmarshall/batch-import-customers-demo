package com.migmeninfo.cipservice.batch.marketserved;

import com.migmeninfo.cipservice.domain.entity.Customer;
import com.migmeninfo.cipservice.domain.entity.MarketServed;
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
public class MarketServedBatchConfig {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private CustomerRepository customerRepository;
    @Value("${batch-data.markets_served}")
    private Resource resource;

    public ItemReader<? extends MarketServedInput> marketServedReader(Resource file) {
        FlatFileItemReader<MarketServedInput> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(file);
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(marketServedLineMapper());
        return itemReader;
    }

    private LineMapper<MarketServedInput> marketServedLineMapper() {
        String[] props = new ArrayList<>(MarketServedInput.getMarketServedHeaders().values()).toArray(new String[0]);
        DefaultLineMapper<MarketServedInput> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(props);
        BeanWrapperFieldSetMapper<MarketServedInput> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(MarketServedInput.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public MarketServedProcessor marketServedProcessor() {
        return new MarketServedProcessor();
    }

    @Bean
    public ItemWriter<MarketServed> marketServedWriter() {
        return list -> list.forEach(marketServed -> {
            Optional<Customer> optionalCustomer = customerRepository
                    .findByCorrelationId(marketServed.getCustomer().getCorrelationId());
            if (optionalCustomer.isPresent()) {
                Customer customer = optionalCustomer.get();
                marketServed.setCustomer(customer);
                HashSet<MarketServed> servedHashSet = new HashSet<>();
                servedHashSet.add(marketServed);
                customer.setMarketsServed(servedHashSet);
                customerRepository.save(customer);
            }
            log.info("marketServed: {}", marketServed);
        });
    }

    @Bean
    public Step marketServedStep() {
        return stepBuilderFactory.get("markets_served-csv").<MarketServedInput, MarketServed>chunk(10)
                .reader(marketServedReader(resource))
                .processor(marketServedProcessor())
                .writer(marketServedWriter())
                .build();
    }


}
