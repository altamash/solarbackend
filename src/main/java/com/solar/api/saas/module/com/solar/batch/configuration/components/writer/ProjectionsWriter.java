package com.solar.api.saas.module.com.solar.batch.configuration.components.writer;

import com.solar.api.AppConstants;
import com.solar.api.saas.service.job.JobHandler;
import com.solar.api.saas.service.process.upload.mapper.MonitorReadingDailyCSV;
import com.solar.api.saas.service.process.upload.mapper.MonitorReadingDailyMapper;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingDaily;
import com.solar.api.tenant.repository.MonitorReadingDailyRepository;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Component
public class ProjectionsWriter implements ItemWriter<MonitorReadingDailyCSV> {

    @Autowired
    private MonitorReadingDailyRepository monitorReadingDailyRepository;
    @Autowired
    private JobHandler jobHandler;

    @Override
    public void write(List<? extends MonitorReadingDailyCSV> csv) throws Exception {
        JobParameters jobParameters = jobHandler.getJobParametersByJobName(AppConstants.PROJECTION_IMPORT_JOB);
        String mongoSubscriptionId = jobParameters.getString(AppConstants.mongoSubId);
        List<MonitorReadingDaily> mrdList = MonitorReadingDailyMapper.toMonitorReadingDailyList((List<MonitorReadingDailyCSV>) csv, mongoSubscriptionId);
        monitorReadingDailyRepository.saveAll(mrdList);
    }
}
