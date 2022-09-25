package com.migmeninfo.cipservice.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Component
@StepScope
public class UncompressTasklet implements Tasklet {
    private String archiveFilename;
    public static String FILES_EXECUTOR_PARAM = "unzip_files";
    private String uncompressBaseDirectory = "./temp/output";
    private final String batchUrl;

    public UncompressTasklet(@Value("#{jobParameters['batch_url']}") String batchUrl) {
        this.batchUrl = batchUrl;
    }


    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws IOException {
        Map<String, String> files = new LinkedHashMap<>();
        log.info("batch-url: {}", batchUrl);

        File dir = new File(this.uncompressBaseDirectory);
        if (!dir.exists()) {
            boolean mkdirs = dir.mkdirs();
            log.info("create-dir: {}", mkdirs);
        }
        FileInputStream fis;
        byte[] buffer = new byte[1024];
        fis = new FileInputStream(this.batchUrl);
        ZipInputStream zis = new ZipInputStream(fis);
        ZipEntry ze = zis.getNextEntry();
        while (ze != null) {
            String fileName = ze.getName();
            log.info("file: {}", fileName);
            File newFile = new File(this.uncompressBaseDirectory + File.separator + fileName);
            files.put(fileName, newFile.getPath());
            log.info("Unzipping to {}", newFile.getAbsolutePath());
            File file = new File(newFile.getParent());
            file.mkdirs();
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zis.closeEntry();
            ze = zis.getNextEntry();
            contribution.incrementWriteCount(1);
        }
        zis.closeEntry();
        zis.close();
        fis.close();
        getExecutionContext(chunkContext).put(FILES_EXECUTOR_PARAM, files);
        contribution.incrementReadCount();
        return RepeatStatus.FINISHED;
    }

    public void setUncompressBaseDirectory(String uncompressBaseDirectory) {
        this.uncompressBaseDirectory = uncompressBaseDirectory;
    }

    public void setArchiveFilename(String archiveFilename) {
        this.archiveFilename = archiveFilename;
    }

    private ExecutionContext getExecutionContext(ChunkContext chunkContext) {
        return chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext();
    }
}
