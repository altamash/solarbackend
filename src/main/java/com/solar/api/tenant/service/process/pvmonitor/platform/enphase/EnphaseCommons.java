package com.solar.api.tenant.service.process.pvmonitor.platform.enphase;

import com.solar.api.tenant.mapper.pvmonitor.MonitorReadingDTO;
import com.solar.api.tenant.mapper.pvmonitor.MonitorReadingDailyMapper;
import com.solar.api.tenant.mapper.pvmonitor.MonitorReadingMapper;
import com.solar.api.tenant.model.pvmonitor.MonitorReading;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingDaily;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingDailyDTO;
import com.solar.api.tenant.repository.MonitorReadingRepository;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class EnphaseCommons {

    private final MonitorReadingRepository monitorReadingRepository;

    public EnphaseCommons(MonitorReadingRepository monitorReadingRepository) {
        this.monitorReadingRepository = monitorReadingRepository;
    }

    public MonitorReadingDailyDTO getMonitorReadingDailyDTO(MonitorReadingDaily exists, String subscriptionId,
                                                             Date day, String site, String inverter,
                                                             double dailyYield, double currentValue) {
        MonitorReadingDailyDTO readingDailyDTO;
        if (exists != null) {
            readingDailyDTO = MonitorReadingDailyMapper.toMonitorReadingDailyDTO(exists);
            double peakValue = exists.getPeakValue() == null || currentValue > exists.getPeakValue() ? currentValue : exists.getPeakValue();
            readingDailyDTO.setYieldValue(dailyYield);
            readingDailyDTO.setPeakValue(peakValue);
            exists.setPeakValue(peakValue);
        } else {
            readingDailyDTO = MonitorReadingDailyDTO.builder()
                    .subscriptionIdMongo(subscriptionId)
                    .day(day)
                    .site(site)
                    .inverterNumber(inverter)
                    .yieldValue(dailyYield)
                    .peakValue(currentValue)
                    .build();
        }
        return readingDailyDTO;
    }

    public MonitorReadingDTO getMonitorReadingDTO(String subscriptionId, Date time, String site, String inverter,
                                                   double currentValue, double peakValue, String apiUrl) {
        MonitorReading exists = monitorReadingRepository.findBySubscriptionIdMongoAndTime(subscriptionId, time);
        MonitorReadingDTO monitorReadingDTO;
        if (exists != null) {
            exists.setCurrentValue(currentValue);
            exists.setYieldValue(currentValue);
            exists.setPeakValue(peakValue);
            monitorReadingDTO = MonitorReadingMapper.toMonitorReadingDTO(exists);
        } else {
            monitorReadingDTO = MonitorReadingDTO.builder()
                    .subscriptionIdMongo(subscriptionId)
                    .site(site)
                    .inverterNumber(inverter)
                    .time(time)
                    .currentValue(currentValue)
                    .yieldValue(currentValue)
                    .peakValue(peakValue)
                    .durl(apiUrl)
                    .build();
        }
        return monitorReadingDTO;
    }
}
