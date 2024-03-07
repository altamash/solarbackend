package com.solar.api.saas.module.com.solar.batch.configuration.weather;

import com.solar.api.AppConstants;
import com.solar.api.tenant.service.weather.WeatherService;
import org.springframework.batch.core.ExitStatus;
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
public class WeatherDataFetchingBatchConfig {

    @Autowired
    private WeatherService weatherService;

    @Bean("WeatherDataFetchingBatch")
    public Job batchJob(JobBuilderFactory jobBuilderFactory,
                        StepBuilderFactory stepBuilderFactory
    ) {

        Step step = stepBuilderFactory.get("FETCHING WEATHER DATA")
                .tasklet(fetchingWeatherData())

                .build();
        return jobBuilderFactory.get(AppConstants.WEATHER_API_FOR_FETCHING_SEVEN_DAYS_DATA)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    private Tasklet fetchingWeatherData() {
        return (contribution, chunkContext) -> {
            chunkContext.getStepContext().getStepExecutionContext();
            try {
                weatherService.findAllGarInfoForWeatherApi();
            } catch (Exception ex) {
                chunkContext.getStepContext().getStepExecution().setExitStatus(new ExitStatus("FAILED", ex.getMessage()));
            }
            return RepeatStatus.FINISHED;
        };
    }
}