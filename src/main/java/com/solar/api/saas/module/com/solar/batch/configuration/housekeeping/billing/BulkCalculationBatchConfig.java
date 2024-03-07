package com.solar.api.saas.module.com.solar.batch.configuration.housekeeping.billing;

import com.solar.api.AppConstants;
import com.solar.api.tenant.service.process.billing.BillingService;
import org.springframework.batch.core.ExitStatus;
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

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Configuration
public class BulkCalculationBatchConfig {
    @Autowired
    private BillingService billingService;

    @Bean("BulkCalculation")
    public Job trigger(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        Step bulkCalculate = stepBuilderFactory.get("BulkCalculate")
                .tasklet(calculateBills())
                .build();
        return jobBuilderFactory.get(AppConstants.BULK_CALCULATE)
                .incrementer(new RunIdIncrementer())
                .start(bulkCalculate)
                .build();
    }

    private Tasklet calculateBills() {
        return (contribution, chunkContext) -> {
            chunkContext.getStepContext().getStepExecutionContext();
            try {
                billingService.calculatePendingBillsInCalTracker(Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("jobId"))),
                        (String.valueOf(chunkContext.getStepContext().getJobParameters().get("period"))));
            } catch (Exception ex) {
                chunkContext.getStepContext().getStepExecution().setExitStatus(new ExitStatus("FAILED", ex.getMessage()));
            }
            return RepeatStatus.FINISHED;
        };
    }
}
