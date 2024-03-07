package com.solar.api.saas.module.com.solar.batch.configuration.housekeeping;

import com.solar.api.saas.module.com.solar.batch.service.JobListener;
import com.solar.api.tenant.service.process.subscription.termination.SubscriptionTermination;
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
public class AdhocSubscriptionTerminationConfig {

    @Autowired
    private SubscriptionTermination subscriptionTermination;
    @Autowired
    JobListener jobListener;

    @Bean("AdhocAllSubscriptionTermination")
    public Job AdhocSubscriptionTermination(JobBuilderFactory jobBuilderFactory,
                                            StepBuilderFactory stepBuilderFactory
                                           ) {
        Step step = stepBuilderFactory.get("AdhocSubscriptionTermination")
                .tasklet(adhocTermination())
                .build();

        return jobBuilderFactory.get("ADHOC_SUBSCRIPTION_TERMINATION")
                .incrementer(new RunIdIncrementer())
                .listener(jobListener)
                .start(step)
                .build();
    }

    private Tasklet adhocTermination() {
        return (contribution, chunkContext) -> {
            subscriptionTermination.executeAdhocTermination();
            return RepeatStatus.FINISHED;
        };

    }
}