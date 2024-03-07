package com.solar.api.tenant.service.process.pvmonitor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.solar.api.configuration.SpringContextHolder;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIAuthBody;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIAuthResponse;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIResponse;
import com.solar.api.tenant.mapper.pvmonitor.MonitorReadingDTO;
import com.solar.api.tenant.model.pvmonitor.MonitorReading;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.repository.MonitorReadingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.solar.api.helper.Utility.SYSTEM_DATE_TIME_FORMAT;
import static java.util.stream.Collectors.toList;

public interface MonitorAPI {

    Logger LOGGER = LoggerFactory.getLogger(MonitorAPI.class);

    MonitorAPIAuthResponse getAuthData(Object... params) throws NoSuchAlgorithmException, JsonProcessingException, UnsupportedEncodingException;

    MonitorAPIResponse getCurrentData(CustomerSubscription cs, Object... params) throws NoSuchAlgorithmException, JsonProcessingException, UnsupportedEncodingException, ParseException;

    /* params order:
    *   1 from DateTime
    *   2 isWidget
    *   3 last saved record DateTime */
    MonitorAPIResponse getCurrentData(ExtDataStageDefinition ext, Object... params) throws NoSuchAlgorithmException, ParseException, JsonProcessingException, UnsupportedEncodingException;

    List<MonitorReading> getMonitorReadingDataForCsComparison(MonitorAPIAuthBody body, CustomerSubscription cs) throws ParseException;

    List<MonitorReading> getMonitorReadingDataForUserComparison(Long userId, MonitorAPIAuthBody body,  List<CustomerSubscription> customerSubscriptionList) throws ParseException;

    /**
     * For ProjectIds (variantIds)
     * @param projectId
     * @param body
     * @param customerSubscriptionList
     * @return
     * @throws ParseException
     */
    List<MonitorReading> getMonitorReadingDataForUserComparison(String projectId, MonitorAPIAuthBody body,  List<CustomerSubscription> customerSubscriptionList) throws ParseException;

    List<MonitorReading> getMonitorReadingDataForMongoComparison(MonitorAPIAuthBody body, String subsId) throws ParseException;

    boolean savesBulk();

    default MonitorReadingHistoricDates getHistoricDates() {
        return null;
    }

    default List<String> getAdjustedDateRange(String extSubscriptionId, String fromDateTime, String toDateTime,
                                              ZoneId zoneId, String ssdt, MonitorReading lastRecord,
                                              SimpleDateFormat dateTimeFormat, Integer minutesIncrement,
                                              boolean onlyDateBounds, boolean force) {
        Date ssdtDateTime = null;
        try {
//            ssdtDateTime = Utility.getZonedDate(MonitorWrapperService.formatDateTimeSSDT.parse(ssdt.replace("GMT", "")), "UTC");
            ssdtDateTime = MonitorWrapperService.formatDateTimeSSDT.parse(ssdt.replace("GMT", ""));
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return getHistoricDates().getEffectiveDateTimeRange(fromDateTime, toDateTime, zoneId, ssdtDateTime,
                        minutesIncrement, new SimpleDateFormat(SYSTEM_DATE_TIME_FORMAT), lastRecord, extSubscriptionId,
                        onlyDateBounds, force)
                .stream()
                .map(dateTimeFormat::format)
                .collect(toList());
    }

}
