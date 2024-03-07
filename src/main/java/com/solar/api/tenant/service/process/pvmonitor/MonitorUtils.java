package com.solar.api.tenant.service.process.pvmonitor;

import com.solar.api.configuration.SpringContextHolder;
import com.solar.api.tenant.mapper.pvmonitor.MonitorReadingDTO;
import com.solar.api.tenant.model.pvmonitor.MonitorReading;
import com.solar.api.tenant.repository.MonitorReadingRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MonitorUtils {

    public static Date roundTimeToMinutes(Date date, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int minute = calendar.get(Calendar.MINUTE);
        int minuteRemainder = minute % minutes;
        int roundedMinute = minute - minuteRemainder;
        calendar.set(Calendar.MINUTE, roundedMinute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static List<MonitorReadingDTO> checkAndUpdateExisting(List<MonitorReadingDTO> monitorReadingDTOs, String subscriptionId) {
        List<MonitorReading> existing = SpringContextHolder.getApplicationContext()
                .getBean(MonitorReadingRepository.class)
                .findBySubscriptionIdMongoAndTimeIn(subscriptionId, monitorReadingDTOs.stream()
                        .map(m -> m.getTime())
                        .collect(Collectors.toList())
                );
        return monitorReadingDTOs.stream().map(dto -> {
            Optional<MonitorReading> readingOptional = existing.stream()
                    .filter(m -> m.getTime().getTime() == dto.getTime().getTime()).findFirst();
            if (readingOptional.isPresent()) {
                MonitorReading reading = readingOptional.get();
                dto.setId(reading.getId());
                dto.setCreatedAt(reading.getCreatedAt());
            }
            return dto;
        }).collect(Collectors.toList());
    }
}
