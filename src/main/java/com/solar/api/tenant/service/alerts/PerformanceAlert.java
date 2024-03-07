package com.solar.api.tenant.service.alerts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.api.helper.Utility;
import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.repository.MonitorReadingDailyRepository;
import com.solar.api.tenant.repository.PhysicalLocationRepository;
import com.solar.api.tenant.repository.TenantConfigRepository;
import com.solar.api.tenant.repository.stage.monitoring.ExtDataStageDefinitionRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.solar.api.Constants.ALERTS.MP;
import static com.solar.api.Constants.ALERTS.*;
import static com.solar.api.Constants.RATE_CODES.*;
import static com.solar.api.helper.Utility.getCurrentMonthName;

@Service
public class PerformanceAlert implements Alert {

    protected final Logger LOGGER = LoggerFactory.getLogger(UnderPerformanceAlert.class);
    private static final int VERTICAL_BAR_HEIGHT = 250;
    private static final int HORIZONTAL_BAR_WIDTH = 400;
    private final MonitorReadingDailyRepository monitorReadingDailyRepository;
    private final AlertService alertService;
    private final ExtDataStageDefinitionRepository extDataStageDefinitionRepository;
    private final PhysicalLocationRepository physicalLocationRepository;
    private final TenantConfigRepository tenantConfigRepository;
    private final Utility utility;
    private final ProjectionUtils projectionUtils;

    public PerformanceAlert(MonitorReadingDailyRepository monitorReadingDailyRepository, AlertService alertService,
                            ExtDataStageDefinitionRepository extDataStageDefinitionRepository,
                            PhysicalLocationRepository physicalLocationRepository,
                            TenantConfigRepository tenantConfigRepository, Utility utility, ProjectionUtils projectionUtils) {
        this.monitorReadingDailyRepository = monitorReadingDailyRepository;
        this.alertService = alertService;
        this.extDataStageDefinitionRepository = extDataStageDefinitionRepository;
        this.physicalLocationRepository = physicalLocationRepository;
        this.tenantConfigRepository = tenantConfigRepository;
        this.utility = utility;
        this.projectionUtils = projectionUtils;
    }

    @Override
    public List<BaseResponse> generate(Object... params) {
        List<Map<String, String>> siteProjectionList = new ArrayList<>();
        List<ExtDataStageDefinition> extDataStageDefinitions = extDataStageDefinitionRepository.findAllBySubsStatus(ACTIVE);
        String yearMonthString = params.length > 0 && params[0] != null ? (String) params[0] : Utility.getLastMonthLikeQuery();
        Date yearMonth = null;
        try {
            yearMonth = new SimpleDateFormat(Utility.YEAR_MONTH_FORMAT).parse(yearMonthString);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
        }
        List<Map<String, String>> projectionMaps = projectionUtils.mapProjections(extDataStageDefinitions, "Monthly", yearMonth);
        if (projectionMaps.isEmpty()) {
            return List.of(BaseResponse.builder().message("No projections found").code(HttpStatus.UNPROCESSABLE_ENTITY.value()).build());
        }
        String gardenPerformanceCriteria = getPerformanceCriteriaConfig();
        List<Double> gardenEfficiencies = new ArrayList<>();
        int rounding = utility.getCompanyPreference().getRounding();
        List<BaseResponse> baseResponses = new ArrayList<>();
        for (Map<String, String> projectionMap : projectionMaps) {
            try {
                String projection = projectionMap.get(PROJECTION);
                String subscriptionId = projectionMap.get(MONGO_SUBSCRIPTION_ID);
                if (projectionIsEmpty(projection, subscriptionId, baseResponses)) {
                    continue;
                }
                String projectionEfficiencyAt100 = projectionMap.get(PROJECTION_EFFICIENCY_AT_100);
                if (projectionEfficiencyAt100IsEmpty(projectionEfficiencyAt100, subscriptionId, baseResponses)) {
                    continue;
                }
                double monthProjection = Double.parseDouble(projection);
                double actualValue = getActualValue(projectionMap.get(MONGO_GARDEN_ID), yearMonthString);
                Map<String, Double> yAxisValues;
                if (monthProjection > actualValue) {
                    yAxisValues = getyAxis(monthProjection);
                } else {
                    yAxisValues = getyAxis(actualValue);
                }
                double maxScale = yAxisValues.entrySet().stream().map(m -> m.getValue()).max(Comparator.comparing(Double::doubleValue)).get();
                int bar1height = getBarSize(VERTICAL_BAR_HEIGHT, maxScale, monthProjection);
                int bar2height = getBarSize(VERTICAL_BAR_HEIGHT, maxScale, actualValue);
                ExtDataStageDefinition subscription = extDataStageDefinitions.stream()
                        .filter(m -> m.getSubsId().equals(subscriptionId))
                        .findFirst().get();
                final double gardenEfficiencyAt100 = Double.parseDouble(projectionEfficiencyAt100);;
                double gardenEfficiency = (actualValue / gardenEfficiencyAt100) * 100;
                String performance = getPerformance(gardenEfficiency);
                Optional<PhysicalLocation> physicalLocation = subscription.getSiteLocationId() != null ?
                        getLocation(subscription.getSiteLocationId()) : Optional.empty();
                siteProjectionList.add(setJSONForUnderPerformance(subscription, physicalLocation, monthProjection,
                        actualValue, gardenEfficiency, bar1height, bar2height, yAxisValues, performance,
                        gardenPerformanceCriteria, yearMonth, rounding));
                gardenEfficiencies.add(gardenEfficiency);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                String message = projectionMap.get(MONGO_GARDEN_ID) + ": " + e.getMessage();
                baseResponses.add(BaseResponse.builder()
                        .message(message.length() > 255 ? message.substring(0, 255) : message)
                        .code(HttpStatus.UNPROCESSABLE_ENTITY.value()).build());
            }
        }
        addPostEfficiencyValues(gardenEfficiencies, siteProjectionList, rounding);
        return triggerEmailForPerformance(siteProjectionList, baseResponses);
    }

    private String getPerformanceCriteriaConfig() {
        TenantConfig gardenPerformanceCriteria = tenantConfigRepository.findByParameter(GARDEN_PERFORMANCE_CRITERIA)
                .orElse(null);
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (gardenPerformanceCriteria != null) {
                String criteriaString = gardenPerformanceCriteria.getText();
                return mapper.writeValueAsString((List.of(mapper.readValue(criteriaString, GardenPerformanceCriteria[].class))));
            } else {
                return mapper.writeValueAsString(List.of(GardenPerformanceCriteria.builder().name("Low").upperLimit(60).build(),
                        GardenPerformanceCriteria.builder().name("Average").upperLimit(75).build(),
                        GardenPerformanceCriteria.builder().name("Good").upperLimit(90).build(),
                        GardenPerformanceCriteria.builder().name("Optimal").upperLimit(100).build()));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean projectionIsEmpty(String projection, String subscriptionId, List<BaseResponse> baseResponses) {
        if (projection == null || projection.isEmpty()) {
            LOGGER.error("PROJECTION value is required for subscription id {}", subscriptionId);
            baseResponses.add(BaseResponse.builder()
                    .message("PROJECTION value is required for subscription id " + subscriptionId)
                    .code(HttpStatus.UNPROCESSABLE_ENTITY.value()).build());
            return true;
        }
        return false;
    }

    private boolean projectionEfficiencyAt100IsEmpty(String projectionEfficiencyAt100, String subscriptionId, List<BaseResponse> baseResponses) {
        if (projectionEfficiencyAt100 == null || projectionEfficiencyAt100.isEmpty()) {
            baseResponses.add(BaseResponse.builder()
                    .message("HPRJEFF (maximum garden efficiency) is required for subscription id " + subscriptionId)
                    .code(HttpStatus.UNPROCESSABLE_ENTITY.value()).build());
            return true;
        }
        return false;
    }

    private double getActualValue(String gardenId, String yearMonthString) {
        double actualValue = 0.0;
        for (ExtDataStageDefinition subscription : extDataStageDefinitionRepository.findAllBySubsStatusAndRefIdIn(ACTIVE,
                List.of(gardenId))) {
            Double monthlyYield = monitorReadingDailyRepository.getMonthlyYield(subscription.getSubsId(),
                    yearMonthString);
            actualValue = actualValue + (monthlyYield != null ? monthlyYield : 0.0);
        }
        return actualValue;
    }

    private void addPostEfficiencyValues(List<Double> gardenEfficiencies, List<Map<String, String>> siteProjectionList, int rounding) {
        double averageEfficiency = gardenEfficiencies.stream().mapToDouble(m -> m.doubleValue()).sum() /
                (siteProjectionList.size() == 0 ? 1 : siteProjectionList.size());
        double maxEfficiency = Collections.max(gardenEfficiencies.size() == 0 ? List.of(0d) : gardenEfficiencies);
        siteProjectionList.forEach(projection -> {
            double aeg = utility.round(averageEfficiency, rounding);
            double mge = utility.round(maxEfficiency, rounding);
            projection.put(AEG, String.valueOf(aeg));
            projection.put(MGE, String.valueOf(mge));
            String ygeString = projection.get(YGE);
            double ygeValue = ygeString != null && !ygeString.isEmpty() ? Double.valueOf(ygeString) : 0.0;
            Map<String, Double> xAxisValues = getxAxis(aeg, ygeValue, mge);
            double maxScale = 100;
            for (int i = 1; i <= xAxisValues.entrySet().size(); i++) {
                String interval = INTERVAL_VAL_HORIZONTAL + i;
                if (i == xAxisValues.entrySet().size() && (aeg > maxScale || (ygeValue > maxScale || mge > maxScale))) {
                    projection.put(interval, utility.round(xAxisValues.get(interval), rounding) + "+");
                } else {
                    projection.put(interval, String.valueOf(utility.round(xAxisValues.get(interval), rounding)));
                }
            }
            // double maxScale = xAxisValues.entrySet().stream().map(m -> m.getValue()).max(Comparator.comparing(Double::doubleValue)).get();
            projection.put(BAR1_WIDTH, String.valueOf(aeg > maxScale ? HORIZONTAL_BAR_WIDTH : getBarSize(HORIZONTAL_BAR_WIDTH, maxScale, aeg)));
            projection.put(BAR2_WIDTH, String.valueOf(ygeValue > maxScale ? HORIZONTAL_BAR_WIDTH : getBarSize(HORIZONTAL_BAR_WIDTH, maxScale, ygeValue)));
            projection.put(BAR3_WIDTH, String.valueOf(mge > maxScale ? HORIZONTAL_BAR_WIDTH : getBarSize(HORIZONTAL_BAR_WIDTH, maxScale, mge)));
        });
    }

    private Map<String, Double> getyAxis(double value) {
        Map<String, Double> interval = new HashMap<>();
        double roundMaxValue = (Math.ceil(value / 100)) * 100;
        double intervalVal = roundMaxValue / 4;
        int looper = 0;
        if (intervalVal != 0.0) {
            for (double i = intervalVal; i <= roundMaxValue; i += intervalVal) {
                looper = looper + 1;
                interval.put(INTERVAL_VAL + looper, i);
            }
        }
        return interval;
    }

    private Map<String, Double> getxAxis(Double... values) {
        double value = List.of(values).stream().max(Comparator.comparing(Double::doubleValue)).get();
        Map<String, Double> interval = new HashMap<>();
        double maxValue = 100;
        double intervalVal = 10;
        int looper = 0;
        if (intervalVal != 0.0) {
            for (double i = intervalVal; i <= maxValue; i += intervalVal) {
                looper = looper + 1;
                interval.put(INTERVAL_VAL_HORIZONTAL + looper, i);
            }
        }
        return interval;
    }

    private String getPerformance(Double yourGardenEfficiency) {
        String performance = null;
        if (yourGardenEfficiency <= 60.0) {
            performance = String.valueOf(12.5);
        } else if (yourGardenEfficiency >= 61 && yourGardenEfficiency <= 75) {
            performance = String.valueOf(37.5);
        } else if (yourGardenEfficiency >= 76 && yourGardenEfficiency <= 90) {
            performance = String.valueOf(62.5);
        } else if (yourGardenEfficiency >= 91 && yourGardenEfficiency <= 100) {
            performance = String.valueOf(87.5);
        }
        return performance;
    }

    private Optional<PhysicalLocation> getLocation(Long siteLocationId) {
        return physicalLocationRepository.findById(siteLocationId);
    }

    /**
     * Creating PlaceHolder JSON
     *
     * @param extDataStageDefinition
     * @param physicalLocation
     * @param monthProjection
     * @param actualValue
     * @param yourGardenEfficiency
     * @param bar1height
     * @param bar2height
     * @param yAxisValues
     * @param performance
     * @return
     */
    private Map<String, String> setJSONForUnderPerformance(ExtDataStageDefinition extDataStageDefinition,
                                                           Optional<PhysicalLocation> physicalLocation,
                                                           double monthProjection, double actualValue,
                                                           double yourGardenEfficiency, int bar1height,
                                                           int bar2height, Map<String, Double> yAxisValues,
                                                           String performance, String performanceCriteria,
                                                           Date yearMonth, int rounding) {
        Map<String, String> siteProjections = new TreeMap<>();
        String systemSize = Utility.getMeasureAsJson(extDataStageDefinition.getMpJson(), S_GS);
        siteProjections.put(GARDEN_NAME, extDataStageDefinition.getRefType());
        siteProjections.put(MP, extDataStageDefinition.getMonPlatform());
        siteProjections.put(SYSTEM_SIZE, systemSize == null ? NOT_FOUND : systemSize);
        siteProjections.put(LOCATION, physicalLocation.isPresent() ? physicalLocation.get().getAdd1() : NOT_FOUND);
        siteProjections.put(CATEGORY, UNDER_PERFORMANCE_CATEGORY_TEXT);
        siteProjections.put(TYPE, UNDER_PERFORMANCE_TYPE_TEXT);
        siteProjections.put(ALERT_DURATION, new SimpleDateFormat(Utility.MONTH_FORMAT).format(yearMonth));
        siteProjections.put(DESCRIPTION, UNDER_PERFORMANCE_DECSRIPTION_TEXT);
        siteProjections.put(YGE, String.valueOf(utility.round(yourGardenEfficiency, rounding)));
        siteProjections.put(ALERT_IMPACT, "HIGH");
        siteProjections.put(ACTUAL_PRODUCTION, String.valueOf(utility.round(actualValue, rounding)));
        siteProjections.put(PROJECTED_PRODUCTION, String.valueOf(utility.round(monthProjection, rounding)));
        siteProjections.put(BAR1_HEIGHT, String.valueOf(bar1height));
        siteProjections.put(BAR2_HEIGHT, String.valueOf(bar2height));
        siteProjections.put(REPORT_DATE, String.valueOf(new Date()));
        siteProjections.put(PERFORMANCE, String.valueOf(utility.round(yourGardenEfficiency, rounding)));
        siteProjections.put(PERFORMANCE_INDICATOR, performance);
        siteProjections.put(PERFORMANCE_CRITERIA, performanceCriteria);
        siteProjections.put(MONGO_SUBSCRIPTION_ID, extDataStageDefinition.getSubsId());
        for (int i = 1; i <= yAxisValues.entrySet().size(); i++) {
            String interval = INTERVAL_VAL + i;
            siteProjections.put(interval, String.valueOf(utility.round(yAxisValues.get(interval), rounding)));
        }
        return siteProjections;
    }

    private List<BaseResponse> triggerEmailForPerformance(List<Map<String, String>> siteProjectionList, List<BaseResponse> baseResponses) {
        String emailTOs = "";
        Optional<TenantConfig> tenantConfig = tenantConfigRepository.findByParameter(PERFORMANCE_TOEMAIL_TENANT_CONFIG_PARAM);
        emailTOs = "tos=" + tenantConfig.get().getText().replace(",", "&tos=");
        for (Map<String, String> map : siteProjectionList) {

            String subscriptionId = map.get(MONGO_SUBSCRIPTION_ID);
            map.remove(MONGO_SUBSCRIPTION_ID);
            BaseResponse response = alertService.superSendEmailTrigger(
                            tenantConfigRepository.findByParameter(PERFORMANCE_TENANT_CONFIG_PARAM).orElse(null),
                            PERFORMANCE_EMAIL_SUBJECT, emailTOs, "", "", map);
            response.setMessage("Subscription Id: " + subscriptionId + " " + response.getMessage());
            baseResponses.add(response);
        }
        return baseResponses;
    }

    @Builder
    @Getter
    @Setter
    private static class GardenPerformanceCriteria {
        private String name;
        @JsonProperty("upper_limit")
        private double upperLimit;
    }
}
