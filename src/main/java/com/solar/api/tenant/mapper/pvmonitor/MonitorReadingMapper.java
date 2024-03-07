package com.solar.api.tenant.mapper.pvmonitor;

import com.solar.api.helper.Utility;
import com.solar.api.tenant.model.pvmonitor.*;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinitionDTO;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MonitorReadingMapper {
    //  MonitorReading
    public static MonitorReading toMonitorReadingDTO(MonitorAPIResponse response) {

        return MonitorReading.builder()
                .userId(response.getUserId())
                .site(response.getSite())
                .inverterNumber(response.getInverterNumber())
                .sytemSize(response.getSytemSize())
                .currentValueToday(response.getCurrentValueToday())
                .currentValue(response.getCurrentValue())
                .peakValue(response.getPeakValue())
                .dailyYield(response.getDailyYield())
                .monthlyYield(response.getMonthYield())
                .annualYield(response.getAnnualYield())
                .grossYield(response.getGrossYield())
                .time(response.getDateTime())
                .build();
    }

    public static MonitorReading toMonitorReading(MonitorReadingDTO reading) {
        if (reading == null) {
            return null;
        }
        return MonitorReading.builder()
                .id(reading.getId())
                .userId(reading.getUserId())
                .subscriptionId(reading.getSubscriptionId())
                .subscriptionIdMongo(reading.getSubscriptionIdMongo())
                .site(reading.getSite())
                .inverterNumber(reading.getInverterNumber())
                .sytemSize(reading.getSytemSize())
                .currentValueToday(reading.getCurrentValueToday())
                .currentValue(reading.getCurrentValue())
                .currentValueRunning(reading.getCurrentValueRunning())
                .yieldValue(reading.getYieldValue())
                .yieldValueRunning(reading.getYieldValueRunning())
                .peakValue(reading.getPeakValue())
                .dailyYield(reading.getDailyYield())
                .monthlyYield(reading.getMonthlyYield())
                .annualYield(reading.getAnnualYield())
                .grossYield(reading.getGrossYield())
                .rawYield(reading.getRawYield())
                .time(reading.getTime())
                .durl(reading.getDurl())
                .logs(reading.getLogs())
                .createdAt(reading.getCreatedAt())
                .build();
    }

    public static MonitorReadingDTO toMonitorReadingDTO(MonitorReading reading) {
        if (reading == null) {
            return null;
        }
        return MonitorReadingDTO.builder()
                .id(reading.getId())
                .userId(reading.getUserId())
                .subscriptionId(reading.getSubscriptionId())
                .subscriptionIdMongo(reading.getSubscriptionIdMongo())
                .site(reading.getSite())
                .inverterNumber(reading.getInverterNumber())
                .sytemSize(reading.getSytemSize())
                .currentValueToday(reading.getCurrentValueToday())
                .currentValue(reading.getCurrentValue())
                .currentValueRunning(reading.getCurrentValueRunning())
                .yieldValue(reading.getYieldValue())
                .yieldValueRunning(reading.getYieldValueRunning())
                .peakValue(reading.getPeakValue())
                .dailyYield(reading.getDailyYield())
                .monthlyYield(reading.getMonthlyYield())
                .annualYield(reading.getAnnualYield())
                .grossYield(reading.getGrossYield())
                .rawYield(reading.getRawYield())
                .time(reading.getTime())
                .durl(reading.getDurl())
                .logs(reading.getLogs())
                .createdAt(reading.getCreatedAt())
                .build();
    }

    public static MonitorAPIResponse toMonitorAPIResponse(MonitorReading reading) {

        if (reading == null) {
            return null;
        }
        return MonitorAPIResponse.builder()
                .id(reading.getId())
                .userId(reading.getUserId())
                .subscriptionId(reading.getSubscriptionId())
                .subscriptionIdMongo(reading.getSubscriptionIdMongo())
                .site(reading.getSite())
                .inverterNumber(reading.getInverterNumber())
                .sytemSize(reading.getSytemSize())
                .currentValueToday(reading.getCurrentValueToday())
                .currentValue(reading.getCurrentValue())
                .currentValueRunning(reading.getCurrentValue())
                .yieldValue(reading.getYieldValue())
                .yieldValueRunning(reading.getYieldValueRunning())
                .peakValue(reading.getPeakValue())
                .dailyYield(reading.getDailyYield())
                .monthYield(reading.getMonthlyYield())
                .annualYield(reading.getAnnualYield())
                .grossYield(reading.getGrossYield())
                .dateTime(reading.getTime())
                .build();
    }

    public static List<MonitorReading> toMonitorReadings(List<MonitorAPIResponse> monitorReadingDTOs) {
        return monitorReadingDTOs.stream().map(res -> toMonitorReadingDTO(res)).collect(Collectors.toList());
    }

    public static List<MonitorReading> toMonitorReading(List<MonitorReadingDTO> monitorReadingDTOs) {
        return monitorReadingDTOs.stream().map(res -> toMonitorReading(res)).collect(Collectors.toList());
    }

    public static List<MonitorAPIResponse> toMonitorAPIResponses(List<MonitorReading> monitorReadings) {
        return monitorReadings.stream().map(res -> toMonitorAPIResponse(res)).collect(Collectors.toList());
    }
    
    //  MonitorReadingDaily
    public static MonitorReadingDaily toMonitorReadingDaily(MonitorAPIResponse response) {
        return MonitorReadingDaily.builder()
                .userId(response.getUserId())
                .site(response.getSite())
                .inverterNumber(response.getInverterNumber())
                .currentValue(response.getCurrentValue())
                .currentValueRunning(response.getCurrentValueRunning())
                .peakValue(response.getPeakValue())
                .yieldValue(response.getYieldValue())
                .yieldValueRunning(response.getYieldValueRunning())
                .day(response.getDateTime())
                .build();
    }

    public static MonitorAPIResponse toMonitorAPIResponse(MonitorReadingDaily reading) {

        if (reading == null) {
            return null;
        }
        return MonitorAPIResponse.builder()
                .id(reading.getId())
                .userId(reading.getUserId())
                .subscriptionId(reading.getSubscriptionId())
                .site(reading.getSite())
                .inverterNumber(reading.getInverterNumber())
                .currentValue(reading.getCurrentValue())
                .currentValueRunning(reading.getCurrentValueRunning())
                .peakValue(reading.getPeakValue())
                .yieldValue(reading.getYieldValue())
                .yieldValueRunning(reading.getYieldValueRunning())
                .dateTime(reading.getDay())
                .build();
    }

    public static List<MonitorReadingDaily> toMonitorReadingDailys(List<MonitorAPIResponse> monitorReadingDailyDTOS) {
        return monitorReadingDailyDTOS.stream().map(res -> toMonitorReadingDaily(res)).collect(Collectors.toList());
    }

    public static List<MonitorAPIResponse> toMonitorAPIDailyResponses(List<MonitorReadingDaily> monitorReadingDailys) {
        return monitorReadingDailys.stream().map(res -> toMonitorAPIResponse(res)).collect(Collectors.toList());
    }

    public static List<MonitorReading> toMonitorReadingList(List<MonitorReadingDTO> readings) {
        return readings.stream().map(MonitorReadingMapper::toMonitorReading).collect(Collectors.toList());
    }

    public static List<MonitorReadingDTO> toMonitorReadingDTOs(List<MonitorReading> readings) {
        return readings.stream().map(MonitorReadingMapper::toMonitorReadingDTO).collect(Collectors.toList());
    }

    public static MonitorAPIResponse toMonitorAPIResponse(MonitorReadingYearWise readingYearWise) {

        if (readingYearWise == null) {
            return null;
        }
        return MonitorAPIResponse.builder()
                .id(readingYearWise.getId())
//                .userId(reading.getUserId())
                .subscriptionIdMongo(readingYearWise.getSubscriptionIdMongo())
                .inverterNumber(readingYearWise.getInverterNumber())
                .yieldValue(readingYearWise.getYield())
                .day(readingYearWise.getDay())
                .build();
    }

    public static MonitorAPIResponse toMonitorAPIComparativeResponse(MonitorReadingYearWise readingYearWise) {

        if (readingYearWise == null) {
            return null;
        }
        return MonitorAPIResponse.builder()
//                .id(readingYearWise.getId())
//                .userId(reading.getUserId())
                .subscriptionIdMongo(readingYearWise.getSubscriptionIdMongo())
                .inverterNumber(readingYearWise.getInverterNumber())
                .yieldValue(readingYearWise.getYield())
                .day(readingYearWise.getDay())
                .build();
    }


    public static MonitorAPIResponse toMonitorAPIResponse(MonitorReadingQuarterWise readingQuarterWise) {

        if (readingQuarterWise == null) {
            return null;
        }
        return MonitorAPIResponse.builder()
                .id(readingQuarterWise.getId())
//                .userId(reading.getUserId())
                .subscriptionIdMongo(readingQuarterWise.getSubscriptionIdMongo())
                .inverterNumber(readingQuarterWise.getInverterNumber())
                .yieldValue(readingQuarterWise.getYield())
                .day(readingQuarterWise.getDay())
                .build();
    }

    public static MonitorAPIResponse toMonitorAPIComparativeResponse(MonitorReadingQuarterWise readingQuarterWise) {

        if (readingQuarterWise == null) {
            return null;
        }
        return MonitorAPIResponse.builder()
//                .userId(reading.getUserId())
                .subscriptionIdMongo(readingQuarterWise.getSubscriptionIdMongo())
                .inverterNumber(readingQuarterWise.getInverterNumber())
                .yieldValue(readingQuarterWise.getYield())
                .day(readingQuarterWise.getDay())
                .build();
    }

    public static MonitorAPIResponse toMonitorAPIResponse(MonitorReadingMonthWise readingMonthWise) {

        if (readingMonthWise == null) {
            return null;
        }
        return MonitorAPIResponse.builder()
                .id(readingMonthWise.getId())
//                .userId(reading.getUserId())
                .subscriptionIdMongo(readingMonthWise.getSubscriptionIdMongo())
                .inverterNumber(readingMonthWise.getInverterNumber())
                .yieldValue(readingMonthWise.getYield())
                .day(readingMonthWise.getDay())
                .build();
    }

    public static MonitorAPIResponse toMonitorAPIComparativeResponse(MonitorReadingMonthWise readingMonthWise) {

        if (readingMonthWise == null) {
            return null;
        }
        return MonitorAPIResponse.builder()
//                .userId(reading.getUserId())
                .subscriptionIdMongo(readingMonthWise.getSubscriptionIdMongo())
                .inverterNumber(readingMonthWise.getInverterNumber())
                .yieldValue(readingMonthWise.getYield())
                .day(readingMonthWise.getDay())
                .build();
    }
    public static List<MonitorAPIResponse> toMonitorAPIResponse(List<MonitorReadingYearWise> monitorReadingYearWiseList) {
        return monitorReadingYearWiseList.stream().map(res -> toMonitorAPIResponse(res)).collect(Collectors.toList());
    }

    public static MonitorAPIResponse toMonitorAPIResponse(MonitorReadingWeekWise readingWeekWise) {

        if (readingWeekWise == null) {
            return null;
        }
        return MonitorAPIResponse.builder()
                .id(readingWeekWise.getId())
                .subscriptionIdMongo(readingWeekWise.getSubscriptionIdMongo())
                .inverterNumber(readingWeekWise.getInverterNumber())
                .yieldValue(readingWeekWise.getYield())
                .day(readingWeekWise.getDay())
                .build();
    }

    public static MonitorAPIResponse toMonitorAPIComparativeResponse(MonitorReadingWeekWise readingWeekWise) {

        if (readingWeekWise == null) {
            return null;
        }
        return MonitorAPIResponse.builder()
                .subscriptionIdMongo(readingWeekWise.getSubscriptionIdMongo())
                .inverterNumber(readingWeekWise.getInverterNumber())
                .yieldValue(readingWeekWise.getYield())
                .day(readingWeekWise.getDay())
                .build();
    }
    public static MonitorAPIResponse toMonitorAPIResponse(MonitorReadingDayWise readingDayWise) {

        if (readingDayWise == null) {
            return null;
        }
        return MonitorAPIResponse.builder()
                .id(readingDayWise.getId())
                .subscriptionIdMongo(readingDayWise.getSubscriptionIdMongo())
                .inverterNumber(readingDayWise.getInverterNumber())
                .yieldValue(readingDayWise.getYield())
                .day(readingDayWise.getDay())
                .build();
    }

    public static MonitorAPIResponse toMonitorAPIComparativeResponse(MonitorReadingDayWise readingDayWise) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
        SimpleDateFormat outputDayFormat = new SimpleDateFormat(Utility.DAY_FORMAT);
        String day = null;
        if (readingDayWise == null) {
            return null;
        }
        if(readingDayWise.getDay()!=null && !readingDayWise.getDay().isEmpty()){
            try {
                day = outputDayFormat.format(inputDateFormat.parse(readingDayWise.getDay()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        }
        return MonitorAPIResponse.builder()
                .subscriptionIdMongo(readingDayWise.getSubscriptionIdMongo())
                .inverterNumber(readingDayWise.getInverterNumber())
                .yieldValue(readingDayWise.getYield())
                .day(day)
                .build();
    }

    public static ExtDataStageDefinition toExtDataStageDefinition(ExtDataStageDefinitionDTO stageDefinitionDTO) {

        if (stageDefinitionDTO == null) {
            return null;
        }
        return ExtDataStageDefinition.builder().
                id(stageDefinitionDTO.getExtDataStageDefinition().getId())
                .subsId(stageDefinitionDTO.getExtDataStageDefinition().getSubsId())
                .refId(stageDefinitionDTO.getExtDataStageDefinition().getRefId())
                .refType(stageDefinitionDTO.getExtDataStageDefinition().getRefType())
                .groupId(stageDefinitionDTO.getExtDataStageDefinition().getGroupId())
                .brand(stageDefinitionDTO.getExtDataStageDefinition().getBrand())
                .mpJson(stageDefinitionDTO.getExtDataStageDefinition().getMpJson())
                .monPlatform(stageDefinitionDTO.getExtDataStageDefinition().getMonPlatform())
                .build();
    }
}
