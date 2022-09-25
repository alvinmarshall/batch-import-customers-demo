package com.migmeninfo.cipservice.batch.productoffered;

import com.migmeninfo.cipservice.domain.entity.ProductsOffered;
import com.migmeninfo.cipservice.repository.CustomerRepository;
import com.migmeninfo.cipservice.repository.ProductOfferedRepository;
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
public class ProductOfferedBatchConfig {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private CustomerRepository customerRepository;
    @Value("${batch-data.default}")
    private Resource defaultResource;

    @Autowired
    private TaskExecutor customerTaskExecutor;
    @Autowired
    private ProductOfferedProcessor productOfferedProcessor;
    @Autowired
    private ProductOfferedRepository productOfferedRepository;

    @Bean
    @StepScope
    @SneakyThrows
    public FlatFileItemReader<ProductOfferedInput> productOfferedReader(@Value("#{jobExecutionContext['unzip_files']}") Object files) {
        String fileName = "products_offered.csv";
        log.info("files: {}", files);
        FlatFileItemReader<ProductOfferedInput> itemReader = new FlatFileItemReader<>();
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(productOfferedLineMapper());
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
    public AsyncItemProcessor<ProductOfferedInput, ProductsOffered> asyncProductOfferedProcessor() {
        AsyncItemProcessor<ProductOfferedInput, ProductsOffered> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(productOfferedProcessor);
        asyncItemProcessor.setTaskExecutor(customerTaskExecutor);
        return asyncItemProcessor;
    }

    @Bean
    public RepositoryItemWriter<ProductsOffered> productOfferedWriter() {
        RepositoryItemWriter<ProductsOffered> writer = new RepositoryItemWriter<>();
        writer.setRepository(productOfferedRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public AsyncItemWriter<ProductsOffered> asyncProductOfferedWriter() {
        AsyncItemWriter<ProductsOffered> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(productOfferedWriter());
        return asyncItemWriter;
    }

    @Bean
    public Step productOfferedStep() {
        return stepBuilderFactory.get("products_offered-csv").<ProductOfferedInput, Future<ProductsOffered>>chunk(10)
                .reader(productOfferedReader(null))
                .processor(asyncProductOfferedProcessor())
                .writer(asyncProductOfferedWriter())
                .taskExecutor(customerTaskExecutor)
                .build();
    }


}
