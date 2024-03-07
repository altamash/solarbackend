package com.solar.api.saas.module.com.solar.batch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.solar.api.tenant.mapper.BaseResponse;
import com.solar.api.tenant.model.pvmonitor.MonitorReading;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.stage.monitoring.SolrenviewResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;

public interface SolrenviewService {

    ResponseEntity<String> getSVHistoricData(String startTime, String endTime);

    BaseResponse getSVData();

    SolrenviewResponseDTO hitURL() throws JsonProcessingException;

    MonitorReading subscriptionLastRecord(String subsId, String sysId);

    void save(MonitorReading monitorReading);

    void saveMonitorDaily(MonitorReading monitorReading, ExtDataStageDefinition subscription, Date date);

    Double getMonthlyYield(Date date, Double dailyYield, MonitorReading lastRecord);

    Double getYearlyYield(Date date, MonitorReading lastRecord);

    Double getDailyYield(Date date, String inverterNo, String subsID);

    Double getHighestCurrentValueOfTheDay(Date date, String inverterNo, String subsID);
}
