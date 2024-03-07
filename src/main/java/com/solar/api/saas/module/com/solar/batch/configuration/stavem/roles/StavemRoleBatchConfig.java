package com.solar.api.saas.module.com.solar.batch.configuration.stavem.roles;

import com.solar.api.AppConstants;
import com.solar.api.saas.model.SaasSchema;
import com.solar.api.saas.service.StorageService;
import com.solar.api.saas.service.job.JobHandler;
import com.solar.api.tenant.model.stavem.StavemRoles;
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
public class StavemRoleBatchConfig {

    @Autowired
    private JobHandler jobHandler;
    @Autowired
    private StorageService storageService;

    @Value("${app.profile}")
    private String appProfile;

    @Bean("StavemRoleBatch")
    public Job stavemBatchJob(JobBuilderFactory jobBuilderFactory2,
                              StepBuilderFactory stepBuilderFactory2,
                              ItemReader<StavemRoles> stavemRolesItemReader,
                              ItemProcessor<StavemRoles, StavemRoles> stavemRolesItemProcessor,
                              ItemWriter<StavemRoles> stavemRolesItemWriter
    ) {

        Step step1 = stepBuilderFactory2.get("EXTRACT")
                .<StavemRoles, StavemRoles>chunk(1000)
                .reader(stavemRolesItemReader)
                .processor(stavemRolesItemProcessor)
                .writer(stavemRolesItemWriter)
                .build();

//        Step step2 = stepBuilderFactory.get("VALIDATE")
//                .tasklet(Validate())
//                .build();
//
//        Step step3 = stepBuilderFactory.get("LOAD")
//                .tasklet(PostJob())
//                .build();

        return jobBuilderFactory2.get(AppConstants.STAVEM_ROLES_IMPORT)
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
    public FlatFileItemReader<StavemRoles> stavemRolesFlatFileItemReader() {
        long fileId = jobHandler.getLastJobInstanceId(AppConstants.STAVEM_ROLES_IMPORT);
        File file = storageService.getBlob(appProfile, "tenant/" + SaasSchema.Company.EC1001.getCompKey()
                        + AppConstants.STAVEM_ROLES_PATH,
                AppConstants.STAVEM_ROLES_STRING + fileId + AppConstants.MIME_TYPE_CSV);
        FlatFileItemReader<StavemRoles> csgFlatFileItemReader = new FlatFileItemReader<>();
        csgFlatFileItemReader.setResource(new FileSystemResource(file));
        csgFlatFileItemReader.setName("CSV-Reader");
        csgFlatFileItemReader.setLineMapper(stavemRolesLineMapper());
        return csgFlatFileItemReader;
    }

    @Bean
    public LineMapper<StavemRoles> stavemRolesLineMapper() {

        DefaultLineMapper<StavemRoles> defaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("employeeName", "phaseId" ,"phaseName" ,"roleName" ,"rateName" ,"rate" ,"hours");
        BeanWrapperFieldSetMapper<StavemRoles> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(StavemRoles.class);

        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);

        return defaultLineMapper;
    }
}
