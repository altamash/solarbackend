package com.solar.api.saas.module.com.solar.batch.configuration.housekeeping.billing;

import com.solar.api.AppConstants;
import com.solar.api.tenant.service.process.billing.BillingService;
import com.solar.api.tenant.service.process.billing.EBillingByType;
import com.solar.api.tenant.service.process.billing.invoice.BillingUtilityService;
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

import java.util.Arrays;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Configuration
public class ActivationBatchConfig {

    @Autowired
    BillingService billingService;

    @Autowired
    BillingUtilityService billingUtilityService;


    @Bean("GenerateBillsOnActivation")
    public Job trigger(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {

        Step step = stepBuilderFactory.get("ACTIVATE")
                .tasklet(initiate())
                .build();

        return jobBuilderFactory.get(AppConstants.ACTIVATION_BATCH)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    private Tasklet initiate() {
        return (contribution, chunkContext) -> {
            try {
                billingService.generateBillsOnActivation(Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("userAccountId"))),
                        String.valueOf(chunkContext.getStepContext().getJobParameters().get("subscriptionId")),
                        String.valueOf(chunkContext.getStepContext().getJobParameters().get("startDate")),
                        (Boolean) chunkContext.getStepContext().getJobParameters().get("isLegacy"));
            } catch (Exception e) {
                billingUtilityService.batchNotification("GenerateBillsOnActivation", null, Arrays.toString(e.getStackTrace()),
                        "ACTIVATION ALERT");
                contribution.setExitStatus(ExitStatus.FAILED);
            }
            return RepeatStatus.FINISHED;
        };
    }
}
