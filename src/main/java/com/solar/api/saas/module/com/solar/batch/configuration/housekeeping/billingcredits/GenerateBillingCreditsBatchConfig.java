package com.solar.api.saas.module.com.solar.batch.configuration.housekeeping.billingcredits;

import com.solar.api.AppConstants;
import com.solar.api.tenant.service.process.billing.BillingService;
import com.solar.api.tenant.service.process.pvmonitor.MonitorWrapperService;
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
public class GenerateBillingCreditsBatchConfig {
    @Autowired
    private MonitorWrapperService monitorWrapperService;

    @Bean("GenerateBillingCreditsBatch")
    public Job trigger(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        Step generateBillingCredits = stepBuilderFactory.get("GenerateBillingCredits")
                .tasklet(generateCredits())
                .build();
        return jobBuilderFactory.get(AppConstants.GENERATE_BILLING_CREDITS_BATCH)
                .incrementer(new RunIdIncrementer())
                .start(generateBillingCredits)
                .build();
    }

    private Tasklet generateCredits() {
        return (contribution, chunkContext) -> {
            chunkContext.getStepContext().getStepExecutionContext();
            try {
                monitorWrapperService.dataConversionForBillingCredits(String.valueOf(chunkContext.getStepContext().getJobParameters().get("date")),Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("jobId"))));
            } catch (Exception ex) {
                chunkContext.getStepContext().getStepExecution().setExitStatus(new ExitStatus("FAILED", ex.getMessage()));
            }
            return RepeatStatus.FINISHED;
        };
    }
}
