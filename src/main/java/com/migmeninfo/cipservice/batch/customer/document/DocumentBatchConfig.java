package com.migmeninfo.cipservice.batch.customer.document;

import com.migmeninfo.cipservice.batch.policy.CustomerBatchSkipPolicy;
import com.migmeninfo.cipservice.config.CustomerZipFileConfig;
import com.migmeninfo.cipservice.domain.entity.Document;
import com.migmeninfo.cipservice.repository.DocumentRepository;
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
public class DocumentBatchConfig {
    private final StepBuilderFactory stepBuilderFactory;
    private final TaskExecutor customerTaskExecutor;
    private final DocumentProcessor documentProcessor;
    private final DocumentRepository documentRepository;
    private final CustomerZipFileConfig zipFileConfig;
    @Value("${batch-data.default}")
    private Resource defaultResource;

    public DocumentBatchConfig(
            @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") StepBuilderFactory stepBuilderFactory,
            TaskExecutor customerTaskExecutor,
            DocumentProcessor documentProcessor,
            DocumentRepository documentRepository,
            CustomerZipFileConfig zipFileConfig) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.customerTaskExecutor = customerTaskExecutor;
        this.documentProcessor = documentProcessor;
        this.documentRepository = documentRepository;
        this.zipFileConfig = zipFileConfig;
    }

    @Bean
    @StepScope
    @SneakyThrows
    public FlatFileItemReader<DocumentInput> documentReader(@Value("#{jobExecutionContext['unzip_files']}") Object files) {
        String fileName = zipFileConfig.getDocument();
        FlatFileItemReader<DocumentInput> itemReader = new FlatFileItemReader<>();
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(documentLineMapper());
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

    public AsyncItemProcessor<DocumentInput, Document> asyncDocumentProcessor() {
        AsyncItemProcessor<DocumentInput, Document> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(documentProcessor);
        asyncItemProcessor.setTaskExecutor(customerTaskExecutor);
        return asyncItemProcessor;
    }

    @Bean
    public RepositoryItemWriter<Document> documentWriter() {
        RepositoryItemWriter<Document> itemWriter = new RepositoryItemWriter<>();
        itemWriter.setRepository(documentRepository);
        itemWriter.setMethodName("save");
        return itemWriter;
    }

    @Bean
    public AsyncItemWriter<Document> asyncDocumentWriter() {
        AsyncItemWriter<Document> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(documentWriter());
        return asyncItemWriter;
    }

    @Bean
    public Step documentStep() {
        return stepBuilderFactory.get("document-csv").<DocumentInput, Future<Document>>chunk(10)
                .reader(documentReader(null))
                .processor(asyncDocumentProcessor())
                .writer(asyncDocumentWriter())
                .faultTolerant().skipPolicy(new CustomerBatchSkipPolicy())
                .taskExecutor(customerTaskExecutor)
                .build();
    }


}
