package com.solar.api.tenant.service.process.pvmonitor.platform.solis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.api.Constants;
import com.solar.api.helper.Utility;
import com.solar.api.helper.WebUtils;
import com.solar.api.tenant.mapper.pvmonitor.*;
import com.solar.api.tenant.model.pvmonitor.MonitorReading;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.repository.CustomerSubscriptionMappingRepository;
import com.solar.api.tenant.repository.MonitorReadingRepository;
import com.solar.api.tenant.service.SubscriptionService;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.process.pvmonitor.APIConstants;
import com.solar.api.tenant.service.process.pvmonitor.MonitorAPI;
import com.solar.api.tenant.service.process.pvmonitor.PrerequisitesFactory;
import com.solar.api.tenant.service.process.pvmonitor.platform.solis.dto.RecordDTO;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.solar.api.Constants.RATE_CODES.*;

@Service
public class SolisAPI implements MonitorAPI {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private Utility utility;
    @Autowired
    private UserService userService;
    @Autowired
    private CustomerSubscriptionMappingRepository customerSubscriptionMappingRepository;
    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private MonitorReadingRepository readingRepository;


    private SimpleDateFormat formatDateTime = new SimpleDateFormat(Utility.SYSTEM_DATE_TIME_FORMAT);
    private SimpleDateFormat format = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);

    @Override
    public MonitorAPIAuthResponse getAuthData(Object... params) throws NoSuchAlgorithmException, JsonProcessingException, UnsupportedEncodingException {

        //MonitorAPIAuthBody authBody = (MonitorAPIAuthBody) params[0];
        String bodyJsonString =
                new ObjectMapper().writeValueAsString(MonitorAPIAuthBody.builder()
                        .userInfo((String) params[0])
                        .passWord(Utility.getMD5String((String) params[1]))
                        .yingZhenType(1)
                        .language("2").build()
                );
        return (MonitorAPIAuthResponse) getAuthenticatedData(bodyJsonString, null, "LOGIN2", null).getBody();
    }

    private static String getMD5ToBase64Encoded(String e) {
        int a = 0;
        int t = e.length();
        if (t % 2 != 0)
            return null;
        t /= 2;
        byte[] i = new byte[t];
        for (int l = 0; l < t; l++) {
            String r = e.substring(a, 2 + a);
            int n = Integer.parseInt(r, 16);
            if (n > 128) {
                n -= 256;
            }
            i[l] = (byte) n;
            a += 2;
        }
        return Base64.getEncoder().encodeToString(i);
    }

    private static MonitorAuthHeaders getAuthDataKey(String bodyJsonString, String api) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        String contentMd5 = Utility.getMD5String(bodyJsonString);
        String md5ToBase64Content = getMD5ToBase64Encoded(contentMd5);
        String formattedTime = Utility.getZoneFormattedTime(new Date(), "GMT");
        String key = "POST\n" +
                md5ToBase64Content +
                "\napplication/json\n" +
                formattedTime + "\n" +
                api;
        String auth = "WEB 2424:" + Base64.getEncoder().encodeToString(new HmacUtils(HmacAlgorithms.HMAC_SHA_1, "5704383536604a8bb94c83ebc059aa8c")
                .hmac(key.getBytes("utf8")));
        return MonitorAuthHeaders.builder()
                .content(md5ToBase64Content)
                .time(formattedTime)
                .auth(auth)
                .build();
    }

    @Override
    public MonitorAPIResponse getCurrentData(CustomerSubscription cs, Object... params) throws NoSuchAlgorithmException, JsonProcessingException, UnsupportedEncodingException {
        Map<String, String> filteredRateCodes = new HashMap<>();
        filteredRateCodes.put("LGNM", cs.getCustomerSubscriptionMappings().stream().filter(l -> "LGNM".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue());
        filteredRateCodes.put("PCD", cs.getCustomerSubscriptionMappings().stream().filter(l -> "PCD".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue());
        filteredRateCodes.put("SN", cs.getCustomerSubscriptionMappings().stream().filter(l -> "SN".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue());
        filteredRateCodes.put("INVRT", cs.getCustomerSubscriptionMappings().stream().filter(l -> "INVRT".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue());

        MonitorAPIAuthResponse authResponse = null;
        try {
            authResponse = getAuthData(filteredRateCodes.get("LGNM"), filteredRateCodes.get("PCD"));
        } catch (ClassCastException e) {
            LOGGER.error(e.toString());
        }

        ResponseEntity<MonitorAPIResponse> inverterChartResponse = null;
        String inverterNo = filteredRateCodes.get("INVRT");
        Map<String, Map<Date, MonitorAPIResponseL2>> inverterValuesOverTime = null;
        RecordDTO widgetRecord = null;
        int rounding = utility.getCompanyPreference().getRounding();
        Boolean isWidget = (Boolean) params[1];
        String ratedPowerJsonString = getBodyJsonString("RATED_POWER", null, null);

        ResponseEntity<MonitorAPIResponse> ratedPowerResponse = getAuthenticatedData(null, ratedPowerJsonString, "RATED_POWER", authResponse.getCsrfToken());

        String inverterChartBodyJsonString = getBodyJsonString("INVERTER_CHART", inverterNo, (String) params[0]);
        inverterChartResponse = getAuthenticatedData(null, inverterChartBodyJsonString, "INVERTER_CHART", authResponse.getCsrfToken());

        MonitorAPIResponse ratedPowerBody = ratedPowerResponse != null ? ratedPowerResponse.getBody() : null;
        MonitorAPIResponse inverterChartDataResponse = inverterChartResponse != null ? inverterChartResponse.getBody() : null;
        AtomicReference<Double> peakValue = new AtomicReference<>(0.0);
        Double ratedPower = null;
        if (ratedPowerBody != null) {
            ratedPower = ratedPowerBody.getData().getCapacity();
        }
        Date lastSavedRecord = params.length > 1 ? (Date) params[2] : null;

        if ((inverterChartDataResponse != null ? inverterChartDataResponse.getData() : null) != null && inverterChartDataResponse.getData().getPage().getRecords() != null) {
            if (inverterChartDataResponse.getData().getPage().getRecords().size() > 0) {
                int totalRecords = inverterChartDataResponse.getData().getPage().getRecords().size();
                widgetRecord = inverterChartDataResponse.getData().getPage().getRecords().get(totalRecords - 1);

                //if (!isWidget) {
                inverterValuesOverTime = new HashMap<>();
                Map<Date, MonitorAPIResponseL2> valuesPower = new TreeMap<>();
                Double yieldValue = 0.0;

                inverterChartDataResponse.getData().getPage().getRecords().forEach(chart -> {
                    Long pvPower = 0l;
                    if ((lastSavedRecord != null && checkLastSavedRecord(lastSavedRecord, chart.getTime())) && !isWidget) {
                        //pow for dc
                        pvPower = chart.getPow1() + chart.getPow2() + chart.getPow3() + chart.getPow4() + chart.getPow5() + chart.getPow6() + chart.getPow7() + chart.getPow8() + chart.getPow9() + chart.getPow10()
                                + chart.getPow11() + chart.getPow12() + chart.getPow13() + chart.getPow14() + chart.getPow15() + chart.getPow16() + chart.getPow17() + chart.getPow18() + chart.getPow19() + chart.getPow20()
                                + chart.getPow21() + chart.getPow22() + chart.getPow23() + chart.getPow24() + chart.getPow25() + chart.getPow26() + chart.getPow27() + chart.getPow28() + chart.getPow29() + chart.getPow30();
                        peakValue.set(peakValue.get() + chart.getPac());

                        valuesPower.put(Utility.getDate(chart.getTime(), Utility.SYSTEM_DATE_TIME_FORMAT), MonitorAPIResponseL2.builder()
                                .time(chart.getTime())
                                .gridpower(chart.getPac())
                                .eToday(chart.getEToday())
                                .eMonth(chart.getEMonth())
                                .eYear(chart.getEYear())
                                .eTotal(chart.getETotal())
                                .pvPower(pvPower.doubleValue())//current val
                                .build());
                    } else if (lastSavedRecord == null && !isWidget) {
                        // new customer record is added
                        pvPower = chart.getPow1() + chart.getPow2() + chart.getPow3() + chart.getPow4() + chart.getPow5() + chart.getPow6() + chart.getPow7() + chart.getPow8() + chart.getPow9() + chart.getPow10()
                                + chart.getPow11() + chart.getPow12() + chart.getPow13() + chart.getPow14() + chart.getPow15() + chart.getPow16() + chart.getPow17() + chart.getPow18() + chart.getPow19() + chart.getPow20()
                                + chart.getPow21() + chart.getPow22() + chart.getPow23() + chart.getPow24() + chart.getPow25() + chart.getPow26() + chart.getPow27() + chart.getPow28() + chart.getPow29() + chart.getPow30();
                        peakValue.set(peakValue.get() + chart.getPac());

                        valuesPower.put(Utility.getDate(chart.getTime(), Utility.SYSTEM_DATE_TIME_FORMAT), MonitorAPIResponseL2.builder()
                                .time(chart.getTime())
                                .gridpower(chart.getPac())
                                .eToday(chart.getEToday())
                                .eMonth(chart.getEMonth())
                                .eYear(chart.getEYear())
                                .eTotal(chart.getETotal())
                                .pvPower(pvPower.doubleValue())//current val
                                .build());
                    } else if (lastSavedRecord == null && isWidget) {
                        // lastSavedRecord is null and widget is true
                        peakValue.set(peakValue.get() + chart.getPac());
                    }
                });
                inverterValuesOverTime.put(inverterNo, valuesPower);
                //}
            }
        }

        return MonitorAPIResponse.builder()
                .sytemSize(ratedPower != null ? utility.round(utility.round(ratedPower, rounding), rounding) : 0)
                .peakValue(peakValue != null ? utility.round(utility.round(peakValue.get().doubleValue(), rounding), rounding) : 0)
                .currentValueToday(widgetRecord != null && widgetRecord.getPac() != null ? utility.round(widgetRecord.getPac(), rounding) : 0)
                .dailyYield(widgetRecord != null && widgetRecord.getEToday() != null ? utility.round(widgetRecord.getEToday(), rounding) : 0)
                .monthlyYield(widgetRecord != null && widgetRecord.getEMonth() != null ? utility.round(widgetRecord.getEMonth(), rounding) : 0)
                .annualYield(widgetRecord != null && widgetRecord.getEYear() != null ? utility.round(widgetRecord.getEYear(), rounding) : 0)
                .grossYield(widgetRecord != null && widgetRecord.getETotal() != null ? utility.round(widgetRecord.getETotal(), rounding) : 0)
                .inverterValuesOverTime(inverterValuesOverTime)
                .build();
    }

    private String getBodyJsonString(String constantName, String sn, String time) throws JsonProcessingException {

        String listBodyJsonString = "";

        if ("LIST".equalsIgnoreCase(constantName)) {
            listBodyJsonString = new ObjectMapper().writeValueAsString(MonitorAPIBody.builder()
                    .pageNo(1)
                    .pageSize(10)
                    .states("0")
                    .language("2").build());
        } else if ("LISTV2".equalsIgnoreCase(constantName)) {
            listBodyJsonString = new ObjectMapper().writeValueAsString(MonitorAPIBody.builder()
                    .pageNo(1)
                    .pageSize(10)
                    .language("2").build());
        } else if ("INVERTER_CHART".equalsIgnoreCase(constantName)) {
            listBodyJsonString = new ObjectMapper().writeValueAsString(MonitorAPIBody.builder()
                    .pageNo(1)
                    .beginTime(time.split(" ")[0]) //Utility.getDateString(new Date(),Utility.SYSTEM_DATE_FORMAT))
                    .sn(sn)
                    .language("2").build());
        } else if ("RATED_POWER".equalsIgnoreCase(constantName)) {
            listBodyJsonString = new ObjectMapper().writeValueAsString(MonitorAPIBody.builder()
                    .language("2").build());
        }
        return listBodyJsonString;
    }

    private ResponseEntity getAuthenticatedData(String bodyJsonStringLogin, String bodyJsonString, String constantName, String token) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Map<String, APIConstants> constants = PrerequisitesFactory.getAPIPrerequisites("SOLIS").getConstants();
        APIConstants c = constants.get(constantName);
        Map<String, List<String>> headers = new HashMap<>();
        MonitorAuthHeaders monitorAuthHeaders = null;
        if (bodyJsonStringLogin != null) {
            monitorAuthHeaders = getAuthDataKey(bodyJsonStringLogin, c.getUrlSuffix());
            headers.put("time", Arrays.asList(monitorAuthHeaders.getTime()));
            headers.put("content-md5", Arrays.asList(monitorAuthHeaders.getContent()));
            headers.put("authorization", Arrays.asList(monitorAuthHeaders.getAuth()));
            headers.put("content-type", Arrays.asList("application/json;charset=UTF-8"));

        } else {
            // apis other than login
            monitorAuthHeaders = getAuthDataKey(bodyJsonString, c.getUrlSuffix());
            headers.put("time", Arrays.asList(monitorAuthHeaders.getTime()));
            headers.put("content-md5", Arrays.asList(monitorAuthHeaders.getContent()));
            headers.put("authorization", Arrays.asList(monitorAuthHeaders.getAuth()));
            headers.put("content-type", Arrays.asList("application/json;charset=UTF-8"));
            headers.put("token", Arrays.asList(token));
            return WebUtils.submitRequest(c.getMethod(), c.getUrl(), bodyJsonString, headers, MonitorAPIResponse.class);
        }
        return WebUtils.submitRequest(c.getMethod(), c.getUrl(), bodyJsonStringLogin, headers, MonitorAPIAuthResponse.class);
    }

    private boolean checkLastSavedRecord(Date lastSavedDateTime, String newDateTime) {

        Date lastSavedRecord = Utility.getDate(lastSavedDateTime, Utility.SYSTEM_DATE_TIME_FORMAT);
        Date newRecord = Utility.getDate(newDateTime, Utility.SYSTEM_DATE_TIME_FORMAT);

        if (newRecord.after(lastSavedRecord)) {
            System.out.println(newRecord + " is in the future");
            return true;
        }
        return false;
    }

    @Override
    public List<MonitorReading> getMonitorReadingDataForCsComparison(MonitorAPIAuthBody body, CustomerSubscription cs) throws ParseException {
        List<Date> labelDatesSolis = getStartEndDateTime(body.getTime());
        List<MonitorReading> daysData = null;
        Date sDate = null;
        Date eDate = null;
        if (labelDatesSolis.size() > 0) {
            sDate = labelDatesSolis.get(0);
            eDate = labelDatesSolis.get(1);
            daysData = getMonitorReadingYieldSumForSolis(body, cs, sDate, eDate);
        }
        return daysData;
    }

    @Override
    public List<MonitorReading> getMonitorReadingDataForUserComparison(Long userId, MonitorAPIAuthBody body, List<CustomerSubscription> customerSubscriptionList) throws ParseException {
        List<MonitorReading> daysSolisData = new ArrayList<>();
        if (customerSubscriptionList.size() > 0) {
            for (CustomerSubscription cs : customerSubscriptionList) {
                List<Date> labelDatesSolis = getStartEndDateTime(body.getTime());
                if (labelDatesSolis.size() > 0) {
                    Date sdate = labelDatesSolis.get(0);
                    Date edate = labelDatesSolis.get(1);
                    daysSolisData = getMonitorReadingYieldSumForSolis(body, cs, sdate, edate);
                }
//                if (daysSolisData != null && daysSolisData.size() > 0) {
//                    daysData.addAll(daysSolisData);
//                }
            }
        }
        return daysSolisData;
    }

    @Override
    public List<MonitorReading> getMonitorReadingDataForUserComparison(String projectId, MonitorAPIAuthBody body, List<CustomerSubscription> customerSubscriptionList) throws ParseException {
        List<MonitorReading> daysSolisData = new ArrayList<>();
        if (customerSubscriptionList.size() > 0) {
            for (CustomerSubscription cs : customerSubscriptionList) {
                List<Date> labelDatesSolis = getStartEndDateTime(body.getTime());
                if (labelDatesSolis.size() > 0) {
                    Date sdate = labelDatesSolis.get(0);
                    Date edate = labelDatesSolis.get(1);
                    daysSolisData = getMonitorReadingYieldSumForSolis(body, cs, sdate, edate);
                }
//                if (daysSolisData != null && daysSolisData.size() > 0) {
//                    daysData.addAll(daysSolisData);
//                }
            }
        }
        return daysSolisData;
    }

    @Override
    public List<MonitorReading> getMonitorReadingDataForMongoComparison(MonitorAPIAuthBody body, String subsId) throws ParseException {
        List<Date> labelDates = getDateTimes(body.getTime());
        List<MonitorReading>  daysData = readingRepository.findBySubscriptionIdMongoAndTimeIn(subsId, labelDates);
        return daysData;    }

    private List<Date> getDateTimes(String time) throws ParseException {
        int LINE_GRAPH_INCREMENT = 30;
        Date date = formatDateTime.parse(time != null ? time : formatDateTime.format(new Date()));
        Date tillDateTime = Utility.addMinutes(new Date(), 300);
        boolean isToday = Utility.areInSameDay(date, tillDateTime);
        Date dateTime = Utility.getStartOfDate(date);
        List<Date> dateTimes = new ArrayList<>();
        if (!isToday) {
            tillDateTime = Utility.getEndOfDate(dateTime);
        }
        while (dateTime.before(tillDateTime)) {
            dateTimes.add(dateTime);
            dateTime = Utility.addMinutes(dateTime, LINE_GRAPH_INCREMENT);
        }
        return dateTimes;
    }

    private List<Date> getStartEndDateTime(String time) throws ParseException {
        Date date = formatDateTime.parse(time != null ? time : formatDateTime.format(new Date()));
        Date tillDateTime = Utility.addMinutes(new Date(), 300);
        boolean isToday = Utility.areInSameDay(date, tillDateTime);
        Date startOfDate = Utility.getStartOfDate(date);
        List<Date> dateTimes = new ArrayList<>();
        if (!isToday) {
            tillDateTime = Utility.getEndOfDate(startOfDate);
        } else {
            tillDateTime = Utility.getDateBeforeNow(formatDateTime.parse(formatDateTime.format(new Date())));
        }
        dateTimes.add(startOfDate);
        dateTimes.add(tillDateTime);
        return dateTimes;
    }

    private List<MonitorReading> getMonitorReadingYieldSumForSolis(MonitorAPIAuthBody body, CustomerSubscription cs, Date sDate, Date eDate) throws ParseException {
        List<Date> labelDates = getDateTimes(body.getTime());
        String startDate = formatDateTime.format(sDate != null ? sDate : formatDateTime.format(new Date()));
        String endDate = formatDateTime.format(eDate != null ? eDate : formatDateTime.format(new Date()));
        List<MonitorReading> monitorReadingData = readingRepository.findBySubscriptionIdAndStartDtTimeAndEndDtTime(cs.getId(), startDate, endDate);
        List<MonitorReading> daysData = new ArrayList<>();

        for (int i = 0; i < labelDates.size() - 1; i++) {
            Date sdate = labelDates.get(i);
            Date edate = labelDates.get(i + 1);
            List<MonitorReading> monitorReadingFilteredList = monitorReadingData.stream().filter(m -> m.getTime().after(sdate) &&
                    (m.getTime().before(edate) || m.getTime().equals(edate))).collect(Collectors.toList());
            if (monitorReadingFilteredList != null && monitorReadingFilteredList.size() > 0) {
                Double yieldValueAverage = monitorReadingFilteredList.stream().mapToDouble(MonitorReading::getYieldValue).average().getAsDouble();
                Optional<String> inverterNumberOp = monitorReadingFilteredList.stream().map(MonitorReading::getInverterNumber).findFirst();
                if (inverterNumberOp.isPresent()) {
                    String inverterNumber = inverterNumberOp.get();
                    MonitorReading monitorReading = monitorReadingFilteredList.get(0);
                    monitorReading.setTime(edate);
                    monitorReading.setInverterNumber(inverterNumber);
                    monitorReading.setYieldValue(yieldValueAverage);
                    daysData.add(monitorReading);
                }
            }
        }
        return daysData;
    }

    @Override
    public MonitorAPIResponse getCurrentData(ExtDataStageDefinition ext, Object... params) throws NoSuchAlgorithmException, JsonProcessingException, UnsupportedEncodingException {
        Map<String, APIConstants> constants = PrerequisitesFactory.getAPIPrerequisites(Constants.MONITOR_PLATFORM.SOLIS).getConstants();

        Map<String, String> filteredRateCodes = new HashMap<>();
        filteredRateCodes.put(LGNM, Utility.getMeasureAsJson(ext.getMpJson(), LGNM));
        filteredRateCodes.put(PCD, Utility.getMeasureAsJson(ext.getMpJson(), PCD));
        filteredRateCodes.put(SN, Utility.getMeasureAsJson(ext.getMpJson(), SN));
        filteredRateCodes.put(INVERTER_NUMBER, Utility.getMeasureAsJson(ext.getMpJson(), INVERTER_NUMBER));
        MonitorAPIAuthResponse authResponse = null;
        try {
            authResponse = getAuthData(filteredRateCodes.get("LGNM"), filteredRateCodes.get("PCD"));
        } catch (ClassCastException | JsonProcessingException | UnsupportedEncodingException e) {
            LOGGER.error(e.toString());
        }

        ResponseEntity<MonitorAPIResponse> inverterChartResponse = null;
        String inverterNo = filteredRateCodes.get(INVERTER_NUMBER);
        Map<String, Map<Date, MonitorAPIResponseL2>> inverterValuesOverTime = null;
        RecordDTO widgetRecord = null;
        int rounding = utility.getCompanyPreference().getRounding();
        Boolean isWidget = (Boolean) params[1];
        String ratedPowerJsonString = getBodyJsonString("RATED_POWER", null, null);

        ResponseEntity<MonitorAPIResponse> ratedPowerResponse = getAuthenticatedData(null, ratedPowerJsonString, "RATED_POWER", authResponse.getCsrfToken());

        String inverterChartBodyJsonString = getBodyJsonString("INVERTER_CHART", inverterNo, (String) params[0]);
        inverterChartResponse = getAuthenticatedData(null, inverterChartBodyJsonString, "INVERTER_CHART", authResponse.getCsrfToken());

        MonitorAPIResponse ratedPowerBody = ratedPowerResponse != null ? ratedPowerResponse.getBody() : null;
        MonitorAPIResponse inverterChartDataResponse = inverterChartResponse != null ? inverterChartResponse.getBody() : null;
        AtomicReference<Double> peakValue = new AtomicReference<>(0.0);
        Double ratedPower = null;
        if (ratedPowerBody != null) {
            ratedPower = ratedPowerBody.getData().getCapacity();
        }
        Date lastSavedRecord = params.length > 1 ? (Date) params[2] : null;

        if ((inverterChartDataResponse != null ? inverterChartDataResponse.getData() : null) != null && inverterChartDataResponse.getData().getPage().getRecords() != null) {
            if (inverterChartDataResponse.getData().getPage().getRecords().size() > 0) {
                int totalRecords = inverterChartDataResponse.getData().getPage().getRecords().size();
                widgetRecord = inverterChartDataResponse.getData().getPage().getRecords().get(totalRecords - 1);

                //if (!isWidget) {
                inverterValuesOverTime = new HashMap<>();
                Map<Date, MonitorAPIResponseL2> valuesPower = new TreeMap<>();
                Double yieldValue = 0.0;

                inverterChartDataResponse.getData().getPage().getRecords().forEach(chart -> {
                    Long pvPower = 0l;
                    if ((lastSavedRecord != null && checkLastSavedRecord(lastSavedRecord, chart.getTime())) && !isWidget) {
                        //pow for dc
                        pvPower = chart.getPow1() + chart.getPow2() + chart.getPow3() + chart.getPow4() + chart.getPow5() + chart.getPow6() + chart.getPow7() + chart.getPow8() + chart.getPow9() + chart.getPow10()
                                + chart.getPow11() + chart.getPow12() + chart.getPow13() + chart.getPow14() + chart.getPow15() + chart.getPow16() + chart.getPow17() + chart.getPow18() + chart.getPow19() + chart.getPow20()
                                + chart.getPow21() + chart.getPow22() + chart.getPow23() + chart.getPow24() + chart.getPow25() + chart.getPow26() + chart.getPow27() + chart.getPow28() + chart.getPow29() + chart.getPow30();
                        peakValue.set(peakValue.get() + chart.getPac());

                        valuesPower.put(Utility.getDate(chart.getTime(), Utility.SYSTEM_DATE_TIME_FORMAT), MonitorAPIResponseL2.builder()
                                .time(chart.getTime())
                                .gridpower(chart.getPac())
                                .eToday(chart.getEToday())
                                .eMonth(chart.getEMonth())
                                .eYear(chart.getEYear())
                                .eTotal(chart.getETotal())
                                .pvPower(pvPower.doubleValue())//current val
                                .build());
                    } else if (lastSavedRecord == null && !isWidget) {
                        // new customer record is added
                        pvPower = chart.getPow1() + chart.getPow2() + chart.getPow3() + chart.getPow4() + chart.getPow5() + chart.getPow6() + chart.getPow7() + chart.getPow8() + chart.getPow9() + chart.getPow10()
                                + chart.getPow11() + chart.getPow12() + chart.getPow13() + chart.getPow14() + chart.getPow15() + chart.getPow16() + chart.getPow17() + chart.getPow18() + chart.getPow19() + chart.getPow20()
                                + chart.getPow21() + chart.getPow22() + chart.getPow23() + chart.getPow24() + chart.getPow25() + chart.getPow26() + chart.getPow27() + chart.getPow28() + chart.getPow29() + chart.getPow30();
                        peakValue.set(peakValue.get() + chart.getPac());

                        valuesPower.put(Utility.getDate(chart.getTime(), Utility.SYSTEM_DATE_TIME_FORMAT), MonitorAPIResponseL2.builder()
                                .time(chart.getTime())
                                .gridpower(chart.getPac())
                                .eToday(chart.getEToday())
                                .eMonth(chart.getEMonth())
                                .eYear(chart.getEYear())
                                .eTotal(chart.getETotal())
                                .pvPower(pvPower.doubleValue())//current val
                                .build());
                    } else if (lastSavedRecord == null && isWidget) {
                        // lastSavedRecord is null and widget is true
                        peakValue.set(peakValue.get() + chart.getPac());
                    }
                });
                inverterValuesOverTime.put(inverterNo, valuesPower);
                //}
            }
        }

        return MonitorAPIResponse.builder()
                .sytemSize(ratedPower != null ? utility.round(utility.round(ratedPower, rounding), rounding) : 0)
                .peakValue(peakValue != null ? utility.round(utility.round(peakValue.get().doubleValue(), rounding), rounding) : 0)
                .currentValueToday(widgetRecord != null && widgetRecord.getPac() != null ? utility.round(widgetRecord.getPac(), rounding) : 0)
                .dailyYield(widgetRecord != null && widgetRecord.getEToday() != null ? utility.round(widgetRecord.getEToday(), rounding) : 0)
                .monthlyYield(widgetRecord != null && widgetRecord.getEMonth() != null ? utility.round(widgetRecord.getEMonth(), rounding) : 0)
                .annualYield(widgetRecord != null && widgetRecord.getEYear() != null ? utility.round(widgetRecord.getEYear(), rounding) : 0)
                .grossYield(widgetRecord != null && widgetRecord.getETotal() != null ? utility.round(widgetRecord.getETotal(), rounding) : 0)
                .inverterValuesOverTime(inverterValuesOverTime)
                .build();
    }

    @Override
    public boolean savesBulk() {
        return false;
    }

}

