package com.solar.api.saas.module.com.solar.batch.configuration.housekeeping.paymentmanagement;

import com.solar.api.AppConstants;
import com.solar.api.saas.service.integration.mongo.DataExchange;
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
public class UpdateProjectStatusBatchConfig {
    @Autowired
    private DataExchange dataExchange;

    @Bean("UpdateProjectStatus")
    public Job trigger(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        Step updateStatus = stepBuilderFactory.get("UpdateProjectStatus")
                .tasklet(updateStatus())
                .build();
        return jobBuilderFactory.get(AppConstants.UPDATE_PROJECT_STATUS)
                .incrementer(new RunIdIncrementer())
                .start(updateStatus)
                .build();
    }

    private Tasklet updateStatus() {
        return (contribution, chunkContext) -> {
            chunkContext.getStepContext().getStepExecutionContext();
            try {
                dataExchange.updateProjectStatuses();
            } catch (Exception ex) {
                chunkContext.getStepContext().getStepExecution().setExitStatus(new ExitStatus("FAILED", ex.getMessage()));
            }
            return RepeatStatus.FINISHED;
        };
    }
}
