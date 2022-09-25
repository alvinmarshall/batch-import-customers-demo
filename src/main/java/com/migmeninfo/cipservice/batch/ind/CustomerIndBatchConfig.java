package com.migmeninfo.cipservice.batch.ind;

import com.migmeninfo.cipservice.domain.entity.Customer;
import com.migmeninfo.cipservice.repository.CustomerRepository;
import com.migmeninfo.cipservice.utils.BatchUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
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
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.Future;

@Configuration
@Slf4j
public class CustomerIndBatchConfig {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private CustomerRepository customerRepository;

    @Value("${batch-data.default}")
    private Resource defaultResource;

    @Autowired
    private TaskExecutor customerTaskExecutor;

    @Autowired
    private CustomerIndProcessor customerIndProcessor;

    @Bean
    @StepScope
    @SneakyThrows
    public FlatFileItemReader<CustomerIndInput> customerIndReader(@Value("#{jobExecutionContext['unzip_files']}") Object files) {
        String fileName = "customers_ind.csv";
        log.info("files: {}", files);
        FlatFileItemReader<CustomerIndInput> itemReader = new FlatFileItemReader<>();
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(customerIndLineMapper());
        Optional<String> optionalFilePath = BatchUtils.getBatchFileName(files, fileName);
        if (optionalFilePath.isEmpty()) {
            itemReader.setResource(defaultResource);
            return itemReader;
        }
        String filePath = optionalFilePath.get();
        FileUrlResource urlResource = new FileUrlResource(filePath);
        log.info("{}-file-exist: {}", fileName, urlResource.exists());
        itemReader.setResource(urlResource);
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

//    @Bean
//    public CustomerIndProcessor customerIndProcessor() {
//        return new CustomerIndProcessor(customerRepository);
//    }

    @Bean
    public AsyncItemProcessor<CustomerIndInput, Customer> asyncCustomerIndProcessor() {
        AsyncItemProcessor<CustomerIndInput, Customer> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(customerIndProcessor);
        asyncItemProcessor.setTaskExecutor(customerTaskExecutor);
        return asyncItemProcessor;
    }

    @Bean
    public RepositoryItemWriter<Customer> customerIndWriter() {
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public AsyncItemWriter<Customer> asyncCustomerIndWriter() {
        AsyncItemWriter<Customer> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(customerIndWriter());

        return asyncItemWriter;
    }

    @Bean
    public Step customerIndStep() {
        return stepBuilderFactory.get("customer-ind-csv").<CustomerIndInput, Future<Customer>>chunk(10)
                .reader(customerIndReader(null))
                .processor(asyncCustomerIndProcessor())
                .writer(asyncCustomerIndWriter())
                .taskExecutor(customerTaskExecutor)
                .build();
    }


}
