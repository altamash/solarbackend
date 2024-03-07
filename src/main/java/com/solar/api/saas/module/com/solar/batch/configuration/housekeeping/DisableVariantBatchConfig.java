package com.solar.api.saas.module.com.solar.batch.configuration.housekeeping;

import com.solar.api.AppConstants;
import com.solar.api.saas.module.com.solar.batch.service.BatchService;
import com.solar.api.tenant.service.SubscriptionService;
import com.solar.api.tenant.service.process.billing.publish.BillingInvoicePublishService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Configuration
@EnableBatchProcessing
public class DisableVariantBatchConfig {

    @Autowired
    BatchService batchService;

    @Bean("DISABLE_VARIANT")
    public Job disableVariant(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        Step step = stepBuilderFactory.get("DISABLE_VARIANT")
                .tasklet(disableVariant())
                .build();

        return jobBuilderFactory.get("DISABLE_VARIANT")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    private Tasklet disableVariant() {
        return (contribution, chunkContext) -> {
            batchService.disableVariant(String.valueOf(chunkContext.getStepContext().getJobParameters().get(AppConstants.PRODUCT_VARIANT)),
                    Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("compKey"))),
                    Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("jobId"))));

            return RepeatStatus.FINISHED;
        };
    }

}
