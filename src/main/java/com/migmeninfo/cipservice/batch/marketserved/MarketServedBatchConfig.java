package com.migmeninfo.cipservice.batch.marketserved;

import com.migmeninfo.cipservice.domain.entity.MarketServed;
import com.migmeninfo.cipservice.repository.CustomerRepository;
import com.migmeninfo.cipservice.repository.MarketServedRepository;
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
public class MarketServedBatchConfig {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private CustomerRepository customerRepository;

    @Value("${batch-data.default}")
    private Resource defaultResource;

    @Autowired
    private TaskExecutor customerTaskExecutor;
    @Autowired
    private MarketServedProcessor marketServedProcessor;
    @Autowired
    private MarketServedRepository marketServedRepository;

    @Bean
    @StepScope
    @SneakyThrows
    public FlatFileItemReader<MarketServedInput> marketServedReader(@Value("#{jobExecutionContext['unzip_files']}") Object files) {
        String fileName = "markets_served.csv";
        FlatFileItemReader<MarketServedInput> itemReader = new FlatFileItemReader<>();
        itemReader.setLineMapper(marketServedLineMapper());
        itemReader.setLinesToSkip(1);

        Optional<String> optionalFilePath = BatchUtils.getBatchFileName(files, fileName);
        if (optionalFilePath.isEmpty()) {
            itemReader.setResource(defaultResource);
            return itemReader;
        }
        String filePath = optionalFilePath.get();
        FileUrlResource urlResource = new FileUrlResource(filePath);
        log.info("{}-file-exist: {}", fileName, urlResource.exists());
        itemReader.setLineMapper(marketServedLineMapper());
        itemReader.setResource(urlResource);
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
    public AsyncItemProcessor<MarketServedInput, MarketServed> asyncMarketServedProcessor() {
        AsyncItemProcessor<MarketServedInput, MarketServed> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(marketServedProcessor);
        asyncItemProcessor.setTaskExecutor(customerTaskExecutor);
        return asyncItemProcessor;
    }

    @Bean
    public RepositoryItemWriter<MarketServed> marketServedWriter() {
        RepositoryItemWriter<MarketServed> itemWriter = new RepositoryItemWriter<>();
        itemWriter.setRepository(marketServedRepository);
        itemWriter.setMethodName("save");
        return itemWriter;
    }

    @Bean
    public AsyncItemWriter<MarketServed> asyncMarketServedWriter() {
        AsyncItemWriter<MarketServed> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(marketServedWriter());
        return asyncItemWriter;
    }

    @Bean
    public Step marketServedStep() {
        return stepBuilderFactory.get("markets_served-csv").<MarketServedInput, Future<MarketServed>>chunk(10)
                .reader(marketServedReader(null))
                .processor(asyncMarketServedProcessor())
                .writer(asyncMarketServedWriter())
                .faultTolerant().skipPolicy(new CustomSkipPolicy())
                .taskExecutor(customerTaskExecutor)
                .build();
    }


}
