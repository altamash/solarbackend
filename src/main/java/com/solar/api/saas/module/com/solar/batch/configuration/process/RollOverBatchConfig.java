package com.solar.api.saas.module.com.solar.batch.configuration.process;

import com.solar.api.saas.module.com.solar.batch.service.JobListener;
import com.solar.api.tenant.service.process.subscription.rollover.SubscriptionRollover;
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
public class RollOverBatchConfig {

    @Autowired
    private SubscriptionRollover subscriptionRollover;
    @Autowired
    JobListener jobListener;

    @Bean("RollOverAllBatch")
    public Job RollOver(JobBuilderFactory jobBuilderFactory,
                        StepBuilderFactory stepBuilderFactory
                       ) {
        Step step = stepBuilderFactory.get("RollOverAll")
                .tasklet(rollOver())
                .build();

        return jobBuilderFactory.get("ROLL_OVER_ALL")
                .incrementer(new RunIdIncrementer())
                .listener(jobListener)
                .start(step)
                .build();
    }

    private Tasklet rollOver() {
        return (contribution, chunkContext) -> {
            subscriptionRollover.rollover(Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("compKey"))));
            return RepeatStatus.FINISHED;
        };
    }

}
