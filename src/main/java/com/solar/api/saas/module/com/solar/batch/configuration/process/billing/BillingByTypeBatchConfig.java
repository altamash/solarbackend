package com.solar.api.saas.module.com.solar.batch.configuration.process.billing;

import com.solar.api.AppConstants;
import com.solar.api.saas.module.com.solar.batch.service.BatchService;
import com.solar.api.tenant.service.process.billing.EBillingByType;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.Date;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Configuration
public class BillingByTypeBatchConfig {

    @Autowired
    BatchService batchService;
//    @Autowired
//    JobListener jobListener;

    @Bean
    public BatchConfigurer batchConfigurer(EntityManagerFactory entityManagerFactory) {
        return new DefaultBatchConfigurer() {
            @Override
            public PlatformTransactionManager getTransactionManager() {
                return new JpaTransactionManager(entityManagerFactory);
            }
        };
    }

//    @Bean
//    public BatchConfigurer batchConfigurer(DataSource ds, SessionFactory sf) {
//        return new DefaultBatchConfigurer(ds) {
//            @Override
//            public PlatformTransactionManager getTransactionManager() {
//                return new HibernateTransactionManager(sf);
//            }
//        };
//    }

    @Bean("BillingByTypeBatch")
    public Job Analyzer(JobBuilderFactory jobBuilderFactory,
                        StepBuilderFactory stepBuilderFactory
    ) {
        Step generate = stepBuilderFactory.get("Generate")
                .tasklet(enqueueGenerate())
                .build();

        Step invoice = stepBuilderFactory.get("Invoice")
                .tasklet(enqueueInvoicing())
                .build();

        return jobBuilderFactory.get(AppConstants.BILLING_BY_TYPE)
                .incrementer(new RunIdIncrementer())
                .start(generate)
                .next(invoice)
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

    private Tasklet enqueueInvoicing() {
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
                    EBillingByType.INVOICE.getType(),
                    Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("compKey"))),
                    Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("jobId"))),
                    chunkContext.getStepContext().getJobInstanceId(),
                    true);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean("InvoicePDFBatch")
    public Job trigger(JobBuilderFactory jobBuilderFactory,
                       StepBuilderFactory stepBuilderFactory
    ) {
        Step invoicePDF = stepBuilderFactory.get("InvoicePDF")
                .tasklet(enqueueInvoicePDF())
                .build();

        return jobBuilderFactory.get(AppConstants.INVOICE_PDF_BATCH_JOB)
                .incrementer(new RunIdIncrementer())
                .start(invoicePDF)
                .build();
    }

    private Tasklet enqueueInvoicePDF() {

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
                    EBillingByType.INVOICE_PDF.getType(),
                    Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("compKey"))),
                    Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("jobId"))),
                    chunkContext.getStepContext().getJobInstanceId(),
                    true);
            return RepeatStatus.FINISHED;
        };
    }
}
