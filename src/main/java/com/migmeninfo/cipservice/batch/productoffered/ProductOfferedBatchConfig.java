package com.migmeninfo.cipservice.batch.productoffered;

import com.migmeninfo.cipservice.domain.entity.Customer;
import com.migmeninfo.cipservice.domain.entity.ProductsOffered;
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
public class ProductOfferedBatchConfig {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private CustomerRepository customerRepository;
    @Value("${batch-data.products_offered}")
    private Resource resource;

    public ItemReader<? extends ProductOfferedInput> productOfferedReader(Resource file) {
        FlatFileItemReader<ProductOfferedInput> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(file);
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(productOfferedLineMapper());
        return itemReader;
    }

    private LineMapper<ProductOfferedInput> productOfferedLineMapper() {
        String[] props = new ArrayList<>(ProductOfferedInput.getProductOfferedHeaders().values()).toArray(new String[0]);
        DefaultLineMapper<ProductOfferedInput> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(props);
        BeanWrapperFieldSetMapper<ProductOfferedInput> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(ProductOfferedInput.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public ProductOfferedProcessor productOfferedProcessor() {
        return new ProductOfferedProcessor();
    }

    @Bean
    public ItemWriter<ProductsOffered> productOfferedWriter() {
        return list -> list.forEach(productsOffered -> {
            Optional<Customer> optionalCustomer = customerRepository
                    .findByCorrelationId(productsOffered.getCustomer().getCorrelationId());
            if (optionalCustomer.isPresent()) {
                Customer customer = optionalCustomer.get();
                productsOffered.setCustomer(customer);
                HashSet<ProductsOffered> offeredHashSet = new HashSet<>();
                offeredHashSet.add(productsOffered);
                customer.setProductsOffered(offeredHashSet);
                customerRepository.save(customer);
            }
            log.info("productsOffered: {}", productsOffered);
        });
    }

    @Bean
    public Step productOfferedStep() {
        return stepBuilderFactory.get("products_offered-csv").<ProductOfferedInput, ProductsOffered>chunk(10)
                .reader(productOfferedReader(resource))
                .processor(productOfferedProcessor())
                .writer(productOfferedWriter())
                .build();
    }


}
