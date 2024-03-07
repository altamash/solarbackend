package com.solar.api.tenant.service.process.pvmonitor.platform.solrenview;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.solar.api.Constants;
import com.solar.api.helper.Utility;
import com.solar.api.helper.WebUtils;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIAuthBody;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIAuthResponse;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIResponse;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIResponseL2;
import com.solar.api.tenant.model.pvmonitor.MonitorReading;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.repository.MonitorReadingRepository;
import com.solar.api.tenant.service.process.pvmonitor.APIConstants;
import com.solar.api.tenant.service.process.pvmonitor.MonitorAPI;
import com.solar.api.tenant.service.process.pvmonitor.PrerequisitesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SolrenviewAPI implements MonitorAPI {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private Utility utility;
    @Autowired
    private MonitorReadingRepository readingRepository;

    private SimpleDateFormat formatDateTime = new SimpleDateFormat(Utility.SYSTEM_DATE_TIME_FORMAT);
    private SimpleDateFormat format = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
    public static final String LOGIN_NEW = "LOGIN_NEW";
    
    @Override
    public MonitorAPIAuthResponse getAuthData(Object... params) throws NoSuchAlgorithmException, JsonProcessingException, UnsupportedEncodingException {
        Map<String, APIConstants> constants = PrerequisitesFactory.getAPIPrerequisites(Constants.MONITOR_PLATFORM.SOLRENVIEW).getConstants();
        APIConstants c = constants.get(LOGIN_NEW);
        String username = (String) params[0];
        String userpwd = getPasswordHash((String) params[1]);
        ResponseEntity<MonitorAPIAuthResponse> response;
        try {
            response = WebUtils.submitRequest(c.getMethod(), c.getUrl() + "?username=" +
                    username + "&userpwd=" + userpwd, null, new HashMap<>(), MonitorAPIAuthResponse.class);
        } catch (HttpClientErrorException e) {
            LOGGER.warn(e.getMessage());
            return MonitorAPIAuthResponse.builder().apiResponseMsg(e.getResponseBodyAsString()).build();
        }
        return response.getBody();
    }

    private String getPasswordHash(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        return DatatypeConverter.printHexBinary(md.digest()).toLowerCase();
    }
    @Override
    public MonitorAPIResponse getCurrentData(CustomerSubscription cs, Object... params) throws NoSuchAlgorithmException, JsonProcessingException, UnsupportedEncodingException, ParseException {
        Map<String, APIConstants> constants = PrerequisitesFactory.getAPIPrerequisites(Constants.MONITOR_PLATFORM.SOLRENVIEW).getConstants();
        Map<String,String> filteredRateCodes = new HashMap<>();
        filteredRateCodes.put(Constants.RATE_CODES.LGNM,cs.getCustomerSubscriptionMappings().stream()
                .filter(l-> Constants.RATE_CODES.LGNM.equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue());
        filteredRateCodes.put(Constants.RATE_CODES.PCD,cs.getCustomerSubscriptionMappings().stream()
                .filter(l-> Constants.RATE_CODES.PCD.equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue());
        filteredRateCodes.put(Constants.RATE_CODES.SN,cs.getCustomerSubscriptionMappings().stream()
                .filter(l-> Constants.RATE_CODES.SN.equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue());
        filteredRateCodes.put(Constants.RATE_CODES.ITKNID,cs.getCustomerSubscriptionMappings().stream()
                .filter(l-> Constants.RATE_CODES.ITKNID.equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue());
        filteredRateCodes.put(Constants.RATE_CODES.INVERTER_NUMBER,cs.getCustomerSubscriptionMappings().stream()
                .filter(l-> Constants.RATE_CODES.INVERTER_NUMBER.equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue());

        Date lastSavedRecord = params.length>1 ? (Date) params[2] : null;

        MonitorAPIAuthBody body = getAuthBody(filteredRateCodes);
        String time = (String) params[0] == null ? format.format(new Date()) : (String) params[0];
        ResponseEntity<MonitorAPIResponse> gpResponse = getAPIData("GET_POWER", body, constants, "?currentTime=" + time, MonitorAPIResponse.class, true);
        ResponseEntity<MonitorAPIResponse> cdResponse = getAPIData("GET_CURRENT_DATA", body, constants, "?currentTime=" + time, MonitorAPIResponse.class, true);
        ResponseEntity<List<String>> asResponse = getAPIData("ALL_SITES", body, constants, "?userId=" + body.getUserId(), List.class, true);
        ResponseEntity<MonitorAPIResponse> spResponse = (ResponseEntity<MonitorAPIResponse>) getAPIData("GET_SITE_TOTAL_POWER", body, constants, "?siteId=" + asResponse.getBody().get(0) + "&time=" + time.split(" ")[0], MonitorAPIResponse.class, true);

        MonitorAPIResponse gpBody = gpResponse != null ? gpResponse.getBody() : null;
        MonitorAPIResponse cdBody = cdResponse != null ? cdResponse.getBody() : null;
        MonitorAPIResponse spBody = spResponse != null ? spResponse.getBody() : null;

        int rounding = utility.getCompanyPreference().getRounding();
        Optional<MonitorAPIResponseL2> spBodyObject = spBody.getObject().stream().filter(inv-> inv.getInverterSn()
                .equals(filteredRateCodes.get(Constants.RATE_CODES.INVERTER_NUMBER))).findFirst();
        String inverter =  spBodyObject.isPresent()?spBodyObject.get().getInverterSn() : null;
        Map<String, Map<Date, MonitorAPIResponseL2>> inverterValuesOverTime = new HashMap<>();
        List<MonitorAPIResponseL2> inverterPowers =
                spBody.getObject().stream().filter(i -> i.getInverterSn() != null && i.getInverterSn().equals(inverter)).collect(Collectors.toList());
        Map<Date, MonitorAPIResponseL2> valuesPower = new TreeMap<>();
        inverterPowers.forEach(i -> {
            try {
                if (lastSavedRecord!=null && checkLastSavedRecord(lastSavedRecord, i.getUploadTimeValue())) {
                    valuesPower.put(formatDateTime.parse(i.getUploadTimeValue()), MonitorAPIResponseL2.builder()
                            .pvPower(i.getPvPower())
                            .gridpower(i.getGridpower())
                            .relayPower(i.getRelayPower())
                            .build());
                }else {
                    valuesPower.put(formatDateTime.parse(i.getUploadTimeValue()), MonitorAPIResponseL2.builder()
                            .pvPower(i.getPvPower())
                            .gridpower(i.getGridpower())
                            .relayPower(i.getRelayPower())
                            .build());
                }
            } catch (ParseException e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
        inverterValuesOverTime.put(inverter, valuesPower);

        return MonitorAPIResponse.builder()
                .sytemSize(gpBody != null && gpBody.getRatedPower() != null ? utility.round(gpBody.getRatedPower(), rounding) : 0)
                .currentValueToday(cdBody != null && cdBody.getGridPower()!= null ? utility.round(cdBody.getGridPower(), rounding) : 0)
                .peakValue(spBody != null && spBody.getObject() != null ? utility.round(spBody.getObject().stream().mapToDouble(m -> m.getGridpower()).sum(), rounding) : 0)
                .dailyYield(cdBody != null && cdBody.getTodayYield() != null ? utility.round(cdBody.getTodayYield(), rounding) : 0)
                .monthlyYield(cdBody != null && cdBody.getMonthYield() != null ? utility.round(cdBody.getMonthYield(), rounding) : 0)
                .annualYield(cdBody != null && cdBody.getYearYield() != null ? utility.round(cdBody.getYearYield(), rounding) : 0)
                .grossYield(cdBody != null && cdBody.getTotalYield() != null ? utility.round(cdBody.getTotalYield(), rounding) : 0)
                .inverterValuesOverTime(inverterValuesOverTime).build();
    }

    @Override
    public MonitorAPIResponse getCurrentData(ExtDataStageDefinition ext, Object... params) throws NoSuchAlgorithmException, ParseException, JsonProcessingException, UnsupportedEncodingException {
        Map<String, APIConstants> constants = PrerequisitesFactory.getAPIPrerequisites(Constants.MONITOR_PLATFORM.SOLRENVIEW).getConstants();
        Map<String,String> filteredRateCodes = new HashMap<>();

        filteredRateCodes.put(Constants.RATE_CODES.LGNM, Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.LGNM));
        filteredRateCodes.put(Constants.RATE_CODES.PCD, Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.PCD));
        filteredRateCodes.put(Constants.RATE_CODES.SN, Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.SN));
        filteredRateCodes.put(Constants.RATE_CODES.ITKNID, Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.ITKNID));
        filteredRateCodes.put(Constants.RATE_CODES.INVERTER_NUMBER, Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.INVERTER_NUMBER));

        Date lastSavedRecord = params.length>1 ? (Date) params[2] : null;

        MonitorAPIAuthBody body = getAuthBody(filteredRateCodes);
        String time = (String) params[0] == null ? format.format(new Date()) : (String) params[0];
        ResponseEntity<MonitorAPIResponse> gpResponse = getAPIData("GET_POWER", body, constants, "?currentTime=" + time, MonitorAPIResponse.class, true);
        ResponseEntity<MonitorAPIResponse> cdResponse = getAPIData("GET_CURRENT_DATA", body, constants, "?currentTime=" + time, MonitorAPIResponse.class, true);
        ResponseEntity<List<String>> asResponse = getAPIData("ALL_SITES", body, constants, "?userId=" + body.getUserId(), List.class, true);
        ResponseEntity<MonitorAPIResponse> spResponse = (ResponseEntity<MonitorAPIResponse>) getAPIData("GET_SITE_TOTAL_POWER", body, constants, "?siteId=" + asResponse.getBody().get(0) + "&time=" + time.split(" ")[0], MonitorAPIResponse.class, true);

        MonitorAPIResponse gpBody = gpResponse != null ? gpResponse.getBody() : null;
        MonitorAPIResponse cdBody = cdResponse != null ? cdResponse.getBody() : null;
        MonitorAPIResponse spBody = spResponse != null ? spResponse.getBody() : null;

        int rounding = utility.getCompanyPreference().getRounding();
        Optional<MonitorAPIResponseL2> spBodyObject = spBody.getObject().stream().filter(inv-> inv.getInverterSn()
                .equals(filteredRateCodes.get(Constants.RATE_CODES.INVERTER_NUMBER))).findFirst();
        String inverter =  spBodyObject.isPresent()?spBodyObject.get().getInverterSn() : null;
        Map<String, Map<Date, MonitorAPIResponseL2>> inverterValuesOverTime = new HashMap<>();
        List<MonitorAPIResponseL2> inverterPowers =
                spBody.getObject().stream().filter(i -> i.getInverterSn() != null && i.getInverterSn().equals(inverter)).collect(Collectors.toList());
        Map<Date, MonitorAPIResponseL2> valuesPower = new TreeMap<>();
        inverterPowers.forEach(i -> {
            try {
                if (lastSavedRecord!=null && checkLastSavedRecord(lastSavedRecord, i.getUploadTimeValue())) {
                    valuesPower.put(formatDateTime.parse(i.getUploadTimeValue()), MonitorAPIResponseL2.builder()
                            .pvPower(i.getPvPower())
                            .gridpower(i.getGridpower())
                            .relayPower(i.getRelayPower())
                            .build());
                }else {
                    valuesPower.put(formatDateTime.parse(i.getUploadTimeValue()), MonitorAPIResponseL2.builder()
                            .pvPower(i.getPvPower())
                            .gridpower(i.getGridpower())
                            .relayPower(i.getRelayPower())
                            .build());
                }
            } catch (ParseException e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
        inverterValuesOverTime.put(inverter, valuesPower);

        return MonitorAPIResponse.builder()
                .sytemSize(gpBody != null && gpBody.getRatedPower() != null ? utility.round(gpBody.getRatedPower(), rounding) : 0)
                .currentValueToday(cdBody != null && cdBody.getGridPower()!= null ? utility.round(cdBody.getGridPower(), rounding) : 0)
                .peakValue(spBody != null && spBody.getObject() != null ? utility.round(spBody.getObject().stream().mapToDouble(m -> m.getGridpower()).sum(), rounding) : 0)
                .dailyYield(cdBody != null && cdBody.getTodayYield() != null ? utility.round(cdBody.getTodayYield(), rounding) : 0)
                .monthlyYield(cdBody != null && cdBody.getMonthYield() != null ? utility.round(cdBody.getMonthYield(), rounding) : 0)
                .annualYield(cdBody != null && cdBody.getYearYield() != null ? utility.round(cdBody.getYearYield(), rounding) : 0)
                .grossYield(cdBody != null && cdBody.getTotalYield() != null ? utility.round(cdBody.getTotalYield(), rounding) : 0)
                .inverterValuesOverTime(inverterValuesOverTime).build();
    }

    private ResponseEntity getAPIData(String apiName, MonitorAPIAuthBody body,
                                      Map<String, APIConstants> constants, String urlSuffix,
                                      Class clazz,
                                      boolean includeHeaders) {
        ResponseEntity response = null;
        Map<String, List<String>> headers = new HashMap<>();
        if (includeHeaders) {
            headers.put("token", Arrays.asList(body.getToken()));
        }
        APIConstants c = constants.get(apiName);
        try {
            response = WebUtils.submitRequest(c.getMethod(), c.getUrl() + urlSuffix, null, headers,
                    clazz);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
        }
        return response;
    }

    private MonitorAPIAuthBody getAuthBody(Map<String,String> filteredRateCodes ) throws NoSuchAlgorithmException {
        MonitorAPIAuthResponse authResponse = null;
        try {
            authResponse = getAuthData(filteredRateCodes.get(Constants.RATE_CODES.LGNM), filteredRateCodes.get(Constants.RATE_CODES.PCD));
        } catch (Exception e) {
            LOGGER.error("Exception", e.getMessage());
       }
        if (authResponse!=null) {
            return MonitorAPIAuthBody.builder()
                    .token(authResponse.getToken())
                    .tokenId(filteredRateCodes.get(Constants.RATE_CODES.ITKNID))
                    .sn(filteredRateCodes.get(Constants.RATE_CODES.SN))
                    .userId(authResponse.getUser().getId()).build();
        }
        return null;
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
        List<Date> labelDates = getDateTimes(body.getTime());
        List<MonitorReading>  daysData = readingRepository.findBySubscriptionIdAndTimeIn(cs.getId(), labelDates);
        return daysData;
    }

    @Override
    public List<MonitorReading> getMonitorReadingDataForUserComparison(Long userId, MonitorAPIAuthBody body, List<CustomerSubscription> customerSubscriptionList) throws ParseException {
        List<MonitorReading>  daysData = new ArrayList<>();
        if(customerSubscriptionList.size()>0) {
            List<Date> labelDatesSOLRENVIEW = getDateTimes(body.getTime());
            daysData  = readingRepository.findByUserIdAndTimeIn(userId, labelDatesSOLRENVIEW, customerSubscriptionList);
        }
        return daysData;
    }

    @Override
    public List<MonitorReading> getMonitorReadingDataForUserComparison(String projectId, MonitorAPIAuthBody body, List<CustomerSubscription> customerSubscriptionList) throws ParseException {
        return null;
    }

    private List<Date> getDateTimes(String time) throws ParseException {
        int lineGraphIncrement = 30;
        int min = 300;

        Date date = formatDateTime.parse(time != null ? time : formatDateTime.format(new Date()));
        Date tillDateTime = Utility.addMinutes(new Date(), min);
        boolean isToday = Utility.areInSameDay(date, tillDateTime);
        Date dateTime = Utility.getStartOfDate(date);
        List<Date> dateTimes = new ArrayList<>();
        if (!isToday) {
            tillDateTime = Utility.getEndOfDate(dateTime);
        }
        while (dateTime.before(tillDateTime)) {
            dateTimes.add(dateTime);
            dateTime = Utility.addMinutes(dateTime, lineGraphIncrement);
        }
        return dateTimes;
    }

    @Override
    public List<MonitorReading> getMonitorReadingDataForMongoComparison(MonitorAPIAuthBody body, String subsId) throws ParseException {
        List<Date> labelDates = getDateTimes(body.getTime());
        return readingRepository.findBySubscriptionIdMongoAndTimeIn(subsId, labelDates);
    }

    @Override
    public boolean savesBulk() {
        return false;
    }

}
