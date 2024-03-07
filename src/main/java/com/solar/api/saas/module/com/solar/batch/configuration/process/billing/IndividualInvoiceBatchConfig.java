package com.solar.api.saas.module.com.solar.batch.configuration.process.billing;

import com.solar.api.AppConstants;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.Arrays;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Configuration
public class IndividualInvoiceBatchConfig {

    @Autowired
    BillingUtilityService billingUtilityService;

//    @Bean
//    public BatchConfigurer batchConfigurer(EntityManagerFactory entityManagerFactory) {
//        return new DefaultBatchConfigurer() {
//            @Override
//            public PlatformTransactionManager getTransactionManager() {
//                return new JpaTransactionManager(entityManagerFactory);
//            }
//        };
//    }

    @Bean("IndividualInvoice")
    public Job trigger(JobBuilderFactory jobBuilderFactory,
                       StepBuilderFactory stepBuilderFactory
    ) {
        Step invoicePDF = stepBuilderFactory.get("Invoice")
                .tasklet(enqueueInvoice())
                .build();

        return jobBuilderFactory.get(AppConstants.INDIVIDUAL_INVOICE)
                .incrementer(new RunIdIncrementer())
                .start(invoicePDF)
                .build();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private Tasklet enqueueInvoice() {

        return (contribution, chunkContext) -> {
            Date date = null;
            if (chunkContext.getStepContext().getJobParameters().get("date") != null) {
                date = Date.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("date")));
            }
            try {
                billingUtilityService.invoicing(Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("billingHeadId"))), date,
                        Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("compKey"))));
            } catch (Exception e) {
                billingUtilityService.batchNotification("IndividualInvoice", null, Arrays.toString(e.getStackTrace()), "INDIVIDUAL INVOICE ALERT");
                contribution.setExitStatus(ExitStatus.FAILED);
            }
            return RepeatStatus.FINISHED;
        };
    }
}
