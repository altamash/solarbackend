package com.solar.api.tenant.service.process.pvmonitor.platform.enphase;

import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.helper.Utility;
import com.solar.api.helper.WebUtils;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIResponse;
import com.solar.api.tenant.mapper.pvmonitor.MonitorReadingDTO;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingDaily;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingDailyDTO;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.repository.MonitorReadingDailyRepository;
import com.solar.api.tenant.repository.MonitorReadingRepository;
import com.solar.api.tenant.service.process.pvmonitor.HistoricalAPI;
import com.solar.api.tenant.service.process.pvmonitor.MonitorReadingHistoricDates;
import com.solar.api.tenant.service.process.pvmonitor.platform.enphase.dto.EnphaseResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.solar.api.tenant.service.process.pvmonitor.platform.enphase.EnphaseAPI.MINUTES_INCREMENT;

@Service
public class EnphaseHistoricalAPI implements HistoricalAPI {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final MonitorReadingDailyRepository monitorReadingDailyRepository;
    private final MonitorReadingRepository readingRepository;
    private final MonitorReadingHistoricDates historicDates;
    private final EnphaseCommons enphaseCommons;
    private final Utility utility;
    private ThreadLocal<String> apiUrl = new ThreadLocal<>();

    public EnphaseHistoricalAPI(MonitorReadingDailyRepository monitorReadingDailyRepository,
                                MonitorReadingRepository readingRepository, MonitorReadingHistoricDates historicDates,
                                EnphaseCommons enphaseCommons, Utility utility) {
        this.monitorReadingDailyRepository = monitorReadingDailyRepository;
        this.readingRepository = readingRepository;
        this.historicDates = historicDates;
        this.enphaseCommons = enphaseCommons;
        this.utility = utility;
    }

    @Override
    public MonitorAPIResponse getHistoricalData(ExtDataStageDefinition ext, String fromDateTime, String toDateTime) {
        MonitorAPIResponse response = new MonitorAPIResponse();
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Utility.SYSTEM_DATE_TIME_FORMAT);
            String siteId = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.SITEID);
            // Convert date time range to Enphase API time zone (US/Eastern)
            fromDateTime = formatter
                    .format(Utility.getDateFromZoneToZone(fromDateTime, formatter, "UTC", AppConstants.EZone.US_DARIEN_CT.getName()));
            toDateTime = formatter
                    .format(Utility.getDateFromZoneToZone(toDateTime, formatter, "UTC", AppConstants.EZone.US_DARIEN_CT.getName()));
            // Get response from Enphase API
            EnphaseResponseDTO enphaseResponseDTO = getResponse(siteId, fromDateTime, toDateTime);
            int rounding = utility.getCompanyPreference().getRounding();
            // Get current values in date time map
            Map<Date, Double> dateProductionMap = getDateTimeValuesMap(fromDateTime, enphaseResponseDTO, rounding);
            SimpleDateFormat format = new SimpleDateFormat(Utility.SYSTEM_DATE_TIME_FORMAT);
            // Filter map with the date time range given
            dateProductionMap = fileterMapByRange(format, fromDateTime, toDateTime, dateProductionMap);
            // Convert date time (US/Eastern) to UTC for saving
            dateProductionMap = convertMapDateTimeToUTC(dateProductionMap, format, formatter);
            // Set MonitorReadingDTO and MonitorReadingDailyDTO records
            setInstantaneousAndDailyDTOs(enphaseResponseDTO, rounding, ext, response, siteId, dateProductionMap);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return response;
    }

    // Get response from enphase API
    private EnphaseResponseDTO getResponse(String siteId, String fromDateTime, String toDateTime) {
        setApiUrl(String.format("https://enlighten.enphaseenergy.com/pv/public_systems/%s/daily_energy?start_date=%s&end_date=%s",
                siteId, fromDateTime, toDateTime));
        ResponseEntity<EnphaseResponseDTO> apiResponse =
                WebUtils.submitRequest(HttpMethod.GET, getApiUrl(), null, new HashMap<>(),
                        EnphaseResponseDTO.class);
        return apiResponse.getBody();
    }

    // Get current values in date time map
    private Map<Date, Double> getDateTimeValuesMap(String fromDateTime, EnphaseResponseDTO enphaseResponseDTO, int rounding) {
        Map<Date, Double> dateProductionMap = new LinkedHashMap<>();
        SimpleDateFormat format = new SimpleDateFormat(Utility.SYSTEM_DATE_TIME_FORMAT);
        Date start;
        try {
            start = Utility.getStartOfDate(format.parse(fromDateTime));
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        List<Integer> production = enphaseResponseDTO.getStats().stream().flatMap(m -> m.getProduction().stream()).collect(Collectors.toList());
        dateProductionMap.put(start, utility.round(production.get(0) != null ? production.get(0) / 0.25 / 1000 : 0, rounding));
        for (int i = 1; i < production.size(); i++) {
            start = Utility.addMinutes(start, MINUTES_INCREMENT);
            dateProductionMap.put(start,
                    utility.round(production.get(i) != null ? production.get(i) / 0.25 / 1000 : 0, rounding));
        }
        return dateProductionMap;
    }

    // Fileter map with the date time range given
    private Map<Date, Double> fileterMapByRange(SimpleDateFormat format, String fromDateTime, String toDateTime,
                                   Map<Date, Double> dateProductionMap) {
        Date from;
        Date to;
        try {
            from = format.parse(fromDateTime);
            to = format.parse(toDateTime);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        // Fileter map with the date time range given
        return dateProductionMap.entrySet().stream()
                .filter(m -> (m.getKey().after(from) && (m.getKey().before(to))) || m.getKey().equals(from)
                        || m.getKey().equals(to))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
    }

    // Convert date time to UTC for saving
    private Map<Date, Double> convertMapDateTimeToUTC(Map<Date, Double> dateProductionMap, SimpleDateFormat format, DateTimeFormatter formatter) {
        return dateProductionMap.entrySet().stream()
                .map(m -> new AbstractMap.SimpleEntry<>(
                        Utility.toDate(Utility.getDateFromZoneToZone(
                                format.format(m.getKey()), formatter, AppConstants.EZone.US_DARIEN_CT.getName(), "UTC")),
                        //                            Date.from(Utility.getDateFromZoneToZone(
                        //                                    format.format(m.getKey()), formatter, AppConstants.EZone.US_DARIEN_CT.getName(), "UTC")
                        //                                    .atZone(ZoneId.systemDefault())
                        //                                    .toInstant()),
                        m.getValue())
                )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
    }

    // Set MonitorReadingDTO and MonitorReadingDailyDTO records
    private void setInstantaneousAndDailyDTOs(EnphaseResponseDTO enphaseResponseDTO, int rounding, ExtDataStageDefinition ext,
                                              MonitorAPIResponse response, String siteId, Map<Date, Double> dateProductionMap) {
        List<Date> dates = getDatesForDaily(enphaseResponseDTO);
        List<MonitorReadingDailyDTO> readingDailyDTOs = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
        String inverter = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.INVRT);
        for (int i = 0; i < dates.size(); i++) {
            double dailyYield;
            if (enphaseResponseDTO.getStats() != null && enphaseResponseDTO.getStats().get(i) != null) {
                dailyYield = utility.round(enphaseResponseDTO.getStats().get(i).getTotals().getProduction() / 1000d, rounding);
            } else {
                dailyYield = 0d;
            }
            double peakForDay = getPeakForDay(dateProductionMap, format, dates.get(i));
            MonitorReadingDaily exists = monitorReadingDailyRepository.findBySubscriptionIdMongoAndDay(ext.getSubsId(), dates.get(i));
            readingDailyDTOs.add(enphaseCommons.getMonitorReadingDailyDTO(exists, ext.getSubsId(), dates.get(i), siteId,
                    inverter, dailyYield, peakForDay));
        }
        // Set monitor_reading records for the day
        setMonitorReading(dateProductionMap, ext.getSubsId(), siteId, inverter, response);
        response.setBulkDailyRecords(readingDailyDTOs);
    }

    private List<Date> getDatesForDaily(EnphaseResponseDTO enphaseResponseDTO) {
        String startDate = enphaseResponseDTO.getStartDate();
        String endDate = enphaseResponseDTO.getEndDate();
        Date start = Utility.toDate(Utility.getDateFromZoneToZone(
                LocalDate.parse(startDate).atStartOfDay(),
//                startDate + " 00:00:00", formatter,
                AppConstants.EZone.US_DARIEN_CT.getName(), "UTC"));
        Date end = Utility.toDate(Utility.getDateFromZoneToZone(
//                endDate + " 23:59:59", formatter,
                LocalDate.parse(endDate).atTime(LocalTime.MAX),
                AppConstants.EZone.US_DARIEN_CT.getName(), "UTC"));

        List<Date> dates = new ArrayList<>();
        while (start.before(end)) {
            dates.add(start);
            start = Utility.addDays(start, 1);
        }
        return dates;
    }

    private double getPeakForDay(Map<Date, Double> dateProductionMap, SimpleDateFormat format, Date date) {
        Map<Date, Double> map = dateProductionMap.entrySet().stream()
                .filter(m -> format.format(m.getKey()).equals(format.format(date.getTime())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
        return map.values().stream().mapToDouble(m -> m.doubleValue()).max().orElse(0.0);
    }

    private void setMonitorReading(Map<Date, Double> dateProductionMap, String subsId, String siteId, String inverter,
                                   MonitorAPIResponse response) {
        List<MonitorReadingDTO> readingList = new ArrayList<>();
        dateProductionMap.entrySet().forEach(set -> {
            Double currentValue = set.getValue();
            double peakValue = response.getPeakValue() != null && response.getPeakValue() > currentValue ?
                    response.getPeakValue() : currentValue;
            response.setPeakValue(peakValue);
            readingList.add(enphaseCommons.getMonitorReadingDTO(subsId, set.getKey(), siteId, inverter,
                    currentValue, peakValue, getApiUrl()));
        });
        response.setMonitorReadingDTOs(readingList);
    }

    String getApiUrl() {
        return this.apiUrl.get();
    }

    void setApiUrl(String url) {
        this.apiUrl.set(url);
    }
}
