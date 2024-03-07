package com.solar.api.saas.module.com.solar.batch.configuration.stavem;

import com.solar.api.AppConstants;
import com.solar.api.saas.model.SaasSchema;
import com.solar.api.saas.service.StorageService;
import com.solar.api.saas.service.job.JobHandler;
import com.solar.api.tenant.model.stavem.StavemThroughCSG;
import com.solar.api.tenant.service.StavemThroughCSGService;
import org.springframework.batch.core.Job;
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

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Configuration
@EnableBatchProcessing
public class StavemThroughCSGConfig {

    @Autowired
    private JobHandler jobHandler;
    @Autowired
    private StorageService storageService;
    @Autowired
    private StavemThroughCSGService stavemThroughCSGService;

    @Value("${app.profile}")
    private String appProfile;

    @Bean("StavemThroughCSGBatch")
    public Job stavemBatchJob(JobBuilderFactory jobBuilderFactory1,
                        StepBuilderFactory stepBuilderFactory1,
                        ItemReader<StavemThroughCSG> stavemThroughCSGItemReader,
                        ItemProcessor<StavemThroughCSG, StavemThroughCSG> stavemThroughCSGItemProcessor,
                        ItemWriter<StavemThroughCSG> stavemThroughCSGItemWriter
    ) {

        Step step1 = stepBuilderFactory1.get("EXTRACT")
                .<StavemThroughCSG, StavemThroughCSG>chunk(1000)
                .reader(stavemThroughCSGItemReader)
                .processor(stavemThroughCSGItemProcessor)
                .writer(stavemThroughCSGItemWriter)
                .build();

//        Step step2 = stepBuilderFactory.get("VALIDATE")
//                .tasklet(Validate())
//                .build();
//
//        Step step3 = stepBuilderFactory.get("LOAD")
//                .tasklet(PostJob())
//                .build();

        return jobBuilderFactory1.get(AppConstants.STAVEM_THROUGH_CSG_IMPORT_JOB)
                .incrementer(new RunIdIncrementer())
                .start(step1)
//                .next(step2)
//                .next(step3)
                .build();
    }

//    @Bean
//    public Tasklet Validate() {
//        return (contribution, chunkContext) -> {
//            stavemThroughCSGService.deleteAll();
//            return RepeatStatus.FINISHED;
//        };
//    }

//    @Bean
//    public Tasklet PostJob() {
//        return (contribution, chunkContext) -> {
//            billingCreditsService.mapBillingCredits();
//            return RepeatStatus.FINISHED;
//        };
//    }

    //TODO: Dynamic DB (Company Name) Fetch from header to JobParams
    @Bean()
    @StepScope()
    public FlatFileItemReader<StavemThroughCSG> stavemThroughCSGFlatFileItemReader() {
        long fileId = jobHandler.getLastJobInstanceId(AppConstants.STAVEM_THROUGH_CSG_IMPORT_JOB);
        File file = storageService.getBlob(appProfile, "tenant/" + SaasSchema.Company.EC1001.getCompKey()
                        + AppConstants.STAVEM_THROUGH_CSG_PATH,
                AppConstants.STAVEM_THROUGH_CSG_STRING + fileId + AppConstants.MIME_TYPE_CSV);
        FlatFileItemReader<StavemThroughCSG> csgFlatFileItemReader = new FlatFileItemReader<>();
        csgFlatFileItemReader.setResource(new FileSystemResource(file));
        csgFlatFileItemReader.setName("CSV-Reader");
        csgFlatFileItemReader.setLineMapper(stavemThroughCSGLineMapper());
        return csgFlatFileItemReader;
    }

    @Bean
    public LineMapper<StavemThroughCSG> stavemThroughCSGLineMapper() {

        DefaultLineMapper<StavemThroughCSG> defaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("prCo", "prGroup" ,"employee" ,"firstName" ,"lastName" ,"postingDate" ,"postingType" ,"jcCompany", "job",
                "phase" ,"smCompany" ,"smWorkOrder" ,"smScope" ,"smPayType" ,"smCostType" ,"earnCode" ,"hours" ,"rate");
        BeanWrapperFieldSetMapper<StavemThroughCSG> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(StavemThroughCSG.class);

        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);

        return defaultLineMapper;
    }
}
