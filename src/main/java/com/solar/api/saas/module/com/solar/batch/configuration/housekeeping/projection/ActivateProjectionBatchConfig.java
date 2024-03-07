package com.solar.api.saas.module.com.solar.batch.configuration.housekeeping.projection;

import com.solar.api.AppConstants;
import com.solar.api.saas.module.com.solar.batch.service.BatchService;
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
public class ActivateProjectionBatchConfig {

    @Autowired
    BatchService batchService;

    @Bean("ActivateProjectionBatch")
    public Job activateProjection(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        Step step = stepBuilderFactory.get("ACTIVATE_PROJECTION")
                .tasklet(activateProjection())
                .build();

        return jobBuilderFactory.get("ACTIVATE_PROJECTION")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    private Tasklet activateProjection() {
        return (contribution, chunkContext) -> {
            batchService.activateProjection(String.valueOf(chunkContext.getStepContext().getJobParameters().get(AppConstants.variantId)),
                    String.valueOf(chunkContext.getStepContext().getJobParameters().get(AppConstants.projectionId)),
                    Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("compKey"))),
                    Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("jobId"))));

            return RepeatStatus.FINISHED;
        };
    }

}
