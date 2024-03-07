package com.solar.api.tenant.service.process.pvmonitor.platform.solax;

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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.solar.api.Constants.RATE_CODES.*;

@Service
public class SolaxAPI implements MonitorAPI {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private Utility utility;
    @Autowired
    private MonitorReadingRepository readingRepository;

    private SimpleDateFormat formatDateTime = new SimpleDateFormat(Utility.SYSTEM_DATE_TIME_FORMAT);
    private SimpleDateFormat format = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);

    @Override
    public MonitorAPIAuthResponse getAuthData(Object... params) throws NoSuchAlgorithmException {
        Map<String, APIConstants> constants = PrerequisitesFactory.getAPIPrerequisites("SOLAX").getConstants();
        APIConstants c = constants.get("LOGIN_NEW");
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
    public MonitorAPIResponse getCurrentData(CustomerSubscription cs, Object... params) throws NoSuchAlgorithmException {
        Map<String, APIConstants> constants = PrerequisitesFactory.getAPIPrerequisites("SOLAX").getConstants();
        Map<String,String> filteredRateCodes = new HashMap<>();
        filteredRateCodes.put("LGNM",cs.getCustomerSubscriptionMappings().stream().filter(l-> "LGNM".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue());
        filteredRateCodes.put("PCD",cs.getCustomerSubscriptionMappings().stream().filter(l-> "PCD".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue());
        filteredRateCodes.put("SN",cs.getCustomerSubscriptionMappings().stream().filter(l-> "SN".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue());
        filteredRateCodes.put("ITKNID",cs.getCustomerSubscriptionMappings().stream().filter(l-> "ITKNID".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue());
        filteredRateCodes.put("INVRT",cs.getCustomerSubscriptionMappings().stream().filter(l-> "INVRT".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue());

        Date lastSavedRecord = params.length>1 ? (Date) params[2] : null;

        MonitorAPIAuthBody body = getAuthBody(filteredRateCodes);
        String time = (String) params[0] == null ? format.format(new Date()) : (String) params[0];
        // GET_POWER
        ResponseEntity<MonitorAPIResponse> gpResponse = getAPIData("GET_POWER", body, constants, "?currentTime=" + time, MonitorAPIResponse.class, true);
        // GET_REALTIME_INFO
//        ResponseEntity<MonitorAPIResponse> rtResponse = getAPIData("GET_REALTIME_INFO", body, constants, "?tokenId=" + body.getTokenId() + "&sn=" + body.getSn(), MonitorAPIResponse.class, false);
        // GET_CURRENT_DATA
        ResponseEntity<MonitorAPIResponse> cdResponse = getAPIData("GET_CURRENT_DATA", body, constants, "?currentTime=" + time, MonitorAPIResponse.class, true);
        // ALL_SITES
        ResponseEntity<List<String>> asResponse = getAPIData("ALL_SITES", body, constants, "?userId=" + body.getUserId(), List.class, true);
        // GET_SITE_TOTAL_POWER
        ResponseEntity<MonitorAPIResponse> spResponse = (ResponseEntity<MonitorAPIResponse>) getAPIData("GET_SITE_TOTAL_POWER", body, constants, "?siteId=" + asResponse.getBody().get(0) + "&time=" + time.split(" ")[0], MonitorAPIResponse.class, true);

        MonitorAPIResponse gpBody = gpResponse != null ? gpResponse.getBody() : null;
//        MonitorAPIResponse rtBody = rtResponse != null ? rtResponse.getBody() : null;
        MonitorAPIResponse cdBody = cdResponse != null ? cdResponse.getBody() : null;
        MonitorAPIResponse spBody = spResponse != null ? spResponse.getBody() : null;

        int rounding = utility.getCompanyPreference().getRounding();
        Optional<MonitorAPIResponseL2> spBodyObject = spBody.getObject().stream().filter(inv-> inv.getInverterSn().equals(filteredRateCodes.get("INVRT"))).findFirst();
        String inverter =  spBodyObject.isPresent()?spBodyObject.get().getInverterSn() : null;
        Map<String, Map<Date, MonitorAPIResponseL2>> inverterValuesOverTime = new HashMap<>();
//        Map<String, Map<Date, Double>> inverterGridpowerOverTime = new HashMap<>();
//        Map<String, Map<Date, Double>> inverterRelayPowerOverTime = new HashMap<>();
   ////     inverters.forEach(inverter -> {
            List<MonitorAPIResponseL2> inverterPowers =
                    spBody.getObject().stream().filter(i -> i.getInverterSn() != null && i.getInverterSn().equals(inverter)).collect(Collectors.toList());
            Map<Date, MonitorAPIResponseL2> valuesPower = new TreeMap<>();
//            Map<Date, Double> valuesGridpower = new TreeMap<>();
//            Map<Date, Double> valuesRelayPower = new TreeMap<>();
            inverterPowers.forEach(i -> {
                try {
                    if (lastSavedRecord!=null && checkLastSavedRecord(lastSavedRecord, i.getUploadTimeValue())) {
                        valuesPower.put(formatDateTime.parse(i.getUploadTimeValue()), MonitorAPIResponseL2.builder()
                                .pvPower(i.getPvPower())
                                .gridpower(i.getGridpower())
                                .relayPower(i.getRelayPower())
                                .build());
                    }else {
                        // new customer record is added
                        valuesPower.put(formatDateTime.parse(i.getUploadTimeValue()), MonitorAPIResponseL2.builder()
                                .pvPower(i.getPvPower())
                                .gridpower(i.getGridpower())
                                .relayPower(i.getRelayPower())
                                .build());
                    }
//                    valuesGridpower.put(format.parse(i.getUploadTimeValue()), i.getGridpower());
//                    valuesRelayPower.put(format.parse(i.getUploadTimeValue()), i.getRelayPower());
                } catch (ParseException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            });
            inverterValuesOverTime.put(inverter, valuesPower);
//            inverterGridpowerOverTime.put(inverter, valuesGridpower);
//            inverterRelayPowerOverTime.put(inverter, valuesRelayPower);
//            inverterRelayPowerOverTime.put(inverter, spBody.getObject().stream().filter(i -> i.getInverterSn().equals(inverter)).collect(Collectors.toMap(MonitorAPIResponseL2::getUploadTimeValue, MonitorAPIResponseL2::getRelayPower)));
       // });

        return MonitorAPIResponse.builder()
                .sytemSize(gpBody != null && gpBody.getRatedPower() != null ? utility.round(gpBody.getRatedPower(), rounding) : 0)
                .currentValueToday(cdBody != null && cdBody.getGridPower()!= null ? utility.round(cdBody.getGridPower(), rounding) : 0)
//                .currentValue(rtBody != null && rtBody.getResult() != null ? utility.round(rtBody.getResult().getAcpower(), rounding) : 0)
                .peakValue(spBody != null && spBody.getObject() != null ? utility.round(spBody.getObject().stream().mapToDouble(m -> m.getGridpower()).sum(), rounding) : 0)
                .dailyYield(cdBody != null && cdBody.getTodayYield() != null ? utility.round(cdBody.getTodayYield(), rounding) : 0)
                .monthlyYield(cdBody != null && cdBody.getMonthYield() != null ? utility.round(cdBody.getMonthYield(), rounding) : 0)
//                .annualYield(cdBody != null && cdBody.getYearYield() != null ? utility.round(cdBody.getYearYield() / 1000, rounding) : 0)
//                .grossYield(rtBody != null && rtBody.getResult() != null ? utility.round(rtBody.getResult().getYieldtotal() / 1000, rounding) : 0)
                .annualYield(cdBody != null && cdBody.getYearYield() != null ? utility.round(cdBody.getYearYield(), rounding) : 0)
                .grossYield(cdBody != null && cdBody.getTotalYield() != null ? utility.round(cdBody.getTotalYield(), rounding) : 0)
                .inverterValuesOverTime(inverterValuesOverTime)
//                .inverterGridpowerOverTime(inverterGridpowerOverTime)
//                .inverterRelayPowerOverTime(inverterRelayPowerOverTime)
                .build();
    }

    @Override
    public MonitorAPIResponse getCurrentData(ExtDataStageDefinition ext , Object... params) throws NoSuchAlgorithmException {
        Map<String, APIConstants> constants = PrerequisitesFactory.getAPIPrerequisites(Constants.MONITOR_PLATFORM.SOLAX).getConstants();

        Map<String, String> filteredRateCodes = new HashMap<>();
        filteredRateCodes.put(LGNM, Utility.getMeasureAsJson(ext.getMpJson(), LGNM));
        filteredRateCodes.put(PCD, Utility.getMeasureAsJson(ext.getMpJson(), PCD));
        filteredRateCodes.put(SN, Utility.getMeasureAsJson(ext.getMpJson(), SN));
        filteredRateCodes.put(INVERTER_NUMBER, Utility.getMeasureAsJson(ext.getMpJson(), INVERTER_NUMBER));

        Date lastSavedRecord = params.length > 1 ? (Date) params[2] : null;

        MonitorAPIAuthBody body = getAuthBody(filteredRateCodes);
        String time = (String) params[0] == null ? format.format(new Date()) : (String) params[0];
        // GET_POWER
        ResponseEntity<MonitorAPIResponse> gpResponse = getAPIData("GET_POWER", body, constants, "?currentTime=" + time, MonitorAPIResponse.class, true);
        // GET_REALTIME_INFO
//        ResponseEntity<MonitorAPIResponse> rtResponse = getAPIData("GET_REALTIME_INFO", body, constants, "?tokenId=" + body.getTokenId() + "&sn=" + body.getSn(), MonitorAPIResponse.class, false);
        // GET_CURRENT_DATA
        ResponseEntity<MonitorAPIResponse> cdResponse = getAPIData("GET_CURRENT_DATA", body, constants, "?currentTime=" + time, MonitorAPIResponse.class, true);
        // ALL_SITES
        ResponseEntity<List<String>> asResponse = getAPIData("ALL_SITES", body, constants, "?userId=" + body.getUserId(), List.class, true);
        // GET_SITE_TOTAL_POWER
        ResponseEntity<MonitorAPIResponse> spResponse = null;
        if (asResponse != null && asResponse.getBody() != null) {
            spResponse = (ResponseEntity<MonitorAPIResponse>) getAPIData("GET_SITE_TOTAL_POWER", body, constants, "?siteId=" + asResponse.getBody().get(0) + "&time=" + time.split(" ")[0], MonitorAPIResponse.class, true);
        }
        MonitorAPIResponse gpBody = gpResponse != null ? gpResponse.getBody() : null;
        MonitorAPIResponse cdBody = cdResponse != null ? cdResponse.getBody() : null;
        MonitorAPIResponse spBody = spResponse != null ? spResponse.getBody() : null;

        int rounding = utility.getCompanyPreference().getRounding();
        Map<String, Map<Date, MonitorAPIResponseL2>> inverterValuesOverTime = new HashMap<>();
        if (spBody != null && spBody.getObject() != null) {
            Optional<MonitorAPIResponseL2> spBodyObject = spBody.getObject().stream().filter(inv -> inv.getInverterSn().equals(filteredRateCodes.get(INVERTER_NUMBER))).findFirst();
            String inverter = spBodyObject.isPresent() ? spBodyObject.get().getInverterSn() : null;
            List<MonitorAPIResponseL2> inverterPowers =
                    spBody.getObject().stream().filter(i -> i.getInverterSn() != null && i.getInverterSn().equals(inverter)).collect(Collectors.toList());
            Map<Date, MonitorAPIResponseL2> valuesPower = new TreeMap<>();
            inverterPowers.forEach(i -> {
                try {
                    if (lastSavedRecord != null && checkLastSavedRecord(lastSavedRecord, i.getUploadTimeValue())) {
                        valuesPower.put(formatDateTime.parse(i.getUploadTimeValue()), MonitorAPIResponseL2.builder()
                                .pvPower(i.getPvPower())
                                .gridpower(i.getGridpower())
                                .relayPower(i.getRelayPower())
                                .build());
                    } else {
                        // new customer record is added
                        valuesPower.put(formatDateTime.parse(i.getUploadTimeValue()), MonitorAPIResponseL2.builder()
                                .pvPower(i.getPvPower())
                                .gridpower(i.getGridpower())
                                .relayPower(i.getRelayPower())
                                .build());
                    }
//                    valuesGridpower.put(format.parse(i.getUploadTimeValue()), i.getGridpower());
//                    valuesRelayPower.put(format.parse(i.getUploadTimeValue()), i.getRelayPower());
                } catch (ParseException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            });
            inverterValuesOverTime.put(inverter, valuesPower);
        }

        return MonitorAPIResponse.builder()
                .sytemSize(gpBody != null && gpBody.getRatedPower() != null ? utility.round(gpBody.getRatedPower(), rounding) : 0)
                .currentValueToday(cdBody != null && cdBody.getGridPower()!= null ? utility.round(cdBody.getGridPower(), rounding) : 0)
                .peakValue(spBody != null && spBody.getObject() != null ? utility.round(spBody.getObject().stream().mapToDouble(m -> m.getGridpower()).sum(), rounding) : 0)
                .dailyYield(cdBody != null && cdBody.getTodayYield() != null ? utility.round(cdBody.getTodayYield(), rounding) : 0)
                .monthlyYield(cdBody != null && cdBody.getMonthYield() != null ? utility.round(cdBody.getMonthYield(), rounding) : 0)
                .annualYield(cdBody != null && cdBody.getYearYield() != null ? utility.round(cdBody.getYearYield(), rounding) : 0)
                .grossYield(cdBody != null && cdBody.getTotalYield() != null ? utility.round(cdBody.getTotalYield(), rounding) : 0)
                .inverterValuesOverTime(inverterValuesOverTime)
                .build();
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
        MonitorAPIAuthResponse authResponse = getAuthData(filteredRateCodes.get("LGNM"), filteredRateCodes.get("PCD"));
        if (authResponse != null) {
            return MonitorAPIAuthBody.builder()
            .token(authResponse.getToken())
            .tokenId(filteredRateCodes.get("ITKNID"))
            .sn(filteredRateCodes.get("SN"))
            .userId(authResponse.getUser() != null ? authResponse.getUser().getId() : null).build();
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
            List<Date> labelDatesSolax = getDateTimes(body.getTime());
            daysData  = readingRepository.findByUserIdAndTimeIn(userId, labelDatesSolax, customerSubscriptionList);
        }
        return daysData;
    }

    @Override
    public List<MonitorReading> getMonitorReadingDataForUserComparison(String projectId, MonitorAPIAuthBody body, List<CustomerSubscription> customerSubscriptionList) throws ParseException {
        return null;
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

    @Override
    public boolean savesBulk() {
        return false;
    }

}
