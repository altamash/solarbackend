package com.solar.api.saas.module.com.solar.batch.configuration.housekeeping.powermonitor;

import com.solar.api.saas.model.JobExecutionParams;
import com.solar.api.saas.module.com.solar.batch.service.BatchService;
import com.solar.api.saas.module.com.solar.batch.service.JobListener;
import com.solar.api.saas.module.com.solar.scheduler.service.JobExecutionParamsService;
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

import java.util.List;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Configuration
public class PowerMonitorReadingsBatchConfig {

    @Autowired
    BatchService batchService;
    @Autowired
    JobListener jobListener;
    @Autowired
    JobExecutionParamsService jobExecutionParamsService;

    @Bean("powerMonitorReadings")
    public Job getPowerMonitorReadingsData(JobBuilderFactory jobBuilderFactory,
                                            StepBuilderFactory stepBuilderFactory
    ) {
        Step step = stepBuilderFactory.get("getPowerMonitorReadings")
                .tasklet(execute())
                .build();

        return jobBuilderFactory.get("ADD_MONITOR_READINGS")
                .incrementer(new RunIdIncrementer())
                .listener(jobListener)
                .start(step)
                .build();
    }

    private Tasklet execute() {
        return (contribution, chunkContext) -> {
            List<JobExecutionParams> jobExecutionParams = jobExecutionParamsService.getByScheduledJobId(
                    Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("jobScheduleId"))));
            batchService.addMonitorReadings(jobExecutionParams);
            return RepeatStatus.FINISHED;
        };
    }
}
