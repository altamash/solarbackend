package com.solar.api.saas.module.com.solar.batch.configuration.housekeeping.powermonitor;

import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.saas.module.com.solar.batch.service.StageMonitorService;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIAuthBody;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.service.preferences.TenantConfigService;
import com.solar.api.tenant.service.process.pvmonitor.MonitorWrapperService;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Configuration
public class SolisBatchConfig {
    @Autowired
    private TenantConfigService tenantConfigService;
    @Autowired
    private StageMonitorService stageMonitorService;
    @Autowired
    private MonitorWrapperService monitorWrapperService;

    @Bean("SolisBatch")
    public Job trigger(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        Step bulkCalculate = stepBuilderFactory.get("SolisBatch")
                .tasklet(fillMonitorData())
                .build();
        return jobBuilderFactory.get(AppConstants.SOLIS_BATCH)
                .incrementer(new RunIdIncrementer())
                .start(bulkCalculate)
                .build();
    }

    private Tasklet fillMonitorData() {
        return (contribution, chunkContext) -> {
            chunkContext.getStepContext().getStepExecutionContext();
            try {
                Optional<TenantConfig> tenantConfig = tenantConfigService.findByParameter(Constants.TENANT_CONFIG_CATEGORY.PARAM_ISMONGOENABLED);
                if (tenantConfig.isPresent() && tenantConfig.get().getText().equals("1")) {
                    List<String> subscriptionIds = stageMonitorService.getAllSubscriptions(String.valueOf(chunkContext.getStepContext().getJobParameters().get(AppConstants.monitorPlatform))).stream()
                            .map(ExtDataStageDefinition::getSubsId).collect(Collectors.toList());
                    monitorWrapperService.saveCurrentDataMongoBatch(MonitorAPIAuthBody.builder().subscriptionIdsMongo(subscriptionIds).build());
                }
            } catch (Exception ex) {
                chunkContext.getStepContext().getStepExecution().setExitStatus(new ExitStatus("FAILED", ex.getMessage()));
            }
            return RepeatStatus.FINISHED;
        };
    }
}
