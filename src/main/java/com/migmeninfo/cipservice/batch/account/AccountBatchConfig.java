package com.migmeninfo.cipservice.batch.account;

import com.migmeninfo.cipservice.domain.entity.Account;
import com.migmeninfo.cipservice.repository.AccountRepository;
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
public class AccountBatchConfig {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private CustomerRepository customerRepository;
    @Value("${batch-data.default}")
    private Resource defaultResource;

    @Autowired
    private TaskExecutor customerTaskExecutor;

    @Autowired
    private AccountProcessor accountProcessor;
    @Autowired
    private AccountRepository addressRepository;

    @Bean
    @StepScope
    @SneakyThrows
    public FlatFileItemReader<AccountInput> accountReader(@Value("#{jobExecutionContext['unzip_files']}") Object files) {
        String fileName = "accounts.csv";
        log.info("files: {}", files);
        FlatFileItemReader<AccountInput> itemReader = new FlatFileItemReader<>();
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(accountLineMapper());
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

    private LineMapper<AccountInput> accountLineMapper() {
        String[] props = new ArrayList<>(AccountInput.getAccountHeaders().values()).toArray(new String[0]);
        DefaultLineMapper<AccountInput> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(props);
        BeanWrapperFieldSetMapper<AccountInput> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(AccountInput.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public AsyncItemProcessor<AccountInput, Account> asyncAccountProcessor() {
        AsyncItemProcessor<AccountInput, Account> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(accountProcessor);
        asyncItemProcessor.setTaskExecutor(customerTaskExecutor);
        return asyncItemProcessor;
    }

    @Bean
    public RepositoryItemWriter<Account> accountWriter() {
        RepositoryItemWriter<Account> writer = new RepositoryItemWriter<>();
        writer.setRepository(addressRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public AsyncItemWriter<Account> asyncAccountWriter() {
        AsyncItemWriter<Account> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(accountWriter());
        return asyncItemWriter;
    }

    @Bean
    public Step accountStep() {
        return stepBuilderFactory.get("accounts-csv").<AccountInput, Future<Account>>chunk(10)
                .reader(accountReader(null))
                .processor(asyncAccountProcessor())
                .writer(asyncAccountWriter())
                .taskExecutor(customerTaskExecutor)
                .build();
    }


}
