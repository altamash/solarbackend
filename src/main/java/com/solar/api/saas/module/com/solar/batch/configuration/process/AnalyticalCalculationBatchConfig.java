package com.solar.api.saas.module.com.solar.batch.configuration.process;

import com.solar.api.saas.module.com.solar.batch.service.JobListener;
import com.solar.api.saas.service.widget.InfoService;
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
public class AnalyticalCalculationBatchConfig {

    @Autowired
    private InfoService infoService;
    @Autowired
    JobListener jobListener;

    @Bean("AnalyticalCalculationBatch")
    public Job Analyzer(JobBuilderFactory jobBuilderFactory,
                        StepBuilderFactory stepBuilderFactory
    ) {
//        Step step1 = stepBuilderFactory.get("Analyzing")
//                .tasklet(analyze())
//                .build();

        Step step2 = stepBuilderFactory.get("Calculating NPV")
                .tasklet(npv())
                .build();

        Step step3 = stepBuilderFactory.get("Calculating lifeTimeSum")
                .tasklet(LifetimeCalculation())
                .build();

        Step step4 = stepBuilderFactory.get("Calculating TPF")
                .tasklet(tpf())
                .build();

        Step step5 = stepBuilderFactory.get("Calculating MPA")
                .tasklet(mpa())
                .build();


        return jobBuilderFactory.get("ANALYTICAL_CALCULATION_BATCH")
                .incrementer(new RunIdIncrementer())
                .listener(jobListener)
                .start(step2)
                .next(step3)
//                .next(step4)
//                .next(step5)
                .build();
    }

    private Tasklet analyze() {
        return (contribution, chunkContext) -> {
            infoService.analyze();
            return RepeatStatus.FINISHED;
        };
    }

    private Tasklet npv() {
        return (contribution, chunkContext) -> {
            infoService.npv();
            return RepeatStatus.FINISHED;
        };
    }

    private Tasklet absav() {
        return (contribution, chunkContext) -> {
            infoService.absav();
            return RepeatStatus.FINISHED;
        };
    }

    private Tasklet tpf() {
        return (contribution, chunkContext) -> {
            infoService.tpf();
            return RepeatStatus.FINISHED;
        };
    }

    private Tasklet mpa() {
        return (contribution, chunkContext) -> {
            infoService.mpa();

            return RepeatStatus.FINISHED;
        };
    }

    private Tasklet LifetimeCalculation() {
        return (contribution, chunkContext) -> {
            infoService.lifeTimeSumBatch();
            return RepeatStatus.FINISHED;
        };
    }
}
