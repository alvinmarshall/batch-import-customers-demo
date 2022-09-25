package com.migmeninfo.cipservice.batch.customer.businessunit;

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
public class BusinessUnitBatchConfig {
    private final StepBuilderFactory stepBuilderFactory;
    private final CustomerRepository customerRepository;
    private final TaskExecutor customerTaskExecutor;
    private final CustomerZipFileConfig zipFileConfig;
    private final BusinessUnitProcessor businessUnitProcessor;
    @Value("${batch-data.default}")
    private Resource defaultResource;

    public BusinessUnitBatchConfig(
            @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") StepBuilderFactory stepBuilderFactory,
            CustomerRepository customerRepository,
            TaskExecutor customerTaskExecutor,
            CustomerZipFileConfig zipFileConfig, BusinessUnitProcessor businessUnitProcessor) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.customerRepository = customerRepository;
        this.customerTaskExecutor = customerTaskExecutor;
        this.zipFileConfig = zipFileConfig;
        this.businessUnitProcessor = businessUnitProcessor;
    }

    @Bean
    @StepScope
    @SneakyThrows
    public FlatFileItemReader<BusinessUnitInput> businessUnitReader(@Value("#{jobExecutionContext['unzip_files']}") Object files) {
        String fileName = zipFileConfig.getBusinessUnit();
        FlatFileItemReader<BusinessUnitInput> itemReader = new FlatFileItemReader<>();
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(businessUnitLineMapper());
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
    public AsyncItemProcessor<BusinessUnitInput, Customer> asyncBuinsessUnitProcessor() {
        AsyncItemProcessor<BusinessUnitInput, Customer> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(businessUnitProcessor);
        asyncItemProcessor.setTaskExecutor(customerTaskExecutor);
        return asyncItemProcessor;
    }

    @Bean
    public RepositoryItemWriter<Customer> businessUnitWriter() {
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public AsyncItemWriter<Customer> asyncBusinessUnitWriter() {
        AsyncItemWriter<Customer> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(businessUnitWriter());
        return asyncItemWriter;
    }

    @Bean
    public Step businessUnitStep() {
        return stepBuilderFactory.get("business_units-csv").<BusinessUnitInput, Future<Customer>>chunk(10)
                .reader(businessUnitReader(null))
                .processor(asyncBuinsessUnitProcessor())
                .writer(asyncBusinessUnitWriter())
                .faultTolerant().skipPolicy(new CustomerBatchSkipPolicy())
                .taskExecutor(customerTaskExecutor)
                .build();
    }

}
