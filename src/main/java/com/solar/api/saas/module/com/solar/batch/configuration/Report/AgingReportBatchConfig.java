package com.solar.api.saas.module.com.solar.batch.configuration.Report;

import com.solar.api.saas.service.ProcedureService;
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
public class AgingReportBatchConfig {

    @Autowired
    private ProcedureService procedureService;

    @Bean("AgingReportBatch")
    public Job Executor(JobBuilderFactory jobBuilderFactory,
                        StepBuilderFactory stepBuilderFactory
    ) {
        Step step1 = stepBuilderFactory.get("Running Procedure")
                .tasklet(callStoredProcedure())
                .build();

        return jobBuilderFactory.get("AGING_REPORT_BATCH")
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .build();
    }

    private Tasklet callStoredProcedure() {
        return (contribution, chunkContext) -> {
            procedureService.callAgingReport();
            return RepeatStatus.FINISHED;
        };
    }
}
