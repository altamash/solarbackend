package com.solar.api.saas.module.com.solar.batch.configuration.process.workflow;

import com.solar.api.AppConstants;
import com.solar.api.saas.module.com.solar.batch.service.BatchService;
import com.solar.api.saas.module.com.solar.batch.service.JobListener;
import com.solar.api.tenant.service.process.billing.EBillingByType;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.Date;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Configuration
public class WFEmailBatchConfig {

    @Autowired
    BatchService batchService;
    @Autowired
    JobListener jobListener;

    @Bean("WFEmailBatch")
    public Job Analyzer(JobBuilderFactory jobBuilderFactory,
                        StepBuilderFactory stepBuilderFactory
    ) {
        Step generate = stepBuilderFactory.get("EmailTrigger")
                .tasklet(enqueueGenerate())
                .build();

        return jobBuilderFactory.get(AppConstants.BILLING_BY_TYPE)
                .incrementer(new RunIdIncrementer())
                .listener(jobListener)
                .start(generate)
                .build();
    }

    private Tasklet enqueueGenerate() {
        return (contribution, chunkContext) -> {
            Date date = null;
            if (chunkContext.getStepContext().getJobParameters().get("date") != null) {
                date = Date.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("date")));
            }
            batchService.billingBatchJob(
                    String.valueOf(chunkContext.getStepContext().getJobParameters().get("subscriptionCode")),
                    String.valueOf(chunkContext.getStepContext().getJobParameters().get("subscriptionRateMatrixIdsCSV")),
                    String.valueOf(chunkContext.getStepContext().getJobParameters().get("billingMonth")),
                    date,
                    EBillingByType.GENERATE.getType(),
                    Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("compKey"))),
                    Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("jobId"))),
                    chunkContext.getStepContext().getJobInstanceId(),
                    true);
            return RepeatStatus.FINISHED;
        };
    }
}

