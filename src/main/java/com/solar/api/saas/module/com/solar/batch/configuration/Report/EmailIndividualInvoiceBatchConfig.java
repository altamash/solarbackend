package com.solar.api.saas.module.com.solar.batch.configuration.Report;

import com.solar.api.tenant.service.process.billing.invoice.BillingUtilityService;
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
public class EmailIndividualInvoiceBatchConfig {

    @Autowired
    BillingUtilityService billingUtilityService;

    @Bean("EmailInvoice")
    public Job emailInvoice(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        Step step = stepBuilderFactory.get("EMAIL_INVOICE")
                .tasklet(sendInvoicesEmails())
                .build();

        return jobBuilderFactory.get("EMAIL_INVOICE")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    private Tasklet sendInvoicesEmails() {
        return (contribution, chunkContext) -> {

            billingUtilityService.emailInvoicing(Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("billingHeadId"))),
                    Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("compKey"))));

//                    String.valueOf(chunkContext.getStepContext().getJobParameters().get("subscriptionCode")),
//                    String.valueOf(chunkContext.getStepContext().getJobParameters().get("rateMatrixHeadIds")),
//                    String.valueOf(chunkContext.getStepContext().getJobParameters().get("billingMonth")),
//                    String.valueOf(chunkContext.getStepContext().getJobParameters().get("type")),
//                    Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("jobId"))));
            return RepeatStatus.FINISHED;
        };
    }
}
