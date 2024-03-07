package com.solar.api.saas.module.com.solar.batch.configuration.process;

import com.solar.api.AppConstants;
import com.solar.api.saas.module.com.solar.batch.configuration.components.writer.ProjectionsWriter;
import com.solar.api.saas.service.StorageService;
import com.solar.api.saas.service.job.JobHandler;
import com.solar.api.saas.service.process.upload.mapper.MonitorReadingDailyCSV;
import com.solar.api.tenant.model.billingCredits.BillingCreditsCsv;
import com.solar.api.tenant.repository.MonitorReadingDailyRepository;
import com.solar.api.tenant.service.BillingCreditsService;
import com.solar.api.tenant.service.process.billing.BillingService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Configuration
@EnableBatchProcessing
public class ProjectionBatchConfig {

    @Autowired
    private JobHandler jobHandler;
    @Autowired
    private StorageService storageService;

    @Value("${app.profile}")
    private String appProfile;

    @Value("${app.storage.container}")
    private String profile;

    @Bean("ProjectionBatch")
    public Job batchJob(JobBuilderFactory jobBuilderFactory,
                        StepBuilderFactory stepBuilderFactory,
                        ItemReader<MonitorReadingDailyCSV> itemReader,
                        ItemProcessor<MonitorReadingDailyCSV, MonitorReadingDailyCSV> itemProcessor,
                        ItemWriter<MonitorReadingDailyCSV> itemWriter
    ) {

        Step step = stepBuilderFactory.get("EXTRACTION")
                .<MonitorReadingDailyCSV, MonitorReadingDailyCSV>chunk(5000)
                 .reader(itemReader)
               // .processor(itemProcessor)
                .writer(itemWriter)
                .build();

        return jobBuilderFactory.get(AppConstants.PROJECTION_IMPORT_JOB)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    @Bean("ProjectionReader")
    @StepScope()
    public FlatFileItemReader<MonitorReadingDailyCSV> itemReader() {
        JobParameters jobParameters = jobHandler.getJobParametersByJobName(AppConstants.PROJECTION_IMPORT_JOB);
        String mongoSubId = jobParameters.getString("mongoSubId");
        //long fileId = jobHandler.getLastJobInstanceIdByJobName(AppConstants.PROJECTION_IMPORT_JOB);
        String fileName = jobParameters.getString("fileName"); //AppConstants.PROJECTION_STRING +fileId+"_"+mongoSubId  + AppConstants.MIME_TYPE_CSV;
        File file = storageService.getBlob(profile,
                "tenant/" + jobParameters.getString("CompanyKey") + AppConstants.PROJECTION_PATH, fileName);
        FlatFileItemReader<MonitorReadingDailyCSV> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource(file));
        flatFileItemReader.setName("CSV-Reader");
        flatFileItemReader.setLinesToSkip(1);//Skip header line first
        flatFileItemReader.setLineMapper(lineMapper());
        return flatFileItemReader;
    }

    @Bean("ProjectionLineMapper")
    @StepScope()
    public LineMapper<MonitorReadingDailyCSV> lineMapper() {
        JobParameters jobParameters = jobHandler.getJobParametersByJobName(AppConstants.PROJECTION_IMPORT_JOB);
        DefaultLineMapper<MonitorReadingDailyCSV> defaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        String[] header = Arrays.stream(jobParameters.getString("headers").split(","))
                .map(String::trim).collect(Collectors.toList()).toArray(new String[0]);
        lineTokenizer.setNames(header);
        BeanWrapperFieldSetMapper<MonitorReadingDailyCSV> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(MonitorReadingDailyCSV.class);
        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);
        return defaultLineMapper;
    }
}
