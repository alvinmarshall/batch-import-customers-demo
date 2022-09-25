package com.migmeninfo.cipservice.batch.countryoperation;

import com.migmeninfo.cipservice.domain.entity.CustomerCountry;
import com.migmeninfo.cipservice.repository.CountryOperationRepository;
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
public class CountryOperationBatchConfig {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private CustomerRepository customerRepository;
    @Value("${batch-data.default}")
    private Resource defaultResource;
    @Autowired
    private TaskExecutor customerTaskExecutor;
    @Autowired
    private CountryOperationProcessor countryOperationProcessor;
    @Autowired
    private CountryOperationRepository countryOperationRepository;

    @Bean
    @StepScope
    @SneakyThrows
    public FlatFileItemReader<CountryOperationInput> countryOperationReader(@Value("#{jobExecutionContext['unzip_files']}") Object files) {
        String fileName = "countries_of_operation.csv";
        log.info("files: {}", files);
        FlatFileItemReader<CountryOperationInput> itemReader = new FlatFileItemReader<>();
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(countryOperationLineMapper());
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

    private LineMapper<CountryOperationInput> countryOperationLineMapper() {
        String[] props = new ArrayList<>(CountryOperationInput.getCountryOperationHeaders().values()).toArray(new String[0]);
        DefaultLineMapper<CountryOperationInput> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(props);
        BeanWrapperFieldSetMapper<CountryOperationInput> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(CountryOperationInput.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public AsyncItemProcessor<CountryOperationInput, CustomerCountry> asyncCountryOperationProcessor() {
        AsyncItemProcessor<CountryOperationInput, CustomerCountry> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(countryOperationProcessor);
        asyncItemProcessor.setTaskExecutor(customerTaskExecutor);
        return asyncItemProcessor;
    }

    @Bean
    public RepositoryItemWriter<CustomerCountry> countryOperationWriter() {
        RepositoryItemWriter<CustomerCountry> itemWriter = new RepositoryItemWriter<>();
        itemWriter.setRepository(countryOperationRepository);
        itemWriter.setMethodName("save");
        return itemWriter;
    }

    @Bean
    public AsyncItemWriter<CustomerCountry> asyncCountryOperationWriter() {
        AsyncItemWriter<CustomerCountry> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(countryOperationWriter());
        return asyncItemWriter;
    }

    @Bean
    public Step countryOperationStep() {
        return stepBuilderFactory.get("country_of_operation-csv").<CountryOperationInput, Future<CustomerCountry>>chunk(10)
                .reader(countryOperationReader(null))
                .processor(asyncCountryOperationProcessor())
                .writer(asyncCountryOperationWriter())
                .taskExecutor(customerTaskExecutor)
                .build();
    }


}
