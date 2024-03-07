package com.solar.api.tenant.service.process.pvmonitor.platform.enphase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.solar.api.AppConstants;
import com.solar.api.helper.Utility;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIAuthBody;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIAuthResponse;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIResponse;
import com.solar.api.tenant.model.pvmonitor.MonitorReading;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.repository.MonitorReadingRepository;
import com.solar.api.tenant.service.process.pvmonitor.HistoricalAPI;
import com.solar.api.tenant.service.process.pvmonitor.InstantaneousAPI;
import com.solar.api.tenant.service.process.pvmonitor.MonitorAPI;
import com.solar.api.tenant.service.process.pvmonitor.MonitorReadingHistoricDates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class EnphaseAPI implements MonitorAPI {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    static final int MINUTES_INCREMENT = 15;
    private final MonitorReadingRepository readingRepository;
    private final MonitorReadingHistoricDates historicDates;
    private final InstantaneousAPI instantaneousAPI;
    private final HistoricalAPI historicalAPI;

    private SimpleDateFormat formatDateTimeSystem = new SimpleDateFormat(Utility.SYSTEM_DATE_TIME_FORMAT);

    public EnphaseAPI(MonitorReadingRepository readingRepository, MonitorReadingHistoricDates historicDates, InstantaneousAPI instantaneousAPI, HistoricalAPI historicalAPI) {
        this.readingRepository = readingRepository;
        this.historicDates = historicDates;
        this.instantaneousAPI = instantaneousAPI;
        this.historicalAPI = historicalAPI;
    }

    @Override
    public MonitorAPIAuthResponse getAuthData(Object... params) throws NoSuchAlgorithmException {
        return null;
    }

    @Override
    public MonitorAPIResponse getCurrentData(CustomerSubscription cs, Object... params) throws NoSuchAlgorithmException, JsonProcessingException, UnsupportedEncodingException, ParseException {
        return null;
    }

    @Override
    public List<MonitorReading> getMonitorReadingDataForCsComparison(MonitorAPIAuthBody body, CustomerSubscription cs) throws ParseException {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<MonitorReading> getMonitorReadingDataForUserComparison(Long userId, MonitorAPIAuthBody body, List<CustomerSubscription> customerSubscriptionList) throws ParseException {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<MonitorReading> getMonitorReadingDataForUserComparison(String projectId, MonitorAPIAuthBody body, List<CustomerSubscription> customerSubscriptionList) throws ParseException {
        return null;
    }

    // Important
    @Override
    public List<MonitorReading> getMonitorReadingDataForMongoComparison(MonitorAPIAuthBody body, String subsId) throws ParseException {
        List<Date> labelDates = getDateTimes(body.getTime());
        List<MonitorReading> daysData = readingRepository.findBySubscriptionIdMongoAndTimeIn(subsId, labelDates);
        return daysData;
    }

    @Override
    public boolean savesBulk() {
        return true;
    }


    private List<Date> getDateTimes(String time) throws ParseException {
        int LINE_GRAPH_INCREMENT = 30;
        String dateTimeString = time != null ? time : formatDateTimeSystem.format(new Date());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Utility.SYSTEM_DATE_TIME_FORMAT);
        Date date;
        date = Date.from(ZonedDateTime.of(LocalDateTime.parse(dateTimeString, formatter), ZoneId.of(AppConstants.EZone.US_CENTRAL.getName())).toInstant());
        List<Date> dateTimes = new ArrayList<>();
        for (int i = 0; i < 1440 / LINE_GRAPH_INCREMENT; i++) {
            dateTimes.add(date);
            date = Utility.addMinutes(date, LINE_GRAPH_INCREMENT);
        }
        return dateTimes;
    }

    // Important
    @Override
    public MonitorAPIResponse getCurrentData(ExtDataStageDefinition ext, Object... params)
            throws NoSuchAlgorithmException, ParseException {
        MonitorAPIAuthBody authBody = (MonitorAPIAuthBody) params[0];
        if (authBody.isInstantaneousCall()) {
            return instantaneousAPI.getInstantaneousData(ext);
        }
        try {
            return historicalAPI.getHistoricalData(ext, authBody.getFromDateTime(), authBody.getToDateTime());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return MonitorAPIResponse.builder()
                    .monitorReadingDTOs(Collections.EMPTY_LIST)
                    .bulkDailyRecords(Collections.EMPTY_LIST)
                    .build();
        }
    }

    @Override
    public MonitorReadingHistoricDates getHistoricDates() {
        return historicDates;
    }
}
