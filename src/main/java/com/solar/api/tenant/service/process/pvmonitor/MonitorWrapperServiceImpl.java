package com.solar.api.tenant.service.process.pvmonitor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.helper.Utility;
import com.solar.api.saas.model.attribute.PortalAttributeValueSAAS;
import com.solar.api.saas.module.com.solar.batch.service.EGaugeService;
import com.solar.api.saas.module.com.solar.batch.service.StageMonitorService;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.saas.service.EmailService;
import com.solar.api.saas.service.PortalAttributeSAASService;
import com.solar.api.tenant.mapper.billing.PowerMonitorPercentileDTO;
import com.solar.api.tenant.mapper.billingCredits.BillingCreditsPMDTO;
import com.solar.api.tenant.mapper.projection.MasterProjectionDataWrapper;
import com.solar.api.tenant.mapper.projection.ProjectionDataWrapper;
import com.solar.api.tenant.mapper.projection.ProjectionTileDTO;
import com.solar.api.tenant.mapper.pvmonitor.*;
import com.solar.api.tenant.mapper.tiles.dataexport.powermonitoring.DataExportPMPaginationTile;
import com.solar.api.tenant.mapper.tiles.dataexport.powermonitoring.DataExportPMTile;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.model.billingCredits.BillingCredits;
import com.solar.api.tenant.model.dataexport.powermonitoring.DataDTO;
import com.solar.api.tenant.model.dataexport.powermonitoring.ExportDTO;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.model.pvmonitor.MonitorReading;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingDaily;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingMonthWise;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingYearWise;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinitionDTO;
import com.solar.api.tenant.model.stage.monitoring.InverterSubscriptionDTO;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.repository.*;
import com.solar.api.tenant.repository.stage.monitoring.ExtDataStageDefinitionRepository;
import com.solar.api.tenant.service.BillingCreditsService;
import com.solar.api.tenant.service.preferences.TenantConfigService;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.MonitoringDashboardWidgetService;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.YieldDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.solar.api.helper.Utility.SYSTEM_DATE_TIME_FORMAT;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
public class MonitorWrapperServiceImpl implements MonitorWrapperService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static int LINE_GRAPH_INCREMENT = 30;
    @Value("${app.profile}")
    private String appProfile;
    @Autowired
    private ExternalAPIFactory externalAPIFactory;
    @Autowired
    private MonitorReadingRepository readingRepository;
    @Autowired
    private MonitorReadingDailyRepository dailyRepository;
    @Autowired
    private Utility utility;
    @Autowired
    private CustomerSubscriptionRepository customerSubscriptionRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private TenantConfigService tenantConfigService;
    @Autowired
    private ExtDataStageDefinitionService extDataStageDefinitionService;
    @Autowired
    private StageMonitorService stageMonitorService;
    @Autowired
    private ExtDataStageDefinitionRepository extDataStageDefinitionRepository;
    @Autowired
    private PortalAttributeSAASService portalAttributeSAASService;
    @Autowired
    private PhysicalLocationRepository physicalLocationRepository;
    @Autowired
    private BillingCreditsService billingCreditsService;
    @Autowired
    private BillingCreditsRepository billingCreditsRepository;
    @Autowired
    private EGaugeService eGaugeService;
    @Autowired
    private MonitorReadingHistoricDates historicDates;

    @Autowired
    private MonitoringDashboardWidgetService monitorWidgetService;

    @Autowired
    private MonitoringDashboardYearWiseRepository monitoringDashboardYearWiseRepository;
    @Autowired
    private MonitoringDashboardMonthWiseRepository monitoringDashboardMonthWiseRepository;

    @Override
    public MonitorAPIAuthBody addMonitorReadings(MonitorAPIAuthBody monitorAPIAuthBody) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Utility.SYSTEM_DATE_TIME_FORMAT);
        try {
            Date dateTime = dateFormat.parse(monitorAPIAuthBody.getTime());
            while (dateTime.before(new Date())) {
                try {
                    saveCurrentData(monitorAPIAuthBody);
                } catch (UnsupportedEncodingException | JsonProcessingException e) {
                    LOGGER.error(e.toString());
                }
                dateTime = Utility.addDays(dateTime, 1);
                monitorAPIAuthBody.setTime(dateFormat.format(dateTime));
            }
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public MonitorAPIAuthBody addMonitorReadingsMongo(MonitorAPIAuthBody monitorAPIAuthBody) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Utility.SYSTEM_DATE_TIME_FORMAT);
        try {
            saveCurrentDataMongo(monitorAPIAuthBody);
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
        return null;
    }

    @Override
    public void addMonitorReadingsMongo(List<String> subscriptionIds, List<String> monitorPlatforms, String fromDateTime, String toDateTime, Boolean instantaneousCall) {
        Optional<TenantConfig> tenantConfig;
        try {
            tenantConfig = tenantConfigService.findByParameter(Constants.TENANT_CONFIG_CATEGORY.PARAM_ISMONGOENABLED);
            if (tenantConfig.isPresent() && tenantConfig.get().getText().equals("1")) {
                if (subscriptionIds != null && !subscriptionIds.isEmpty()) {
                    addMonitorReadingsMongo(MonitorAPIAuthBody.builder().subscriptionIdsMongo(subscriptionIds)
                            .fromDateTime(fromDateTime)
                            .toDateTime(toDateTime)
                            .instantaneousCall(instantaneousCall).build());
                } else if (monitorPlatforms != null && !monitorPlatforms.isEmpty()) {
                    for (String monitorPlatform : monitorPlatforms) {
                        subscriptionIds = stageMonitorService.getAllSubscriptions(monitorPlatform).stream()
                                .map(ExtDataStageDefinition::getSubsId).collect(Collectors.toList());
                        addMonitorReadingsMongo(MonitorAPIAuthBody.builder().subscriptionIdsMongo(subscriptionIds)
                                .fromDateTime(fromDateTime)
                                .toDateTime(toDateTime)
                                .instantaneousCall(instantaneousCall).build());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public MonitorAPIAuthResponse getAuthData(Object... params) throws NoSuchAlgorithmException, UnsupportedEncodingException, JsonProcessingException {
        if (externalAPIFactory.get((String) params[0]) == null) {
            return null;
        }
        return externalAPIFactory.get((String) params[0]).getAuthData(params);
    }

    @Override
    public MonitorAPIResponse getCurrentWidgetData(MonitorAPIAuthBody body, boolean isRefresh) throws NoSuchAlgorithmException, ParseException, UnsupportedEncodingException, JsonProcessingException {
        MonitorAPIResponse responseWrapper = MonitorAPIResponse.builder()
                .sytemSize(0d)
                .currentValue(0d)
                .peakValue(0d)
                .dailyYield(0d)
                .monthlyYield(0d)
                .annualYield(0d)
                .grossYield(0d)
                .build();
        List<ExtDataStageDefinition> extDataStageDefinitionList = new ArrayList<>();
        List<CustomerSubscription> cs = new ArrayList<>();
        try {
            Optional<TenantConfig> tenantConfig = tenantConfigService.findByParameter(Constants.TENANT_CONFIG_CATEGORY.PARAM_ISMONGOENABLED);
            if (tenantConfig.isPresent() && tenantConfig.get().getText().equals("1")) {
                extDataStageDefinitionList = extDataStageDefinitionService.findAllBySubsIdIn(body.getSubscriptionIdsMongo());
                if (isRefresh) {
                    for (ExtDataStageDefinition ext : extDataStageDefinitionList) {
                        String mp = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.MP);
                        //pass true for solis widget data
                        if (externalAPIFactory.get(mp) == null) {
                            continue;
                        }
                        MonitorAPIResponse response = externalAPIFactory.get(mp).getCurrentData(ext, body.getTime(), true, null);
                        getResponse(responseWrapper, response);
                    }
                    Double systemSize = extDataStageDefinitionList.stream().mapToDouble(data -> convertSystemSizeToDouble(data.getSystemSize())).sum();
                    responseWrapper.setPeakValue(responseWrapper.getPeakValue() / body.getSubscriptionIdsMongo().size());
                    responseWrapper.setAnnualYield(responseWrapper.getAnnualYield() / 1000);
                    responseWrapper.setGrossYield(responseWrapper.getGrossYield() / 1000);
                    responseWrapper.setSytemSize(systemSize);
                    return responseWrapper;
                }
                for (ExtDataStageDefinition extDataStageDefinition : extDataStageDefinitionList) {
                    MonitorAPIResponse response = getWidgetDataMongoForUser(extDataStageDefinition, body);
                    getResponse(responseWrapper, response);
                }
            } else {
                cs = customerSubscriptionRepository.findAllByIdInFetchCustomerSubscriptionMappings(body.getSubscriptionIds());
                if (isRefresh) {
                    for (CustomerSubscription customerSubscription : cs) {
                        String mp = customerSubscription.getCustomerSubscriptionMappings().stream().filter(l -> "MP".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue();
                        //pass true for solis widget data
                        if (externalAPIFactory.get(mp) == null) {
                            continue;
                        }
                        MonitorAPIResponse response = externalAPIFactory.get(mp).getCurrentData(customerSubscription, body.getTime(), true, null);
                        getResponse(responseWrapper, response);
                    }

                    responseWrapper.setPeakValue(responseWrapper.getPeakValue() / body.getSubscriptionIds().size());
                    responseWrapper.setAnnualYield(responseWrapper.getAnnualYield() / 1000);
                    responseWrapper.setGrossYield(responseWrapper.getGrossYield() / 1000);
                    return responseWrapper;
                }
                for (CustomerSubscription customerSubscription : cs) {
                    MonitorAPIResponse response = getWidgetDataForUser(customerSubscription, body);
                    getResponse(responseWrapper, response);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        if (extDataStageDefinitionList.size() > 0 || cs.size() > 0) {
            /*if (isRefresh) {
                for (CustomerSubscription customerSubscription : cs) {
                    String mp = customerSubscription.getCustomerSubscriptionMappings().stream().filter(l -> "MP".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue();
                    //pass true for solis widget data
                    MonitorAPIResponse response = externalAPIFactory.get(mp).getCurrentData(customerSubscription, body.getTime(), true);
                    getResponse(responseWrapper, response);
                }

                responseWrapper.setPeakValue(responseWrapper.getPeakValue() / body.getSubscriptionIds().size());
                responseWrapper.setAnnualYield(responseWrapper.getAnnualYield() / 1000);
                responseWrapper.setGrossYield(responseWrapper.getGrossYield() / 1000);
                return responseWrapper;
            }

            for (CustomerSubscription customerSubscription : cs) {
                MonitorAPIResponse response = getWidgetDataForUser(customerSubscription, body);
                getResponse(responseWrapper, response);
            }*/

//            responseWrapper.setPeakValue(responseWrapper.getPeakValue() / body.getSubscriptionIds().size());
            int rounding = utility.getCompanyPreference().getRounding();
            responseWrapper.setSytemSize(utility.round(responseWrapper.getSytemSize(), rounding));
            responseWrapper.setCurrentValue(utility.round(responseWrapper.getCurrentValue(), rounding));
            responseWrapper.setPeakValue(utility.round(responseWrapper.getPeakValue(), rounding));
            responseWrapper.setDailyYield(utility.round(responseWrapper.getDailyYield(), rounding));
            responseWrapper.setMonthlyYield(utility.round(responseWrapper.getMonthlyYield(), rounding));
//        responseWrapper.setAnnualYield(utility.round(responseWrapper.getAnnualYield(), rounding));
//        responseWrapper.setGrossYield(utility.round(responseWrapper.getGrossYield(), rounding));
            responseWrapper.setAnnualYield(utility.round(responseWrapper.getAnnualYield() / 1000, rounding));
            responseWrapper.setGrossYield(utility.round(responseWrapper.getGrossYield() / 1000, rounding));
            if (cs.size() > 0) {
                responseWrapper.setTreesPlanted(utility.round(getTreesPlantedLegacy(cs), rounding));
                responseWrapper.setCo2Reduction(utility.round(getCO2ReductLegacy(cs), rounding));
            } else {
                responseWrapper.setTreesPlanted(utility.round(getTreesPlanted(extDataStageDefinitionList), rounding));
                responseWrapper.setCo2Reduction(utility.round(getCO2Reduct(extDataStageDefinitionList), rounding));
            }
        }
        if (extDataStageDefinitionList.size() > 0) {
            responseWrapper.setPeakValue(responseWrapper.getPeakValue() / body.getSubscriptionIdsMongo().size());
        } else if (cs.size() > 0) {
            responseWrapper.setPeakValue(responseWrapper.getPeakValue() / body.getSubscriptionIds().size());
        }
        return responseWrapper;
    }

    @Override
    public MonitorAPIResponse getAllUsersCurrentWidgetData(Integer pageNumber, Integer pageSize) throws NoSuchAlgorithmException, ParseException, UnsupportedEncodingException, JsonProcessingException {
        MonitorAPIResponse responseWrapper = MonitorAPIResponse.builder().build();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        try {
            WidgetCustomResponse widgetCustomResponse = WidgetCustomResponse.builder()
                    .sytemSize(0d)
                    .currentValue(0d)
                    .peakValue(0d)
                    .dailyYield(0d)
                    .monthlyYield(0d)
                    .annualYield(0d)
                    .grossYield(0d)
                    .build();
            List<WidgetCustomResponse> responseWrappers = new ArrayList<>();
            List<PortalAttributeValueSAAS> portalAttributeValueSAAS = portalAttributeSAASService.findByPortalAttributeName(AppConstants.MP_PORTAL_ATTR_NAME);
//        List<com.solar.api.tenant.model.user.User> users = userRepository.findCustomerInverterSubs(subscriptionTypes);
            List<String> mps = portalAttributeValueSAAS != null ?
                    portalAttributeValueSAAS.stream().map(PortalAttributeValueSAAS::getAttributeValue).collect(toList()) : null;
            List<InverterSubscriptionDTO> inverterSubscriptionDTOS = extDataStageDefinitionRepository.findAllCustomerSubscriptionDTO(mps);
//        List<Long> acctIds = inverterSubscriptionDTOS.stream().map(InverterSubscriptionDTO::getAcctId).collect(toList());
            List<String> subsIdsMongo = inverterSubscriptionDTOS.stream().map(InverterSubscriptionDTO::getSubsId).collect(toList());
            Page<MonitorReadingCustomResponse> pageResult = readingRepository.getLastRecordByUserAndSubscription(subsIdsMongo, pageable);
            List<MonitorReadingCustomResponse> monitorReadingCustomList = pageResult.getContent();
            int rounding = utility.getCompanyPreference().getRounding();
            if (monitorReadingCustomList.size() == 0) {
                widgetCustomResponse.setCustomerName("");
                widgetCustomResponse.setPeakValue(0d);
                widgetCustomResponse.setSytemSize(0d);
                widgetCustomResponse.setCurrentValue(0d);
                widgetCustomResponse.setDailyYield(0d);
                widgetCustomResponse.setMonthlyYield(0d);
                widgetCustomResponse.setAnnualYield(0d);
                widgetCustomResponse.setGrossYield(0d);
                //widgetCustomResponse.setTreesPlanted(utility.round(getTreesPlantedFactor(subscriptionIdslist), rounding));
                //widgetCustomResponse.setCo2Reduction(utility.round(getCO2Reduction(subscriptionIdslist), rounding));
                responseWrappers.add(widgetCustomResponse);
            }
            for (MonitorReadingCustomResponse monitorReadingCustom : monitorReadingCustomList) {
                widgetCustomResponse = WidgetCustomResponse.builder().build();
                InverterSubscriptionDTO inverterSubscriptionDTO = getInverterSubscriptionDTO(monitorReadingCustom, inverterSubscriptionDTOS);
                widgetCustomResponse.setCustomerName(inverterSubscriptionDTO.getCustomerName());
                widgetCustomResponse.setImage(inverterSubscriptionDTO.getImage());
                widgetCustomResponse.setInvertedBrand(inverterSubscriptionDTO.getInvertedBrand());
                widgetCustomResponse.setMonitoringBrand(inverterSubscriptionDTO.getMonitoringBrand());
                widgetCustomResponse.setSubscriptionName(inverterSubscriptionDTO.getSubscriptionName());
                widgetCustomResponse.setSubsId(inverterSubscriptionDTO.getSubsId());
                widgetCustomResponse.setSystemSize(inverterSubscriptionDTO.getSystemSize());
                widgetCustomResponse.setSiteLocation(inverterSubscriptionDTO.getSiteLocation());

                widgetCustomResponse.setEntityId(inverterSubscriptionDTO.getEntityId());
                widgetCustomResponse.setProductId(inverterSubscriptionDTO.getProductId());
                widgetCustomResponse.setVariantId(inverterSubscriptionDTO.getVariantId());
                widgetCustomResponse.setUserId(inverterSubscriptionDTO.getAcctId());
//            widgetCustomResponse.setPeakValue(monitorReadingCustom.getPeakValue() / subscriptionIdslist.size());
//            widgetCustomResponse.setSytemSize(utility.round(monitorReadingCustom.getSytemSize(), rounding));
                widgetCustomResponse.setCurrentValue(monitorReadingCustom.getCurrentValue() != null ? utility.round(monitorReadingCustom.getCurrentValue(), rounding) : 0d);
//            widgetCustomResponse.setPeakValue(utility.round(monitorReadingCustom.getPeakValue(), rounding));
                widgetCustomResponse.setDailyYield(monitorReadingCustom.getDailyYield() != null ? utility.round(monitorReadingCustom.getDailyYield(), rounding) : 0d);
                widgetCustomResponse.setMonthlyYield(monitorReadingCustom.getMonthlyYield() != null ? utility.round(monitorReadingCustom.getMonthlyYield(), rounding) : 0d);
                widgetCustomResponse.setAnnualYield(monitorReadingCustom.getAnnualYield() != null ? utility.round(monitorReadingCustom.getAnnualYield() / 1000, rounding) : 0d);
                widgetCustomResponse.setGrossYield(monitorReadingCustom.getGrossYield() != null ? utility.round(monitorReadingCustom.getGrossYield() / 1000, rounding) : 0d);
                //widgetCustomResponse.setTreesPlanted(utility.round(getTreesPlantedFactor(subscriptionIdslist), rounding));
                //widgetCustomResponse.setCo2Reduction(utility.round(getCO2Reduction(subscriptionIdslist), rounding));
                responseWrappers.add(widgetCustomResponse);
            }
            responseWrapper.setTotalPages(pageResult.getTotalPages());
            responseWrapper.setTotalElements(pageResult.getTotalElements());
            responseWrapper.setWidgetCustomResponseList(responseWrappers);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return responseWrapper;
    }

    private void getResponse(MonitorAPIResponse responseWrapper, MonitorAPIResponse response) {
        if (response != null) {
            responseWrapper.setSytemSize(responseWrapper.getSytemSize() + (response.getSytemSize() == null ? 0 :
                    response.getSytemSize()));
            responseWrapper.setCurrentValue(responseWrapper.getCurrentValue() + (response.getCurrentValue() == null ? 0 :
                    response.getCurrentValue()));
            responseWrapper.setPeakValue(responseWrapper.getPeakValue() + (response.getPeakValue() == null ? 0 :
                    response.getPeakValue()));
            responseWrapper.setDailyYield(responseWrapper.getDailyYield() + (response.getDailyYield() == null ? 0 :
                    response.getDailyYield()));
            responseWrapper.setMonthlyYield(responseWrapper.getMonthlyYield() + (response.getMonthlyYield() == null ? 0 :
                    response.getMonthlyYield()));
            responseWrapper.setAnnualYield(responseWrapper.getAnnualYield() + (response.getAnnualYield() == null ? 0 :
                    response.getAnnualYield()));
            responseWrapper.setGrossYield(responseWrapper.getGrossYield() + (response.getGrossYield() == null ? 0 :
                    response.getGrossYield()));
        }
    }

    private MonitorAPIResponse getWidgetDataForUser(CustomerSubscription cs, MonitorAPIAuthBody body) throws ParseException {
        //User user = userService.findByUserName(userName);
        MonitorReading reading;
        //String inverterNumber = cs.getCustomerSubscriptionMappings().stream().filter(l-> "INVRT".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue();
        if (body.getTime() == null) {
            reading = readingRepository.getLastRecord(cs.getId());
        } else {
            reading = readingRepository.findBySubscriptionIdAndTime(cs.getId(), formatDateTime.parse(body.getTime()));
        }
        return reading == null ? new MonitorAPIResponse() : MonitorAPIResponse.builder()
                .id(reading.getId())
                .inverterNumber(reading.getInverterNumber())
                .site(reading.getSite())
                .sytemSize(reading.getSytemSize())
                .currentValueToday(reading.getCurrentValueToday())
                .currentValue(reading.getCurrentValue())
                .currentValueRunning(reading.getCurrentValueRunning())
                .peakValue(reading.getPeakValue())
                .dailyYield(reading.getDailyYield())
                .monthlyYield(reading.getMonthlyYield())
//                .annualYield(reading.getAnnualYield() / 1000)
//                .grossYield(reading.getGrossYield() / 1000)
                .annualYield(reading.getAnnualYield())
                .grossYield(reading.getGrossYield())
                .dateTime(reading.getTime())
                .build();
    }

//    private String getCustomerName(MonitorReadingCustomResponse reading, List<User> users) throws ParseException {
//        //MonitorReading reading= null;
//        User user = users.stream().filter(u -> u.getAcctId().equals(reading.getUserId())).findFirst().get();
//        String customerName = user.getFirstName() != null ? user.getFirstName() : "";
//        customerName += user.getLastName() != null ? " " + user.getLastName() : "";
//
//        return customerName;
//

//    }

    private InverterSubscriptionDTO getInverterSubscriptionDTO(MonitorReadingCustomResponse reading, List<InverterSubscriptionDTO> inverterSubscriptionDTOs) throws ParseException {
        InverterSubscriptionDTO inverterSubscriptionDTO = inverterSubscriptionDTOs.stream().filter(invSub -> invSub.getSubsId().equals(reading.getSubscriptionIds())).findFirst().get();
        String customerName = inverterSubscriptionDTO.getCustomerName() != null ? inverterSubscriptionDTO.getCustomerName() : "";
        String mp = inverterSubscriptionDTO.getMonitoringBrand();
        String image = inverterSubscriptionDTO.getImage();
        String variantName = inverterSubscriptionDTO.getInvertedBrand();
        String csName = inverterSubscriptionDTO.getSubscriptionName() != null ? inverterSubscriptionDTO.getSubscriptionName() : "";
        String systemSize = inverterSubscriptionDTO.getSystemSize() != null ? inverterSubscriptionDTO.getSystemSize() : "";
        Long acctId = inverterSubscriptionDTO.getAcctId();
        Long entityId = inverterSubscriptionDTO.getEntityId();
        String productId = inverterSubscriptionDTO.getProductId();
        String variantId = inverterSubscriptionDTO.getVariantId();
        Optional<PhysicalLocation> ploc = physicalLocationRepository.findById(inverterSubscriptionDTO.getPlocId());
        String siteLocation = "";
        if (ploc.isPresent()) {
            PhysicalLocation physicalLocation = ploc.get();
            String ext1 = physicalLocation.getExt1();
            String ext2 = physicalLocation.getExt2();
            String ext3 = physicalLocation.getAdd3();
            //city                        state                           country
            siteLocation = ext1 != null ? ext1 : "" + " " + ext2 != null ? ext2 : "" + " " + ext3 != null ? ext3 : "";
        }
        return InverterSubscriptionDTO.builder().image(image).customerName(customerName).siteLocation(siteLocation)
                .subscriptionName(csName).invertedBrand(variantName).monitoringBrand(mp).systemSize(systemSize).acctId(acctId).productId(productId).entityId(entityId).variantId(variantId).build();

    }

    @Override
    public GraphDataWrapper getCurrentGraphData(MonitorAPIAuthBody body, boolean isComparison, boolean isSubscriptionComparison) throws ParseException {
        Map<Long, List<MonitorAPIResponse>> map = new TreeMap<>();
        Map<String, List<MonitorAPIResponse>> cumulativeMap = new TreeMap<>();
        List<CustomerSubscription> customerSubscriptionList = null;
        List<CustomerSubscription> customerSubscriptions = null;
        Map<String, List<MonitorAPIResponse>> mapMongo = new TreeMap<>();
        List<ExtDataStageDefinition> extDataStageDefinitionList = null;
        Map<String, List<MonitorAPIResponse>> cumulativeMongoMap = new TreeMap<>();
        /**
         * For ProjectIds Logic
         * Since projectIds are List<String>
         */
        Map<String, List<MonitorAPIResponse>> mapString = new TreeMap<>();
        Map<String, List<MonitorAPIResponse>> cumulativeMapString = new TreeMap<>();

        try {
            Optional<TenantConfig> tenantConfig = tenantConfigService.findByParameter(Constants.TENANT_CONFIG_CATEGORY.PARAM_ISMONGOENABLED);
            /**
             * DEFAULT for Mongo
             * isComparison = false
             * isSubscriptionComparison = true
             */
            if (!isComparison || isSubscriptionComparison) {
                if (tenantConfig.isPresent() && tenantConfig.get().getText().equals("1")) {
                    extDataStageDefinitionList = extDataStageDefinitionService.findAllBySubsIdIn(body.getSubscriptionIdsMongo());
                    for (ExtDataStageDefinition ext : extDataStageDefinitionList) {
                        mapMongo.put(ext.getSubsId(), getGraphDataForMongoSubscription(ext, body));
                    }
                } else {
                    customerSubscriptionList = customerSubscriptionRepository.findAllByIdInFetchCustomerSubscriptionMappings(body.getSubscriptionIds());
                    customerSubscriptions = getFilteredCustomerSubCList(customerSubscriptionList);
                    for (CustomerSubscription customerSubscription : customerSubscriptions) {
                        map.put(customerSubscription.getId(), getGraphDataForSubscription(customerSubscription, body));
                    }
                }
            }

            /**
             * DEFAULT for Data
             * isComparison = false
             * isSubscriptionComparison = true
             */
            if (!isComparison) {
                List<Date> dateTimes = null;
                Map<Date, MonitorReadingDailyDTO> dateValues = new TreeMap<>();
                if (tenantConfig.isPresent() && tenantConfig.get().getText().equals("1")) {
                    dateTimes = mapMongo.values().stream().flatMap(m -> m.stream()).map(MonitorAPIResponse::getDateTime)
                            .distinct().collect(toList());
                    dateTimes.forEach(dt -> {
                        List<String> inverterNumbers = mapMongo.values().stream().flatMap(m -> m.stream()).filter(m -> m.getDateTime().equals(dt)).filter(m -> m.getInverterNumber() != null).map(MonitorAPIResponse::getInverterNumber).collect(toList());
                        Double yieldValueSum = mapMongo.values().stream().flatMap(m -> m.stream()).filter(m -> m.getDateTime().equals(dt)).mapToDouble(MonitorAPIResponse::getYieldValue).sum();
                        MonitorReadingDailyDTO monitorReadingDailyDTO = MonitorReadingDailyDTO.builder().build();
                        monitorReadingDailyDTO.setInverterNumbers(inverterNumbers);
                        monitorReadingDailyDTO.setYieldValue(yieldValueSum);
                        dateValues.put(dt, monitorReadingDailyDTO);
                    });
                } else {
                    dateTimes = map.values().stream().flatMap(m -> m.stream()).map(MonitorAPIResponse::getDateTime)
                            .distinct().collect(toList());
                    dateTimes.forEach(dt -> {
                        List<String> inverterNumbers = map.values().stream().flatMap(m -> m.stream())
                                .filter(m -> m.getDateTime().equals(dt)).filter(m -> m.getInverterNumber() != null)
                                .map(MonitorAPIResponse::getInverterNumber).collect(toList());
                        Double yieldValueSum = map.values().stream().flatMap(m -> m.stream())
                                .filter(m -> m.getDateTime().equals(dt)).mapToDouble(MonitorAPIResponse::getYieldValue).sum();
                        MonitorReadingDailyDTO monitorReadingDailyDTO = MonitorReadingDailyDTO.builder().build();
                        monitorReadingDailyDTO.setInverterNumbers(inverterNumbers);
                        monitorReadingDailyDTO.setYieldValue(yieldValueSum);
                        dateValues.put(dt, monitorReadingDailyDTO);
                    });
                }

                List<MonitorAPIResponse> responses = new ArrayList<>();
                for (Map.Entry<Date, MonitorReadingDailyDTO> set : dateValues.entrySet()) {
                    set.getKey().getTimezoneOffset();
                    responses.add(MonitorAPIResponse.builder()
                            .yieldValue(set.getValue().getYieldValue())
                            .inverterNumbers(set.getValue().getInverterNumbers())
                            .dateTime(set.getKey())
                            .build());
                }
                cumulativeMap.put(String.valueOf(-1L), responses);
                return GraphDataWrapper.builder()
                        .graphData(cumulativeMap)
                        .xAxis(getXAxisLabels(body.getTime()))
                        .build();
            } else {
                //if comparison is true and type is subscription then execute this block
                if (isSubscriptionComparison) {
                    List<MonitorAPIResponse> list = null;
                    List<Date> dateTimes = null;
                    if (tenantConfig.isPresent() && tenantConfig.get().getText().equals("1")) {
                        for (ExtDataStageDefinition ext : extDataStageDefinitionList) {
//                            list = map.get(ext.getSubsId());
                            list = mapMongo.put(ext.getSubsId(), getGraphDataForMongoSubscription(ext, body));
                            dateTimes =
                                    mapMongo.values().stream().flatMap(m -> m.stream())
                                            .map(MonitorAPIResponse::getDateTime).distinct().collect(toList());
                            Map<Date, MonitorReadingDailyDTO> dateValues = getInverterAndYieldSum(dateTimes, list);
                            List<MonitorAPIResponse> responses = new ArrayList<>();
                            for (Map.Entry<Date, MonitorReadingDailyDTO> set : dateValues.entrySet()) {
                                set.getKey().getTimezoneOffset();
                                responses.add(MonitorAPIResponse.builder()
                                        .yieldValue(set.getValue().getYieldValue())
                                        .inverterNumbers(set.getValue().getInverterNumbers())
                                        .dateTime(set.getKey())
                                        .detailData(set.getValue().getDataDTO())
                                        .build());

                            }
                            cumulativeMongoMap.put(ext.getSubsId(), responses);
                        }
                    } else {
                        for (CustomerSubscription customerSubscription : customerSubscriptions) {
                            Long csId = customerSubscription.getId();
                            list = map.get(csId);
                            dateTimes =
                                    map.values().stream().flatMap(m -> m.stream()).map(MonitorAPIResponse::getDateTime).distinct().collect(toList());
                            Map<Date, MonitorReadingDailyDTO> dateValues = getInverterAndYieldSum(dateTimes, list);
                            List<MonitorAPIResponse> responses = new ArrayList<>();
                            for (Map.Entry<Date, MonitorReadingDailyDTO> set : dateValues.entrySet()) {
                                set.getKey().getTimezoneOffset();
                                responses.add(MonitorAPIResponse.builder()
                                        .yieldValue(set.getValue().getYieldValue())
                                        .inverterNumbers(set.getValue().getInverterNumbers())
                                        .dateTime(set.getKey())
                                        .detailData(set.getValue().getDataDTO())
                                        .build());
                            }
                            cumulativeMap.put(String.valueOf(csId), responses);
                        }
                    }
                }
                //comparison is true and isSubscriptionComparison false then execute this block for user
                if (!isSubscriptionComparison) {
                    List<String> subscriptionIds = body.getSubscriptionIdsMongo() == null ? body.getProjectIds() : body.getSubscriptionIdsMongo();
//                        == null? body.getSubscriptionIdsMongo() : body.getProjectIds();
                    /*if (tenantConfig.isPresent() && tenantConfig.get().getText().equals("0")) {
                        customerSubscriptions = customerSubscriptionRepository.findAllByUserIdInFetchCustomerSubscriptionMappings(body.getUserIds());
                    }*/
//                List filteredRateCodes = getFilterRateCodes(customerSubscriptions).keySet().stream().collect(Collectors.toList()); //have to comment this line
                    extDataStageDefinitionList = extDataStageDefinitionService.findAllBySubsIdIn(subscriptionIds);
                    Map<String, List<ExtDataStageDefinition>> extDataStageDefinitionMap = extDataStageDefinitionList.stream().collect(groupingBy(ExtDataStageDefinition::getRefId, toList()));

                    for (String projectId : extDataStageDefinitionMap.keySet()) {
                        for (ExtDataStageDefinition ext : extDataStageDefinitionMap.get(projectId)) {
                            List<MonitorAPIResponse> subscriptionMongoGraphData = getGraphDataForMongoSubscription(ext, body);
                            if (mapMongo.get(projectId) != null) {
                                List<MonitorAPIResponse> exsistingProjectSubGraphDataList = mapMongo.get(projectId);
                                exsistingProjectSubGraphDataList.addAll(subscriptionMongoGraphData);
                                mapMongo.put(projectId, exsistingProjectSubGraphDataList);
                            } else {
                                mapMongo.put(projectId, subscriptionMongoGraphData);
                            }
//                        mapString.put(ext.getRefId(), getGraphDataForUser(ext.getSubsId(), body));
                        }
                    }
                    for (String projectId : mapMongo.keySet()) {
                        List<MonitorAPIResponse> list = mapMongo.get(projectId);
                        List<Date> dateTimes = mapMongo.values().stream().flatMap(m -> m.stream())
                                .map(MonitorAPIResponse::getDateTime).distinct().collect(toList());
                        Map<Date, MonitorReadingDailyDTO> monthValues = getInverterAndYieldSum(dateTimes, list);

                        List<MonitorAPIResponse> responses = new ArrayList<>();
                        if (!monthValues.isEmpty()) {
                            for (Map.Entry<Date, MonitorReadingDailyDTO> set : monthValues.entrySet()) {
                                set.getKey().getTimezoneOffset();
                                responses.add(MonitorAPIResponse.builder()
                                        .yieldValue(set.getValue().getYieldValue() != null ? set.getValue().getYieldValue() : null)
                                        .inverterNumbers(set.getValue().getInverterNumbers() != null ? set.getValue().getInverterNumbers() : null)
                                        .dateTime(set.getKey())
                                        .detailData(set.getValue().getDataDTO() != null ? set.getValue().getDataDTO() : null)
                                        .build());
                            }
                        }
                        cumulativeMapString.put(projectId, responses);
                    }
                }
            }

        } catch (Exception ex) {
            ex.getMessage();
        }
        return GraphDataWrapper.builder()
                .graphData(cumulativeMapString.isEmpty() ? cumulativeMongoMap : cumulativeMapString)
                .xAxis(getXAxisLabels(body.getTime()))
                .build();
    }

    /*private List<MonitorAPIResponse> getGraphDataForUser(String extSubId, MonitorAPIAuthBody body) throws ParseException {
        List<MonitorReading> daysData = new ArrayList<MonitorReading>();
        List<MonitorAPIResponse> responses = new ArrayList<>();
        CustomerSubscription customerSubscriptions = customerSubscriptionRepository.findByExtSubsId(extSubId);
        Map<String, List<CustomerSubscription>> customerSubscriptionsMPMap =
                customerSubscriptions.getCustomerSubscriptionMappings().stream()
                        .filter(csmapping -> "MP".equalsIgnoreCase(csmapping.getRateCode()))
                        .map(csm -> new AbstractMap.SimpleEntry<>(customerSubscriptions, csm.getValue()))
                        .collect(groupingBy(
                                Map.Entry::getValue,
                                Collectors.mapping(Map.Entry::getKey, toList())));

        for (Map.Entry<String, List<CustomerSubscription>> cs : customerSubscriptionsMPMap.entrySet()) {
            List<MonitorReading> monitorReadingDaysData = externalAPIFactory.get(cs.getKey())
                    .getMonitorReadingDataForUserComparison(extSubId, body, cs.getValue());
            daysData.addAll(monitorReadingDaysData);
        }
        List<MonitorReading> finalDaysData = daysData;
        getMaxDateTimesForXAxis(body.getTime()).forEach(d -> {
//            Optional<MonitorReading> data = finalDaysData.stream().filter(m ->
//                    m.getTime().getTime() == d.getTime()).findFirst();
            Map<Long, List<MonitorReading>> dataMap = finalDaysData.stream().filter(m ->
                    m.getTime().getTime() == d.getTime()).collect(groupingBy(MonitorReading::getSubscriptionId));

            if (dataMap.size() > 0) {
                dataMap.forEach((subscriptionIdKey, monitorReadingValues) -> {
                    Optional<MonitorReading> data = monitorReadingValues.stream().findFirst();
                    if (data.isPresent()) {
                        responses.add(MonitorReadingMapper.toMonitorAPIResponse(data.get()));
                    } else {
                        responses.add(MonitorAPIResponse.builder().dateTime(d).yieldValue(0d).build());
                    }
                });
            } else {
                responses.add(MonitorAPIResponse.builder().dateTime(d).yieldValue(0d).build());
            }

        });
        return responses;
    }*/

    private List<MonitorAPIResponse> getGraphDataForSubscription(CustomerSubscription cs, MonitorAPIAuthBody body) throws ParseException {
        String mp = cs.getCustomerSubscriptionMappings().stream().filter(l -> "MP".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue();
        List<MonitorReading> daysData = externalAPIFactory.get(mp).getMonitorReadingDataForCsComparison(body, cs);

        List<MonitorAPIResponse> responses = new ArrayList<>();
        List<MonitorReading> finalDaysData = daysData;
        getMaxDateTimesForXAxis(body.getTime()).forEach(d -> {
            Optional<MonitorReading> data = finalDaysData.stream().filter(m ->
                    m.getTime().getTime() == d.getTime()).findFirst();
            if (data.isPresent()) {
                responses.add(MonitorReadingMapper.toMonitorAPIResponse(data.get()));
            } else {
                responses.add(MonitorAPIResponse.builder().dateTime(d).yieldValue(0d).build());
            }
        });
        return responses;
    }

    private List<Date> getMaxDateTimesForXAxis(String time) throws ParseException {
        Date date = formatDateTime.parse(time != null ? time : formatDateTime.format(new Date()));
//        Date tillDateTime = Utility.addMinutes(new Date(), 300);
//        boolean isToday = Utility.areInSameDay(date, tillDateTime);
        Date dateTime = Utility.getStartOfDate(date);
        Date tillDateTime = Utility.getEndOfDate(date);
        List<Date> dateTimes = new ArrayList<>();
        while (dateTime.before(tillDateTime)) {
            dateTimes.add(dateTime);
            dateTime = Utility.addMinutes(dateTime, LINE_GRAPH_INCREMENT);
        }
        return dateTimes;
    }

    private List<String> getXAxisLabels(String dateString) throws ParseException {
        return getMaxDateTimesForXAxis(dateString).stream().map(d -> new SimpleDateFormat("hh:mm").format(d)).collect(toList());
    }

    @Override
    public GraphDataMonthlyWrapper getMonthlyGraphData(MonitorAPIAuthBody body, boolean isComparison, boolean isSubscriptionComparison, boolean isWeekly) throws ParseException {
        Map<Long, List<MonitorAPIResponse>> map = new TreeMap<>();
        Map<String, List<MonitorAPIResponse>> cumulativeMap = new TreeMap<>();
        List<CustomerSubscription> customerSubscriptions = null;
        List<String> filteredRateCodes = null;
        List<ExtDataStageDefinition> extDataStageDefinitionList = null;
        Map<String, List<MonitorAPIResponse>> mongoMap = new TreeMap<>();
        Map<String, List<MonitorAPIResponse>> cumulativeMongoMap = new TreeMap<>();
//        List<CustomerSubscription> cs = customerSubscriptionRepository.findAllByIdInFetchCustomerSubscriptionMappings(body.getSubscriptionIds());
//
//        for (CustomerSubscription customerSubscription: cs) {
//            map.put(customerSubscription.getId(), getMonthlyGraphDataForSubscription(customerSubscription, body));
//        }
        Optional<TenantConfig> tenantConfig = null;
        try {
            tenantConfig = tenantConfigService.findByParameter(Constants.TENANT_CONFIG_CATEGORY.PARAM_ISMONGOENABLED);
            if (!isComparison || isSubscriptionComparison) {
                if (tenantConfig.isPresent() && tenantConfig.get().getText().equals("1")) {
                    extDataStageDefinitionList = extDataStageDefinitionService.findAllBySubsIdIn(body.getSubscriptionIdsMongo());
                    for (ExtDataStageDefinition ext : extDataStageDefinitionList) {
                        mongoMap.put(ext.getSubsId(), getMonthlyGraphDataForMongoSubscription(ext, body));
                    }
                } else {
                    customerSubscriptions = customerSubscriptionRepository.findAllByIdInFetchCustomerSubscriptionMappings(body.getSubscriptionIds());
                    filteredRateCodes = getFilterRateCodes(customerSubscriptions).keySet().stream().collect(toList());
                    for (CustomerSubscription customerSubscription : customerSubscriptions) {
                        map.put(customerSubscription.getId(), getMonthlyGraphDataForSubscription(customerSubscription, body, filteredRateCodes));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        if (!isComparison) {
            List<Date> dateTimes = null;
            Map<Date, MonitorReadingDailyDTO> dateValues = new TreeMap<>();
            if (tenantConfig.isPresent() && tenantConfig.get().getText().equals("1")) {
                dateTimes =
                        mongoMap.values().stream().flatMap(m -> m.stream()).map(MonitorAPIResponse::getDateTime).distinct().collect(toList());
                dateTimes.forEach(dt -> {
                    List<String> inverterNumbers = mongoMap.values().stream().flatMap(m -> m.stream()).filter(m -> m.getDateTime().equals(dt)).filter(m -> m.getInverterNumber() != null).map(MonitorAPIResponse::getInverterNumber).collect(toList());
                    Double yieldValueSum = mongoMap.values().stream().flatMap(m -> m.stream()).filter(m -> m.getDateTime().equals(dt)).mapToDouble(MonitorAPIResponse::getYieldValue).sum();
                    MonitorReadingDailyDTO monitorReadingDailyDTO = MonitorReadingDailyDTO.builder().build();
                    monitorReadingDailyDTO.setInverterNumbers(inverterNumbers);
                    monitorReadingDailyDTO.setYieldValue(yieldValueSum);
                    dateValues.put(dt, monitorReadingDailyDTO);
                });
            } else {
                dateTimes = map.values().stream().flatMap(m -> m.stream()).map(MonitorAPIResponse::getDateTime).distinct().collect(toList());
                dateTimes.forEach(dt -> {
                    List<String> inverterNumbers = map.values().stream().flatMap(m -> m.stream()).filter(m -> m.getDateTime().equals(dt)).filter(m -> m.getInverterNumber() != null).map(MonitorAPIResponse::getInverterNumber).collect(toList());
                    Double yieldValueSum = map.values().stream().flatMap(m -> m.stream()).filter(m -> m.getDateTime().equals(dt)).mapToDouble(MonitorAPIResponse::getYieldValue).sum();
                    MonitorReadingDailyDTO monitorReadingDailyDTO = MonitorReadingDailyDTO.builder().build();
                    monitorReadingDailyDTO.setInverterNumbers(inverterNumbers);
                    monitorReadingDailyDTO.setYieldValue(yieldValueSum);
                    dateValues.put(dt, monitorReadingDailyDTO);
                });
            }

            List<MonitorAPIResponse> responses = new ArrayList<>();
            for (Map.Entry<Date, MonitorReadingDailyDTO> set : dateValues.entrySet()) {
                responses.add(MonitorAPIResponse.builder()
                        .dateTime(set.getKey())
                        .yieldValue(set.getValue().getYieldValue())
                        .inverterNumbers(set.getValue().getInverterNumbers())
                        .build());
            }
//            cumulativeMap.put(String.valueOf(-1L), responses);
            updateCumulativeMap(responses, body.getTime(), cumulativeMap, isWeekly, String.valueOf(-1L));
            return GraphDataMonthlyWrapper.builder()
                    .monthlyGraphData(cumulativeMap)
                    .xAxis(isWeekly ? getBarXAxisLabelsForWeek(body.getTime()) : getBarXAxisLabels(body.getTime()))
                    .build();
        } else {
            //if comparison is true and type is subscription then execute this block
            if (isSubscriptionComparison) {
                List<Date> dateTimes = null;
                if (tenantConfig.isPresent() && tenantConfig.get().getText().equals("1")) {
                    for (ExtDataStageDefinition ext : extDataStageDefinitionList) {
                        List<MonitorAPIResponse> list = mongoMap.get(ext.getSubsId());
                        dateTimes =
                                mongoMap.values().stream().flatMap(m -> m.stream()).map(MonitorAPIResponse::getDateTime).distinct().collect(toList());
                        Map<Date, MonitorReadingDailyDTO> dateValues = getInverterAndYieldSum(dateTimes, list);
                        List<MonitorAPIResponse> responses = new ArrayList<>();
                        for (Map.Entry<Date, MonitorReadingDailyDTO> set : dateValues.entrySet()) {
                            responses.add(MonitorAPIResponse.builder()
                                    .dateTime(set.getKey())
                                    .yieldValue(set.getValue().getYieldValue())
                                    .inverterNumbers(set.getValue().getInverterNumbers())
                                    .detailData(set.getValue().getDataDTO())
                                    .build());
                        }
//                        cumulativeMongoMap.put(ext.getSubsId(), responses);
                        updateCumulativeMap(responses, body.getTime(), cumulativeMongoMap, isWeekly, ext.getSubsId());
                    }
                } else {
                    for (CustomerSubscription customerSubscription : customerSubscriptions) {
                        Long csId = customerSubscription.getId();
                        List<MonitorAPIResponse> list = map.get(csId);
                        dateTimes =
                                map.values().stream().flatMap(m -> m.stream()).map(MonitorAPIResponse::getDateTime).distinct().collect(toList());
                        Map<Date, MonitorReadingDailyDTO> dateValues = getInverterAndYieldSum(dateTimes, list);
                        List<MonitorAPIResponse> responses = new ArrayList<>();
                        for (Map.Entry<Date, MonitorReadingDailyDTO> set : dateValues.entrySet()) {
                            responses.add(MonitorAPIResponse.builder()
                                    .dateTime(set.getKey())
                                    .yieldValue(set.getValue().getYieldValue())
                                    .inverterNumbers(set.getValue().getInverterNumbers())
                                    .detailData(set.getValue().getDataDTO())
                                    .build());

                        }
//                        cumulativeMap.put(String.valueOf(csId), responses);
                        updateCumulativeMap(responses, body.getTime(), cumulativeMap, isWeekly, String.valueOf(csId));
                    }
                }
            }
            //comparison is true and isSubscriptionComparison false then execute this block for user
            if (!isSubscriptionComparison) {
                List<String> subscriptionIds = body.getSubscriptionIdsMongo() == null ? body.getProjectIds() : body.getSubscriptionIdsMongo();
//                        == null? body.getSubscriptionIdsMongo() : body.getProjectIds();

                if (tenantConfig.isPresent() && tenantConfig.get().getText().equals("1")) {
                    extDataStageDefinitionList = extDataStageDefinitionService.findAllBySubsIdIn(subscriptionIds);
                    // ? body.getVariantIds() : body.getProjectIds());
                    Map<String, List<ExtDataStageDefinition>> extDataStageDefinitionMap = extDataStageDefinitionList.stream().collect(groupingBy(ExtDataStageDefinition::getRefId, toList()));

                    // below code will combine project based subscription in one list
                    for (String projectId : extDataStageDefinitionMap.keySet()) {
                        for (ExtDataStageDefinition ext : extDataStageDefinitionMap.get(projectId)) {
                            List<MonitorAPIResponse> subscriptionMonthlyGraphData = getMonthlyGraphDataForMongoSubscription(ext, body);
                            if (mongoMap.get(projectId) != null) {
                                List<MonitorAPIResponse> exsistingProjectSubGraphDataList = mongoMap.get(projectId);
                                exsistingProjectSubGraphDataList.addAll(subscriptionMonthlyGraphData);
                                mongoMap.put(projectId, exsistingProjectSubGraphDataList);
                            } else {
                                mongoMap.put(projectId, subscriptionMonthlyGraphData);
                            }
                        }
                    }

                }/* else {
//                    customerSubscriptions = customerSubscriptionRepository.findAllByUserIdInFetchCustomerSubscriptionMappings(variantIds);
                    filteredRateCodes = getFilterRateCodes(customerSubscriptions).keySet().stream().collect(toList());
                    for (String variantId : subscriptionIds) {
//                        mongoMap.put(variantId, getMonthlyGraphDataForVariant(variantId, body, filteredRateCodes));
                    }
                }*/

                for (String variantId : mongoMap.keySet()) {
                    List<MonitorAPIResponse> list = mongoMap.get(variantId);
                    List<Date> dateTimes = mongoMap.values().stream().flatMap(m -> m.stream())
                            .map(MonitorAPIResponse::getDateTime).distinct().collect(toList());
                    Map<Date, MonitorReadingDailyDTO> monthValues = getInverterAndYieldSum(dateTimes, list);

                    List<MonitorAPIResponse> responses = new ArrayList<>();
                    for (Map.Entry<Date, MonitorReadingDailyDTO> set : monthValues.entrySet()) {
                        responses.add(MonitorAPIResponse.builder()
                                .yieldValue(set.getValue().getYieldValue())
                                .inverterNumbers(set.getValue().getInverterNumbers())
                                .dateTime(set.getKey())
                                .detailData(set.getValue().getDataDTO())
                                .build());
                    }
//                    cumulativeMongoMap.put(variantId, responses);
                    updateCumulativeMap(responses, body.getTime(), cumulativeMongoMap, isWeekly, variantId);
                }
            }
        }
        return GraphDataMonthlyWrapper.builder()
                .monthlyGraphData(cumulativeMongoMap)
                .xAxis(isWeekly ? getBarXAxisLabelsForWeek(body.getTime()) : getBarXAxisLabels(body.getTime()))
                .build();
    }

    private void updateCumulativeMap(List<MonitorAPIResponse> responses, String time, Map<String, List<MonitorAPIResponse>> cumulativeMap, boolean isWeekly, String variantId) throws ParseException {
        if (isWeekly) {
            int rounding = utility.getCompanyPreference().getRounding();
            MonitorAPIResponse weekOne = responses.size() >= 7 ? MonitorAPIResponse.builder().yieldValue(utility.round(responses.subList(0, 7).stream().mapToDouble(m -> m.getYieldValue()).sum(), rounding)).build() : MonitorAPIResponse.builder().yieldValue(0.0d).build();
            MonitorAPIResponse weekTwo = responses.size() >= 14 ? MonitorAPIResponse.builder().yieldValue(utility.round(responses.subList(7, 14).stream().mapToDouble(m -> m.getYieldValue()).sum(), rounding)).build() : MonitorAPIResponse.builder().yieldValue(0.0d).build();
            MonitorAPIResponse weekThree = responses.size() >= 21 ? MonitorAPIResponse.builder().yieldValue(utility.round(responses.subList(14, 21).stream().mapToDouble(m -> m.getYieldValue()).sum(), rounding)).build() : MonitorAPIResponse.builder().yieldValue(0.0d).build();
            MonitorAPIResponse weekFour = responses.size() >= 28 ? MonitorAPIResponse.builder().yieldValue(utility.round(responses.subList(21, 28).stream().mapToDouble(m -> m.getYieldValue()).sum(), rounding)).build() : MonitorAPIResponse.builder().yieldValue(0.0d).build();
            List<String> barXAxisLabels = getBarXAxisLabelsForWeek(time);
            if (barXAxisLabels.size() > 4) {
                MonitorAPIResponse weekRemaining = responses.size() > 28 ? MonitorAPIResponse.builder().yieldValue(utility.round(responses.subList(28, responses.size()).stream().mapToDouble(m -> m.getYieldValue()).sum(), rounding)).build() : new MonitorAPIResponse();
                cumulativeMap.put(variantId, List.of(weekOne, weekTwo, weekThree, weekFour, weekRemaining));
            } else {
                cumulativeMap.put(variantId, List.of(weekOne, weekTwo, weekThree, weekFour));
            }
        } else {
            cumulativeMap.put(variantId, responses);
        }
    }

    private List<String> getBarXAxisLabelsForWeek(String dateString) throws ParseException {
        Date date = formatDate.parse(dateString != null ? dateString : formatDate.format(new Date()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int totalDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (totalDays == 28) {
            return List.of("01", "02", "03", "04");
        }
        return List.of("01", "02", "03", "04", "05");
    }

    /*private List<MonitorAPIResponse> getMonthlyGraphDataForUser(Long userId, MonitorAPIAuthBody body, List<String> filteredRateCodes) throws ParseException {
        String rateCode = filteredRateCodes.stream().filter(l -> "INVRT".equalsIgnoreCase(l)).findAny().orElse(null);
        ;
        List<Date> labelDates = getDates(body.getTime());
        List<String> dates = labelDates.stream().map(d -> {
            String dt = Utility.getDateString(d, Utility.SYSTEM_DATE_FORMAT);
            return dt;
        }).collect(toList());
        List<MonitorReadingDaily> monthsData = dailyRepository.findByUserAndDayIn(userId, dates, rateCode);
        List<MonitorAPIResponse> responses = new ArrayList<>();
        getMaxDatesForXAxis(body.getTime()).forEach(d -> {
            Optional<MonitorReadingDaily> data = monthsData.stream().filter(m -> m.getDay().equals(d)).findFirst();
            if (data.isPresent()) {
                responses.add(MonitorReadingMapper.toMonitorAPIResponse(data.get()));
            } else {
                responses.add(MonitorAPIResponse.builder().dateTime(d).yieldValue(0d).build());
            }
        });
//        return MonitorReadingMapper.toMonitorAPIDailyResponses(dailyRepository.findByUserIdAndDayIn(user.getAcctId(), getDates(date)));
        return responses;
    }*/

    /*private List<MonitorAPIResponse> getMonthlyGraphDataForVariant(String variantId, MonitorAPIAuthBody body, List<String> filteredRateCodes) throws ParseException {
        String rateCode = filteredRateCodes.stream().filter(l -> "INVRT".equalsIgnoreCase(l)).findAny().orElse(null);
        ;
        List<Date> labelDates = getDates(body.getTime());
        List<String> dates = labelDates.stream().map(d -> {
            String dt = Utility.getDateString(d, Utility.SYSTEM_DATE_FORMAT);
            return dt;
        }).collect(toList());
        List<MonitorReadingDaily> monthsData = dailyRepository.findByUserAndDayIn(Long.valueOf(variantId), dates, rateCode);
        List<MonitorAPIResponse> responses = new ArrayList<>();
        getMaxDatesForXAxis(body.getTime()).forEach(d -> {
            Optional<MonitorReadingDaily> data = monthsData.stream().filter(m -> m.getDay().equals(d)).findFirst();
            if (data.isPresent()) {
                responses.add(MonitorReadingMapper.toMonitorAPIResponse(data.get()));
            } else {
                responses.add(MonitorAPIResponse.builder().dateTime(d).yieldValue(0d).build());
            }
        });
//        return MonitorReadingMapper.toMonitorAPIDailyResponses(dailyRepository.findByUserIdAndDayIn(user.getAcctId(), getDates(date)));
        return responses;
    }*/

    private List<MonitorAPIResponse> getMonthlyGraphDataForSubscription(CustomerSubscription cs, MonitorAPIAuthBody body, List<String> filteredRateCodes) throws ParseException {
        String rate_code = filteredRateCodes.stream().filter(l -> "INVRT".equalsIgnoreCase(l)).findAny().orElse(null);
        ;
        List<Date> labelDates = getDates(body.getTime());
        List<String> dates = labelDates.stream().map(d -> {
            String dt = Utility.getDateString(d, Utility.SYSTEM_DATE_FORMAT);
            return dt;
        }).collect(toList());

        List<MonitorReadingDaily> monthData = dailyRepository.findBySubscriptionAndDayIn(cs.getId(), dates, rate_code);
        List<MonitorAPIResponse> responses = new ArrayList<>();
        getMaxDatesForXAxis(body.getTime()).forEach(d -> {
            Optional<MonitorReadingDaily> data = monthData.stream().filter(m -> m.getDay().equals(d)).findFirst();
            if (data.isPresent()) {
                responses.add(MonitorReadingMapper.toMonitorAPIResponse(data.get()));
            } else {
                responses.add(MonitorAPIResponse.builder().dateTime(d).yieldValue(0d).build());
            }
        });
//        return MonitorReadingMapper.toMonitorAPIDailyResponses(dailyRepository.findByUserIdAndDayIn(user.getAcctId(), getDates(date)));
        return responses;
    }

    @Override
    public GraphDataYearlyWrapper getYearlyGraphData(MonitorAPIAuthBody body, boolean isComparison, boolean isSubscriptionComparison, boolean isQuarterly) throws ParseException {
        Map<Long, List<MonitorAPIResponse>> map = new TreeMap<>();
        Map<String, List<MonitorAPIResponse>> cumulativeMap = new TreeMap<>();
        List<CustomerSubscription> customerSubscriptions = null;
        List<String> filteredRateCodes = null;
        List<ExtDataStageDefinition> extDataStageDefinitionList = null;
        Map<String, List<MonitorAPIResponse>> mongoMap = new TreeMap<>();
        Map<String, List<MonitorAPIResponse>> cumulativeMapMongo = new TreeMap<>();
        String year = body.getTime();
        Optional<TenantConfig> tenantConfig = null;
        try {
            tenantConfig = tenantConfigService.findByParameter(Constants.TENANT_CONFIG_CATEGORY.PARAM_ISMONGOENABLED);
            if (!isComparison || isSubscriptionComparison) {
                if (tenantConfig.isPresent() && tenantConfig.get().getText().equals("1")) {
                    extDataStageDefinitionList = extDataStageDefinitionService.findAllBySubsIdIn(body.getSubscriptionIdsMongo());
                    for (ExtDataStageDefinition ext : extDataStageDefinitionList) {
                        cumulativeMapMongo.put(ext.getSubsId(), getYearlyGraphDataMongoForSubscription(ext, body.getTime()));
                    }
                } else {
                    customerSubscriptions = customerSubscriptionRepository.findAllByIdInFetchCustomerSubscriptionMappings(body.getSubscriptionIds());
                    filteredRateCodes = getFilterRateCodes(customerSubscriptions).keySet().stream().collect(toList());
                    for (CustomerSubscription customerSubscription : customerSubscriptions) {
                        map.put(customerSubscription.getId(), getYearlyGraphDataForSubscription(customerSubscription, body.getTime(), filteredRateCodes));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        if (!isComparison) {
            List<String> months = getBarXAxisLabelsForMonths();
            Map<String, MonitorReadingDailyDTO> monthValueMap = new HashMap<String, MonitorReadingDailyDTO>();
            if (tenantConfig.isPresent() && tenantConfig.get().getText().equals("1")) {
                months.forEach(month -> {
                    double yieldValueSum = cumulativeMapMongo.values().stream().flatMap(m -> m.stream()).filter(m -> getMonthFromDate(m.getDateTime()).equals(month)).mapToDouble(MonitorAPIResponse::getYieldValue).sum();
                    List<String> inverterNumbers = cumulativeMapMongo.values().stream().flatMap(m -> m.stream()).filter(m -> getMonthFromDate(m.getDateTime()).equals(month)).filter(m -> m.getInverterNumber() != null).map(MonitorAPIResponse::getInverterNumber).collect(toList());
                    MonitorReadingDailyDTO monitorReadingDailyDTO = MonitorReadingDailyDTO.builder().build();
                    monitorReadingDailyDTO.setInverterNumbers(inverterNumbers);
                    monitorReadingDailyDTO.setYieldValue(yieldValueSum);
                    monthValueMap.put(year + "-" + month, monitorReadingDailyDTO);
                });
            } else {
                months.forEach(month -> {
                    double yieldValueSum = map.values().stream().flatMap(m -> m.stream()).filter(m -> getMonthFromDate(m.getDateTime()).equals(month)).mapToDouble(MonitorAPIResponse::getYieldValue).sum();
                    List<String> inverterNumbers = map.values().stream().flatMap(m -> m.stream()).filter(m -> getMonthFromDate(m.getDateTime()).equals(month)).filter(m -> m.getInverterNumber() != null).map(MonitorAPIResponse::getInverterNumber).collect(toList());
                    MonitorReadingDailyDTO monitorReadingDailyDTO = MonitorReadingDailyDTO.builder().build();
                    monitorReadingDailyDTO.setInverterNumbers(inverterNumbers);
                    monitorReadingDailyDTO.setYieldValue(yieldValueSum);
                    monthValueMap.put(year + "-" + month, monitorReadingDailyDTO);
                });
            }

            Map<String, MonitorReadingDailyDTO> monthValues = sortByKey(monthValueMap);
            List<MonitorAPIResponse> responses = new ArrayList<>();
            for (Map.Entry<String, MonitorReadingDailyDTO> set : monthValues.entrySet()) {
                responses.add(MonitorAPIResponse.builder()
                        .month(set.getKey())
                        .yieldValue(set.getValue().getYieldValue())
                        .inverterNumbers(set.getValue().getInverterNumbers())
                        .build());
            }
//            cumulativeMap.put(String.valueOf(-1L), responses);
            updateCumulativeMapForQuarter(responses, cumulativeMap, isQuarterly, String.valueOf(-1L));
            return GraphDataYearlyWrapper.builder()
                    .yearlyGraphData(cumulativeMap.isEmpty() ? cumulativeMapMongo : cumulativeMap)
                    .xAxis(isQuarterly ? getBarXAxisLabelsForQuarterly() : getBarXAxisLabelsForMonths())
                    .build();
        } else {
            //if comparison is true and type is subscription then execute this block
            if (isSubscriptionComparison) {
                if (tenantConfig.isPresent() && tenantConfig.get().getText().equals("1")) {
                    for (ExtDataStageDefinition ext : extDataStageDefinitionList) {
                        List<MonitorAPIResponse> list = cumulativeMapMongo.get(ext.getSubsId());
                        List<String> months = getBarXAxisLabelsForMonths();
                        Map<String, MonitorReadingDailyDTO> monthValueMap = getInverterAndYieldSum(months, list, year);
                        List<MonitorAPIResponse> responses = new ArrayList<>();
                        Map<String, MonitorReadingDailyDTO> monthValues = sortByKey(monthValueMap);

                        for (Map.Entry<String, MonitorReadingDailyDTO> set : monthValues.entrySet()) {
                            responses.add(MonitorAPIResponse.builder()
                                    .month(set.getKey())
                                    .yieldValue(set.getValue().getYieldValue())
                                    .inverterNumbers(set.getValue().getInverterNumbers())
                                    .detailData(set.getValue().getDataDTO())
                                    .build());
                        }
//                        cumulativeMapMongo.put(ext.getSubsId(), responses);
                        updateCumulativeMapForQuarter(responses, cumulativeMapMongo, isQuarterly, ext.getSubsId());
                    }
                } else {
                    for (CustomerSubscription cs : customerSubscriptions) {
                        Long csId = cs.getId();
                        List<MonitorAPIResponse> list = map.get(csId);
                        List<String> months = getBarXAxisLabelsForMonths();
                        Map<String, MonitorReadingDailyDTO> monthValueMap = getInverterAndYieldSum(months, list, year);
                        List<MonitorAPIResponse> responses = new ArrayList<>();
                        Map<String, MonitorReadingDailyDTO> monthValues = sortByKey(monthValueMap);

                        for (Map.Entry<String, MonitorReadingDailyDTO> set : monthValues.entrySet()) {
                            responses.add(MonitorAPIResponse.builder()
                                    .month(set.getKey())
                                    .yieldValue(set.getValue().getYieldValue())
                                    .inverterNumbers(set.getValue().getInverterNumbers())
                                    .detailData(set.getValue().getDataDTO())
                                    .build());
                        }
//                        cumulativeMap.put(String.valueOf(csId), responses);
                        updateCumulativeMapForQuarter(responses, cumulativeMap, isQuarterly, String.valueOf(csId));
                    }
                }
            }
            //comparison is true and isSubscriptionComparison false then execute this block for user
            if (!isSubscriptionComparison) {
                List<String> subscriptionIds = body.getSubscriptionIdsMongo() == null ? body.getProjectIds() : body.getSubscriptionIdsMongo();
//                        == null? body.getSubscriptionIdsMongo() : body.getProjectIds();
                if (tenantConfig.isPresent() && tenantConfig.get().getText().equals("1")) {
                    extDataStageDefinitionList = extDataStageDefinitionService.findAllBySubsIdIn(subscriptionIds);
                    Map<String, List<ExtDataStageDefinition>> extDataStageDefinitionMap = extDataStageDefinitionList.stream().collect(groupingBy(ExtDataStageDefinition::getRefId, toList()));
                    // below code will combine project based subscription in one list
                    for (String projectId : extDataStageDefinitionMap.keySet()) {
                        for (ExtDataStageDefinition ext : extDataStageDefinitionMap.get(projectId)) {
                            List<MonitorAPIResponse> subscriptionYearlyGraphData = getYearlyGraphDataMongoForSubscription(ext, body.getTime());
                            if (mongoMap.get(projectId) != null) {
                                List<MonitorAPIResponse> exsistingProjectSubGraphDataList = mongoMap.get(projectId);
                                exsistingProjectSubGraphDataList.addAll(subscriptionYearlyGraphData);
                                mongoMap.put(projectId, exsistingProjectSubGraphDataList);
                            } else {
                                mongoMap.put(projectId, subscriptionYearlyGraphData);
                            }
                        }

//                        for (ExtDataStageDefinition ext : extDataStageDefinitionList) {
//                        mongoMap.put(ext.getRefId(), getYearlyGraphDataMongoForSubscription(ext, body.getTime()));
//                    }
                    }

                }/* else {
                    List<Long> userIds = body.getUserIds();
                    customerSubscriptions = customerSubscriptionRepository.findAllByUserIdInFetchCustomerSubscriptionMappings(userIds);
                    filteredRateCodes = getFilterRateCodes(customerSubscriptions).keySet().stream().collect(toList());
                }*/

//                for (String variantId : variantIds) {
//                    map.put(variantId, getYearlyGraphDataForUser(variantId, year, filteredRateCodes));
//                }
                for (String variantId : mongoMap.keySet()) {
                    List<MonitorAPIResponse> list = mongoMap.get(variantId);
                    List<String> months = getBarXAxisLabelsForMonths();
                    Map<String, MonitorReadingDailyDTO> monthValueMap = getInverterAndYieldSum(months, list, year);
                    Map<String, MonitorReadingDailyDTO> monthValues = sortByKey(monthValueMap);
                    List<MonitorAPIResponse> responses = new ArrayList<>();
                    for (Map.Entry<String, MonitorReadingDailyDTO> set : monthValues.entrySet()) {
                        responses.add(MonitorAPIResponse.builder()
                                .month(set.getKey())
                                .yieldValue(set.getValue().getYieldValue())
                                .inverterNumbers(set.getValue().getInverterNumbers())
                                .detailData(set.getValue().getDataDTO())
                                .build());
                    }
//                    cumulativeMap.put(variantId, responses);
                    updateCumulativeMapForQuarter(responses, cumulativeMap, isQuarterly, variantId);
                }
            }
        }
        return GraphDataYearlyWrapper.builder()
                .yearlyGraphData(cumulativeMap.isEmpty() ? cumulativeMapMongo : cumulativeMap)
                .xAxis(isQuarterly ? getBarXAxisLabelsForQuarterly() : getBarXAxisLabelsForMonths())
                .build();
    }

    private void updateCumulativeMapForQuarter(List<MonitorAPIResponse> responses, Map<String, List<MonitorAPIResponse>> cumulativeMap, boolean isQuarterly, String variantId) {
        if (isQuarterly) {
            int rounding = utility.getCompanyPreference().getRounding();
            MonitorAPIResponse quarterOne = MonitorAPIResponse.builder().yieldValue(utility.round(responses.subList(0, 3).stream().mapToDouble(m -> m.getYieldValue()).sum(), rounding)).build();
            MonitorAPIResponse quarterTwo = MonitorAPIResponse.builder().yieldValue(utility.round(responses.subList(3, 6).stream().mapToDouble(m -> m.getYieldValue()).sum(), rounding)).build();
            MonitorAPIResponse quarterThree = MonitorAPIResponse.builder().yieldValue(utility.round(responses.subList(6, 9).stream().mapToDouble(m -> m.getYieldValue()).sum(), rounding)).build();
            MonitorAPIResponse quarterFour = MonitorAPIResponse.builder().yieldValue(utility.round(responses.subList(9, 12).stream().mapToDouble(m -> m.getYieldValue()).sum(), rounding)).build();
            cumulativeMap.put(variantId, List.of(quarterOne, quarterTwo, quarterThree, quarterFour));
        } else {
            cumulativeMap.put(variantId, responses);
        }
    }

    private List<String> getBarXAxisLabelsForQuarterly() throws ParseException {
        return List.of("01", "02", "03", "04");
    }

    private List<MonitorAPIResponse> getYearlyGraphDataForSubscription(CustomerSubscription customerSubscription, String year, List<String> filteredRateCodes) throws ParseException {
        String firstDayOfMonth = "1";
        String rateCode = filteredRateCodes.stream().filter(l -> "INVRT".equalsIgnoreCase(l)).findAny().orElse(null);
        List<MonitorReadingDaily> monthsData = dailyRepository.findBySubscriptionIdAndYear(customerSubscription.getId(), year, rateCode);
        List<MonitorAPIResponse> responses = new ArrayList<>();
        getBarXAxisLabelsForMonths().forEach(month -> {
            //YYYY-MM-DD
            String dateObj = year + "-" + month + "-" + firstDayOfMonth;
            try {
                getMaxDatesForXAxis(dateObj).forEach(d -> {
                    Optional<MonitorReadingDaily> data = monthsData.stream().filter(m -> m.getDay().equals(d)).findFirst();
                    if (data.isPresent()) {
                        responses.add(MonitorReadingMapper.toMonitorAPIResponse(data.get()));
                    } else {
                        responses.add(MonitorAPIResponse.builder().dateTime(d).yieldValue(0d).build());
                    }
                });
            } catch (ParseException e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
        return responses;
    }

    /*private List<MonitorAPIResponse> getYearlyGraphDataForUser(Long userId, String year, List<String> filteredRateCodes) throws ParseException {
        String firstDayOfMonth = "1";
        String rateCode = filteredRateCodes.stream().filter(l -> "INVRT".equalsIgnoreCase(l)).findAny().orElse(null);
        ;
        List<MonitorReadingDaily> monthsData = dailyRepository.findByUserSubscriptionIdAndYear(userId, year, rateCode);
        List<MonitorAPIResponse> responses = new ArrayList<>();
        getBarXAxisLabelsForMonths().forEach(month -> {
            //YYYY-MM-DD
            String dateObj = year + "-" + month + "-" + firstDayOfMonth;
            try {
                getMaxDatesForXAxis(dateObj).forEach(d -> {
                    Optional<MonitorReadingDaily> data = monthsData.stream().filter(m -> m.getDay().equals(d)).findFirst();
                    if (data.isPresent()) {
                        responses.add(MonitorReadingMapper.toMonitorAPIResponse(data.get()));
                    } else {
                        responses.add(MonitorAPIResponse.builder().dateTime(d).yieldValue(0d).build());
                    }
                });
            } catch (ParseException e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
        return responses;
    }*/


    private List<Date> getDates(String paramDate) throws ParseException {
        Date date = formatDate.parse(paramDate != null ? paramDate : formatDate.format(new Date()));
        Date tillDate = Utility.addMinutes(new Date(), 300);
        boolean isSameMonth = Utility.areInSameMonth(date, tillDate);
        Date dateTime = Utility.getStartOfMonth(date, Utility.SYSTEM_DATE_FORMAT);
        List<Date> dates = new ArrayList<>();
        if (!isSameMonth) {
            tillDate = Utility.getEndOfMonth(date, Utility.SYSTEM_DATE_FORMAT);
        }
        while (dateTime.before(tillDate)) {
            dates.add(dateTime);
            dateTime = Utility.addDays(dateTime, 1);
        }
        return dates;
    }

    private List<Date> getMaxDatesForXAxis(String paramDate) throws ParseException {
        Date date = formatDate.parse(paramDate != null ? paramDate : formatDate.format(new Date()));
//        Date tillDate = Utility.addMinutes(new Date(), 300);
//        boolean isSameMonth = Utility.areInSameMonth(date, tillDate);
        Date dateTime = Utility.getStartOfMonth(date, Utility.SYSTEM_DATE_FORMAT);
//        Date tillDate = Utility.addMinutes(Utility.getEndOfMonth(date, Utility.SYSTEM_DATE_FORMAT), 300);
        Date tillDate = Utility.getEndOfMonth(date, Utility.SYSTEM_DATE_FORMAT);
        List<Date> dates = new ArrayList<>();
        while (dateTime.before(tillDate)) {
            dates.add(dateTime);
            dateTime = Utility.addDays(dateTime, 1);
        }
        return dates;
    }

    private List<String> getBarXAxisLabels(String dateString) throws ParseException {
        return getMaxDatesForXAxis(dateString).stream().map(d -> new SimpleDateFormat("dd").format(d)).collect(toList());
    }

    @Override
    public MonitorResponseWrapper getCurrentData(MonitorAPIAuthBody body, boolean isRefresh, boolean isComparison, boolean isSubscriptionComparison) throws NoSuchAlgorithmException, ParseException, UnsupportedEncodingException, JsonProcessingException {
        return MonitorResponseWrapper.builder()
                .widgetData(getCurrentWidgetData(body, isRefresh))
                .graphData(getCurrentGraphData(body, isComparison, isSubscriptionComparison))
                .monthlyGraphData(getMonthlyGraphData(body, isComparison, isSubscriptionComparison, false))
                .build();
    }

    @Override
    public List<MonitorReading> saveCurrentData(MonitorAPIAuthBody authBody) throws UnsupportedEncodingException, JsonProcessingException, ParseException {
        List<CustomerSubscription> cs = customerSubscriptionRepository.findAllByIdInFetchCustomerSubscriptionMappings(authBody.getSubscriptionIds());
        List<MonitorReading> pvPowerReadings = new ArrayList<>();
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(Utility.SYSTEM_DATE_TIME_FORMAT);
        SimpleDateFormat dateFormat = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);

        String authBodyDtTime = authBody.getTime();

        for (CustomerSubscription customerSubscription : cs) {
            try {
                String mp = customerSubscription.getCustomerSubscriptionMappings().stream().filter(l -> "MP".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue();
                String inverterNumber = customerSubscription.getCustomerSubscriptionMappings().stream().filter(l -> "INVRT".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue();
                //ssdt = subscription start date
                String ssdt = customerSubscription.getCustomerSubscriptionMappings().stream().filter(l -> "SSDT".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue();
                Date dateTime = null;
                if (authBodyDtTime != null) {
                    dateTime = dateTimeFormat.parse(authBodyDtTime);
                } else {
                    dateTime = dateFormat.parse(ssdt);
                }
                authBody.setTime(dateTimeFormat.format(dateTime));
                while (dateTimeFormat.parse(authBody.getTime()).before(new Date())) {

                    MonitorAPIResponse apiResponse = null;
                    //false for solis widget data
                    MonitorReading lastRecord = readingRepository.getLastRecord(customerSubscription.getId());
                    Date lastSavedRecord = lastRecord != null ? lastRecord.getTime() : null;
                    apiResponse = externalAPIFactory.get(mp).getCurrentData(customerSubscription, authBody.getTime(), false, lastSavedRecord);
                    Double currentValueRunning = lastRecord != null ? lastRecord.getCurrentValueRunning() : null;
                    Double yieldValueRunning = lastRecord != null ? lastRecord.getYieldValueRunning() : null;
                    if (apiResponse.getInverterValuesOverTime() != null) {
                        for (Map.Entry<String, Map<Date, MonitorAPIResponseL2>> set :
                                apiResponse.getInverterValuesOverTime().entrySet()) {
                            for (Map.Entry<Date, MonitorAPIResponseL2> m :
                                    set.getValue().entrySet()) {
                                currentValueRunning = currentValueRunning != null ? currentValueRunning + m.getValue().getPvPower() :
                                        m.getValue().getPvPower();
                                yieldValueRunning = yieldValueRunning != null ? yieldValueRunning + m.getValue().getGridpower() :
                                        m.getValue().getGridpower();

                                pvPowerReadings.add(
                                        MonitorReading.builder()
                                                .userId(customerSubscription.getUserAccount().getAcctId())
                                                .subscriptionId(customerSubscription.getId())
//                .site()
                                                .inverterNumber(set.getKey())
                                                .sytemSize(apiResponse.getSytemSize())
                                                .currentValueToday(apiResponse.getCurrentValueToday())
                                                .currentValue(m.getValue().getPvPower())
                                                .currentValueRunning(currentValueRunning)
                                                .yieldValue(m.getValue().getGridpower())
                                                .yieldValueRunning(yieldValueRunning)
                                                .peakValue(apiResponse.getPeakValue())
                                                .dailyYield(apiResponse.getDailyYield() != null ? apiResponse.getDailyYield() : m.getValue().getEToday())
                                                .monthlyYield(apiResponse.getMonthlyYield() != null ? apiResponse.getMonthlyYield() : m.getValue().getEMonth())
                                                .annualYield(apiResponse.getAnnualYield() != null ? apiResponse.getAnnualYield() : m.getValue().getEYear())
                                                .grossYield(apiResponse.getGrossYield() != null ? apiResponse.getGrossYield() : m.getValue().getETotal())
//                                .time(Utility.deductMinutes(m.getKey(), 300))
                                                .time(m.getKey())
                                                .build());
                            }
                        }

                        MonitorReadingDaily mrd = dailyRepository.getLastSavedRecordBySubIdAndDate(customerSubscription.getId(), authBody.getTime().split(" ")[0]);
                        if (mrd == null) {
                            MonitorReadingDaily monitorReadingDaily = dailyRepository.save(MonitorReadingDaily.builder()
                                    .userId(customerSubscription.getUserAccount().getAcctId())
                                    .subscriptionId(customerSubscription.getId())
                                    .inverterNumber(inverterNumber)
                                    .yieldValue(apiResponse.getDailyYield())
                                    .day(formatDate.parse(authBody.getTime().split(" ")[0]))
                                    .build());
                        } else {
                            mrd.setYieldValue(apiResponse.getDailyYield());
                            dailyRepository.save(mrd);
                        }
                    }
                    dateTime = Utility.addDays(dateTime, 1);
                    authBody.setTime(dateTimeFormat.format(dateTime));
                }
            } catch (Exception e) {
                try {
                    //send email when there is exception in api call
                    String inverterNumber = customerSubscription.getCustomerSubscriptionMappings().stream().filter(l -> "INVRT".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue();
                    String loginName = customerSubscription.getCustomerSubscriptionMappings().stream().filter(l -> "LGNM".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue();
                    String mp = customerSubscription.getCustomerSubscriptionMappings().stream().filter(l -> "MP".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue();

                    emailService.monitoringApiNotification(appProfile, customerSubscription.getId(), inverterNumber, loginName, mp, Utility.getStackTrace(e));
                    LOGGER.error("CustomerSubscription[Id:" + customerSubscription.getId() +
                            ", inverterNumber:" + inverterNumber + ", loginName:" + loginName + ", monitoringPlatform:" + mp + "], ErrorMsg=" + Utility.getStackTrace(e));
                    LOGGER.error(e.getMessage(), e);
                } catch (IOException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }
        return eGaugeService.save(pvPowerReadings);

    }

    @Override
    public List<MonitorReading> saveCurrentDataMongo(MonitorAPIAuthBody authBody) {
        List<ExtDataStageDefinition> es = extDataStageDefinitionService.findAllBySubsIdIn(authBody.getSubscriptionIdsMongo());
        List<MonitorReading> pvPowerReadings = new ArrayList<>();
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(Utility.SYSTEM_DATE_TIME_FORMAT);

        String authBodyDtTime = authBody.getTime() == null ? authBody.getFromDateTime() : authBody.getTime();
        String from = authBody.getFromDateTime();
        String to = authBody.getToDateTime();

        for (ExtDataStageDefinition ext : es) {
            try {

                String mp = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.MP);
                if (externalAPIFactory.get(mp) == null) {
                    continue;
                }
                String inverterNumber;
                String siteId = null;
                if (ext.getBrand().equals(Constants.RATE_CODES.BRAND_SOLAR_EDGE)) {
                    inverterNumber = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.INVRT);
                    siteId = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.SITE_ID);
                } else {
                    inverterNumber = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.INVERTER_NUMBER);
                }
                String ssdt = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.SUB_START_DATE);

                Date dateTime = null;
                MonitorReading lastRec = readingRepository.getLastRecord(ext.getSubsId());
                if (authBodyDtTime != null) {
                    dateTime = Utility.getZonedDate(dateTimeFormat.parse(authBodyDtTime), "UTC");
                } else if (lastRec != null) {
                    dateTime = Utility.getZonedDate(Utility.addMinutes(lastRec.getTime(), 1), "UTC");
                } else {
                    dateTime = Utility.getZonedDate(formatDateTimeSSDT.parse(ssdt.replace("GMT", "")), "UTC"); // Wed May 31 19:00:00 GMT 2023
                }
                authBody.setTime(dateTimeFormat.format(dateTime));
                authBody.setFromDateTime(dateTimeFormat.format(dateTime));

                if (externalAPIFactory.get(mp).savesBulk()) {
                    if (mp.equalsIgnoreCase(Constants.RATE_CODES.BRAND_SOLAR_EDGE) || mp.equalsIgnoreCase(Constants.RATE_CODES.BRAND_ENPHASE)) {
                        authBody.setFromDateTime(from);
                    }
                    MonitorAPIResponse apiResponse = externalAPIFactory.get(mp).getCurrentData(ext, authBody);
                    if (apiResponse != null) {
                        pvPowerReadings = Optional.ofNullable(apiResponse.getMonitorReadingDTOs())
                                .map(MonitorReadingMapper::toMonitorReadingList)
                                .orElse(Collections.emptyList());
                        if (!pvPowerReadings.isEmpty()) {
                            pvPowerReadings = eGaugeService.save(pvPowerReadings);
                        }
                        List<com.solar.api.tenant.model.pvmonitor.MonitorReadingDailyDTO> bulkDailyRecords = Optional.ofNullable(apiResponse.getBulkDailyRecords())
                                .orElse(Collections.emptyList());
                        if (!bulkDailyRecords.isEmpty()) {
                            eGaugeService.saveAllMonitorReadingDaily(MonitorReadingDailyMapper.toMonitorReadingDailList(bulkDailyRecords));
                        }
                    }

                } else {
                    while (dateTimeFormat.parse(authBody.getTime()).before(new Date())) {
                        MonitorAPIResponse apiResponse = null;
                        //false for solis widget data
                        MonitorReading lastRecord = readingRepository.getLastRecord(ext.getSubsId());
                        Date lastSavedRecord = lastRecord != null ? lastRecord.getTime() : null;
                        apiResponse = externalAPIFactory.get(mp).getCurrentData(ext, authBody.getTime(), false, lastSavedRecord);
                        Double currentValueRunning = lastRecord != null ? lastRecord.getCurrentValueRunning() : null;
                        Double yieldValueRunning = lastRecord != null ? lastRecord.getYieldValueRunning() : null;
                        if (apiResponse.getInverterValuesOverTime() != null) {
                            for (Map.Entry<String, Map<Date, MonitorAPIResponseL2>> set :
                                    apiResponse.getInverterValuesOverTime().entrySet()) {
                                for (Map.Entry<Date, MonitorAPIResponseL2> m :
                                        set.getValue().entrySet()) {
                                    currentValueRunning = currentValueRunning != null ? currentValueRunning + m.getValue().getPvPower() :
                                            m.getValue().getPvPower();
                                    yieldValueRunning = yieldValueRunning != null ? yieldValueRunning + m.getValue().getGridpower() :
                                            m.getValue().getGridpower();

                                    pvPowerReadings.add(
                                            MonitorReading.builder()
                                                    .subscriptionIdMongo(ext.getSubsId())
                                                    .inverterNumber(set.getKey())
                                                    .sytemSize(apiResponse.getSytemSize())
                                                    .currentValueToday(apiResponse.getCurrentValueToday())
                                                    .currentValue(m.getValue().getPvPower())
                                                    .currentValueRunning(currentValueRunning)
                                                    .yieldValue(m.getValue().getGridpower())
                                                    .yieldValueRunning(yieldValueRunning)
                                                    .peakValue(apiResponse.getPeakValue())
                                                    .dailyYield(apiResponse.getDailyYield() != null ? apiResponse.getDailyYield() : m.getValue().getEToday())
                                                    .monthlyYield(apiResponse.getMonthlyYield() != null ? apiResponse.getMonthlyYield() : m.getValue().getEMonth())
                                                    .annualYield(apiResponse.getAnnualYield() != null ? apiResponse.getAnnualYield() : m.getValue().getEYear())
                                                    .grossYield(apiResponse.getGrossYield() != null ? apiResponse.getGrossYield() : m.getValue().getETotal())
//                                .time(Utility.deductMinutes(m.getKey(), 300))
                                                    .time(m.getKey())
                                                    .build());
                                }
                            }

                            MonitorReadingDaily mrd = dailyRepository.getLastSavedRecordByMongoSubIdAndDate(ext.getSubsId(), authBody.getTime().split(" ")[0]);
                            if (mrd == null) {
                                MonitorReadingDaily monitorReadingDaily = dailyRepository.save(MonitorReadingDaily.builder()
                                        .subscriptionIdMongo(ext.getSubsId())
                                        .site(siteId)
                                        .inverterNumber(inverterNumber)
                                        .currentValue(apiResponse.getCurrentValue()) // check
                                        .yieldValue(apiResponse.getDailyYield())
                                        .peakValue(apiResponse.getPeakValue()) // check
//                                    .variantId(ext.getRefId())
                                        .day(formatDate.parse(authBody.getTime().split(" ")[0]))
                                        .build());
                            } else {
                                mrd.setYieldValue(apiResponse.getDailyYield());
                                dailyRepository.save(mrd);
                            }
                        }
                        dateTime = Utility.addDays(dateTime, 1);
                        authBody.setTime(dateTimeFormat.format(dateTime));
                    }
                    pvPowerReadings = readingRepository.saveAll(pvPowerReadings);
                }
            } catch (Exception e) {
                try {
                    //send email when there is exception in api call
                    String mp = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.MP);
                    String inverterNumber = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.INVERTER_NUMBER);
                    String loginName = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.LGNM);

                    emailService.monitoringApiNotification(appProfile, ext.getSubsId(), inverterNumber, loginName, mp, Utility.getStackTrace(e));
                    LOGGER.error("CustomerSubscription[Id:" + ext.getSubsId() +
                            ", inverterNumber:" + inverterNumber + ", loginName:" + loginName + ", monitoringPlatform:" + mp + "], ErrorMsg=" + Utility.getStackTrace(e));
                    LOGGER.error(e.getMessage(), e);
                } catch (IOException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
            authBody.setFromDateTime(from);
            authBody.setToDateTime(to);
        }
        return pvPowerReadings;

    }

    @Override
    public Double getTreesPlantedFactorLegacy(List<Long> subscriptionIds) {
        return getTreesPlantedLegacy(customerSubscriptionRepository.findAllByIdInFetchCustomerSubscriptionMappings(subscriptionIds));
    }

    private Double getTreesPlantedLegacy(List<CustomerSubscription> cs) {
        Double treesPlantedFactor = 0d;
        for (CustomerSubscription customerSubscription : cs) {
            MonitorReading lastGrossRecord = readingRepository.getLastGrossYieldRecordLegacy(customerSubscription.getId());
            treesPlantedFactor += (lastGrossRecord == null ? 0 : lastGrossRecord.getGrossYield()) * AppConstants.TPLMULTIPLIER;
        }
        return treesPlantedFactor;
    }

    @Override
    public Double getTreesPlantedFactor(List<String> subscriptionIds) {
        return getTreesPlanted(extDataStageDefinitionService.findAllBySubsIdIn(subscriptionIds));
    }

    private Double getTreesPlanted(List<ExtDataStageDefinition> extDataStageDefinitionList) {
        Double treesPlantedFactor = 0d;
        for (ExtDataStageDefinition dataStageDefinition : extDataStageDefinitionList) {
            MonitorReading lastGrossRecord = readingRepository.getLastGrossYieldRecord(dataStageDefinition.getSubsId());
            treesPlantedFactor += (lastGrossRecord == null ? 0 : lastGrossRecord.getGrossYield()) * AppConstants.TPLMULTIPLIER;
        }
        return treesPlantedFactor;
    }

    @Override
    public Double getCO2ReductionLegacy(List<Long> subscriptionIds) {
        return getCO2ReductLegacy(customerSubscriptionRepository.findAllByIdInFetchCustomerSubscriptionMappings(subscriptionIds));
    }

    private Double getCO2ReductLegacy(List<CustomerSubscription> cs) {
        Double co2Reduction = 0d;
        for (CustomerSubscription customerSubscription : cs) {
            MonitorReading lastGrossRecord = readingRepository.getLastGrossYieldRecordLegacy(customerSubscription.getId());
//            co2Reduction += lastGrossRecord.getGrossYield() * 0.71 / 907.185;
            co2Reduction += (lastGrossRecord == null ? 0 : lastGrossRecord.getGrossYield() / 1003) / 1000;
        }
        return co2Reduction;
    }

    @Override
    public Double getCO2Reduction(List<String> subscriptionIds) {
        return getCO2Reduct(extDataStageDefinitionService.findAllBySubsIdIn(subscriptionIds));
    }

    private Double getCO2Reduct(List<ExtDataStageDefinition> extDataStageDefinitionList) {
        Double co2Reduction = 0d;
        for (ExtDataStageDefinition dataStageDefinition : extDataStageDefinitionList) {
            MonitorReading lastGrossRecord = readingRepository.getLastGrossYieldRecord(dataStageDefinition.getSubsId());
//            co2Reduction += lastGrossRecord.getGrossYield() * 0.71 / 907.185;
            co2Reduction += (lastGrossRecord == null ? 0 : lastGrossRecord.getGrossYield() / 1003) / 1000;
        }
        return co2Reduction;
    }

    private List<String> getBarXAxisLabelsForMonths() throws ParseException {
        List<String> monthsList = Arrays.asList(Month.values()).stream().map(m -> m.getValue() < 10 ? "0" + m.getValue() : String.valueOf(m.getValue())).collect(toList());
        return monthsList;
    }

    public String getMonthFromDate(Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        return dateFormat.format(date);
    }

    private Map<String, MonitorReadingDailyDTO> getInverterAndYieldSum(List<String> months, List<MonitorAPIResponse> list, String year) {
        Map<String, MonitorReadingDailyDTO> monthValues = new TreeMap<>();
        months.forEach(month -> {
            MonitorReadingDailyDTO monitorReadingDailyDTO = MonitorReadingDailyDTO.builder().build();
            List<InverterDetailDTO> dataDTOList = getInvertersAndYieldValueByMonth(month, list);
            monitorReadingDailyDTO.setDataDTO(dataDTOList);
            Double yieldValueSum = list.stream().filter(m -> getMonthFromDate(m.getDateTime()).equals(month)).mapToDouble(MonitorAPIResponse::getYieldValue).sum();
            List<String> inverterNumbers = list.stream().filter(m -> getMonthFromDate(m.getDateTime()).equals(month)).filter(m -> m.getInverterNumber() != null).map(MonitorAPIResponse::getInverterNumber).collect(toList());
            monitorReadingDailyDTO.setInverterNumbers(inverterNumbers);
            monitorReadingDailyDTO.setYieldValue(yieldValueSum);
            monthValues.put(year + "-" + month, monitorReadingDailyDTO);
        });
        return monthValues;
    }

    private Map<Date, MonitorReadingDailyDTO> getInverterAndYieldSum(List<Date> dateTimes, List<MonitorAPIResponse> list) {
        Map<Date, MonitorReadingDailyDTO> monthValues = new TreeMap<>();
        dateTimes.forEach(dt -> {
            MonitorReadingDailyDTO monitorReadingDailyDTO = MonitorReadingDailyDTO.builder().build();
            List<InverterDetailDTO> dataDTOList = getInvertersAndYieldValueByMonth(dt, list);
            monitorReadingDailyDTO.setDataDTO(dataDTOList);

            List<String> inverterNumbers = list.stream().filter(m -> m.getDateTime().equals(dt)).filter(m -> m.getInverterNumber() != null).map(MonitorAPIResponse::getInverterNumber).collect(toList());
            Double yieldValueSum = list.stream().filter(m -> m.getDateTime().equals(dt)).mapToDouble(MonitorAPIResponse::getYieldValue).sum();
            monitorReadingDailyDTO.setInverterNumbers(inverterNumbers);
            monitorReadingDailyDTO.setYieldValue(yieldValueSum);
            monthValues.put(dt, monitorReadingDailyDTO);
        });
        return monthValues;
    }

    private Map<String, String> getFilterRateCodes(List<CustomerSubscription> cs) {
        Map<String, String> filteredRateCodes = new HashMap<>();
        for (CustomerSubscription customerSubscription : cs) {
            if (customerSubscription.getCustomerSubscriptionMappings() != null) {
                customerSubscription.getCustomerSubscriptionMappings().forEach(csMapping -> {
                    filteredRateCodes.put(csMapping.getRateCode(), csMapping.getValue());
                });
            }
        }
        return filteredRateCodes;
    }

    private List<InverterDetailDTO> getInvertersAndYieldValueByMonth(String month, List<MonitorAPIResponse> list) {
        List<InverterDetailDTO> dataDTOList = list.stream().filter(m -> getMonthFromDate(m.getDateTime()).equals(month)).filter(m -> m.getInverterNumber() != null).map(temp -> {
            InverterDetailDTO dataDTO = InverterDetailDTO.builder().build();
            dataDTO.setInverterNumber(temp.getInverterNumber());
            dataDTO.setYieldValue(temp.getYieldValue());
            return dataDTO;
        }).collect(toList());

        return dataDTOList;
    }

    private List<InverterDetailDTO> getInvertersAndYieldValueByMonth(Date date, List<MonitorAPIResponse> list) {
        List<InverterDetailDTO> dataDTOList = list.stream().filter(m -> (m.getDateTime()).equals(date)).filter(m -> m.getInverterNumber() != null).map(temp -> {
            InverterDetailDTO dataDTO = InverterDetailDTO.builder().build();
            dataDTO.setInverterNumber(temp.getInverterNumber());
            dataDTO.setYieldValue(temp.getYieldValue());
            return dataDTO;
        }).collect(toList());

        return dataDTOList;
    }

    private static Map<String, MonitorReadingDailyDTO> sortByKey(Map<String, MonitorReadingDailyDTO> map) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, MonitorReadingDailyDTO>> list = new LinkedList<Map.Entry<String, MonitorReadingDailyDTO>>(map.entrySet());
        // Sort the list using lambda expression
        Collections.sort(list, (i1, i2) -> (i1.getKey().substring(i1.getKey().indexOf("-") + 1)).compareTo((i2.getKey().substring(i1.getKey().indexOf("-") + 1))));
        // put data from sorted list to hashmap
        HashMap<String, MonitorReadingDailyDTO> temp = new LinkedHashMap<String, MonitorReadingDailyDTO>();
        for (Map.Entry<String, MonitorReadingDailyDTO> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private List<CustomerSubscription> getFilteredCustomerSubCList(List<CustomerSubscription> customerSubscriptionList) {
        List<String> filteredRateCodes = getFilterRateCodes(customerSubscriptionList).keySet().stream().collect(toList());
        String rate_code = filteredRateCodes.stream().filter(l -> "INVRT".equalsIgnoreCase(l)).findAny().orElse(null);
        List<CustomerSubscription> customerSubscriptions = customerSubscriptionList.stream()
                .filter(cs -> cs.getCustomerSubscriptionMappings().stream()
                        .anyMatch(csm -> csm.getRateCode().equals(rate_code)))
                .collect(toList());
        return customerSubscriptions;
    }

    // Checking
    private List<MonitorAPIResponse> getGraphDataForMongoSubscription(ExtDataStageDefinition ext, MonitorAPIAuthBody body) throws ParseException {
        String subscriptionIdMongo = ext.getSubsId();
        String inverterNumber;
        String siteId;
        String mp = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.MP);
        if (externalAPIFactory.get(mp) == null) {
            return Collections.EMPTY_LIST;
        }
        if (ext.getBrand().equals(Constants.RATE_CODES.BRAND_SOLAR_EDGE)) {
            inverterNumber = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.INVRT);
            siteId = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.SITE_ID);
        } else {
            siteId = "";
            inverterNumber = "";
        }
        List<MonitorReading> daysData = externalAPIFactory.get(mp).getMonitorReadingDataForMongoComparison(body, ext.getSubsId());
        List<MonitorAPIResponse> responses = new ArrayList<>();
        List<MonitorReading> finalDaysData = daysData;
        getMaxDateTimesForXAxis(body.getTime()).forEach(d -> {
            Optional<MonitorReading> data = finalDaysData.stream().filter(m ->
                    m.getTime().getTime() == d.getTime()).findFirst();
            if (data.isPresent()) {
                responses.add(MonitorReadingMapper.toMonitorAPIResponse(data.get()));
            } else {
                responses.add(MonitorAPIResponse.builder()
                        .subscriptionIdMongo(subscriptionIdMongo)
                        .inverterNumber(inverterNumber)
                        .site(siteId)
                        .dateTime(d)
                        .yieldValue(0d).build());
            }
        });
        return responses;
    }

    private List<MonitorAPIResponse> getMonthlyGraphDataForMongoSubscription(ExtDataStageDefinition ext, MonitorAPIAuthBody body) throws ParseException {
        String inverterNo;
        if (ext.getBrand().equals(Constants.RATE_CODES.BRAND_SOLRENVEIW)) {
            inverterNo = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.SVDID);
        } else if (ext.getBrand().equals(Constants.RATE_CODES.BRAND_EGAUGE)) {
            inverterNo = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.EGDID);
        } else if ((ext.getBrand().equals(Constants.RATE_CODES.BRAND_SOLAX)) ||
                (ext.getBrand().equals(Constants.RATE_CODES.BRAND_SOLIS)) ||
                (ext.getBrand().equals(Constants.RATE_CODES.BRAND_GOODWE))) {
            inverterNo = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.INVRT);
//        } else if ((ext.getBrand().equals(Constants.RATE_CODES.SOLAR_EDGE))) {
        } else if ((ext.getBrand().equals(Constants.RATE_CODES.BRAND_SOLAR_EDGE))) {
            inverterNo = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.INVRT);
        } else if ((ext.getBrand().equals(Constants.RATE_CODES.BRAND_TIGO))) {
            inverterNo = checkForInverter(ext.getMpJson());
        } else {
            inverterNo = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.INVRT);
        }

        List<Date> labelDates = getDates(body.getTime());
        List<String> dates = labelDates.stream().map(d -> {
            String dt = Utility.getDateString(d, Utility.SYSTEM_DATE_FORMAT);
            return dt;
        }).collect(toList());

        List<MonitorReadingDaily> monthData = dailyRepository.findByInverterNumberAndSubscriptionIdMongoAndDayIn(ext.getSubsId(), dates, inverterNo);
        List<MonitorAPIResponse> responses = new ArrayList<>();
        getMaxDatesForXAxis(body.getTime()).forEach(d -> {
            Optional<MonitorReadingDaily> data = monthData.stream().filter(m -> m.getDay().equals(d)).findFirst();
            if (data.isPresent()) {
                responses.add(MonitorReadingMapper.toMonitorAPIResponse(data.get()));
            } else {
                responses.add(MonitorAPIResponse.builder().dateTime(d).yieldValue(0d).build());
            }
        });
//        return MonitorReadingMapper.toMonitorAPIDailyResponses(dailyRepository.findByUserIdAndDayIn(user.getAcctId(), getDates(date)));
        return responses;
    }

    private String checkForInverter(String mpJson) {
        String inverterNo = Utility.getMeasureAsJson(mpJson, Constants.RATE_CODES.DEVICE_NUMBER);
        if (inverterNo == null || inverterNo.isBlank() || inverterNo.equals("-1")) {
            inverterNo = Utility.getMeasureAsJson(mpJson, Constants.RATE_CODES.SITEID);
        }
        return inverterNo;
    }

    /*private List<MonitorAPIResponse> getMonthlyGraphDataMongoForUser(Long userId, MonitorAPIAuthBody body, ExtDataStageDefinition extDataStageDefinition) throws ParseException {
        String inverterNumber = Utility.getMeasureAsJson(extDataStageDefinition.getMpJson(), Cfeature/Q2_23_S4_invoice_template<<<<onstants.RATE_CODES.EGDID);
        List<Date> labelDates = getDates(body.getTime());
        List<String> dates = labelDates.stream().map(d -> {
            String dt = Utility.getDateString(d, Utility.SYSTEM_DATE_FORMAT);
            return dt;
        }).collect(toList());
        List<MonitorReadingDaily> monthsData = dailyRepository.findByUserAndDayIn(userId, dates, inverterNumber);
        List<MonitorAPIResponse> responses = new ArrayList<>();
        getMaxDatesForXAxis(body.getTime()).forEach(d -> {
            Optional<MonitorReadingDaily> data = monthsData.stream().filter(m -> m.getDay().equals(d)).findFirst();
            if (data.isPresent()) {
                responses.add(MonitorReadingMapper.toMonitorAPIResponse(data.get()));
            } else {
                responses.add(MonitorAPIResponse.builder().dateTime(d).yieldValue(0d).build());
            }
        });
//        return MonitorReadingMapper.toMonitorAPIDailyResponses(dailyRepository.findByUserIdAndDayIn(user.getAcctId(), getDates(date)));
        return responses;
    }*/

    /*private List<MonitorAPIResponse> getMonthlyGraphDataMongoForVariant(String subId, MonitorAPIAuthBody body, ExtDataStageDefinition extDataStageDefinition) throws ParseException {
        String inverterNumber = Utility.getMeasureAsJson(extDataStageDefinition.getMpJson(), Constants.RATE_CODES.EGDID);
        List<Date> labelDates = getDates(body.getTime());
        List<String> dates = labelDates.stream().map(d -> {
            String dt = Utility.getDateString(d, Utility.SYSTEM_DATE_FORMAT);
            return dt;
        }).collect(toList());
        List<MonitorReadingDaily> monthsData = dailyRepository.findBySubscriptionAndDayIn(Long.valueOf(subId), dates, inverterNumber);
        List<MonitorAPIResponse> responses = new ArrayList<>();
        getMaxDatesForXAxis(body.getTime()).forEach(d -> {
            Optional<MonitorReadingDaily> data = monthsData.stream().filter(m -> m.getDay().equals(d)).findFirst();
            if (data.isPresent()) {
                responses.add(MonitorReadingMapper.toMonitorAPIResponse(data.get()));
            } else {
                responses.add(MonitorAPIResponse.builder().dateTime(d).yieldValue(0d).build());
            }
        });
//        return MonitorReadingMapper.toMonitorAPIDailyResponses(dailyRepository.findByUserIdAndDayIn(user.getAcctId(), getDates(date)));
        return responses;
    }*/

    private List<MonitorAPIResponse> getYearlyGraphDataMongoForSubscription(ExtDataStageDefinition ext, String year) throws ParseException {
        String firstDayOfMonth = "1";

        String inverterNumber;
        if (ext.getBrand().equals(Constants.RATE_CODES.BRAND_SOLRENVEIW)) {
            inverterNumber = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.SVDID);
        } else if (ext.getBrand().equals(Constants.RATE_CODES.BRAND_EGAUGE)) {
            inverterNumber = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.EGDID);
        } else if ((ext.getBrand().equals(Constants.RATE_CODES.BRAND_SOLAX)) ||
                (ext.getBrand().equals(Constants.RATE_CODES.BRAND_SOLIS)) ||
                (ext.getBrand().equals(Constants.RATE_CODES.BRAND_GOODWE))) {
            inverterNumber = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.INVRT);
        } else if ((ext.getBrand().equals(Constants.RATE_CODES.BRAND_SOLAR_EDGE))) {
            inverterNumber = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.INVRT);
        } else if ((ext.getBrand().equals(Constants.RATE_CODES.BRAND_GOODWE))) {
            inverterNumber = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.INVRT);
        } else if ((ext.getBrand().equals(Constants.RATE_CODES.BRAND_TIGO))) {
            inverterNumber = checkForInverter(ext.getMpJson());
        } else {
            inverterNumber = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.INVRT);
        }

        List<MonitorReadingDaily> monthsData = dailyRepository.findByInverterNumberAndYearAndSubId(year, inverterNumber, ext.getSubsId());
        List<MonitorAPIResponse> responses = new ArrayList<>();
        getBarXAxisLabelsForMonths().forEach(month -> {
            //YYYY-MM-DD
            String dateObj = year + "-" + month + "-" + firstDayOfMonth;
            try {
                getMaxDatesForXAxis(dateObj).forEach(d -> {
                    Optional<MonitorReadingDaily> data = monthsData.stream().filter(m -> m.getDay().equals(d)).findFirst();
                    if (data.isPresent()) {
                        responses.add(MonitorReadingMapper.toMonitorAPIResponse(data.get()));
                    } else {
                        responses.add(MonitorAPIResponse.builder().dateTime(d).yieldValue(0d).build());
                    }
                });
            } catch (ParseException e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
        return responses;
    }

    private MonitorAPIResponse getWidgetDataMongoForUser(ExtDataStageDefinition ext, MonitorAPIAuthBody body) throws ParseException {
        //User user = userService.findByUserName(userName);
        MonitorReading reading;
        //String inverterNumber = cs.getCustomerSubscriptionMappings().stream().filter(l-> "INVRT".equalsIgnoreCase(l.getRateCode())).findFirst().get().getValue();
        if (body.getTime() == null) {
            reading = readingRepository.getLastRecord(ext.getSubsId());
        } else {
            reading = readingRepository.findBySubscriptionIdMongoAndTime(ext.getSubsId(), formatDateTime.parse(body.getTime()));
        }
        return reading == null ? new MonitorAPIResponse() : MonitorAPIResponse.builder()
                .id(reading.getId())
                .inverterNumber(reading.getInverterNumber())
                .site(reading.getSite())
                .sytemSize(reading.getSytemSize())
                .currentValueToday(reading.getCurrentValueToday())
                .currentValue(reading.getCurrentValue())
                .currentValueRunning(reading.getCurrentValueRunning())
                .peakValue(reading.getPeakValue())
                .dailyYield(reading.getDailyYield())
                .monthlyYield(reading.getMonthlyYield())
//                .annualYield(reading.getAnnualYield() / 1000)
//                .grossYield(reading.getGrossYield() / 1000)
                .annualYield(reading.getAnnualYield())
                .grossYield(reading.getGrossYield())
                .dateTime(reading.getTime())
                .sytemSize(convertSystemSizeToDouble(ext.getSystemSize()))
                .build();
    }

    private List<MonitorAPIResponse> getMonthlyGraphDataForMongoSubscription(ExtDataStageDefinitionDTO ext, MonitorAPIAuthBody body) throws ParseException {
        String inverterNo;
        if (ext.getExtDataStageDefinition().getBrand().equals(Constants.RATE_CODES.BRAND_SOLRENVEIW)) {
            inverterNo = Utility.getMeasureAsJson(ext.getExtDataStageDefinition().getMpJson(), Constants.RATE_CODES.SVDID);
        } else if (ext.getExtDataStageDefinition().getBrand().equals(Constants.RATE_CODES.BRAND_EGAUGE)) {
            inverterNo = Utility.getMeasureAsJson(ext.getExtDataStageDefinition().getMpJson(), Constants.RATE_CODES.EGDID);
        } else if ((ext.getExtDataStageDefinition().getBrand().equals(Constants.RATE_CODES.BRAND_SOLAX)) ||
                (ext.getExtDataStageDefinition().getBrand().equals(Constants.RATE_CODES.BRAND_SOLIS)) ||
                (ext.getExtDataStageDefinition().getBrand().equals(Constants.RATE_CODES.BRAND_GOODWE))) {
            inverterNo = Utility.getMeasureAsJson(ext.getExtDataStageDefinition().getMpJson(), Constants.RATE_CODES.INVRT);
        } else if ((ext.getExtDataStageDefinition().getBrand().equals(Constants.RATE_CODES.BRAND_SOLAR_EDGE))) {
            inverterNo = Utility.getMeasureAsJson(ext.getExtDataStageDefinition().getMpJson(), Constants.RATE_CODES.INVRT);
        } else if ((ext.getExtDataStageDefinition().getBrand().equals(Constants.RATE_CODES.BRAND_GOODWE))) {
            inverterNo = Utility.getMeasureAsJson(ext.getExtDataStageDefinition().getMpJson(), Constants.RATE_CODES.INVRT);
        } else {
            inverterNo = Utility.getMeasureAsJson(ext.getExtDataStageDefinition().getMpJson(), Constants.RATE_CODES.INVRT);
        }

        List<Date> labelDates = getDates(body.getTime());
        List<String> dates = labelDates.stream().map(d -> {
            String dt = Utility.getDateString(d, Utility.SYSTEM_DATE_FORMAT);
            return dt;
        }).collect(toList());

        List<MonitorReadingDaily> monthData = dailyRepository.findByInverterNumberAndSubscriptionIdMongoAndDayIn(ext.getExtDataStageDefinition().getSubsId(), dates, inverterNo);
        List<MonitorAPIResponse> responses = new ArrayList<>();
        getMaxDatesForXAxis(body.getTime()).forEach(d -> {
            Optional<MonitorReadingDaily> data = monthData.stream().filter(m -> m.getDay().equals(d)).findFirst();
            if (data.isPresent()) {
                responses.add(MonitorReadingMapper.toMonitorAPIResponse(data.get()));
            } else {
                responses.add(MonitorAPIResponse.builder().dateTime(d).yieldValue(0d).build());
            }
        });
//        return MonitorReadingMapper.toMonitorAPIDailyResponses(dailyRepository.findByUserIdAndDayIn(user.getAcctId(), getDates(date)));
        return responses;
    }


    @Override
    public GraphDataMonthlyWrapper getMonthlyCustomerGraphData(MonitorAPIAuthBody body) throws ParseException {
        Map<Long, List<MonitorAPIResponse>> map = new TreeMap<>();
        Map<String, List<MonitorAPIResponse>> cumulativeMap = new TreeMap<>();
        List<CustomerSubscription> customerSubscriptions = null;
        List<String> filteredRateCodes = null;
        List<ExtDataStageDefinitionDTO> extDataStageDefinitionList = null;
        Map<String, List<MonitorAPIResponse>> mongoMap = new TreeMap<>();
        Map<String, List<MonitorAPIResponse>> cumulativeMongoMap = new TreeMap<>();
        Optional<TenantConfig> tenantConfig = null;
        try {
            tenantConfig = tenantConfigService.findByParameter(Constants.TENANT_CONFIG_CATEGORY.PARAM_ISMONGOENABLED);
        } catch (Exception ex) {
            ex.getMessage();
        }
        List<String> subscriptionIds = body.getSubscriptionIdsMongo() == null ? body.getProjectIds() : body.getSubscriptionIdsMongo();
        if (tenantConfig.isPresent() && tenantConfig.get().getText().equals("1")) {
            extDataStageDefinitionList = extDataStageDefinitionService.findAllSubsAndCustomerBySubIds(subscriptionIds);
            Map<Long, List<ExtDataStageDefinitionDTO>> extDataStageDefinitionMap = extDataStageDefinitionList.stream().collect(groupingBy(ExtDataStageDefinitionDTO::getAcctId, toList()));
            // below code will combine project based subscription in one list
            for (Long acctId : extDataStageDefinitionMap.keySet()) {
                for (ExtDataStageDefinitionDTO ext : extDataStageDefinitionMap.get(acctId)) {
                    List<MonitorAPIResponse> subscriptionMonthlyGraphData = getMonthlyGraphDataForMongoSubscription(ext, body);
                    if (mongoMap.get(String.valueOf(acctId)) != null) {
                        List<MonitorAPIResponse> exsistingProjectSubGraphDataList = mongoMap.get(String.valueOf(acctId));
                        exsistingProjectSubGraphDataList.addAll(subscriptionMonthlyGraphData);
                        mongoMap.put(String.valueOf(acctId), exsistingProjectSubGraphDataList);
                    } else {
                        mongoMap.put(String.valueOf(acctId), subscriptionMonthlyGraphData);
                    }
                }
            }

        } else {
            filteredRateCodes = getFilterRateCodes(customerSubscriptions).keySet().stream().collect(toList());
            for (String variantId : subscriptionIds) {
//                        mongoMap.put(variantId, getMonthlyGraphDataForVariant(variantId, body, filteredRateCodes));
            }
        }
        for (String acctId : mongoMap.keySet()) {
            List<MonitorAPIResponse> list = mongoMap.get(acctId);
            List<Date> dateTimes = mongoMap.values().stream().flatMap(m -> m.stream())
                    .map(MonitorAPIResponse::getDateTime).distinct().collect(toList());
            Map<Date, MonitorReadingDailyDTO> monthValues = getInverterAndYieldSum(dateTimes, list);

            List<MonitorAPIResponse> responses = new ArrayList<>();
            for (Map.Entry<Date, MonitorReadingDailyDTO> set : monthValues.entrySet()) {
                responses.add(MonitorAPIResponse.builder()
                        .yieldValue(set.getValue().getYieldValue())
                        .inverterNumbers(set.getValue().getInverterNumbers())
                        .dateTime(set.getKey())
                        .detailData(set.getValue().getDataDTO())
                        .build());
            }
            cumulativeMongoMap.put(acctId, responses);
        }

        return GraphDataMonthlyWrapper.builder()
                .monthlyGraphData(cumulativeMongoMap)
                .xAxis(getBarXAxisLabels(body.getTime()))
                .build();
    }

    @Override
    public GraphDataYearlyWrapper getYearlyCustomerGraphData(MonitorAPIAuthBody body) throws ParseException {
        Map<Long, List<MonitorAPIResponse>> map = new TreeMap<>();
        Map<String, List<MonitorAPIResponse>> cumulativeMap = new TreeMap<>();
        List<CustomerSubscription> customerSubscriptions = null;
        List<String> filteredRateCodes = null;
        List<ExtDataStageDefinitionDTO> extDataStageDefinitionList = null;
        Map<String, List<MonitorAPIResponse>> mongoMap = new TreeMap<>();
        Map<String, List<MonitorAPIResponse>> cumulativeMapMongo = new TreeMap<>();
        String year = body.getTime();
        Optional<TenantConfig> tenantConfig = null;
        //comparison is true and isSubscriptionComparison false then execute this block for user
        try {
            tenantConfig = tenantConfigService.findByParameter(Constants.TENANT_CONFIG_CATEGORY.PARAM_ISMONGOENABLED);
        } catch (Exception ex) {
            ex.getMessage();
        }
        List<String> subscriptionIds = body.getSubscriptionIdsMongo() == null ? body.getProjectIds() : body.getSubscriptionIdsMongo();
        if (tenantConfig.isPresent() && tenantConfig.get().getText().equals("1")) {
            extDataStageDefinitionList = extDataStageDefinitionService.findAllSubsAndCustomerBySubIds(subscriptionIds);
            Map<Long, List<ExtDataStageDefinitionDTO>> extDataStageDefinitionMap = extDataStageDefinitionList.stream().collect(groupingBy(ExtDataStageDefinitionDTO::getAcctId, toList()));
            // below code will combine project based subscription in one list
            for (Long acctId : extDataStageDefinitionMap.keySet()) {
                for (ExtDataStageDefinitionDTO ext : extDataStageDefinitionMap.get(acctId)) {
                    List<MonitorAPIResponse> subscriptionYearlyGraphData = getYearlyGraphDataMongoForSubscription(ext, body.getTime());
                    if (mongoMap.get(String.valueOf(acctId)) != null) {
                        List<MonitorAPIResponse> exsistingProjectSubGraphDataList = mongoMap.get(String.valueOf(acctId));
                        exsistingProjectSubGraphDataList.addAll(subscriptionYearlyGraphData);
                        mongoMap.put(String.valueOf(acctId), exsistingProjectSubGraphDataList);
                    } else {
                        mongoMap.put(String.valueOf(acctId), subscriptionYearlyGraphData);
                    }
                }
            }

        } else {
            List<Long> userIds = body.getUserIds();
            customerSubscriptions = customerSubscriptionRepository.findAllByUserIdInFetchCustomerSubscriptionMappings(userIds);
            filteredRateCodes = getFilterRateCodes(customerSubscriptions).keySet().stream().collect(toList());
        }
        for (String acctId : mongoMap.keySet()) {
            List<MonitorAPIResponse> list = mongoMap.get(acctId);
            List<String> months = getBarXAxisLabelsForMonths();
            Map<String, MonitorReadingDailyDTO> monthValueMap = getInverterAndYieldSum(months, list, year);
            Map<String, MonitorReadingDailyDTO> monthValues = sortByKey(monthValueMap);
            List<MonitorAPIResponse> responses = new ArrayList<>();
            for (Map.Entry<String, MonitorReadingDailyDTO> set : monthValues.entrySet()) {
                responses.add(MonitorAPIResponse.builder()
                        .month(set.getKey())
                        .yieldValue(set.getValue().getYieldValue())
                        .inverterNumbers(set.getValue().getInverterNumbers())
                        .detailData(set.getValue().getDataDTO())
                        .build());
            }
            cumulativeMap.put(String.valueOf(acctId), responses);
        }
        return GraphDataYearlyWrapper.builder()
                .yearlyGraphData(cumulativeMap.isEmpty() ? cumulativeMapMongo : cumulativeMap)
                .xAxis(getBarXAxisLabelsForMonths())
                .build();
    }

    private List<MonitorAPIResponse> getYearlyGraphDataMongoForSubscription(ExtDataStageDefinitionDTO ext, String year) throws ParseException {
        String firstDayOfMonth = "1";
        String inverterNo;
        if (ext.getExtDataStageDefinition().getBrand().equals(Constants.RATE_CODES.BRAND_SOLRENVEIW)) {
            inverterNo = Utility.getMeasureAsJson(ext.getExtDataStageDefinition().getMpJson(), Constants.RATE_CODES.SVDID);
        } else if (ext.getExtDataStageDefinition().getBrand().equals(Constants.RATE_CODES.BRAND_EGAUGE)) {
            inverterNo = Utility.getMeasureAsJson(ext.getExtDataStageDefinition().getMpJson(), Constants.RATE_CODES.EGDID);
        } else if ((ext.getExtDataStageDefinition().getBrand().equals(Constants.RATE_CODES.BRAND_SOLAX)) ||
                (ext.getExtDataStageDefinition().getBrand().equals(Constants.RATE_CODES.BRAND_SOLIS)) ||
                (ext.getExtDataStageDefinition().getBrand().equals(Constants.RATE_CODES.BRAND_GOODWE))) {
            inverterNo = Utility.getMeasureAsJson(ext.getExtDataStageDefinition().getMpJson(), Constants.RATE_CODES.INVRT);
        } else if ((ext.getExtDataStageDefinition().getBrand().equals(Constants.RATE_CODES.BRAND_SOLAR_EDGE))) {
            inverterNo = Utility.getMeasureAsJson(ext.getExtDataStageDefinition().getMpJson(), Constants.RATE_CODES.INVRT);
        } else if ((ext.getExtDataStageDefinition().getBrand().equals(Constants.RATE_CODES.BRAND_GOODWE))) {
            inverterNo = Utility.getMeasureAsJson(ext.getExtDataStageDefinition().getMpJson(), Constants.RATE_CODES.INVRT);
        } else {
            inverterNo = Utility.getMeasureAsJson(ext.getExtDataStageDefinition().getMpJson(), Constants.RATE_CODES.INVRT);
        }
        List<MonitorReadingDaily> monthsData = dailyRepository.findByInverterNumberAndYearAndSubId(year, inverterNo, ext.getExtDataStageDefinition().getSubsId());
        List<MonitorAPIResponse> responses = new ArrayList<>();
        getBarXAxisLabelsForMonths().forEach(month -> {
            //YYYY-MM-DD
            String dateObj = year + "-" + month + "-" + firstDayOfMonth;
            try {
                getMaxDatesForXAxis(dateObj).forEach(d -> {
                    Optional<MonitorReadingDaily> data = monthsData.stream().filter(m -> m.getDay().equals(d)).findFirst();
                    if (data.isPresent()) {
                        responses.add(MonitorReadingMapper.toMonitorAPIResponse(data.get()));
                    } else {
                        responses.add(MonitorAPIResponse.builder().dateTime(d).yieldValue(0d).build());
                    }
                });
            } catch (ParseException e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
        return responses;
    }

    @Override
    public GraphDataWrapper getCurrentCustomerGraphData(MonitorAPIAuthBody body) throws ParseException {
        List<CustomerSubscription> customerSubscriptions = null;
        Map<String, List<MonitorAPIResponse>> mapMongo = new TreeMap<>();
        List<ExtDataStageDefinitionDTO> extDataStageDefinitionList = null;
        Map<String, List<MonitorAPIResponse>> cumulativeMongoMap = new TreeMap<>();
        Map<String, List<MonitorAPIResponse>> mapString = new TreeMap<>();
        Map<String, List<MonitorAPIResponse>> cumulativeMapString = new TreeMap<>();
        try {
            Optional<TenantConfig> tenantConfig = tenantConfigService.findByParameter(Constants.TENANT_CONFIG_CATEGORY.PARAM_ISMONGOENABLED);
            List<String> subscriptionIds = body.getSubscriptionIdsMongo() == null ? body.getProjectIds() : body.getSubscriptionIdsMongo();
//                        == null? body.getSubscriptionIdsMongo() : body.getProjectIds();
            if (tenantConfig.isPresent() && tenantConfig.get().getText().equals("1")) {
                extDataStageDefinitionList = extDataStageDefinitionService.findAllSubsAndCustomerBySubIds(subscriptionIds);
                Map<Long, List<ExtDataStageDefinitionDTO>> extDataStageDefinitionMap = extDataStageDefinitionList.stream().collect(groupingBy(ExtDataStageDefinitionDTO::getAcctId, toList()));
                for (Long acctId : extDataStageDefinitionMap.keySet()) {
                    for (ExtDataStageDefinitionDTO ext : extDataStageDefinitionMap.get(acctId)) {
                        List<MonitorAPIResponse> subscriptionMongoGraphData = getGraphDataForMongoSubscription(ext, body);
                        if (mapMongo.get(String.valueOf(acctId)) != null) {
                            List<MonitorAPIResponse> exsistingProjectSubGraphDataList = mapMongo.get(String.valueOf(acctId));
                            exsistingProjectSubGraphDataList.addAll(subscriptionMongoGraphData);
                            mapMongo.put(String.valueOf(acctId), exsistingProjectSubGraphDataList);
                        } else {
                            mapMongo.put(String.valueOf(acctId), subscriptionMongoGraphData);
                        }
                    }
                }
            } else {
                List<Long> userIds = body.getUserIds();
                customerSubscriptions = customerSubscriptionRepository.findAllByUserIdInFetchCustomerSubscriptionMappings(userIds);
//                        filteredRateCodes = getFilterRateCodes(customerSubscriptions).keySet().stream().collect(toList());
            }
            for (String acctId : mapMongo.keySet()) {
                List<MonitorAPIResponse> list = mapMongo.get(acctId);
                List<Date> dateTimes = mapMongo.values().stream().flatMap(m -> m.stream())
                        .map(MonitorAPIResponse::getDateTime).distinct().collect(toList());
                Map<Date, MonitorReadingDailyDTO> monthValues = getInverterAndYieldSum(dateTimes, list);

                List<MonitorAPIResponse> responses = new ArrayList<>();
                if (!monthValues.isEmpty()) {
                    for (Map.Entry<Date, MonitorReadingDailyDTO> set : monthValues.entrySet()) {
                        set.getKey().getTimezoneOffset();
                        responses.add(MonitorAPIResponse.builder()
                                .yieldValue(set.getValue().getYieldValue() != null ? set.getValue().getYieldValue() : null)
                                .inverterNumbers(set.getValue().getInverterNumbers() != null ? set.getValue().getInverterNumbers() : null)
                                .dateTime(set.getKey())
                                .detailData(set.getValue().getDataDTO() != null ? set.getValue().getDataDTO() : null)
                                .build());
                    }
                }
                cumulativeMapString.put(acctId, responses);
            }

        } catch (Exception ex) {
            ex.getMessage();
        }

        return GraphDataWrapper.builder()
                .graphData(cumulativeMapString.isEmpty() ? cumulativeMongoMap : cumulativeMapString)
                .xAxis(getXAxisLabels(body.getTime()))
                .build();

    }

    private List<MonitorAPIResponse> getGraphDataForMongoSubscription(ExtDataStageDefinitionDTO ext, MonitorAPIAuthBody body) throws ParseException {
        String mp = Utility.getMeasureAsJson(ext.getExtDataStageDefinition().getMpJson(), Constants.RATE_CODES.MP);
        List<MonitorReading> daysData = externalAPIFactory.get(mp).getMonitorReadingDataForMongoComparison(body, ext.getExtDataStageDefinition().getSubsId());
        List<MonitorAPIResponse> responses = new ArrayList<>();
        List<MonitorReading> finalDaysData = daysData;
        getMaxDateTimesForXAxis(body.getTime()).forEach(d -> {
            Optional<MonitorReading> data = finalDaysData.stream().filter(m ->
                    m.getTime().getTime() == d.getTime()).findFirst();
            if (data.isPresent()) {
                responses.add(MonitorReadingMapper.toMonitorAPIResponse(data.get()));
            } else {
                responses.add(MonitorAPIResponse.builder().dateTime(d).yieldValue(0d).build());
            }
        });
        return responses;
    }

    private Double convertSystemSizeToDouble(String systemSize) {
        String cleanedSize = systemSize != null ? systemSize.replaceAll("[^0-9.]", "").trim() : "0";
        return Double.parseDouble(cleanedSize);
    }


    @Async
    @Override
    public List<MonitorReading> saveCurrentDataMongoBatch(MonitorAPIAuthBody authBody) throws UnsupportedEncodingException, JsonProcessingException, ParseException {
        List<ExtDataStageDefinition> es = extDataStageDefinitionService.findAllBySubsIdIn(authBody.getSubscriptionIdsMongo());
        List<MonitorReading> pvPowerReadings = new ArrayList<>();
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(Utility.SYSTEM_DATE_TIME_FORMAT);
        SimpleDateFormat dateFormat = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);

        for (ExtDataStageDefinition ext : es) {
            try {
                MonitorReading lastRecord = readingRepository.getLastRecord(ext.getSubsId());
                String authBodyDtTime = lastRecord.getTime().toString();
                String mp = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.MP);
                String inverterNumber = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.INVERTER_NUMBER);
                String ssdt = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.SUB_START_DATE);

                Date dateTime = null;
                if (authBodyDtTime != null) {
                    dateTime = dateTimeFormat.parse(authBodyDtTime);
                } else {
                    dateTime = dateFormat.parse(ssdt);
                }
                authBody.setTime(dateTimeFormat.format(dateTime));
                while (dateTimeFormat.parse(authBody.getTime()).before(new Date())) {

                    MonitorAPIResponse apiResponse = null;
                    //false for solis widget data

                    Date lastSavedRecord = lastRecord != null ? lastRecord.getTime() : null;
                    apiResponse = externalAPIFactory.get(mp).getCurrentData(ext, authBody.getTime(), false, lastSavedRecord);
                    Double currentValueRunning = lastRecord != null ? lastRecord.getCurrentValueRunning() : null;
                    Double yieldValueRunning = lastRecord != null ? lastRecord.getYieldValueRunning() : null;
                    if (apiResponse.getInverterValuesOverTime() != null) {
                        for (Map.Entry<String, Map<Date, MonitorAPIResponseL2>> set :
                                apiResponse.getInverterValuesOverTime().entrySet()) {
                            for (Map.Entry<Date, MonitorAPIResponseL2> m :
                                    set.getValue().entrySet()) {
                                currentValueRunning = currentValueRunning != null ? currentValueRunning + m.getValue().getPvPower() :
                                        m.getValue().getPvPower();
                                yieldValueRunning = yieldValueRunning != null ? yieldValueRunning + m.getValue().getGridpower() :
                                        m.getValue().getGridpower();

                                pvPowerReadings.add(
                                        MonitorReading.builder()
                                                .subscriptionIdMongo(ext.getSubsId())
                                                .inverterNumber(set.getKey())
                                                .sytemSize(apiResponse.getSytemSize())
                                                .currentValueToday(apiResponse.getCurrentValueToday())
                                                .currentValue(m.getValue().getPvPower())
                                                .currentValueRunning(currentValueRunning)
                                                .yieldValue(m.getValue().getGridpower())
                                                .yieldValueRunning(yieldValueRunning)
                                                .peakValue(apiResponse.getPeakValue())
                                                .dailyYield(apiResponse.getDailyYield() != null ? apiResponse.getDailyYield() : m.getValue().getEToday())
                                                .monthlyYield(apiResponse.getMonthlyYield() != null ? apiResponse.getMonthlyYield() : m.getValue().getEMonth())
                                                .annualYield(apiResponse.getAnnualYield() != null ? apiResponse.getAnnualYield() : m.getValue().getEYear())
                                                .grossYield(apiResponse.getGrossYield() != null ? apiResponse.getGrossYield() : m.getValue().getETotal())
//                                .time(Utility.deductMinutes(m.getKey(), 300))
                                                .time(m.getKey())
                                                .build());
                            }
                        }

                        MonitorReadingDaily mrd = dailyRepository.getLastSavedRecordByMongoSubIdAndDate(ext.getSubsId(), authBody.getTime().split(" ")[0]);
                        if (mrd == null) {
                            MonitorReadingDaily monitorReadingDaily = dailyRepository.save(MonitorReadingDaily.builder()
                                    .subscriptionIdMongo(ext.getSubsId())
                                    .inverterNumber(inverterNumber)
                                    .yieldValue(apiResponse.getDailyYield())
//                                    .variantId(ext.getRefId())
                                    .day(formatDate.parse(authBody.getTime().split(" ")[0]))
                                    .build());
                        } else {
                            mrd.setYieldValue(apiResponse.getDailyYield());
                            dailyRepository.save(mrd);
                        }
                    }
                    dateTime = Utility.addDays(dateTime, 1);
                    authBody.setTime(dateTimeFormat.format(dateTime));
                }
            } catch (Exception e) {
                try {
                    //send email when there is exception in api call
                    String mp = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.MP);
                    String inverterNumber = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.INVERTER_NUMBER);
                    String loginName = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.LGNM);

                    emailService.monitoringApiNotification(appProfile, ext.getSubsId(), inverterNumber, loginName, mp, Utility.getStackTrace(e));
                    LOGGER.error("CustomerSubscription[Id:" + ext.getSubsId() +
                            ", inverterNumber:" + inverterNumber + ", loginName:" + loginName + ", monitoringPlatform:" + mp + "], ErrorMsg=" + Utility.getStackTrace(e));
                    LOGGER.error(e.getMessage(), e);
                } catch (IOException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }
        return readingRepository.saveAll(pvPowerReadings);

    }

    @Override
    public BaseResponse loadFilterData(String exportDTO) {
        ExportDTO filterDTO = new ExportDTO();
        try {

            if (exportDTO != null) {
                filterDTO = new ObjectMapper().readValue(exportDTO, ExportDTO.class);
                List<PortalAttributeValueSAAS> portalAttributeValueSAAS = portalAttributeSAASService.findByPortalAttributeName(AppConstants.POWER_DATA_EXPORT);
                List<String> periodList = portalAttributeValueSAAS != null ?
                        portalAttributeValueSAAS.stream().map(PortalAttributeValueSAAS::getAttributeValue).collect(toList()) : null;
                List<String> variantIds = filterDTO.getProjects().stream().map(DataDTO::getId).collect(Collectors.toList());
                filterDTO.setSubscriptions(extDataStageDefinitionService.findAllSubscriptionsByVariantIdForFilters(variantIds));
                filterDTO.setCustomers(extDataStageDefinitionService.findAllCustomersByVariantIdForFilters(variantIds));
                filterDTO.setPeriod(periodList);
            } else {
                filterDTO.setProjects(extDataStageDefinitionService.findAllProjectsForFilters());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message("ERROR").data(e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(filterDTO).build();
    }

    @Override
    public BaseResponse getMonitorReadingExportData(String type, String subsIds, String startDate, String endDate, Boolean isCust, Integer pageNumber, Integer pageSize) {
        DataExportPMPaginationTile result = new DataExportPMPaginationTile();
        try {

            Page<DataExportPMTile> page = null;
            List<String> subsArray = Arrays.stream(subsIds.split(",")).collect(Collectors.toList());
            if (endDate == null) {
                endDate = startDate;
            }
            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
            if (type.equalsIgnoreCase(AppConstants.DAY_WISE)) {
                page = readingRepository.findDayWiseExportDataMR(subsArray, startDate, endDate, isCust, pageRequest);
            }
            if (type.equalsIgnoreCase(AppConstants.DAILY)) {
                page = dailyRepository.findDailyExportDataMR(subsArray, startDate, endDate, isCust, pageRequest);
            }
            if (type.equalsIgnoreCase(AppConstants.MONTHLY)) {
                page = dailyRepository.findMonthlySummedExportDataMR(subsArray, startDate, endDate, isCust, pageRequest);
            }
            result.setDataExportPMTileList(page.getContent());
            result.setTotalPages(page.getTotalPages());
            result.setTotalElements(page.getTotalElements());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message("ERROR").data(e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(result).build();
    }

    @Override
    public BaseResponse getPercentileByMonthAndSub(String month, String subsId) {
        try {

            List<PowerMonitorPercentileDTO> finalData = generatePercentileByMonthAndSub(month, subsId);
            return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(finalData).build();

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message("ERROR").data(e.getMessage()).build();
        }
    }

    @Override
    public List<PowerMonitorPercentileDTO> generatePercentileByMonthAndSub(String month, String subsId) {

        List<PowerMonitorPercentileDTO> finalData = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth yearMonth = YearMonth.parse(month, dtf);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();  // exclusive end date
        int rounding = utility.getCompanyPreference().getRounding();
        List<PowerMonitorPercentileDTO> rawData = dailyRepository.findPercentileDTOByMonthAndSubsId(month, subsId);
        Map<LocalDate, PowerMonitorPercentileDTO> rawDataByDate = rawData.stream().collect(
                Collectors.toMap(
                        dto -> Instant.ofEpochMilli(dto.getDay().getTime()).atZone(ZoneId.systemDefault()).toLocalDate(),
                        dto -> dto,
                        (dto1, dto2) -> dto1.getYield() > dto2.getYield() ? dto1 : dto2));

        finalData = Stream.iterate(start, date -> !date.isAfter(end), date -> date.plusDays(1))
                .map(date -> rawDataByDate.getOrDefault(date, new PowerMonitorPercentileDTO(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()), 0.0, 0.0)))
                .collect(Collectors.toList());

        List<Double> sortedYields = finalData.stream()
                .map(PowerMonitorPercentileDTO::getYield)
                .sorted()
                .collect(Collectors.toList());

        for (PowerMonitorPercentileDTO powerMonitorPercentileDTO : finalData) {
            if (powerMonitorPercentileDTO.getYield() != 0) {
                double p = calculatePercentile(sortedYields, powerMonitorPercentileDTO.getYield());
                powerMonitorPercentileDTO.setPercentile(utility.round(p, rounding));
            } else {
                powerMonitorPercentileDTO.setPercentile(1.0);
            }
        }
        Collections.reverse(finalData);
        return finalData;
    }

    private double calculatePercentile(List<Double> sortedValues, double value) {
        int index = Collections.binarySearch(sortedValues, value);
        if (index < 0) {
            return 0.0;
        } else {
            return Math.min(90.0, 100.0 * index / (sortedValues.size() - 1));
        }
    }

    @Async
    @Override
    public void dataConversionForBillingCredits(String month, Long jobId) {
        List<BillingCredits> finalBillingCredits = new ArrayList<>();
        List<BillingCreditsPMDTO> billingCreditsPMDTO = dailyRepository.findMonthlySummedDataForBillingCredits(month);
        List<String> premiseNoList = billingCreditsPMDTO.stream().map(item -> Utility.getMeasureAsJson(item.getMpJson(), Constants.RATE_CODES.S_PN)).distinct().collect(Collectors.toList());
        List<String> gardenSrcList = billingCreditsPMDTO.stream().map(BillingCreditsPMDTO::getGardenSrc).distinct().collect(Collectors.toList());

        List<BillingCredits> existingCreditList = billingCreditsService.findAllByPremiseNoAndMonthAndGardenSrc(premiseNoList, month, gardenSrcList);
        List<BillingCreditsPMDTO> filteredBillingCreditsPMDTO = filterBillingCreditsPMDTO(existingCreditList, billingCreditsPMDTO);

        filteredBillingCreditsPMDTO.stream().forEach(reading -> {
            Long creditId = null;
            String premiseNo = Utility.getMeasureAsJson(reading.getMpJson(), Constants.RATE_CODES.S_PN);
            Optional<BillingCredits> foundCreditItem = existingCreditList.stream()
                    .filter(billingCredit ->
                            Boolean.TRUE.equals(billingCredit.getImported())
                                    && reading.getGardenSrc().equalsIgnoreCase(billingCredit.getGardenId())
                                    && month.equalsIgnoreCase(billingCredit.getCalendarMonth())
                                    && premiseNo.equalsIgnoreCase(billingCredit.getCreditCodeVal()))
                    .findFirst();
            if (foundCreditItem.isPresent()) {
                creditId = foundCreditItem.get().getId();
            }
            finalBillingCredits.add(BillingCredits.builder().id(creditId).tariffRate(0d).subscriptionCode(reading.getSubsId())
                    .mpa(reading.getMpa()).jobId(jobId).importType(AppConstants.POWER_MONITORING).gardenId(reading.getGardenSrc())
                    .creditValue(0d).creditCodeVal(premiseNo).creditCodeType(AppConstants.CREDIT_CODE_TYPE_S).calendarMonth(month).imported(false).build());

        });
        if (finalBillingCredits.size() > 0) {
            billingCreditsRepository.saveAll(finalBillingCredits);
        }
    }

    private List<BillingCreditsPMDTO> filterBillingCreditsPMDTO(List<BillingCredits> existingCreditList, List<BillingCreditsPMDTO> billingCreditsPMDTO) {
        return billingCreditsPMDTO.stream()
                .filter(billingCreditPM -> {
                    String premiseNo = Utility.getMeasureAsJson(billingCreditPM.getMpJson(), Constants.RATE_CODES.S_PN);
                    return existingCreditList.stream()
                            .noneMatch(billingCredit ->
                                    (billingCredit.getImported() == null || !billingCredit.getImported())
                                            && billingCredit.getGardenId().equals(billingCreditPM.getGardenSrc())
                                            && billingCredit.getCalendarMonth().equals(billingCreditPM.getDate())
                                            && billingCredit.getCreditCodeVal().equals(premiseNo));
                })
                .collect(Collectors.toList());
    }


    private List<MonitorAPIResponse> getDateWiseDataForMongoProjection(String mongoProjectionId, String projectionPeriod) throws ParseException {
        String firstDayOfMonth = "1";
        List<MonitorReadingDaily> monthsData = dailyRepository.findByProjectionId(mongoProjectionId);
        List<MonitorAPIResponse> responses = new ArrayList<>();
        List<Integer> years = monthsData.stream()
                .map(obj -> obj.getDay().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear()).collect(toList());
        years.stream().forEach(year -> {
            try {
                getBarXAxisLabelsForMonths().forEach(month -> {
                    //YYYY-MM-DD
                    String dateObj = year + "-" + month + "-" + firstDayOfMonth;
                    try {
                        getMaxDatesForXAxis(dateObj).forEach(d -> {
                            Optional<MonitorReadingDaily> data = monthsData.stream().filter(m -> m.getDay().equals(d)).findFirst();
                            if (data.isPresent()) {
                                responses.add(MonitorReadingMapper.toMonitorAPIResponse(data.get()));
                            } else {
                                responses.add(MonitorAPIResponse.builder().dateTime(d).yieldValue(0d).build());
                            }
                        });
                    } catch (ParseException e) {
                    }
                });
            } catch (ParseException e) {
            }
        });
        return responses;
    }

    @Override
    public ProjectionDataWrapper getYearlyMongoProjectionData(String mongoProjectionId, String gardenId) {
        //get projected data
        List<ProjectionTileDTO> projectedYearsData = dailyRepository.findTotalYieldByProjectionIdForYearly(mongoProjectionId);
        //get actual yield data for all subs of garden id (for given years list)
        List<ExtDataStageDefinition> extDataStageDefinitionList = extDataStageDefinitionService.findAllByRefIdIn(Arrays.asList(gardenId), "ACTIVE");
        List<String> years = projectedYearsData.stream().map(obj -> obj.getDay().split("-")[0]).distinct().collect(Collectors.toList());
        String startYear = years.get(0);
        String endYear = years.get(years.size() - 1);
        List<MonitorReadingYearWise> yearWiseActualYieldList = monitoringDashboardYearWiseRepository.findYieldSumByStartAndEndYearAndSubIds(startYear, endYear, extDataStageDefinitionList.stream().map(ExtDataStageDefinition::getSubsId).collect(Collectors.toList()));
        //finalizing data on output list
        years.stream().forEach(year -> {
            //get actual data from yearWiseList for match years
            Double actualYieldData = yearWiseActualYieldList.stream().filter(mrdy -> mrdy.getDay().equals(year))
                    .mapToDouble(MonitorReadingYearWise::getYield).findFirst().orElse(0.0);
            //set actual data yearsData list for match years
            Double finalData = actualYieldData;
            projectedYearsData.stream().filter(obj -> obj.getDay().split("-")[0].equalsIgnoreCase(year)).forEach(obj -> {
                obj.setActualYield(finalData);
            });
        });
        return ProjectionDataWrapper.builder()
                .data(projectedYearsData.isEmpty() ? null : projectedYearsData)
                .yAxis(null)
                .build();
    }

    @Transactional
    @Override
    public MasterProjectionDataWrapper getMonthlyMongoProjectionDataForYears(String mongoProjectionId) {
        try {
            dailyRepository.createDBYearsTemporaryTable(mongoProjectionId);
            dailyRepository.createAllMonthsTemporaryTable();
            List<ProjectionTileDTO> monthlyDataForYears = dailyRepository.findTotalYieldByProjectionIdForMonthly(mongoProjectionId)
                    .stream()
                    .map(ProjectionTileDTO::new)
                    .collect(Collectors.toList());
            List<String> years = monthlyDataForYears.stream().map(obj -> obj.getDay().split("-")[0]).distinct().collect(Collectors.toList());
            List<ProjectionDataWrapper> projectionDataWrapperList = new ArrayList<>();
            //get actual yield data for all subs of garden id (for given years list)
            //List<ExtDataStageDefinition> extDataStageDefinitionList = extDataStageDefinitionService.findAllByRefIdIn(Arrays.asList(gardenId), "ACTIVE");
            //String startYear = years.get(0);
            //String endYear = years.get(years.size() - 1);
           // List<MonitorReadingMonthWise> monitorReadingMonthWise = monitoringDashboardMonthWiseRepository.findYieldSumByStartYearAndEndYearAndSubId(startYear, endYear, extDataStageDefinitionList.stream().map(ExtDataStageDefinition::getSubsId).collect(Collectors.toList()));
            // Group monitorReadingMonthWise by the 'month' attribute
           // Map<String, List<MonitorReadingMonthWise>> groupedByMonth = monitorReadingMonthWise.stream()
             //       .collect(Collectors.groupingBy(MonitorReadingMonthWise::getDay));

            years.stream().forEach(year -> {
                List<ProjectionTileDTO> data = monthlyDataForYears.stream().filter(obj -> obj.getDay().split("-")[0].equalsIgnoreCase(year)).collect(toList());
                projectionDataWrapperList.add(ProjectionDataWrapper.builder().year(year).data(data).build());
            });
            dailyRepository.dropTemporaryTableAllMonths();
            dailyRepository.dropTemporaryTableAllYears();
            return MasterProjectionDataWrapper.builder()
                    .data(projectionDataWrapperList.isEmpty() ? null : projectionDataWrapperList)
                    .yAxis(years)
                    .build();
        } catch (Exception e) {
            dailyRepository.dropTemporaryTableAllMonths();
            dailyRepository.dropTemporaryTableAllYears();
        }
        return MasterProjectionDataWrapper.builder()
                .data(null)
                .yAxis(null)
                .build();
    }

    @Transactional
    @Override
    public MasterProjectionDataWrapper getMonthlyMongoProjectionDataForDates(String mongoProjectionId) {
        try {
            dailyRepository.createTemporaryTable(mongoProjectionId);
            List<ProjectionTileDTO> monthlyDatesDataForYears = dailyRepository.findTotalYieldByProjectionIdForMonthlyDates(mongoProjectionId)
                    .stream()
                    .map(ProjectionTileDTO::new)
                    .collect(Collectors.toList());
            List<String> years = monthlyDatesDataForYears.stream().map(obj -> obj.getDay().split("-")[0]).distinct().collect(Collectors.toList());
            List<ProjectionDataWrapper> projectionDataWrapperList = new ArrayList<>();
            years.stream().forEach(year -> {
                List<ProjectionTileDTO> data = monthlyDatesDataForYears.stream().filter(obj -> obj.getDay().split("-")[0].equalsIgnoreCase(year)).collect(toList());
                projectionDataWrapperList.add(ProjectionDataWrapper.builder().year(year).data(data).build());
            });
            dailyRepository.dropTemporaryTableDate_range();
            return MasterProjectionDataWrapper.builder()
                    .data(projectionDataWrapperList.isEmpty() ? null : projectionDataWrapperList)
                    .yAxis(years)
                    .build();
        } catch (Exception e) {
            dailyRepository.dropTemporaryTableDate_range();
        }
        return MasterProjectionDataWrapper.builder()
                .data(null)
                .yAxis(null)
                .build();
    }

    @Transactional
    @Override
    public MasterProjectionDataWrapper getMongoProjectionDataForQuarterly(String mongoProjectionId) {
        try {
            dailyRepository.createQuartersTemporaryTable(mongoProjectionId);
            List<ProjectionTileDTO> monthlyDatesDataForYears = dailyRepository.findTotalYieldByProjectionIdForQuarterly(mongoProjectionId)
                    .stream()
                    .map(ProjectionTileDTO::new)
                    .collect(Collectors.toList());
            List<String> years = monthlyDatesDataForYears.stream().map(obj -> obj.getDay()).distinct().collect(Collectors.toList());
            List<ProjectionDataWrapper> projectionDataWrapperList = new ArrayList<>();
            years.stream().forEach(year -> {
                List<ProjectionTileDTO> data = monthlyDatesDataForYears.stream().filter(obj -> obj.getDay().equalsIgnoreCase(year)).collect(toList());
                projectionDataWrapperList.add(ProjectionDataWrapper.builder().year(year).data(data).build());
            });
            dailyRepository.dropTemporaryTableQuarterCalendar();
            return MasterProjectionDataWrapper.builder()
                    .data(projectionDataWrapperList.isEmpty() ? null : projectionDataWrapperList)
                    .yAxis(years)
                    .build();
        } catch (Exception e) {
            dailyRepository.dropTemporaryTableQuarterCalendar();
        }
        return MasterProjectionDataWrapper.builder()
                .data(null)
                .yAxis(null)
                .build();
    }
}