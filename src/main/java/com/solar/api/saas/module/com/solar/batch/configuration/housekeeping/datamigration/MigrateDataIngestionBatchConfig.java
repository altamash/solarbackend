package com.solar.api.saas.module.com.solar.batch.configuration.housekeeping.datamigration;

import com.solar.api.AppConstants;
import com.solar.api.saas.module.com.solar.batch.service.StageMonitorService;
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
public class MigrateDataIngestionBatchConfig {
    @Autowired
    StageMonitorService stageMonitorService;
    @Autowired
    JobExecutionParamsService jobExecutionParamsService;
    @Bean("MigrateDataIngestionBatch")
    public Job Analyzer(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        Step job = stepBuilderFactory.get("migrateDataIngestion")
                .tasklet(execute())
                .build();
        return jobBuilderFactory.get(AppConstants.MIGRATE_DATA_INGESTION_BATCH)
                .incrementer(new RunIdIncrementer())
                .start(job).build();
    }
    private Tasklet execute() {
        return (contribution, chunkContext) -> {
            chunkContext.getStepContext().getStepExecutionContext();
            try {
                stageMonitorService.transferSubscriptionsToStageDefinition();
            }catch(Exception ex){
                chunkContext.getStepContext().getStepExecution().setExitStatus(new ExitStatus("FAILED", ex.getMessage()));
            }
            return RepeatStatus.FINISHED;
        };
    }
}