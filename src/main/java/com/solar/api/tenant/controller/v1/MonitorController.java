package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.mapper.projection.MasterProjectionDataWrapper;
import com.solar.api.tenant.mapper.projection.ProjectionDataWrapper;
import com.solar.api.tenant.mapper.pvmonitor.*;
import com.solar.api.tenant.service.process.pvmonitor.MonitorWrapperService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.List;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("MonitorController")
@RequestMapping(value = "/monitor")
public class MonitorController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private MonitorWrapperService wrapperService;

    @PostMapping("/getAuthData")
    public ResponseEntity<MonitorAPIAuthResponse> getAuthData(@RequestBody MonitorAPIAuthBody body) throws NoSuchAlgorithmException, UnsupportedEncodingException, JsonProcessingException {
        return new ResponseEntity<>(wrapperService.getAuthData(body.getUserName(), body.getUserPass()), HttpStatus.OK);
    }

    @PostMapping("/getCurrentWidgetData")
    public ResponseEntity<MonitorAPIResponse> getCurrentWidgetData(@RequestBody MonitorAPIAuthBody body,
                                                                   @RequestParam(value = "isRefresh", required =
                                                                           false, defaultValue = "false") boolean isRefresh) throws NoSuchAlgorithmException, ParseException, UnsupportedEncodingException, JsonProcessingException {
        return new ResponseEntity<>(wrapperService.getCurrentWidgetData(body, isRefresh), HttpStatus.OK);
    }

    @GetMapping("/getAllUsersCurrentWidgetData")
    public ResponseEntity<MonitorAPIResponse> getAllUsersCurrentWidgetData(@RequestParam("pageNumber") Integer pageNumber,
                                                                           @RequestParam("pageSize") Integer pageSize) throws NoSuchAlgorithmException, ParseException, UnsupportedEncodingException, JsonProcessingException {
        return new ResponseEntity<>(wrapperService.getAllUsersCurrentWidgetData(pageNumber, pageSize), HttpStatus.OK);
    }

    @PostMapping("/getCurrentGraphData")
    public GraphDataWrapper getCurrentGraphData(
            @RequestBody MonitorAPIAuthBody body,
            @RequestParam(value = "isComparison", required = false, defaultValue = "false") boolean isComparison,
            @RequestParam(value = "isSubscriptionComparison", required = false, defaultValue = "true") boolean isSubscriptionComparison)
            throws ParseException {
        return wrapperService.getCurrentGraphData(body, isComparison, isSubscriptionComparison);
    }

    @PostMapping("/getMonthlyCurrentGraphData")
    public GraphDataMonthlyWrapper getMonthlyGraphData(@RequestBody MonitorAPIAuthBody body,
                                                       @RequestParam(value = "isComparison", required =
                                                               false, defaultValue = "false") boolean isComparison,
                                                       @RequestParam(value = "isSubscriptionComparison", required =
                                                               false, defaultValue = "true") boolean isSubscriptionComparison) throws ParseException {

        return wrapperService.getMonthlyGraphData(body, isComparison, isSubscriptionComparison, false);
    }

    @PostMapping("/getWeeklyGraphData")
    public GraphDataMonthlyWrapper getWeeklyGraphData(@RequestBody MonitorAPIAuthBody body,
                                                      @RequestParam(value = "isComparison", required =
                                                              false, defaultValue = "false") boolean isComparison,
                                                      @RequestParam(value = "isSubscriptionComparison", required =
                                                              false, defaultValue = "true") boolean isSubscriptionComparison) throws ParseException {

        return wrapperService.getMonthlyGraphData(body, isComparison, isSubscriptionComparison, true);
    }

    @PostMapping("/getQuarterlyCurrentGraphData")
    public GraphDataYearlyWrapper getQuarterlyGraphData(@RequestBody MonitorAPIAuthBody body,
                                                       @RequestParam(value = "isComparison", required =
                                                               false, defaultValue = "false") boolean isComparison,
                                                       @RequestParam(value = "isSubscriptionComparison", required =
                                                               false, defaultValue = "true") boolean isSubscriptionComparison) throws ParseException {

        return wrapperService.getYearlyGraphData(body, isComparison, isSubscriptionComparison, true);
    }

    @PostMapping("/getYearlyCurrentGraphData")
    public GraphDataYearlyWrapper getYearlyGraphData(@RequestBody MonitorAPIAuthBody body,
                                                     @RequestParam(value = "isComparison", required =
                                                             false, defaultValue = "false") boolean isComparison,
                                                     @RequestParam(value = "isSubscriptionComparison", required =
                                                             false, defaultValue = "true") boolean isSubscriptionComparison) throws ParseException {

        return wrapperService.getYearlyGraphData(body, isComparison, isSubscriptionComparison, false);
    }

    @ApiOperation(value = "isComparison if true returns multiples graph data (lines)")
    @PostMapping("/getCurrentData")
    public MonitorResponseWrapper getCurrentData(@RequestBody MonitorAPIAuthBody body,
                                                 @RequestParam(value = "isRefresh", required = false,
                                                         defaultValue = "false") boolean isRefresh,
                                                 @RequestParam(value = "isComparison", required = false,
                                                         defaultValue = "false") boolean isComparison,
                                                 @RequestParam(value = "isSubscriptionComparison", required =
                                                         false, defaultValue = "true") boolean isSubscriptionComparison)

            throws NoSuchAlgorithmException, ParseException, UnsupportedEncodingException, JsonProcessingException {
        return wrapperService.getCurrentData(body, isRefresh, isComparison, isSubscriptionComparison);
    }

/*    @PostMapping("/saveCurrentData/user/{userName}/time/{time}")
    public List<MonitorAPIResponse> saveCurrentData(@PathVariable String userName, @PathVariable String time) throws NoSuchAlgorithmException, ParseException, UnsupportedEncodingException, JsonProcessingException {
        return MonitorReadingMapper.toMonitorAPIResponses(wrapperService.saveCurrentData(userName, time));
    }*/


    @PostMapping("/monitorReadings/user")
    public void addMonitorReadings(@RequestBody MonitorAPIAuthBody body) {
        try {
            wrapperService.saveCurrentData(body);

        } catch (ParseException | NoSuchAlgorithmException | UnsupportedEncodingException | JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @GetMapping("/treesPlantedFactorLegacy/user/{userIds}")
    public Double getTreesPlantedFactorLegacy(@RequestParam List<Long> userIds) {
        return wrapperService.getTreesPlantedFactorLegacy(userIds);
    }

    @GetMapping("/carbonReductionLegacy/user/{userIds}")
    public Double getCarbonReductionLegacy(@RequestParam List<Long> userIds) {
        return wrapperService.getCO2ReductionLegacy(userIds);
    }

    @GetMapping("/treesPlantedFactor/user/{userIds}")
    public Double getTreesPlantedFactor(@RequestParam List<String> userIds) {
        return wrapperService.getTreesPlantedFactor(userIds);
    }

    @GetMapping("/carbonReduction/user/{userIds}")
    public Double getCarbonReduction(@RequestParam List<String> userIds) {
        return wrapperService.getCO2Reduction(userIds);
    }

    @GetMapping("/addMonitorReadings")
    public void addMonitorReadings(@RequestParam(value = "extSubscriptionIds", required = false) List<String> subscriptionIds,
                                   @RequestParam(value = "brand", required = false) @ApiParam("GOODWE SOLAX SOLIS SOLAREDGE ENPHASE (supported)") List<String> monitorPlatforms,
                                   @RequestParam(value = "today", defaultValue = "true") Boolean instantaneousCall,
                                   @RequestParam(value = "fromDateTime", required = false) @ApiParam("yyyy-MM-dd HH:mm:ss") String fromDateTime,
                                   @RequestParam(value = "toDateTime", required = false) @ApiParam("yyyy-MM-dd HH:mm:ss") String toDateTime) {
        if (monitorPlatforms.contains("GOODWE") || monitorPlatforms.contains("SOLAX") ||
                monitorPlatforms.contains("SOLIS") || monitorPlatforms.contains("SOLAREDGE") ||
                monitorPlatforms.contains("ENPHASE")) {
            wrapperService.addMonitorReadingsMongo(subscriptionIds, monitorPlatforms, fromDateTime, toDateTime, instantaneousCall);
        }
    }

    @PostMapping("/getCustomerCurrentGraphData")
    public GraphDataWrapper getCustomerCurrentGraphData(@RequestBody MonitorAPIAuthBody body) throws ParseException {
        return wrapperService.getCurrentCustomerGraphData(body);
    }

    @PostMapping("/getCustomerMonthlyCurrentGraphData")
    public GraphDataMonthlyWrapper getCustomerMonthlyGraphData(@RequestBody MonitorAPIAuthBody body) throws ParseException {
        return wrapperService.getMonthlyCustomerGraphData(body);
    }

    @PostMapping("/getCustomerYearlyCurrentGraphData")
    public GraphDataYearlyWrapper getCustomerYearlyGraphData(@RequestBody MonitorAPIAuthBody body) throws ParseException {
        return wrapperService.getYearlyCustomerGraphData(body);
    }

    @GetMapping("/loadFilterData")
    public BaseResponse loadFilterData(@RequestParam(value = "exportDTO", required = false) String exportDTO) {
        return wrapperService.loadFilterData(exportDTO);
    }

    @GetMapping("/getMonitorReadingExportData")
    public BaseResponse getMonitorReadingExportData(@RequestParam("type") String type, @RequestParam("subsIds") String subsIds, @RequestParam("startDate") String startDate,
                                                    @RequestParam("endDate") String endDate, @RequestParam(value = "isCust", required = true, defaultValue = "true") boolean isCust,
                                                    @RequestParam("pageNumber") Integer pageNumber,
                                                    @RequestParam("pageSize") Integer pageSize) {
        return wrapperService.getMonitorReadingExportData(type, subsIds, startDate, endDate, isCust, pageNumber, pageSize);
    }

    @GetMapping("/getPercentileByMonthAndSub")
    public BaseResponse getPercentileByMonthAndSub(@RequestParam("month") String month, @RequestParam("subsId") String subsId) {
        return wrapperService.getPercentileByMonthAndSub(month, subsId);
    }

    @GetMapping("/getProjectionYearlyData")
    public ProjectionDataWrapper getProjectionYearlyData(@RequestParam("mongoSubId") String mongoSubId,
                                                         @RequestParam("gardenId") String gardenId) {
        return wrapperService.getYearlyMongoProjectionData(mongoSubId,gardenId);
    }

    @GetMapping("/getMonthlyProjectionDataForYears")
    public MasterProjectionDataWrapper getMonthlyProjectionDataForYears(@RequestParam("mongoSubId") String mongoSubId) {
        return wrapperService.getMonthlyMongoProjectionDataForYears(mongoSubId);
    }

    @GetMapping("/getMonthlyMongoProjectionDataForDates")
    public MasterProjectionDataWrapper getMonthlyMongoProjectionDataForDates(@RequestParam("mongoSubId") String mongoSubId) {
        return wrapperService.getMonthlyMongoProjectionDataForDates(mongoSubId);
    }

    @GetMapping("/getMongoProjectionDataForQuarterly")
    public MasterProjectionDataWrapper getMongoProjectionDataForQuarterly(@RequestParam("mongoSubId") String mongoSubId) {
        return wrapperService.getMongoProjectionDataForQuarterly(mongoSubId);
    }
}
