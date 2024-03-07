package com.solar.api.saas.module.com.solar.batch.configuration.weather;

import com.solar.api.AppConstants;
import com.solar.api.tenant.service.weather.WeatherService;
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
public class WeatherDataDailyBatchConfig {

    @Autowired
    private WeatherService weatherService;
    @Bean("StoringHourlyWeatherDataInDatabase")
    public Job batchJob(JobBuilderFactory jobBuilderFactory,
                        StepBuilderFactory stepBuilderFactory
    ) {

        Step step = stepBuilderFactory.get("STORING_HOURLY_DATA_IN_DATABASE")
                .tasklet(storingWeatherDataInDatabase())

                .build();
        return jobBuilderFactory.get(AppConstants.WEATHER_API_FOR_STORING_HOURLY_DATA_IN_DATABASE)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    private Tasklet storingWeatherDataInDatabase() {
        return (contribution, chunkContext) -> {
            chunkContext.getStepContext().getStepExecutionContext();
            try {
                weatherService.getHourlyData((Long) chunkContext.getStepContext().getJobParameters().get("compKey") , null);
            } catch (Exception ex) {
                chunkContext.getStepContext().getStepExecution().setExitStatus(new ExitStatus("FAILED", ex.getMessage()));
            }
            return RepeatStatus.FINISHED;
        };
    }
}
