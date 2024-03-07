package com.solar.api.saas.module.com.solar.batch.configuration.housekeeping.powermonitor;

import com.solar.api.AppConstants;
import com.solar.api.saas.module.com.solar.batch.service.EGaugeService;
import com.solar.api.saas.module.com.solar.scheduler.service.JobExecutionParamsService;
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
public class EGaugeBatchConfig {
    @Autowired
    EGaugeService eGaugeService;
    @Autowired
    JobExecutionParamsService jobExecutionParamsService;
    @Bean("EGaugeBatch")
    public Job Analyzer(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        Step job = stepBuilderFactory.get("eGaugeGraphData")
                .tasklet(execute())
                .build();
        return jobBuilderFactory.get(AppConstants.EGAUGE_GRAPH_DATA)
                .incrementer(new RunIdIncrementer())
                .start(job).build();
    }
    private Tasklet execute() {
        return (contribution, chunkContext) -> {
            chunkContext.getStepContext().getStepExecutionContext();
            try {
                eGaugeService.getEGaugeData();
            }catch(Exception ex){
                chunkContext.getStepContext().getStepExecution().setExitStatus(new ExitStatus("FAILED", ex.getMessage()));
            }
            return RepeatStatus.FINISHED;
        };
    }
}