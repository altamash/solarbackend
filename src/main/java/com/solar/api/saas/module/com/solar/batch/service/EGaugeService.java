package com.solar.api.saas.module.com.solar.batch.service;

import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.model.pvmonitor.MonitorReading;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingDaily;
import com.solar.api.tenant.model.pvmonitor.RecordedData;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import org.springframework.http.ResponseEntity;
import org.w3c.dom.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface EGaugeService {
    ResponseEntity<String> getEGaugeData();
    List<MonitorReading> xmlDecoding(ExtDataStageDefinition ext, String url, String inverterNumber, MonitorReading subscriptionRecord) throws Exception;
    RecordedData getRecordedData(Document doc, int index);
    MonitorReading subscriptionLastRecord(String subscriptionId, String inverterNumber);
    MonitorReading subscriptionLastRecord(String subscriptionId, String inverterNumber, Date date) ;

    void saveMonitorDaily(MonitorReading monitorReading, ExtDataStageDefinition subscription, Date date);

    int getInverterIndexFromXML(Document doc, String inverterId);
    List<MonitorReading>  save(List<MonitorReading> monitorReadings);

    void save(MonitorReading monitorReading);
    BaseResponse getHistoricGraphData(List<String> subscriptionIds, String fromDate, String toDate) throws Exception;

    /**
     * For calculation of daily Yield value
     */
    Double getHighestValueOfTheDay(Date date,String inverterNo,String subsID);

    Double getHighestCurrentValueOfTheDay(Date date,String inverterNo,String subsID);

    List<MonitorReadingDaily>  saveAllMonitorReadingDaily(List<MonitorReadingDaily> monitorReadingDailyList);
    void composeErrorLog(String subsId, String message, Map<String, List<String>> subscriptionMessage);

    void deleteRecordsInRangeAndCondition(String startDate,String endDate,String mp,String subsId);

}
