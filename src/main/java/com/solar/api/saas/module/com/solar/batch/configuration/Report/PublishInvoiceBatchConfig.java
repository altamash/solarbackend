package com.solar.api.saas.module.com.solar.batch.configuration.Report;

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
public class PublishInvoiceBatchConfig {

    @Autowired
    BillingInvoicePublishService billingInvoicePublishService;

    @Bean("PUBLISH_INVOICES_EMAIL")
    public Job publishInvoices(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        Step step = stepBuilderFactory.get("SENDING_INVOICE_EMAILS")
                .tasklet(sendInvoicesEmails())
                .build();

        Step step2 = stepBuilderFactory.get("VALIDATING_INVOICE_EMAILS")
                .tasklet(validateInvoicesEmails())
                .build();

        return jobBuilderFactory.get("PUBLISH_INVOICES_EMAIL")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .next(step2)
                .build();
    }

    private Tasklet sendInvoicesEmails() {
        return (contribution, chunkContext) -> {

            billingInvoicePublishService.publishInvoiceByMonth(String.valueOf(chunkContext.getStepContext().getJobParameters().get("subscriptionCode")),
                    String.valueOf(chunkContext.getStepContext().getJobParameters().get("rateMatrixHeadIds")),
                    String.valueOf(chunkContext.getStepContext().getJobParameters().get("billingMonth")),
                    String.valueOf(chunkContext.getStepContext().getJobParameters().get("type")),
                    Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("jobId"))));
            return RepeatStatus.FINISHED;
        };
    }

    private Tasklet validateInvoicesEmails() {
        return (contribution, chunkContext) -> {
            billingInvoicePublishService.validateInvoices();
            return RepeatStatus.FINISHED;
        };
    }
}
