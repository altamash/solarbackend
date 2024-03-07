package com.solar.api.saas.module.com.solar.batch.service;

import com.solar.api.Constants;
import com.solar.api.Constants.*;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Utility;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.mapper.contract.GaugeMapper;
import com.solar.api.tenant.model.pvmonitor.MonitorReading;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingDaily;
import com.solar.api.tenant.model.pvmonitor.RecordedData;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.repository.MonitorReadingDailyRepository;
import com.solar.api.tenant.repository.MonitorReadingRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpressionException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.solar.api.Constants.E_GAUGE.THIRTY;

@Service
public class EGaugeServiceImpl implements EGaugeService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    MonitorReadingRepository monitorReadingRepository;
    @Autowired
    @Lazy
    private MonitorReadingDailyRepository dailyRepository;
    @Autowired
    private StageMonitorService stageMonitorService;
    @Autowired
    private MonitorPlatformUtilityService monitorPlatformUtilityService;

    @Override
    public ResponseEntity<String> getEGaugeData() {
        for (ExtDataStageDefinition subscription : stageMonitorService.getAllSubscriptions(MONITOR_PLATFORM.EGAUGE)) {
            try {

                LOGGER.info("subscription: {}", subscription.getSubsId());

                String inverterNumber = Utility.getMeasureAsJson(subscription.getMpJson(), RATE_CODES.EGDID);
                String deviceNumber = Utility.getMeasureAsJson(subscription.getMpJson(), RATE_CODES.DEVICE_NUMBER);
                String subStartDate = Utility.getMeasureAsJson(subscription.getMpJson(), RATE_CODES.SUB_START_DATE);
                String deviceURL = Utility.getMeasureAsJson(subscription.getMpJson(), RATE_CODES.DEVICE_URL).replace(":devNo", deviceNumber);

                LOGGER.info("inverterNumber: {}", inverterNumber);
                LOGGER.info("deviceNumber: {}", deviceNumber);
                LOGGER.info("subStartDate: {}", subStartDate);
                LOGGER.info("deviceURL: {}", deviceURL);

                if (inverterNumber != null && !inverterNumber.isEmpty() && deviceURL != null && !deviceURL.isEmpty()) {
                    MonitorReading subscriptionLastRecord = subscriptionLastRecord(subscription.getSubsId(), inverterNumber);


                    String url = GaugeMapper.parseUrl(deviceURL, subscriptionLastRecord, subStartDate);
                    if (url != null) {
                        LOGGER.info("url: {}", url);
                        List<MonitorReading> monitorReadings = xmlDecoding(subscription, url, inverterNumber, subscriptionLastRecord);
                        if (monitorReadings != null) {
                            save(monitorReadings);

//                        LOGGER.info("monitor reading daily time: {}", monitorReadings.get(5).getTime());

                            MonitorReading monitorReading = monitorReadings.get(5) != null ? monitorReadings.get(5) :
                                    monitorReadings.get(4) != null ? monitorReadings.get(4) : monitorReadings.get(3);

                            List<MonitorReadingDaily> mrdList = dailyRepository.getAllLastSavedRecordByMongoSubIdAndDate(
                                    subscription.getSubsId(), Utility.getDateString(monitorReading.getTime(), Utility.SYSTEM_DATE_FORMAT));
                            if (mrdList != null && mrdList.size() > 1) {
                                dailyRepository.deleteAllById(mrdList.stream().map(MonitorReadingDaily::getId).collect(Collectors.toList()));
                            }
                            saveMonitorDaily(monitorReading, subscription, monitorReading.getTime());
                        }
                    }
                }
            } catch (Exception e) {
                if (e == null) {
                    LOGGER.warn("Exception is null for mpJson {} brand {} subscription {}", subscription.getMpJson(), subscription.getBrand(), subscription.getSubsId());
                    Utility.batchNotification("Tigo", null, subscription.getMpJson() + " " + subscription.getBrand() + " " + subscription.getSubsId(), "EXCEPTION");
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
                LOGGER.error(e.getMessage(), e);
                Utility.batchNotification("Tigo", null, e.getMessage(), "EXCEPTION");
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public BaseResponse getHistoricGraphData(List<String> subscriptionIds, String fromDate, String toDate) {

        Map<String, String> subscriptionSuccessMessage = new HashMap<>();
        Map<String, List<String>> subscriptionErrorMessage = new HashMap<>();
        List<ExtDataStageDefinition> subscriptions;
        if (subscriptionIds != null && !subscriptionIds.isEmpty()) {
            subscriptions = stageMonitorService.getAllSubscriptionsByMpAndSubsIds(MONITOR_PLATFORM.EGAUGE, subscriptionIds);
        } else {
            subscriptions = stageMonitorService.getAllSubscriptions(MONITOR_PLATFORM.EGAUGE);
        }
        for (ExtDataStageDefinition subscription : subscriptions) {
            try {
                String inverterNumber = Utility.getMeasureAsJson(subscription.getMpJson(), RATE_CODES.EGDID);
                String deviceNumber = Utility.getMeasureAsJson(subscription.getMpJson(), RATE_CODES.DEVICE_NUMBER);
                String subStartDate = Utility.getMeasureAsJson(subscription.getMpJson(), RATE_CODES.SUB_START_DATE);
                String deviceURL = Utility.getMeasureAsJson(subscription.getMpJson(), RATE_CODES.DEVICE_URL).replace(":devNo", deviceNumber);
                checkMandatoryMeasures(inverterNumber, deviceNumber, subStartDate, deviceURL, subscription.getSubsId(), subscriptionErrorMessage);
                if (inverterNumber == null || inverterNumber.isEmpty() || deviceNumber == null || deviceNumber.isEmpty()
                        || subStartDate == null || subStartDate.isEmpty() || deviceURL == null || deviceURL.isEmpty()) {
                    continue;
                }
                try {
                    LocalDateTime startOfDay = Utility.toLocalDate(fromDate);
                    LocalDateTime endOfDay = Utility.toLocalDate(toDate);

                    LOGGER.info("--------------------------------------------------------------------------");
                    LOGGER.info("Start Of Day: " + startOfDay);
                    LOGGER.info("End Of Day:  " + endOfDay);
                    LOGGER.info("--------------------------------------------------------------------------");

                    startOfDay = startOfDay.minusMinutes(THIRTY);
                    while (startOfDay.isBefore(endOfDay)) {
                        startOfDay = startOfDay.plusMinutes(THIRTY);

                        Date date = Utility.localDateTimeToDate(startOfDay);

                        MonitorReading subscriptionLastRecord = subscriptionLastRecord(subscription.getSubsId(), inverterNumber, date);
                        MonitorReading lastRecord = GaugeMapper.toLastSubscriptionMapping(subscription.getSubsId(), subscriptionLastRecord, date);

                        String url = GaugeMapper.parseHistoricUrl(deviceURL, lastRecord, date, subStartDate);
                        List<MonitorReading> monitorReadings = xmlDecoding(subscription, url, inverterNumber, lastRecord);
                        if (monitorReadings != null) {
                            save(monitorReadings);
                            saveMonitorDaily(monitorReadings.get(5), subscription, date);
                        }
                    }
                    subscriptionSuccessMessage.put(subscription.getSubsId(), "Data imported from " + startOfDay.toLocalDate() + " till " + endOfDay.toLocalDate());
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            } catch (Exception e) {
                if (e == null) {
                    LOGGER.warn("Exception is null for mpJson {} brand {} subscription {}", subscription.getMpJson(), subscription.getBrand(), subscription.getSubsId());
                    Utility.batchNotification("Tigo", null, subscription.getMpJson() + " " + subscription.getBrand() + " " + subscription.getSubsId(), "EXCEPTION");
                    return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).response(subscriptionErrorMessage).build();
                }
                LOGGER.error(e.getMessage(), e);
                Utility.batchNotification("EGauge", null, e.getMessage(), "EXCEPTION");
                return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).response(subscriptionErrorMessage).build();
            }
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).response(subscriptionSuccessMessage).errors(subscriptionErrorMessage).build();
    }

    private void checkMandatoryMeasures(String inverterNumber, String deviceNumber, String subStartDate, String deviceURL,
                                        String subsId, Map<String, List<String>> subscriptionMessage) {
        if (inverterNumber == null || inverterNumber.isEmpty()) {
            LOGGER.error("EGDID is not defined for EGauge subscription with id {}" + subsId);
            composeErrorLog(subsId, "EGDID is not defined", subscriptionMessage);
        }
        if (deviceNumber == null || deviceNumber.isEmpty()) {
            LOGGER.error("DEVNO is not defined for EGauge subscription with id {}" + subsId);
            composeErrorLog(subsId, "DEVNO is not defined", subscriptionMessage);
        }
        if (subStartDate == null || subStartDate.isEmpty()) {
            LOGGER.error("S_SSDT is not defined for EGauge subscription with id {}" + subsId);
            composeErrorLog(subsId, "S_SSDT is not defined", subscriptionMessage);
        }
        if (deviceURL == null || deviceURL.isEmpty()) {
            LOGGER.error("DURL is not defined for EGauge subscription with id {}" + subsId);
            composeErrorLog(subsId, "DURL is not defined", subscriptionMessage);
        }
    }

    @Override
    public void composeErrorLog(String subsId, String message, Map<String, List<String>> subscriptionMessage) {
        if (subscriptionMessage.get(subsId) == null) {
            subscriptionMessage.put(subsId, new ArrayList<>());
        }
        subscriptionMessage.get(subsId).add(message);
    }

    @Override
    public Double getHighestValueOfTheDay(Date date, String inverterNo, String subsID) {
        return monitorReadingRepository.getHighestValueOfTheDay(Utility.toLocalDateTime(date).toLocalDate(), inverterNo, subsID);
    }

    @Override
    public Double getHighestCurrentValueOfTheDay(Date date, String inverterNo, String subsID) {
        return monitorReadingRepository.getHighestCurrentValueOfTheDay(Utility.toLocalDateTime(date).toLocalDate(), inverterNo, subsID);
    }

    @Override
    public MonitorReading subscriptionLastRecord(String subscriptionIdMongo, String inverterNumber) {
        MonitorReading monitorReading = monitorReadingRepository.getLastRecordByTime(subscriptionIdMongo, inverterNumber);
        if (monitorReading == null) {
            return MonitorReading.builder()
                    .time(Utility.fromGMT(Utility.getStartOfDate(new Date())))
                    .subscriptionIdMongo(subscriptionIdMongo)
                    .currentValue(0.00)
                    .currentValueRunning(0.00)
                    .build();
        }
        return monitorReading;
    }

    @Override
    public MonitorReading subscriptionLastRecord(String subscriptionIdMongo, String inverterNumber, Date date) {
        MonitorReading monitorReading = monitorReadingRepository.getLastRecordByTime(subscriptionIdMongo, inverterNumber, Utility.toLocalDate(date));
        if (monitorReading == null) {
            return MonitorReading.builder().time(Utility.fromGMT(Utility.getStartOfDate(new Date()))).subscriptionIdMongo(subscriptionIdMongo).currentValue(0.00).currentValueRunning(0.00).build();
        }
        return monitorReading;
    }

    @Override
    public List<MonitorReading> xmlDecoding(ExtDataStageDefinition ext, String URL, String inverterNumber, MonitorReading subscriptionRecord) throws Exception {
        Document doc = null;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(URL);
        } catch (Exception e) {
            monitorPlatformUtilityService.outages(ext);
            LOGGER.error("Error triggering outages for EGauge. SubscriptionId: {}, Message: {}", ext.getSubsId(), e.getMessage());
        }
        try {
            if (doc != null) {
                int index = getInverterIndexFromXML(doc, inverterNumber);
                LOGGER.info("URL: {}", URL);
                LOGGER.info("Inverter indexed at: {}", index);

                if (index != -1) {
                    RecordedData recordedData = getRecordedData(doc, index + 1);
                    assert recordedData != null;
                    recordedData.setDurl(URL);
                    recordedData.setInverterNumber(inverterNumber);
                    recordedData.setLastTime(subscriptionRecord.getTime());
                    recordedData.setUserId(subscriptionRecord.getUserId());
                    recordedData.setSubscriptionId(subscriptionRecord.getSubscriptionIdMongo());
                    recordedData.setPeakValue(findMaxPeakValue(subscriptionRecord.getSubscriptionIdMongo()));

                    LOGGER.info("Recorded data from XML: {}", recordedData);
                    return GaugeMapper.setMonitorReadings(recordedData, subscriptionRecord, ext.getSubsId());
                }
            }
            LOGGER.error("No Inverted Found! for subscription Id: " + subscriptionRecord.getSubscriptionIdMongo());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<MonitorReading> save(List<MonitorReading> monitorReadings) {
        List<MonitorReading> savedRecords = new ArrayList<>();
        if (monitorReadings != null) {
//            manageExistingMonitorReadings(monitorReadings);
            savedRecords = monitorReadingRepository.saveAll(monitorReadings);
        }
        return savedRecords;
    }

    @Override
    public void save(MonitorReading monitorReading) {
        SimpleDateFormat sdf = new SimpleDateFormat(Utility.SYSTEM_DATE_TIME_FORMAT);
        String time = sdf.format(Utility.toGMT(monitorReading.getTime()));
        Optional<MonitorReading> existingReading = monitorReadingRepository.findBySubsIdAndInverterNoAndTime(monitorReading.getSubscriptionIdMongo(), monitorReading.getInverterNumber(), time);
        if (existingReading.isPresent()) {
            monitorReading.setId(existingReading.get().getId());
            monitorReading.setCreatedAt(existingReading.get().getCreatedAt());
        }
        monitorReadingRepository.save(monitorReading);
    }

    @Override
    public void saveMonitorDaily(MonitorReading monitorReading, ExtDataStageDefinition subscription, Date date) {
        if (monitorReading != null) {
            try {
                MonitorReadingDaily mrd = dailyRepository.getLastSavedRecordByMongoSubIdAndDate(subscription.getSubsId(), Utility.getDateString(date, Utility.SYSTEM_DATE_FORMAT));
                if (mrd == null) {
                    dailyRepository.save(MonitorReadingDaily.builder()
                            .subscriptionIdMongo(subscription.getSubsId())
                            .inverterNumber(monitorReading.getInverterNumber())
                            .yieldValue(monitorReading.getDailyYield() < 0 ? 0 : monitorReading.getDailyYield())
                            .day(monitorReading.getTime())
                            .build());
                } else {
                    if (monitorReading.getDailyYield() != null) {
                        mrd.setYieldValue(monitorReading.getDailyYield());
                        dailyRepository.save(mrd);
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public int getInverterIndexFromXML(Document doc, String inverterId) {
        List<String> list = new ArrayList<>();
        String xpathExpression = "/group/data/cname";

        try {
            NodeList nodes = Utility.toXPath(doc, xpathExpression);
            for (int n = 0; n < nodes.getLength(); n++) {
                NamedNodeMap nodeMap = nodes.item(n).getAttributes();
                list.add(nodeMap.getNamedItem(E_GAUGE.DID).getTextContent().concat(nodeMap.getNamedItem(E_GAUGE.TYPE).getTextContent()));
            }
            return Utility.getIndex(inverterId.concat(E_GAUGE.POWER), list);

        } catch (XPathExpressionException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return -1;
    }

    @Override
    public RecordedData getRecordedData(Document doc, int index) {
        String xpathExpression = "(//r/c[" + index + "])";

        try {
            return GaugeMapper.toRecords(Utility.toXPath(doc, xpathExpression));
        } catch (XPathExpressionException e) {
            LOGGER.error(e.getMessage(), e);
            throw new NotFoundException(EGaugeService.class, e.getMessage());
        }
    }

    public Double findMaxPeakValue(String subscriptionIdMongo) {
        Long peakValue = monitorReadingRepository.getMaxPeakValue(subscriptionIdMongo);
        if (peakValue != null) {
            return peakValue.doubleValue();
        } else return 0.00;
    }

    private void manageExistingMonitorReadings(List<MonitorReading> monitorReadingList) {
        SimpleDateFormat sdf = new SimpleDateFormat(Utility.SYSTEM_DATE_TIME_FORMAT);
        Set<String> timesAsString = monitorReadingList.stream()
                .map(reading -> sdf.format(Utility.toGMT(reading.getTime())))
                .collect(Collectors.toSet());
        String subsId = monitorReadingList.stream().map(MonitorReading::getSubscriptionIdMongo).findFirst().orElse(null);
        String inverterNumber = monitorReadingList.stream().map(MonitorReading::getInverterNumber).findFirst().orElse(null);
        List<MonitorReading> matchingReadings = monitorReadingRepository.findBySubsIdAndInverterNoAndTimeIn(subsId, inverterNumber, timesAsString);

        Map<String, MonitorReading> matchingReadingsMap = matchingReadings.stream()
                .collect(Collectors.toMap(
                        reading -> reading.getSubscriptionIdMongo() + "_" + reading.getInverterNumber() + "_" + sdf.format(reading.getTime()),
                        reading -> reading,
                        (existing, replacement) -> existing
                ));

        for (MonitorReading reading : monitorReadingList) {
            String key = reading.getSubscriptionIdMongo() + "_" + reading.getInverterNumber() + "_" + sdf.format(reading.getTime());
            MonitorReading matchingReading = matchingReadingsMap.get(key);
            if (matchingReading != null) {
                reading.setId(matchingReading.getId());
                reading.setCreatedAt(matchingReading.getCreatedAt());
            }
        }
    }

    @Override
    public List<MonitorReadingDaily> saveAllMonitorReadingDaily(List<MonitorReadingDaily> monitorReadingDailyList) {
        List<MonitorReadingDaily> savedRecords = new ArrayList<>();
        if (monitorReadingDailyList != null) {
//            manageExistingDailyMonitorReadings(monitorReadingDailyList);
            savedRecords = dailyRepository.saveAll(monitorReadingDailyList);
        }
        return savedRecords;
    }

    private void manageExistingDailyMonitorReadings(List<MonitorReadingDaily> monitorReadingDailyList) {
        SimpleDateFormat sdf = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
        Set<String> daysAsString = monitorReadingDailyList.stream()
                .map(reading -> sdf.format(Utility.toGMT(reading.getDay())))
                .collect(Collectors.toSet());
        String subsId = monitorReadingDailyList.stream().map(MonitorReadingDaily::getSubscriptionIdMongo).findFirst().orElse(null);
        String inverterNumber = monitorReadingDailyList.stream().map(MonitorReadingDaily::getInverterNumber).findFirst().orElse(null);
        List<MonitorReadingDaily> dailyMatchingReadings = dailyRepository.findBySubsIdAndInverterNoAndTimeIn(subsId, inverterNumber, daysAsString);

        Map<String, MonitorReadingDaily> matchingReadingsMap = dailyMatchingReadings.stream()
                .collect(Collectors.toMap(
                        reading -> reading.getSubscriptionIdMongo() + "_" + reading.getInverterNumber() + "_" + sdf.format(reading.getDay()),
                        reading -> reading,
                        (existing, replacement) -> existing
                ));

        for (MonitorReadingDaily dailyReading : monitorReadingDailyList) {
            String key = dailyReading.getSubscriptionIdMongo() + "_" + dailyReading.getInverterNumber() + "_" + sdf.format(dailyReading.getDay());
            MonitorReadingDaily matchingReading = matchingReadingsMap.get(key);
            if (matchingReading != null) {
                dailyReading.setId(matchingReading.getId());
                dailyReading.setCreatedAt(matchingReading.getCreatedAt());
            }
        }
    }

    @Transactional
    @Override
    public void deleteRecordsInRangeAndCondition(String start, String end, String mp, String subsId) {
        SimpleDateFormat sdf = getDateFormatForMp(mp);
        try {
            Date startDate = sdf.parse(start);
            Date endDate = sdf.parse(end);
            monitorReadingRepository.deleteRecordsInRangeAndCondition(startDate, endDate, subsId);
            dailyRepository.deleteRecordsInRangeAndCondition(startDate, endDate, subsId);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private SimpleDateFormat getDateFormatForMp(String mp) {
        String format = null;
        if (mp.trim().equalsIgnoreCase(RATE_CODES.BRAND_TIGO)) {
            format = Utility.YEAR_MON_DATE_HH_MM_SS;
        }
        return new SimpleDateFormat(format);
    }
}
