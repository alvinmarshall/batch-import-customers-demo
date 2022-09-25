package com.migmeninfo.cipservice.batch.listener;

import com.migmeninfo.cipservice.batch.tasklet.UncompressTasklet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Component
@Slf4j
public class CustomerBatchJobListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        cleanUpFiles(jobExecution);
    }

    private void cleanUpFiles(JobExecution jobExecution) {
        Object files = jobExecution.getExecutionContext().get(UncompressTasklet.FILES_EXECUTOR_PARAM);
        if (files instanceof Map) {
            log.info("job has ended {}", files);
            @SuppressWarnings("unchecked") Map<String, String> unzipFiles = (Map<String, String>) files;
            unzipFiles.values().forEach(path -> {
                try {
                    File file = new File(path);
                    if (file.isDirectory()) {
                        FileUtils.deleteDirectory(file);
                        log.info("dir-clean-up: {}", path);
                    } else {
                        if (file.exists()) {
                            Files.deleteIfExists(Path.of(path));
                            log.info("file-clean-up: {}", path);
                        }
                    }
                } catch (IOException e) {
                    log.error("delete-file-error: {}", e.getMessage(), e);
                }
            });
        }
    }
}
