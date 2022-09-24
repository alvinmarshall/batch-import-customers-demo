package com.migmeninfo.cipservice.batch.document;

import com.migmeninfo.cipservice.domain.entity.Customer;
import com.migmeninfo.cipservice.domain.entity.Document;
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
public class DocumentBatchConfig {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private CustomerRepository customerRepository;
    @Value("${batch-data.documents}")
    private Resource resource;

    public ItemReader<? extends DocumentInput> documentReader(Resource file) {
        FlatFileItemReader<DocumentInput> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(file);
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(documentLineMapper());
        return itemReader;
    }

    private LineMapper<DocumentInput> documentLineMapper() {
        String[] props = new ArrayList<>(DocumentInput.getDocumentHeaders().values()).toArray(new String[0]);
        DefaultLineMapper<DocumentInput> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(props);
        BeanWrapperFieldSetMapper<DocumentInput> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(DocumentInput.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public DocumentProcessor documentProcessor() {
        return new DocumentProcessor();
    }

    @Bean
    public ItemWriter<Document> documentWriter() {
        return list -> list.forEach(document -> {
            Optional<Customer> optionalCustomer = customerRepository
                    .findByCorrelationId(document.getCustomer().getCorrelationId());
            if (optionalCustomer.isPresent()) {
                Customer customer = optionalCustomer.get();
                document.setCustomer(customer);
                HashSet<Document> documents = new HashSet<>();
                documents.add(document);
                customer.setDocuments(documents);
                customerRepository.save(customer);
            }
            log.info("document: {}", document);
        });
    }

    @Bean
    public Step documentStep() {
        return stepBuilderFactory.get("document-csv").<DocumentInput, Document>chunk(10)
                .reader(documentReader(resource))
                .processor(documentProcessor())
                .writer(documentWriter())
                .build();
    }


}
