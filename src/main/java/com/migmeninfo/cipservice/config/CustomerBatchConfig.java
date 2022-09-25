package com.migmeninfo.cipservice.config;

import com.migmeninfo.cipservice.batch.tasklet.UncompressTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class CustomerBatchConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final Step customerIndStep;
    private final Step customerOrgStep;
    private final Step addressStep;
    private final Step businessUnitStep;
    private final Step documentStep;
    private final Step accountStep;
    private final Step productOfferedStep;
    private final Step marketServedStep;
    private final Step countryOperationStep;
    private final UncompressTasklet uncompressTasklet;

    public CustomerBatchConfig(
            @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") JobBuilderFactory jobBuilderFactory,
            @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") StepBuilderFactory stepBuilderFactory,
            Step customerIndStep,
            Step customerOrgStep,
            Step addressStep,
            Step businessUnitStep,
            Step documentStep,
            Step accountStep,
            Step productOfferedStep,
            Step marketServedStep,
            Step countryOperationStep,
            UncompressTasklet uncompressTasklet
    ) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.customerIndStep = customerIndStep;
        this.customerOrgStep = customerOrgStep;
        this.addressStep = addressStep;
        this.businessUnitStep = businessUnitStep;
        this.documentStep = documentStep;
        this.accountStep = accountStep;
        this.productOfferedStep = productOfferedStep;
        this.marketServedStep = marketServedStep;
        this.countryOperationStep = countryOperationStep;
        this.uncompressTasklet = uncompressTasklet;
    }

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
                .next(documentStep)
                .next(accountStep)
                .next(productOfferedStep)
                .next(marketServedStep)
                .next(countryOperationStep)
                .next(businessUnitStep)
                .end()
                .build();

    }
}
