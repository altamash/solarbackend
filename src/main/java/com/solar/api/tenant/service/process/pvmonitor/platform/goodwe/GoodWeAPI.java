package com.solar.api.tenant.service.process.pvmonitor.platform.goodwe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.solar.api.tenant.repository.CustomerSubscriptionRepository;
import com.solar.api.tenant.repository.MonitorReadingRepository;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.process.pvmonitor.APIConstants;
import com.solar.api.tenant.service.process.pvmonitor.MonitorAPI;
import com.solar.api.tenant.service.process.pvmonitor.PrerequisitesFactory;
import com.solar.api.tenant.service.process.pvmonitor.platform.goodwe.dto.DetailDataDTO;
import com.solar.api.tenant.service.process.pvmonitor.platform.goodwe.dto.MonitorAPIBodyDTO;
import com.solar.api.tenant.service.process.pvmonitor.platform.goodwe.dto.MonitorAPIGoodWeResponse;
import com.solar.api.tenant.service.process.pvmonitor.platform.goodwe.dto.MonitorAPIYieldRatioResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.solar.api.Constants.RATE_CODES.*;

@Service
public class GoodWeAPI implements MonitorAPI {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private Utility utility;
    @Autowired
    private UserService userService;
    @Autowired
    private CustomerSubscriptionRepository customerSubscriptionRepository;
    @Autowired
    private MonitorReadingRepository readingRepository;

    private SimpleDateFormat formatDateTime = new SimpleDateFormat(Utility.SYSTEM_DATE_TIME_FORMAT);
    private SimpleDateFormat format = new SimpleDateFormat(Utility.MONTH_DATE_YEAR_FORMAT);
    private SimpleDateFormat formatPacDateTime = new SimpleDateFormat(Utility.SYSTEM_DATE_TIME_SIMPLE_FORMAT);
    private SimpleDateFormat formatYearMonthDt = new SimpleDateFormat(Utility.YEAR_MONTH_DATE_FORMAT);

    @Override
    public MonitorAPIAuthResponse getAuthData(Object... params) throws NoSuchAlgorithmException{
        String bodyJsonString = null;
        MonitorAPIAuthResponse monitorAPIAuthResponse = null;
        try {
            bodyJsonString = new ObjectMapper().writeValueAsString(MonitorAPIAuthBody.builder()
                        .account((String)params[0])
                        .pwd((String)params[1])
                         .build()
                );
             monitorAPIAuthResponse = (MonitorAPIAuthResponse) getAuthenticatedData(bodyJsonString, null,"GETTOKENV2", null).getBody();
        }
         catch (UnsupportedEncodingException e)  {
            LOGGER.error(e.getMessage(), e);
        }
        catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return monitorAPIAuthResponse;
    }

    private String getPasswordHash(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        return DatatypeConverter.printHexBinary(md.digest()).toLowerCase();
    }

    @Override
    public MonitorAPIResponse getCurrentData(CustomerSubscription cs,Object... params) throws NoSuchAlgorithmException, JsonProcessingException, UnsupportedEncodingException ,ParseException{
        Map<String, APIConstants> constants = PrerequisitesFactory.getAPIPrerequisites("GOODWE").getConstants();
        Map<String,String> filteredRateCodes = new HashMap<>();
        filteredRateCodes.put("LGNM",cs.getCustomerSubscriptionMappings().stream().filter(l-> "LGNM".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue());
        filteredRateCodes.put("PCD",cs.getCustomerSubscriptionMappings().stream().filter(l-> "PCD".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue());
        filteredRateCodes.put("SN",cs.getCustomerSubscriptionMappings().stream().filter(l-> "SN".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue());
        filteredRateCodes.put("ITKNID",cs.getCustomerSubscriptionMappings().stream().filter(l-> "ITKNID".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue());
        filteredRateCodes.put("INVRT",cs.getCustomerSubscriptionMappings().stream().filter(l-> "INVRT".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue());

        Date lastSavedRecord = params.length>1 ? (Date) params[2] : null;
        MonitorAPIAuthBody body = getAuthBody(filteredRateCodes);
        String time = (String) params[0] == null ? format.format(new Date()) : format.format(formatYearMonthDt.parse(params[0].toString()));

        // GET_inverterDetail
        ResponseEntity<MonitorAPIResponse> inverterSnResponse = getMonitorAPIData(getAPIData("INVERTER_BY_Sn", body, constants,time, MonitorAPIGoodWeResponse.class, true,0));
        // GET_YIELD_VALUE by sn type =4 (yearly)
        ResponseEntity<MonitorAPIResponse> rtYearlyResponse = getMonitorAPIPVData(getAPIData("INVERTER_YIELD_RATIO_BY_Sn", body, constants,  time , MonitorAPIGoodWeResponse.class, true,4));
        // GET_Daily_powerData
        ResponseEntity<MonitorAPIResponse> inverterPacResponse = getMonitorAPIPacDataDetail(getAPIData("INVERTER_PAC_BY_DAY", body, constants, time, MonitorAPIYieldRatioResponse.class, true,0));

        MonitorAPIResponse inverterSnBody = inverterSnResponse != null ? inverterSnResponse.getBody() : null;
        MonitorAPIResponse inverterPacBody = inverterPacResponse != null ? inverterPacResponse.getBody() : null;
        MonitorAPIResponse rtYearlyBody = rtYearlyResponse != null ? rtYearlyResponse.getBody() : null;

        int rounding = utility.getCompanyPreference().getRounding();
      //  List<String> inverters = spBody.getObject().stream().map(o -> o.getInverterSn()).distinct().collect(Collectors.toList());
        Map<String, Map<Date, MonitorAPIResponseL2>> inverterValuesOverTime = new HashMap<>();
//        Map<String, Map<Date, Double>> inverterGridpowerOverTime = new HashMap<>();
//        Map<String, Map<Date, Double>> inverterRelayPowerOverTime = new HashMap<>();
        Map<Date, MonitorAPIResponseL2> valuesPower = new TreeMap<>();
        inverterPacBody.getPacDataDetail().forEach(i-> {

            try {
                if (lastSavedRecord!=null && checkLastSavedRecord(lastSavedRecord, formatDateTime.format(formatPacDateTime.parse(i.getDate())))){

                    valuesPower.put(formatDateTime.parse(formatDateTime.format(formatPacDateTime.parse(i.getDate()))), MonitorAPIResponseL2.builder()
                            .pvPower(i.getPac())
                            .gridpower(i.getPac())
//                        .relayPower(i.getRelayPower())
                            .build());
                }
                    else {
                        // new customer record is added
                    valuesPower.put(formatDateTime.parse(formatDateTime.format(formatPacDateTime.parse(i.getDate()))), MonitorAPIResponseL2.builder()
                            .pvPower(i.getPac())
                            .gridpower(i.getPac())
//                        .relayPower(i.getRelayPower())
                            .build());
                    }
            } catch (ParseException e) {
                LOGGER.error(e.getMessage(), e);
            }

            inverterValuesOverTime.put(i.getSn(), valuesPower);

        });
        List<DetailDataDTO> inverterPacBodyList = inverterPacBody != null && inverterPacBody.getPacDataDetail() != null ? inverterPacBody.getPacDataDetail():null;
        if(inverterPacBodyList!=null)
        Collections.sort(inverterPacBodyList, inverterPacDetailComparator());
         Calendar cal = Calendar.getInstance();
         cal.setTime(format.parse(time));
         String year = String.valueOf(cal.get(Calendar.YEAR));
        return MonitorAPIResponse.builder()
//              .currentValueToday(rtBody != null && rtBody.getResult() != null ? utility.round(rtBody.getResult().getAcpower(), rounding) : 0)
                .dailyYield(inverterSnBody != null && inverterSnBody.getDetailedData() != null ? utility.round(inverterSnBody.getDetailedData().getEday(), rounding) : 0)
                .monthlyYield(inverterSnBody != null && inverterSnBody.getDetailedData() != null ? utility.round(inverterSnBody.getDetailedData().getEmonth(), rounding) : 0)
                //capacity is in kw unit for goodwe
                .sytemSize(inverterSnBody != null && inverterSnBody.getDetailedData()!= null ? utility.round(inverterSnBody.getDetailedData().getCapacity(), rounding) : 0)
                .grossYield(inverterSnBody != null && inverterSnBody.getDetailedData()!= null ? utility.round(inverterSnBody.getDetailedData().getEtotal(), rounding) : 0)
                .peakValue(inverterPacBody != null && inverterPacBody.getPacDataDetail() != null ? utility.round(inverterPacBody.getPacDataDetail().stream().mapToDouble(k -> k.getPac()).max().orElse(0), rounding) : 0)
                .annualYield(rtYearlyBody != null && rtYearlyBody.getPvData() != null ? utility.round((rtYearlyBody.getPvData().stream().filter(pv->pv.getX().equals(year)).mapToDouble(pv->pv.getY()).findFirst().getAsDouble()), rounding) : 0)
                .currentValue(inverterPacBodyList != null && !inverterPacBodyList.isEmpty() ? utility.round(inverterPacBodyList.stream().findFirst().get().getPac(), rounding) : 0)
                .inverterValuesOverTime(inverterValuesOverTime)
                .build();
    }

    private ResponseEntity getAPIData(String apiName, MonitorAPIAuthBody body,
                                      Map<String, APIConstants> constants, String time,
                                      Class clazz,
                                      boolean includeHeaders,
                                      int type) {
        ResponseEntity response = null;
        Map<String, List<String>> headers = new HashMap<>();
            if (includeHeaders) {
            String loginToken = body.getDataDTO().toString();
            headers.put("Token", Arrays.asList(loginToken));
            headers.put("content-type", Arrays.asList("application/json;charset=UTF-8"));
        }
        APIConstants c = constants.get(apiName);
        try {
            String bodyJson = getBodyJsonString(apiName,body,type,time);
            response = WebUtils.submitRequest(c.getMethod(), c.getUrl(), bodyJson, headers,
                    clazz);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
        }
        return response;
    }

    private MonitorAPIAuthBody getAuthBody(Map<String,String> filteredRateCodes ) throws NoSuchAlgorithmException {
        MonitorAPIAuthResponse authResponse = getAuthData(filteredRateCodes.get("LGNM"), filteredRateCodes.get("PCD"));
        if (authResponse!=null) {
            return MonitorAPIAuthBody.builder()
                    .tokenId(filteredRateCodes.get("ITKNID"))
                    .sn(filteredRateCodes.get("SN"))
                    .inverterNumber(filteredRateCodes.get(INVERTER_NUMBER))
                    .dataDTO(authResponse.getData()).build();
        }
        return null;
    }

    private ResponseEntity getAuthenticatedData(String bodyJsonStringLogin, String bodyJsonString, String constantName, String token) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Map<String, APIConstants> constants = PrerequisitesFactory.getAPIPrerequisites("GOODWE").getConstants();
        APIConstants c = constants.get(constantName);
        Map<String, List<String>> headers = new HashMap<>();

        String client = Constants.GOODWE_API_HEADER_VALUES.client;
        String version = Constants.GOODWE_API_HEADER_VALUES.version;
        String language = Constants.GOODWE_API_HEADER_VALUES.language;
        String loginToken = "{\"client\":\""+client+"\",\"version\":\""+version+"\",\"language\":\""+language+"\"}";

        if (bodyJsonStringLogin != null) {
            headers.put("Token", Arrays.asList(loginToken));
            headers.put("content-type", Arrays.asList("application/json;charset=UTF-8"));

        }
        return WebUtils.submitRequest(c.getMethod(), c.getUrl(), bodyJsonStringLogin, headers, MonitorAPIAuthResponse.class);
    }


    private String getBodyJsonString(String constantName, MonitorAPIAuthBody body, int type, String time) throws JsonProcessingException, ParseException {

        String listBodyJsonString = "";

        if ("INVERTER_BY_Sn".equalsIgnoreCase(constantName)) {
            listBodyJsonString = new ObjectMapper().writeValueAsString(MonitorAPIBodyDTO.builder()
                    .InverterSn(body.getInverterNumber()).build());
        } else if ("INVERTER_YIELD_RATIO_BY_Sn".equalsIgnoreCase(constantName)) {
            listBodyJsonString = new ObjectMapper().writeValueAsString(MonitorAPIBodyDTO.builder()
                    .sn(body.getInverterNumber())
                    .type(type)
                    .date(time)  // mm/dd/yyyy
                    .build());
        } else if ("INVERTER_PAC_BY_DAY".equalsIgnoreCase(constantName)) {
            listBodyJsonString = new ObjectMapper().writeValueAsString(MonitorAPIBodyDTO.builder()
                    .id(body.getInverterNumber())
                    .date(time) //mm/dd/yyyy
                    .build());
        }
        return listBodyJsonString;
    }
    private ResponseEntity getMonitorAPIData(ResponseEntity<MonitorAPIGoodWeResponse> gpDetailResponse){
        MonitorAPIResponse monitorAPIResponse  = MonitorAPIResponse.builder().build();
        if(gpDetailResponse != null)
        monitorAPIResponse.setDetailedData(gpDetailResponse.getBody().getData());
        ResponseEntity<MonitorAPIResponse> gpResponse = new ResponseEntity<MonitorAPIResponse>(monitorAPIResponse, HttpStatus.OK);
        return gpResponse;
    }
    private ResponseEntity getMonitorAPIPacDataDetail(ResponseEntity<MonitorAPIYieldRatioResponse> pacDetailResponse){
        MonitorAPIResponse monitorAPIResponse  = MonitorAPIResponse.builder().build();
        if(pacDetailResponse != null)
            monitorAPIResponse.setPacDataDetail(pacDetailResponse.getBody().getData());
        ResponseEntity<MonitorAPIResponse> gpResponse = new ResponseEntity<MonitorAPIResponse>(monitorAPIResponse, HttpStatus.OK);
        return gpResponse;
    }
    private ResponseEntity getMonitorAPIPVData(ResponseEntity<MonitorAPIGoodWeResponse> pvDetailResponse){
        MonitorAPIResponse monitorAPIResponse  = MonitorAPIResponse.builder().build();
        if(pvDetailResponse != null) {
            if (pvDetailResponse.getBody().getData() != null) {
                monitorAPIResponse.setPvData(pvDetailResponse.getBody().getData().getPv());
            } else {
                LOGGER.error("GoodWeAPI#getMonitorAPIPVData - data is null");
            }
        }
        ResponseEntity<MonitorAPIResponse> gpResponse = new ResponseEntity<MonitorAPIResponse>(monitorAPIResponse, HttpStatus.OK);
        return gpResponse;
    }
    private Comparator<DetailDataDTO> inverterPacDetailComparator(){
        Comparator<DetailDataDTO> inverterPacDateComparator  = Comparator.comparing(DetailDataDTO::getDate,(d1,d2)->{
            try {
                return formatPacDateTime.parse(d1).compareTo(formatPacDateTime.parse(d2));
            } catch (ParseException e) {
                LOGGER.error(e.getMessage());
            }
            return 0;
        }).reversed();
        return inverterPacDateComparator;
    }

    @Override
    public List<MonitorReading> getMonitorReadingDataForCsComparison(MonitorAPIAuthBody body, CustomerSubscription cs) throws ParseException {
        List<Date> labelDatesGoodWe = getStartEndDateTime(body.getTime());
        List<MonitorReading> daysData = null;
        Date sDate = null;
        Date eDate = null;
        if (labelDatesGoodWe.size() > 0) {
            sDate = labelDatesGoodWe.get(0);
            eDate = labelDatesGoodWe.get(1);
            daysData = getMonitorReadingYieldSumForGoodWe(body, cs, sDate, eDate);
        }
        return daysData;
    }

    @Override
    public List<MonitorReading> getMonitorReadingDataForUserComparison(Long userId, MonitorAPIAuthBody body, List<CustomerSubscription> customerSubscriptionList) throws ParseException {
        List<MonitorReading> daysData = new ArrayList<>();
        if (customerSubscriptionList.size() > 0) {
            for (CustomerSubscription cs : customerSubscriptionList) {
                List<Date> labelDatesSolis = getStartEndDateTime(body.getTime());
                if (labelDatesSolis.size() > 0) {
                    Date sdate = labelDatesSolis.get(0);
                    Date edate = labelDatesSolis.get(1);
                    daysData = getMonitorReadingYieldSumForGoodWe(body, cs, sdate, edate);
                }
            }
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
        return daysData;
    }


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
        }
        else{
            tillDateTime =  Utility.getDateBeforeNow(formatDateTime.parse(formatDateTime.format(new Date())));
        }
        dateTimes.add(startOfDate);
        dateTimes.add(tillDateTime);
        return dateTimes;
    }

    private List<MonitorReading> getMonitorReadingYieldSumForGoodWe(MonitorAPIAuthBody body, CustomerSubscription cs, Date sDate, Date eDate) throws ParseException {
        List<Date> labelDates = getDateTimes(body.getTime());
        String startDate = formatDateTime.format(sDate != null ? sDate : formatDateTime.format(new Date()));
        String endDate = formatDateTime.format(eDate != null ? eDate : formatDateTime.format(new Date()));
        List<MonitorReading>  monitorReadingData = readingRepository.findBySubscriptionIdAndStartDtTimeAndEndDtTime(cs.getId(), startDate, endDate);
        List<MonitorReading> daysData = new ArrayList<>();

        for (int i = 0; i < labelDates.size() - 1; i++) {
            Date sdate = labelDates.get(i);
            Date edate = labelDates.get(i + 1);
            List<MonitorReading>  monitorReadingFilteredList = monitorReadingData.stream().filter(m -> m.getTime().after(sdate) &&
                    (m.getTime().before(edate) || getDate(m.getTime()).equals(edate))).collect(Collectors.toList());
            if (monitorReadingFilteredList != null && monitorReadingFilteredList.size()>0) {
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

    private Date getDate(Date dt){
        Date date = null;
        try {
            date = formatDateTime.parse(formatDateTime.format(dt));
        } catch (ParseException e) {
            LOGGER.error(e.getMessage());
        }
        return date;
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
    public MonitorAPIResponse getCurrentData(ExtDataStageDefinition ext, Object... params) throws NoSuchAlgorithmException, ParseException {
        Map<String, APIConstants> constants = PrerequisitesFactory.getAPIPrerequisites(Constants.MONITOR_PLATFORM.GOODWE).getConstants();

        Map<String,String> filteredRateCodes = new HashMap<>();
        filteredRateCodes.put(LGNM, Utility.getMeasureAsJson(ext.getMpJson(), LGNM));
        filteredRateCodes.put(PCD, Utility.getMeasureAsJson(ext.getMpJson(), PCD));
        filteredRateCodes.put(SN, Utility.getMeasureAsJson(ext.getMpJson(), SN));
        filteredRateCodes.put(ITKNID, Utility.getMeasureAsJson(ext.getMpJson(), ITKNID));
        filteredRateCodes.put(INVERTER_NUMBER, Utility.getMeasureAsJson(ext.getMpJson(), INVERTER_NUMBER));

        Date lastSavedRecord = params.length>1 ? (Date) params[2] : null;
        MonitorAPIAuthBody body = getAuthBody(filteredRateCodes);
        String time = (String) params[0] == null ? format.format(new Date()) : format.format(formatYearMonthDt.parse(params[0].toString()));

        // GET_inverterDetail
        ResponseEntity<MonitorAPIResponse> inverterSnResponse = getMonitorAPIData(getAPIData("INVERTER_BY_Sn", body, constants,time, MonitorAPIGoodWeResponse.class, true,0));
        // GET_YIELD_VALUE by sn type =4 (yearly)
        ResponseEntity<MonitorAPIResponse> rtYearlyResponse = getMonitorAPIPVData(getAPIData("INVERTER_YIELD_RATIO_BY_Sn", body, constants,  time , MonitorAPIGoodWeResponse.class, true,4));
        // GET_Daily_powerData
        ResponseEntity<MonitorAPIResponse> inverterPacResponse = getMonitorAPIPacDataDetail(getAPIData("INVERTER_PAC_BY_DAY", body, constants, time, MonitorAPIYieldRatioResponse.class, true,0));

        MonitorAPIResponse inverterSnBody = inverterSnResponse != null ? inverterSnResponse.getBody() : null;
        MonitorAPIResponse inverterPacBody = inverterPacResponse != null ? inverterPacResponse.getBody() : null;
        MonitorAPIResponse rtYearlyBody = rtYearlyResponse != null ? rtYearlyResponse.getBody() : null;

        int rounding = utility.getCompanyPreference().getRounding();
        //  List<String> inverters = spBody.getObject().stream().map(o -> o.getInverterSn()).distinct().collect(Collectors.toList());
        Map<String, Map<Date, MonitorAPIResponseL2>> inverterValuesOverTime = new HashMap<>();
//        Map<String, Map<Date, Double>> inverterGridpowerOverTime = new HashMap<>();
//        Map<String, Map<Date, Double>> inverterRelayPowerOverTime = new HashMap<>();
        Map<Date, MonitorAPIResponseL2> valuesPower = new TreeMap<>();
        if (inverterPacBody.getPacDataDetail() != null) {
            inverterPacBody.getPacDataDetail().forEach(i -> {

                try {
                    if (lastSavedRecord != null && checkLastSavedRecord(lastSavedRecord, formatDateTime.format(formatPacDateTime.parse(i.getDate())))) {

                        valuesPower.put(formatDateTime.parse(formatDateTime.format(formatPacDateTime.parse(i.getDate()))), MonitorAPIResponseL2.builder()
                                .pvPower(i.getPac())
                                .gridpower(i.getPac())
//                        .relayPower(i.getRelayPower())
                                .build());
                    } else {
                        // new customer record is added
                        valuesPower.put(formatDateTime.parse(formatDateTime.format(formatPacDateTime.parse(i.getDate()))), MonitorAPIResponseL2.builder()
                                .pvPower(i.getPac())
                                .gridpower(i.getPac())
//                        .relayPower(i.getRelayPower())
                                .build());
                    }
                } catch (ParseException e) {
                    LOGGER.error(e.getMessage(), e);
                }

                inverterValuesOverTime.put(i.getSn(), valuesPower);

            });
        }
        List<DetailDataDTO> inverterPacBodyList = inverterPacBody != null && inverterPacBody.getPacDataDetail() != null ? inverterPacBody.getPacDataDetail() : null;
        if (inverterPacBodyList != null)
            Collections.sort(inverterPacBodyList, inverterPacDetailComparator());
        Calendar cal = Calendar.getInstance();
        cal.setTime(format.parse(time));
        String year = String.valueOf(cal.get(Calendar.YEAR));
        return MonitorAPIResponse.builder()
//              .currentValueToday(rtBody != null && rtBody.getResult() != null ? utility.round(rtBody.getResult().getAcpower(), rounding) : 0)
                .dailyYield(inverterSnBody != null && inverterSnBody.getDetailedData() != null ? utility.round(inverterSnBody.getDetailedData().getEday(), rounding) : 0)
                .monthlyYield(inverterSnBody != null && inverterSnBody.getDetailedData() != null ? utility.round(inverterSnBody.getDetailedData().getEmonth(), rounding) : 0)
                //capacity is in kw unit for goodwe
                .sytemSize(inverterSnBody != null && inverterSnBody.getDetailedData() != null ? utility.round(inverterSnBody.getDetailedData().getCapacity(), rounding) : 0)
                .grossYield(inverterSnBody != null && inverterSnBody.getDetailedData() != null ? utility.round(inverterSnBody.getDetailedData().getEtotal(), rounding) : 0)
                .peakValue(inverterPacBody != null && inverterPacBody.getPacDataDetail() != null ? utility.round(inverterPacBody.getPacDataDetail().stream().mapToDouble(k -> k.getPac()).max().orElse(0), rounding) : 0)
                .annualYield(rtYearlyBody != null && rtYearlyBody.getPvData() != null ? utility.round((rtYearlyBody.getPvData().stream().filter(pv -> pv.getX().equals(year)).mapToDouble(pv -> pv.getY()).findFirst().getAsDouble()), rounding) : 0)
                .currentValue(inverterPacBodyList != null && !inverterPacBodyList.isEmpty() ? utility.round(inverterPacBodyList.stream().findFirst().get().getPac(), rounding) : 0)
                .inverterValuesOverTime(inverterValuesOverTime)
                .build();
    }

    @Override
    public boolean savesBulk() {
        return false;
    }

}
