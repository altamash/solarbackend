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
public class DeActivateProjectionBatchConfig {

    @Autowired
    BatchService batchService;

    @Bean("DeActivateProjectionBatch")
    public Job deactivateProjection(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        Step step = stepBuilderFactory.get("DEACTIVATE_PROJECTION")
                .tasklet(deactivateProjection())
                .build();

        return jobBuilderFactory.get("DEACTIVATE_PROJECTION")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    private Tasklet deactivateProjection() {
        return (contribution, chunkContext) -> {
            batchService.deActivateProjection(String.valueOf(chunkContext.getStepContext().getJobParameters().get(AppConstants.VARIANT_PROJECTION)),
                    Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("compKey"))),
                    Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("jobId"))));

            return RepeatStatus.FINISHED;
        };
    }

}
