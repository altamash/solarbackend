package com.solar.api.saas.module.com.solar.utility;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.saas.model.extended.BatchDefinition;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.saas.module.com.solar.scheduler.model.JobScheduler;
import com.solar.api.tenant.mapper.weather.WeatherDTO;
import org.springframework.batch.core.JobExecution;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface BatchEngineUtilityService {

    ObjectNode scheduleJobWithParameters(Map<String, String> hashmap);

    void scheduleJobWithDifferential(JobExecution jobExecution);

    String generateCron(LocalDateTime localDateTime);

    List<JobScheduler> getAll();

    void emptyScheduledTaskList();

    String timeTest();

    BaseResponse<?> scheduleJob(Long jobId);

    void removeScheduledTask(String jobId);

    List<WeatherDTO> getFifteenDayWeatherDay(Long compKey);

}
