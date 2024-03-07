package com.solar.api.tenant.mapper.pvmonitor;

import com.solar.api.tenant.model.pvmonitor.MonitorReadingDaily;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingDailyDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MonitorReadingDailyMapper {

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
                .site(reading.getSite())
                .inverterNumber(reading.getInverterNumber())
                .currentValue(reading.getCurrentValue())
                .currentValueRunning(reading.getCurrentValueRunning())
                .peakValue(reading.getPeakValue())
                .yieldValue(reading.getYieldValue())
                .yieldValueRunning(reading.getYieldValueRunning())
//                .day(response.getDateTime())
                .build();
    }

    public static MonitorReadingDaily toMonitorReadingDaily(MonitorReadingDailyDTO reading) {
        if (reading == null) {
            return null;
        }
        return MonitorReadingDaily.builder()
                .id(reading.getId())
                .userId(reading.getUserId())
                .subscriptionId(reading.getSubscriptionId())
                .subscriptionIdMongo(reading.getSubscriptionIdMongo())
                .site(reading.getSite())
                .inverterNumber(reading.getInverterNumber())
                .currentValue(reading.getCurrentValue())
                .currentValueRunning(reading.getCurrentValueRunning())
                .peakValue(reading.getPeakValue())
                .yieldValue(reading.getYieldValue())
                .yieldValueRunning(reading.getYieldValueRunning())
                .day(reading.getDay())
                .createdAt(reading.getCreatedAt())
                .build();
    }

    public static MonitorReadingDailyDTO toMonitorReadingDailyDTO(MonitorReadingDaily reading) {
        if (reading == null) {
            return null;
        }
        return MonitorReadingDailyDTO.builder()
                .id(reading.getId())
                .userId(reading.getUserId())
                .subscriptionId(reading.getSubscriptionId())
                .subscriptionIdMongo(reading.getSubscriptionIdMongo())
                .site(reading.getSite())
                .inverterNumber(reading.getInverterNumber())
                .currentValue(reading.getCurrentValue())
                .currentValueRunning(reading.getCurrentValueRunning())
                .peakValue(reading.getPeakValue())
                .yieldValue(reading.getYieldValue())
                .yieldValueRunning(reading.getYieldValueRunning())
                .day(reading.getDay())
                .createdAt(reading.getCreatedAt())
                .build();
    }

    public static List<MonitorReadingDaily> toMonitorReadingDailys(List<MonitorAPIResponse> monitorReadingDailyDTOS) {
        return monitorReadingDailyDTOS.stream().map(res -> toMonitorReadingDaily(res)).collect(Collectors.toList());
    }

    public static List<MonitorAPIResponse> toMonitorAPIResponses(List<MonitorReadingDaily> monitorReadingDailys) {
        return monitorReadingDailys.stream().map(res -> toMonitorAPIResponse(res)).collect(Collectors.toList());
    }

    public static List<MonitorReadingDaily> toMonitorReadingDailList(List<MonitorReadingDailyDTO> list) {
        return list.stream().map(r -> toMonitorReadingDaily(r)).collect(Collectors.toList());
    }
}
