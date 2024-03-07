package com.solar.api.saas.module.com.solar.batch.configuration.housekeeping.billing;

import com.solar.api.AppConstants;
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

import java.sql.Date;
import java.util.Arrays;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Configuration
public class BulkInvoiceBatchConfig {

    @Autowired
    BillingUtilityService billingUtilityService;

    @Bean("BulkInvoice")
    public Job trigger(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        Step invoicePDF = stepBuilderFactory.get("BulkInvoice")
                .tasklet(enqueueInvoice())
                .build();
        return jobBuilderFactory.get(AppConstants.BULK_INVOICE)
                .incrementer(new RunIdIncrementer())
                .start(invoicePDF)
                .build();
    }

    private Tasklet enqueueInvoice() {
        return (contribution, chunkContext) -> {
            Date date = null;
            if (chunkContext.getStepContext().getJobParameters().get("date") != null) {
                date = Date.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("date")));
            }
            try {
                String lastMonthNotInvoicedStrings = billingUtilityService.invoicing(String.valueOf(chunkContext.getStepContext().getJobParameters().get("subscriptionCode")),
                        (String.valueOf(chunkContext.getStepContext().getJobParameters().get("subscriptionRateMatrixIdsCSV"))),
                        String.valueOf(chunkContext.getStepContext().getJobParameters().get("billingMonth")),
                        date,
                        EBillingByType.INVOICE.getType(),
                        Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("compKey"))));
                chunkContext.getStepContext().getJobParameters().put("lastMonthNotInvoicedStrings", lastMonthNotInvoicedStrings);
            } catch (Exception e) {
                billingUtilityService.batchNotification("BulkInvoice", null, Arrays.toString(e.getStackTrace()),
                        "BULK INVOICE ALERT");
                contribution.setExitStatus(ExitStatus.FAILED);
            }
            return RepeatStatus.FINISHED;
        };
    }
}
