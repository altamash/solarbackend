package com.solar.api.saas.module.com.solar.batch.configuration.housekeeping.billing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.api.AppConstants;
import com.solar.api.tenant.mapper.billing.billingHead.BillingDiscountDTO;
import com.solar.api.tenant.service.process.billing.billingDiscount.BillingDiscount;
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
public class BulkDiscountBatchConfig {
    @Autowired
    private BillingDiscount billingDiscountService;
    @Bean("BulkDiscount")
    public Job trigger(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        Step bulkDiscount = stepBuilderFactory.get("BulkDiscount")
                .tasklet(addDiscount())
                .build();
        return jobBuilderFactory.get(AppConstants.BULK_DISCOUNT)
                .incrementer(new RunIdIncrementer())
                .start(bulkDiscount)
                .build();
    }

    private Tasklet addDiscount() {
        return (contribution, chunkContext) -> {
            chunkContext.getStepContext().getStepExecutionContext();
            try {
                BillingDiscountDTO billingDiscountDTO = new ObjectMapper().readValue
                        (String.valueOf(chunkContext.getStepContext().getJobParameters().get("billingDiscountDTO"))
                        , BillingDiscountDTO.class);
                billingDiscountService.bulkAddDiscountV1(billingDiscountDTO,
                        String.valueOf(chunkContext.getStepContext().getJobParameters().get("billingHeadIds")));
            } catch (Exception ex) {
                chunkContext.getStepContext().getStepExecution().setExitStatus(new ExitStatus("FAILED", ex.getMessage()));
            }
            return RepeatStatus.FINISHED;
        };
    }
}
