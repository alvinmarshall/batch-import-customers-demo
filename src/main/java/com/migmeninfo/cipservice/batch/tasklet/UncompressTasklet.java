package com.migmeninfo.cipservice.batch.tasklet;

import com.migmeninfo.cipservice.utils.UnzipUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@StepScope
public class UncompressTasklet implements Tasklet {
    private String archivePath;
    public static String FILES_EXECUTOR_PARAM = "unzip_files";
    @Value("${batch-data.temp-dir:./temp/output}")
    private String uncompressBaseDirectory;

    public UncompressTasklet(@Value("#{jobParameters['batch_url']}") String batchUrl) {
        this.archivePath = batchUrl;
    }


    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws IOException {
        log.info("batch-url: {}", archivePath);
        UnzipUtility utility = new UnzipUtility();
        utility.unzip(archivePath, uncompressBaseDirectory, "/");
        Map<String, String> files = utility.getFiles();
        log.info("files: {}", files);
        contribution.incrementWriteCount(files.size());
        getExecutionContext(chunkContext).put(FILES_EXECUTOR_PARAM, files);
        contribution.incrementReadCount();
        return RepeatStatus.FINISHED;
    }

    public void setUncompressBaseDirectory(String uncompressBaseDirectory) {
        this.uncompressBaseDirectory = uncompressBaseDirectory;
    }

    public void setArchivePath(String archivePath) {
        this.archivePath = archivePath;
    }

    private ExecutionContext getExecutionContext(ChunkContext chunkContext) {
        return chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext();
    }
}
