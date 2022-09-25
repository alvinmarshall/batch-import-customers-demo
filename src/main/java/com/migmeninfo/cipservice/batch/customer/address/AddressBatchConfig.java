package com.migmeninfo.cipservice.batch.customer.address;

import com.migmeninfo.cipservice.batch.policy.CustomerBatchSkipPolicy;
import com.migmeninfo.cipservice.config.CustomerZipFileConfig;
import com.migmeninfo.cipservice.domain.entity.Address;
import com.migmeninfo.cipservice.repository.AddressRepository;
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
public class AddressBatchConfig {
    private final StepBuilderFactory stepBuilderFactory;
    private final AddressRepository addressRepository;
    private final TaskExecutor customerTaskExecutor;
    private final AddressProcessor addressProcessor;
    private final CustomerZipFileConfig zipFileConfig;
    @Value("${batch-data.default}")
    private Resource defaultResource;

    public AddressBatchConfig(
            @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") StepBuilderFactory stepBuilderFactory,
            AddressRepository addressRepository,
            TaskExecutor customerTaskExecutor,
            AddressProcessor addressProcessor,
            CustomerZipFileConfig zipFileConfig) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.addressRepository = addressRepository;
        this.customerTaskExecutor = customerTaskExecutor;
        this.addressProcessor = addressProcessor;
        this.zipFileConfig = zipFileConfig;
    }

    @Bean
    @StepScope
    @SneakyThrows
    public FlatFileItemReader<AddressInput> addressReader(@Value("#{jobExecutionContext['unzip_files']}") Object files) {
        String fileName = zipFileConfig.getAddress();
        FlatFileItemReader<AddressInput> itemReader = new FlatFileItemReader<>();
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(addressLineMapper());
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

    private LineMapper<AddressInput> addressLineMapper() {
        String[] props = new ArrayList<>(AddressInput.getAddressHeaders().values()).toArray(new String[0]);
        DefaultLineMapper<AddressInput> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(props);
        BeanWrapperFieldSetMapper<AddressInput> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(AddressInput.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public AsyncItemProcessor<AddressInput, Address> asyncAddressProcessor() {
        AsyncItemProcessor<AddressInput, Address> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(addressProcessor);
        asyncItemProcessor.setTaskExecutor(customerTaskExecutor);
        return asyncItemProcessor;
    }

    @Bean
    public RepositoryItemWriter<Address> addressWriter() {
        RepositoryItemWriter<Address> writer = new RepositoryItemWriter<>();
        writer.setRepository(addressRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public AsyncItemWriter<Address> asyncAddressWriter() {
        AsyncItemWriter<Address> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(addressWriter());
        return asyncItemWriter;
    }

    @Bean
    public Step addressStep() {
        return stepBuilderFactory.get("addresses-csv").<AddressInput, Future<Address>>chunk(10)
                .reader(addressReader(null))
                .processor(asyncAddressProcessor())
                .writer(asyncAddressWriter())
                .faultTolerant().skipPolicy(new CustomerBatchSkipPolicy())
                .taskExecutor(customerTaskExecutor)
                .build();
    }


}
