package com.migmeninfo.cipservice.batch.customer.org;

import com.migmeninfo.cipservice.batch.policy.CustomerBatchSkipPolicy;
import com.migmeninfo.cipservice.config.CustomerZipFileConfig;
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
public class CustomerOrgBatchConfig {
    private final StepBuilderFactory stepBuilderFactory;
    private final CustomerRepository customerRepository;
    private final TaskExecutor customerTaskExecutor;
    private final CustomerOrgProcessor customerOrgProcessor;
    private final CustomerZipFileConfig zipFileConfig;
    @Value("${batch-data.default}")
    private Resource defaultResource;

    public CustomerOrgBatchConfig(
            @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") StepBuilderFactory stepBuilderFactory,
            CustomerRepository customerRepository,
            TaskExecutor customerTaskExecutor,
            CustomerOrgProcessor customerOrgProcessor,
            CustomerZipFileConfig zipFileConfig
    ) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.customerRepository = customerRepository;
        this.customerTaskExecutor = customerTaskExecutor;
        this.customerOrgProcessor = customerOrgProcessor;
        this.zipFileConfig = zipFileConfig;
    }

    @Bean
    @StepScope
    @SneakyThrows
    public FlatFileItemReader<CustomerOrgInput> customerOrgReader(@Value("#{jobExecutionContext['unzip_files']}") Object files) {
        String fileName = zipFileConfig.getOrganization();
        FlatFileItemReader<CustomerOrgInput> itemReader = new FlatFileItemReader<>();
        itemReader.setLineMapper(customerOrgLineMapper());
        itemReader.setLinesToSkip(1);
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
    public AsyncItemProcessor<CustomerOrgInput, Customer> asyncCustomerOrgProcessor() {
        AsyncItemProcessor<CustomerOrgInput, Customer> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(customerOrgProcessor);
        asyncItemProcessor.setTaskExecutor(customerTaskExecutor);
        return asyncItemProcessor;
    }

    @Bean
    public RepositoryItemWriter<Customer> customerOrgWriter() {
        RepositoryItemWriter<Customer> itemWriter = new RepositoryItemWriter<>();
        itemWriter.setRepository(customerRepository);
        itemWriter.setMethodName("save");
        return itemWriter;
    }

    @Bean
    public AsyncItemWriter<Customer> asyncCustomerOrgWriter() {
        AsyncItemWriter<Customer> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(customerOrgWriter());
        return asyncItemWriter;
    }

    @Bean
    public Step customerOrgStep() {
        return stepBuilderFactory.get("customer-org-csv").<CustomerOrgInput, Future<Customer>>chunk(10)
                .reader(customerOrgReader(null))
                .processor(asyncCustomerOrgProcessor())
                .writer(asyncCustomerOrgWriter())
                .faultTolerant().skipPolicy(new CustomerBatchSkipPolicy())
                .taskExecutor(customerTaskExecutor)
                .build();
    }


}
