package com.solar.api.saas.module.com.solar.batch.configuration.Report;

import com.solar.api.AppConstants;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Configuration
public class EmailBatchConfig {

    @Bean("HK_Email")
    public Job trigger(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        Step step = stepBuilderFactory.get("HK_Email")
                .tasklet(initiate())
                .build();
        return jobBuilderFactory.get(AppConstants.HK_EMAIL)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    private Tasklet initiate() {
        return (contribution, chunkContext) -> {

            return RepeatStatus.FINISHED;
        };
    }
}
