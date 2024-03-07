package com.solar.api.saas.module.com.solar.batch.configuration.stavem;

import com.solar.api.tenant.service.extended.project.FinancialAccrualService;
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

import java.util.Map;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Configuration
@EnableBatchProcessing
public class StavemHoursCalculationBatchConfig {

    @Autowired
    FinancialAccrualService financialAccrualService;

    @Bean("HoursCalculationBatch")
    public Job HoursProcessing(JobBuilderFactory jobBuilderFactory,
                               StepBuilderFactory stepBuilderFactory
    ) {
        Step step1 = stepBuilderFactory.get("Analyzing")
                .tasklet(hoursForFinancialAccruals())
                .build();

        return jobBuilderFactory.get("HOURS_CALCULATION_BATCH")
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .build();
    }

    private Tasklet hoursForFinancialAccruals() {

        return (contribution, chunkContext) -> {
            Map<String, Object> jobParameters =  chunkContext.getStepContext().getJobParameters();
            Long employeeId = (Long) jobParameters.get("EmployeeId");
            Long taskId = (Long) jobParameters.get("TaskId");
            Long compKey = (Long) jobParameters.get("CompKey");
            financialAccrualService.analyze(compKey, employeeId, taskId);
            return RepeatStatus.FINISHED;
        };
    }
}
