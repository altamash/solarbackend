package com.solar.api.saas.module.com.solar.batch.configuration.housekeeping.billing;

import com.solar.api.AppConstants;
import com.solar.api.tenant.service.process.billing.invoice.BillInvoiceService;
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
public class BulkGenerateInvoiceBatchConfig {
    @Autowired
    private BillInvoiceService billInvoiceService;

    @Bean("BulkGenerateInvoice")
    public Job trigger(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        Step generateInvoice = stepBuilderFactory.get("BulkGenerateInvoice")
                .tasklet(generateInvoices())
                .build();
        return jobBuilderFactory.get(AppConstants.BULK_GENERATE_INVOICE)
                .incrementer(new RunIdIncrementer())
                .start(generateInvoice)
                .build();
    }

    private Tasklet generateInvoices() {
        return (contribution, chunkContext) -> {
            chunkContext.getStepContext().getStepExecutionContext();
            try {
                billInvoiceService.generateInvoiceBulkV1(String.valueOf(chunkContext.getStepContext().getJobParameters().get("billingHeadIds"))
                        ,Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("compKey"))));
            } catch (Exception ex) {
                chunkContext.getStepContext().getStepExecution().setExitStatus(new ExitStatus("FAILED", ex.getMessage()));
            }
            return RepeatStatus.FINISHED;
        };
    }
}
