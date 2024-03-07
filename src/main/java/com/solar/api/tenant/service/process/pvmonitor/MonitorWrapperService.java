package com.solar.api.tenant.service.process.pvmonitor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.solar.api.helper.Utility;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.mapper.billing.PowerMonitorPercentileDTO;
import com.solar.api.tenant.mapper.projection.MasterProjectionDataWrapper;
import com.solar.api.tenant.mapper.projection.ProjectionDataWrapper;
import com.solar.api.tenant.mapper.pvmonitor.*;
import com.solar.api.tenant.model.pvmonitor.MonitorReading;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public interface MonitorWrapperService {

    SimpleDateFormat formatDateTime = new SimpleDateFormat(Utility.SYSTEM_DATE_TIME_FORMAT);
    SimpleDateFormat formatDate = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
    SimpleDateFormat formatDateTimeSSDT = new SimpleDateFormat(Utility.SYSTEM_S_SSDT_FORMAT);

    MonitorAPIAuthBody addMonitorReadings(MonitorAPIAuthBody monitorAPIAuthBody);

    MonitorAPIAuthBody addMonitorReadingsMongo(MonitorAPIAuthBody monitorAPIAuthBody);

    void addMonitorReadingsMongo(List<String> subscriptionIds, List<String> monitorPlatforms, String fromDateTime, String toDateTime, Boolean instantaneousCall);

    MonitorAPIAuthResponse getAuthData(Object... params) throws NoSuchAlgorithmException, UnsupportedEncodingException, JsonProcessingException;

    MonitorAPIResponse getCurrentWidgetData(MonitorAPIAuthBody body, boolean isRefresh) throws NoSuchAlgorithmException, ParseException, UnsupportedEncodingException, JsonProcessingException;

    GraphDataWrapper getCurrentGraphData(MonitorAPIAuthBody body, boolean isComparison, boolean isSubscriptionComparison) throws ParseException;

    GraphDataMonthlyWrapper getMonthlyGraphData(MonitorAPIAuthBody body, boolean isComparison, boolean isSubscriptionComparison, boolean isWeekly) throws ParseException;

    MonitorResponseWrapper getCurrentData(MonitorAPIAuthBody body, boolean isRefresh, boolean isComparison, boolean isSubscriptionComparison) throws NoSuchAlgorithmException, ParseException, UnsupportedEncodingException, JsonProcessingException;

    List<MonitorReading> saveCurrentData(MonitorAPIAuthBody body) throws NoSuchAlgorithmException, ParseException, UnsupportedEncodingException, JsonProcessingException;

    List<MonitorReading> saveCurrentDataMongo(MonitorAPIAuthBody authBody);

    Double getTreesPlantedFactorLegacy(List<Long> subscriptionIds);

    Double getTreesPlantedFactor(List<String> subscriptionIds);

    Double getCO2ReductionLegacy(List<Long> subscriptionIds);

    Double getCO2Reduction(List<String> subscriptionIds);

    GraphDataYearlyWrapper getYearlyGraphData(MonitorAPIAuthBody body, boolean isComparison, boolean isSubscriptionComparison, boolean isQuarterly) throws ParseException;

    MonitorAPIResponse getAllUsersCurrentWidgetData(Integer pageNumber, Integer pageSize) throws NoSuchAlgorithmException, ParseException, UnsupportedEncodingException, JsonProcessingException;

    GraphDataMonthlyWrapper getMonthlyCustomerGraphData(MonitorAPIAuthBody body) throws ParseException;

    GraphDataYearlyWrapper getYearlyCustomerGraphData(MonitorAPIAuthBody body) throws ParseException;

    GraphDataWrapper getCurrentCustomerGraphData(MonitorAPIAuthBody body) throws ParseException;

    List<MonitorReading> saveCurrentDataMongoBatch(MonitorAPIAuthBody authBody) throws UnsupportedEncodingException, JsonProcessingException, ParseException;

    BaseResponse loadFilterData(String exportDTO);

    BaseResponse getMonitorReadingExportData(String type, String subsIds, String stateDate, String endDate, Boolean isCust, Integer pageNumber, Integer pageSize);

    BaseResponse getPercentileByMonthAndSub(String month, String subsId);

    List<PowerMonitorPercentileDTO> generatePercentileByMonthAndSub(String month, String subsId);

    void dataConversionForBillingCredits(String month,Long jobId);

    ProjectionDataWrapper getYearlyMongoProjectionData(String mongoProjectionId,String gardenId);

    MasterProjectionDataWrapper getMonthlyMongoProjectionDataForYears(String mongoProjectionId);

    MasterProjectionDataWrapper getMonthlyMongoProjectionDataForDates(String mongoProjectionId);

    MasterProjectionDataWrapper getMongoProjectionDataForQuarterly(String mongoProjectionId);
    }
