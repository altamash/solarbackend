package com.solar.api.saas.module.com.solar.batch.service;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.solar.api.tenant.mapper.pvmonitor.solaredge.MeterEnergyDetailResponse;
import com.solar.api.tenant.mapper.pvmonitor.solaredge.energy.SiteEnergyResponse;
import com.solar.api.tenant.mapper.pvmonitor.solaredge.overview.SiteOverviewResponse;
import com.solar.api.tenant.model.pvmonitor.MonitorReading;
import com.solar.api.tenant.model.pvmonitor.SolarEdgeDaily;
import org.springframework.http.ResponseEntity;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface SolarEdgeService {
    void saveMonitorReadings(List<MonitorReading> monitorReadings);
    @Deprecated
    ResponseEntity<Object>  runDaily(Date date);
    @Deprecated
    ResponseEntity<Object> runAtIntervals(Date date);

    @Deprecated
    void saveDailyEdge(List<SolarEdgeDaily> solarEdgeDailyList);

    @Deprecated
    ResponseEntity<Object> runDailyHistoric(String fromDate, String toDate);

    ResponseEntity<Object> runAtIntervalsHistoricBetween(String fromDate, String toDate);

    ResponseEntity<Object> getCurrentData();

    @Deprecated
    ResponseEntity<Object> runAtIntervalsHistoric(String date);

    @Deprecated
    List<SolarEdgeDaily> getAnnualYield(String siteId, String inverterNumber, String subscriptionId, Date date, String year, String token) throws UnirestException;
    @Deprecated
    List<SolarEdgeDaily> getMonthlyAndGrossYield(List<SolarEdgeDaily> solarEdgeDailyList, String siteId, Date date, String token) throws UnirestException, ParseException;
    @Deprecated
    Double getLastGrossYield(String siteId, String inverterNumber, String subscriptionId, Date date);
    @Deprecated
    SolarEdgeDaily getTodayRecord(String siteId, String inverterNumber, String subscriptionId, Date date);
    @Deprecated
    Double findMaxPeakValue(String subscriptionIdMongo);
    MonitorReading subscriptionLastRecord(String subscriptionIdMongo, String inverterNumber, String siteId, Date date);
    @Deprecated
    List<MonitorReading> performCalculations(String siteId, MonitorReading lr, SolarEdgeDaily solarEdgeDaily, String token) throws UnirestException;
    @Deprecated
    MeterEnergyDetailResponse getMeterDataBySiteId(String siteId, MonitorReading lastRecord, String token) throws UnirestException;
    @Deprecated
    MeterEnergyDetailResponse getMonthlyYieldBySiteId(Date date, String siteId, String token) throws UnirestException, ParseException;
    @Deprecated
    MeterEnergyDetailResponse getAnnualYieldBySiteId(String siteId, String year, String token) throws UnirestException;

    SiteEnergyResponse getHistoricYieldBySiteId(String fromDate, String toDate, String siteId, String timeUnit, String token) throws UnirestException, ParseException;

    SiteOverviewResponse getSiteOverviewBySiteId(String siteId, String token) throws UnirestException, ParseException;
}
