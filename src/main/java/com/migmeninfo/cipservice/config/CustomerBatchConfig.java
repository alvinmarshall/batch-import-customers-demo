package com.migmeninfo.cipservice.config;

import com.migmeninfo.cipservice.tasklet.UncompressTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class CustomerBatchConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private Step customerIndStep;
    @Autowired
    private Step customerOrgStep;

    @Autowired
    private Step addressStep;
    @Autowired
    private Step businessUnitStep;

    @Autowired
    private Step documentStep;
    @Autowired
    private Step accountStep;
    @Autowired
    private Step productOfferedStep;
    @Autowired
    private Step marketServedStep;
    @Autowired
    private Step countryOperationStep;
    @Autowired
    private UncompressTasklet uncompressTasklet;

    @Bean
    public Step unCompressBatchFilesStep() {
        return stepBuilderFactory.get("uncompress-customers-zip")
                .tasklet(uncompressTasklet)
                .build();
    }

    @Bean
    public Job processCustomerBatchJob() {
        return jobBuilderFactory.get("importCustomers")
                .flow(unCompressBatchFilesStep())
                .next(customerIndStep)
                .next(customerOrgStep)
                .next(addressStep)
//                .next(businessUnitStep)
                .next(documentStep)
                .next(accountStep)
                .next(productOfferedStep)
//                .next(marketServedStep)
                .next(countryOperationStep)
                .end()
                .build();

    }
}
