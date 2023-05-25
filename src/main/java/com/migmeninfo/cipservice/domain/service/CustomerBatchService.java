package com.migmeninfo.cipservice.domain.service;

import com.migmeninfo.cipservice.dto.CustomerBatchDto;
import com.migmeninfo.cipservice.dto.DocumentDto;
import com.migmeninfo.cipservice.service.StorageService;
import lombok.SneakyThrows;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerBatchService {
    private final JobLauncher jobLauncher;
    private final Job processCustomerBatchJob;
    private final StorageService storageService;
    private Path rootLocation;
    @Value("${TEMP_DIR:./temp}")
    private String dir;

    @Value("${batch-data.batch_file}")
    private Resource batchResource;

    public CustomerBatchService(
            @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
            JobLauncher jobLauncher,
            Job processCustomerBatchJob,
            StorageService storageService
    ) {
        this.jobLauncher = jobLauncher;
        this.processCustomerBatchJob = processCustomerBatchJob;
        this.storageService = storageService;
    }

    public void processBatch(CustomerBatchDto input) {
        Set<DocumentDto.File> batchFiles = input.getBatchFiles()
                .stream().
                map(documentDto -> {
                    if (ObjectUtils.isEmpty(documentDto.getFile())) return null;
                    return documentDto.getFile();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<URL> urls = downloadBatchFiles(batchFiles);

        urls.forEach(url -> {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .addString("batch_url", url.getPath())
                    .toJobParameters();
            try {
                jobLauncher.run(processCustomerBatchJob, jobParameters);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @SneakyThrows
    public void processBatch() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis())
                .addString("batch_url", batchResource.getFile().getPath())
                .toJobParameters();
        try {
            jobLauncher.run(processCustomerBatchJob, jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Set<URL> downloadBatchFiles(Set<DocumentDto.File> batchFiles) {
        return batchFiles.stream().map(batchFile -> {
                    InputStream inputStream = storageService.getFileStream(batchFile.getBucket(), batchFile.getKey());
                    return storeFile(batchFile.getBucket(), batchFile.getKey(), inputStream);
                })
                .collect(Collectors.toSet());
    }

    @SneakyThrows
    private URL storeFile(String bucketName, String key, InputStream inputStream) {
        Files.copy(inputStream, rootLocation.resolve(bucketName + "/" + key),
                StandardCopyOption.REPLACE_EXISTING);
        return rootLocation.resolve(bucketName + "/" + key).toUri().toURL();
    }

    @PostConstruct
    public void init() {
        try {
            rootLocation = Paths.get(dir);
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize storage location");
        }
    }
}
