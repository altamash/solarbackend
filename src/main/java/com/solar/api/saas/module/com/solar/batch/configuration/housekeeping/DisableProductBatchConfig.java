package com.solar.api.saas.module.com.solar.batch.configuration.housekeeping;
import com.solar.api.AppConstants;
import com.solar.api.saas.module.com.solar.batch.service.BatchService;
import com.solar.api.saas.module.com.solar.scheduler.service.JobExecutionParamsService;
import com.solar.api.tenant.repository.ScanCodesRepository;
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
public class DisableProductBatchConfig {
    @Autowired
    BatchService batchService;
    @Autowired
    JobExecutionParamsService jobExecutionParamsService;
    @Autowired
    private ScanCodesRepository scanCodesRepository;
    @Bean("DisableProductBatch")
    public Job Analyzer(JobBuilderFactory jobBuilderFactory,
                        StepBuilderFactory stepBuilderFactory
    ) {
        Step disable = stepBuilderFactory.get("disableProduct")
                .tasklet(execute())
                .build();
        return jobBuilderFactory.get("DISABLE_PRODUCT")
                .incrementer(new RunIdIncrementer())
                .start(disable).build();
    }
    private Tasklet execute() {
        return (contribution, chunkContext) -> {
            chunkContext.getStepContext().getStepExecutionContext();
            try {
                batchService.disableProduct(String.valueOf(chunkContext.getStepContext().getJobParameters().get(AppConstants.productId)),
                        Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("compKey"))),
                        Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("jobId"))));
            }catch(Exception ex){
                chunkContext.getStepContext().getStepExecution().setExitStatus(new ExitStatus("FAILED", ex.getMessage()));
            }
            return RepeatStatus.FINISHED;
        };
    }
}