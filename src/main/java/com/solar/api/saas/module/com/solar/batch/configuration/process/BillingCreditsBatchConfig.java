package com.solar.api.saas.module.com.solar.batch.configuration.process;

import java.util.Arrays;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.AppConstants;
import com.solar.api.saas.model.SaasSchema;
import com.solar.api.saas.service.StorageService;
import com.solar.api.saas.service.job.EJobName;
import com.solar.api.saas.service.job.EJobStatus;
import com.solar.api.saas.service.job.JobHandler;
import com.solar.api.tenant.model.billingCredits.BillingCredits;
import com.solar.api.tenant.model.billingCredits.BillingCreditsCsv;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.service.BillingCreditsService;
import com.solar.api.tenant.service.process.billing.BillingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Configuration
@EnableBatchProcessing
public class BillingCreditsBatchConfig {

    @Autowired
    private JobHandler jobHandler;
    @Autowired
    private StorageService storageService;
    @Autowired
    private BillingCreditsService billingCreditsService;

    @Autowired
    private BillingService billingService;

    @Value("${app.profile}")
    private String appProfile;

    @Bean("BillingCreditsBatch")
    public Job batchJob(JobBuilderFactory jobBuilderFactory,
                        StepBuilderFactory stepBuilderFactory,
                        ItemReader<BillingCreditsCsv> itemReader,
                        ItemProcessor<BillingCreditsCsv, BillingCreditsCsv> itemProcessor,
                        ItemWriter<BillingCreditsCsv> itemWriter
    ) {

        Step step = stepBuilderFactory.get("EXTRACTION")
                .<BillingCreditsCsv, BillingCreditsCsv>chunk(5000)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)

                .build();
        Step generateBillingCredits = stepBuilderFactory.get("GenerateBillingCredits")
                .tasklet(generateCredits())
                .build();
        return jobBuilderFactory.get(AppConstants.BILLING_CREDITS_IMPORT_JOB)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .next(generateBillingCredits)
                .build();
    }

    @Bean()
    @StepScope()
    public FlatFileItemReader<BillingCreditsCsv> itemReader() {
        long fileId = jobHandler.getLastJobInstanceId(AppConstants.BILLING_CREDITS_IMPORT_JOB);
        JobParameters jobParameters = jobHandler.getJobParameters(AppConstants.BILLING_CREDITS_IMPORT_JOB);
        String timePath = jobParameters.getString("timePath");
        File file = storageService.getBlob(
                appProfile,
                "tenant/" + jobParameters.getString("CompanyKey") + AppConstants.BILLING_CREDITS_PATH + "/" + timePath,
                AppConstants.BILLING_CREDITS_STRING + fileId + AppConstants.MIME_TYPE_CSV);
        FlatFileItemReader<BillingCreditsCsv> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource(file));
        flatFileItemReader.setName("CSV-Reader");
        flatFileItemReader.setLineMapper(lineMapper());
        return flatFileItemReader;
    }

    @Bean
    @StepScope()
    public LineMapper<BillingCreditsCsv> lineMapper() {
        JobParameters jobParameters = jobHandler.getJobParameters(AppConstants.BILLING_CREDITS_IMPORT_JOB);
        DefaultLineMapper<BillingCreditsCsv> defaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        String[] header = Arrays.stream(jobParameters.getString("headers").split(","))
                .map(String::trim).collect(Collectors.toList()).toArray(new String[0]);
        lineTokenizer.setNames(header);
        BeanWrapperFieldSetMapper<BillingCreditsCsv> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(BillingCreditsCsv.class);
        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);
        return defaultLineMapper;
    }

    private Tasklet generateCredits() {
        return (contribution, chunkContext) -> {
            chunkContext.getStepContext().getStepExecutionContext();
            try {
                billingCreditsService.dataConversionForBillingCredits(Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("JobManagerId"))));
            } catch (Exception ex) {
                chunkContext.getStepContext().getStepExecution().setExitStatus(new ExitStatus("FAILED", ex.getMessage()));
            }
            return RepeatStatus.FINISHED;
        };
    }
}
