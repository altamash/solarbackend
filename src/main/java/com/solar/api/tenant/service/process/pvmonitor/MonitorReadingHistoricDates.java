package com.solar.api.tenant.service.process.pvmonitor;

import com.solar.api.helper.Utility;
import com.solar.api.tenant.model.pvmonitor.MonitorReading;
import com.solar.api.tenant.repository.MonitorReadingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.solar.api.helper.Utility.SYSTEM_DATE_TIME_FORMAT;
import static java.util.Collections.EMPTY_LIST;
import static java.util.stream.Collectors.toList;

@Component
public class MonitorReadingHistoricDates {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorReadingHistoricDates.class);

    private static final String DATE_TIME_FORMAT = SYSTEM_DATE_TIME_FORMAT;

    public List<String> getAdjustedDateRange(String extSubscriptionId, String fromDateTime, String toDateTime,
                                             ZoneId zoneId, String ssdt, MonitorReading lastRecord,
                                             Integer minutesIncrement,
                                             boolean onlyDateBounds, boolean force) {
        Date ssdtDateTime = null;
        try {
//            ssdtDateTime = Utility.getZonedDate(MonitorWrapperService.formatDateTimeSSDT.parse(ssdt.replace("GMT", "")), "UTC");
            ssdtDateTime = MonitorWrapperService.formatDateTimeSSDT.parse(ssdt.replace("GMT", ""));
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
        }
        SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
        return getEffectiveDateTimeRange(fromDateTime, toDateTime, zoneId, ssdtDateTime,
                        minutesIncrement, format, lastRecord, extSubscriptionId,
                        onlyDateBounds, force)
                .stream()
                .map(format::format)
                .collect(toList());
    }

    public List<Date> getEffectiveDateTimeRange(String fromDate, String toDate, ZoneId zoneId, Date ssdtDateTime,
                                                Integer minutesIncrementDB, SimpleDateFormat format,
                                                MonitorReading lastMonitorReading, String subscriptionId,
                                                boolean onlyDateBounds, boolean force) {
        try {
            if (fromDate != null && toDate != null && format.parse(fromDate).after(format.parse(toDate))) {
                LOGGER.error("fromDate cannot be after toDate");
                return EMPTY_LIST;
            }
            Date current = new Date();
            if (fromDate != null && format.parse(fromDate).after(current)) {
                LOGGER.error("fromDate cannot be after current date");
                return EMPTY_LIST;
            }
            if (fromDate != null && format.parse(fromDate).before(ssdtDateTime)) {
                LOGGER.error("fromDate cannot be before subscription start date '{}'" + ssdtDateTime.toString());
                return EMPTY_LIST;
            }
            if (toDate != null && format.parse(toDate).after(current)) {
                LOGGER.error("toDate cannot be after current date");
                return EMPTY_LIST;
            }
        } catch (ParseException e) {
            LOGGER.error(e.getMessage());
        }
        if (force) {
            return getEffectiveDateTimeRangeForce(fromDate, toDate, DATE_TIME_FORMAT, zoneId, ssdtDateTime, minutesIncrementDB, onlyDateBounds);
        } else {
            return getEffectiveDateTimeRange(fromDate, toDate, lastMonitorReading, DATE_TIME_FORMAT, zoneId, ssdtDateTime,
                    subscriptionId, minutesIncrementDB, onlyDateBounds);
        }
    }

    private List<Date> getEffectiveDateTimeRangeForce(String fromDate, String toDate, String dateTimeFormat,
                                                      ZoneId zoneId, Date ssdtDateTime, Integer minutesIncrementDB,
                                                      boolean onlyDateBounds) {
        Date fromDateTime;
        Date toDateTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
        SimpleDateFormat format = new SimpleDateFormat(Utility.DATE_TIME_FORMAT);
        // fromDate
        if (fromDate != null) {
//            fromDateTime = Date.from(ZonedDateTime.of(LocalDateTime.parse(fromDate, formatter), zoneId).toInstant());
//            if (fromDateTime.before(ssdtDateTime)) {
//                fromDateTime = ssdtDateTime;
//            }
            try {
                fromDateTime = format.parse(fromDate);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            fromDateTime = ssdtDateTime;
        }
        // toDate
        if (toDate != null) {
            toDateTime = Date.from(ZonedDateTime.of(LocalDateTime.parse(toDate, formatter), zoneId).toInstant());
//            try {
//                toDateTime = format.parse(toDate);
//            } catch (ParseException e) {
//                throw new RuntimeException(e);
//            }
        } else {
            toDateTime = new Date();
        }
        return getDateTimes(fromDateTime, toDateTime, minutesIncrementDB, onlyDateBounds);
    }

    private List<Date> getEffectiveDateTimeRange(String fromDate, String toDate, MonitorReading lastMonitorReading,
                                                 String dateTimeFormat, ZoneId zoneId, Date ssdtDateTime,
                                                 String subscriptionId, Integer minutesIncrementDB, boolean onlyDateBounds) {
        Date fromDateTime;
        Date toDateTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
        SimpleDateFormat format = new SimpleDateFormat(Utility.DATE_TIME_FORMAT);
        // fromDate
        if (fromDate != null) {
//            fromDateTime = Date.from(ZonedDateTime.of(LocalDateTime.parse(fromDate, DateTimeFormatter.ofPattern(dateTimeFormat)), ZoneId.systemDefault()).toInstant());

//            Instant nowUtc = Date.from(ZonedDateTime.of(LocalDateTime.parse(fromDate, DateTimeFormatter.ofPattern(dateTimeFormat)), ZoneId.systemDefault()).toInstant()).toInstant();
//            ZoneId asiaSingapore = ZoneId.of(zoneId.getId());
            /*fromDateTime = Date.from(ZonedDateTime.ofInstant(Date.from(ZonedDateTime
                    .of(LocalDateTime.parse(fromDate, DateTimeFormatter.ofPattern(dateTimeFormat)),
                            ZoneId.systemDefault()).toInstant()).toInstant(), ZoneId.systemDefault()).toInstant());*/

            try {
//                fromDateTime = Utility.getZonedDate(format.parse(fromDate), "UTC");
                fromDateTime = format.parse(fromDate);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            /*try {
//                fromDateTime = Utility.fromGMT(format.parse(fromDate));
                fromDateTime = format.parse(fromDate);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }*/
//            fromDateTime = Date.from(LocalDateTime.parse(fromDate, formatter).atZone(ZoneId.systemDefault()).toInstant());
//            if (lastMonitorReading != null && lastMonitorReading.getTime().after(ssdtDateTime)) {
//                try {
//                    fromDateTime = format.parse(fromDate);
//                } catch (ParseException e) {
//                    throw new RuntimeException(e);
//                }
//                if (!fromDateTime.equals(Utility.addMinutes(lastMonitorReading.getTime(), minutesIncrementDB))) {
//                    fromDateTime = Utility.addMinutes(lastMonitorReading.getTime(), minutesIncrementDB);
//                }
        } else {
            if (lastMonitorReading != null && lastMonitorReading.getTime().after(ssdtDateTime)) {
                fromDateTime = Utility.addMinutes(lastMonitorReading.getTime(), minutesIncrementDB);
            } else {
                fromDateTime = ssdtDateTime;
            }
        }
        // toDate
        if (toDate != null) {
//            toDateTime = Date.from(ZonedDateTime.of(LocalDateTime.parse(toDate, formatter), zoneId).toInstant());
            /*try {
                toDateTime = Utility.fromGMT(format.parse(toDate));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }*/
            try {
                toDateTime = format.parse(toDate);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
//            toDateTime = Date.from(LocalDateTime.parse(toDate, formatter).atZone(ZoneId.systemDefault()).toInstant());
//            try {
//                toDateTime = format.parse(toDate);
//            } catch (ParseException e) {
//                throw new RuntimeException(e);
//            }
//            List<Date> dates = getDateTimes(fromDateTime, toDateTime, minutesIncrementDB, false);
//            List<MonitorReading> daysData = readingRepository.findBySubscriptionIdMongoAndTimeIn(subscriptionId, dates);
//            dates.removeAll(daysData.stream().map(MonitorReading::getTime).collect(Collectors.toList()));
        } else {
//            toDateTime = Date.from(ZonedDateTime.of(LocalDateTime.now(), zoneId).toInstant());
            toDateTime = new Date();
//            toDateTime = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        }
        return getDateTimes(fromDateTime, toDateTime, minutesIncrementDB, onlyDateBounds);
    }

    private List<Date> getDateTimes(Date fromDateTime, Date toDateTime, Integer minutesIncrement, boolean onlyDateBounds) {
        List<Date> dateTimes = new ArrayList<>();
        if (onlyDateBounds) {
            dateTimes.add(fromDateTime);
            dateTimes.add(toDateTime);
            return dateTimes;
        }
        if (fromDateTime.before(toDateTime)) {
            dateTimes.add(fromDateTime);
            while (fromDateTime.before(toDateTime)) {
                fromDateTime = Utility.addMinutes(fromDateTime, minutesIncrement);
                dateTimes.add(fromDateTime);
            }
        }
        dateTimes.add(toDateTime);
        return dateTimes;
    }
}
