package com.solar.api.tenant.service.alerts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.helper.Utility;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.tenant.model.AlertCalculationLog;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.repository.AlertCalculationLogRepository;
import com.solar.api.tenant.repository.MonitorReadingDailyRepository;
import com.solar.api.tenant.repository.PhysicalLocationRepository;
import com.solar.api.tenant.repository.TenantConfigRepository;
import com.solar.api.tenant.repository.stage.monitoring.ExtDataStageDefinitionRepository;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.solar.api.Constants.ALERTS.*;

@Service
public class UnderPerformanceAlert implements Alert {

    protected final Logger LOGGER = LoggerFactory.getLogger(UnderPerformanceAlert.class);
    private List<String> colorCodes = List.of("#ffefd4", "#ffe6be", "#ffdb9f", "#fcc874", "#f6b54c");
    private List<String> colorCodesHistorical = List.of("#c6dff5", "#c2bbec", "#f2af9a", "#ffec9e", "#d8b5c3");
    private String colorCodeActual = "#caf9df";
    private String DURATION_FORMAT = "MMMM, yyyy";
    private static final int VERTICAL_BAR_HEIGHT = 250;
    private static final int VERTICAL_BAR_WIDTH_BOTH = 60;
    private final ExtDataStageDefinitionRepository extDataStageDefinitionRepository;
    private final MonitorReadingDailyRepository monitorReadingDailyRepository;
    private final PhysicalLocationRepository physicalLocationRepository;
    private final Utility utility;
    private final ProjectionUtils projectionUtils;
    private final AlertService alertService;
    private final TenantConfigRepository tenantConfigRepository;
    private final AlertCalculationLogRepository alertCalculationLogRepository;
    private final int MAX_PROJECTIONS = 5;

    public UnderPerformanceAlert(ExtDataStageDefinitionRepository extDataStageDefinitionRepository,
                                 MonitorReadingDailyRepository monitorReadingDailyRepository, PhysicalLocationRepository physicalLocationRepository, Utility utility,
                                 ProjectionUtils projectionUtils, AlertService alertService,
                                 TenantConfigRepository tenantConfigRepository, AlertCalculationLogRepository alertCalculationLogRepository) {
        this.extDataStageDefinitionRepository = extDataStageDefinitionRepository;
        this.monitorReadingDailyRepository = monitorReadingDailyRepository;
        this.physicalLocationRepository = physicalLocationRepository;
        this.utility = utility;
        this.projectionUtils = projectionUtils;
        this.alertService = alertService;
        this.tenantConfigRepository = tenantConfigRepository;
        this.alertCalculationLogRepository = alertCalculationLogRepository;
    }

    @Override
    public List<BaseResponse> generate(Object... params) {
        Double threshold = getThreshold();
        if (threshold == null) {
            return Collections.emptyList();
        }
        List<ExtDataStageDefinition> extDataStageDefinitions = extDataStageDefinitionRepository.findAllBySubsStatus(ACTIVE);
        String yearMonthString = params.length > 0 && params[0] != null ? params[0].toString() : Utility.getCurrentMonthLikeQuery();
        Date yearMonth = null;
        try {
            yearMonth = new SimpleDateFormat(Utility.YEAR_MONTH_FORMAT).parse(yearMonthString);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
        }
        List<Map<String, String>> projectionMap = projectionUtils.mapProjections(extDataStageDefinitions, "Monthly", yearMonth);
        if (projectionMap.isEmpty()) {
            return List.of(BaseResponse.builder().message("No projections found").code(HttpStatus.UNPROCESSABLE_ENTITY.value()).build());
        }
        Map<String, List<Map<String, String>>> projectionsGroupedByGarden = projectionMap.stream()
                .collect(Collectors.groupingBy(m -> m.get(MONGO_GARDEN_ID)));
        Map<String, Performance> gardenUnderPerformances = new HashMap<>();
        int rounding = utility.getCompanyPreference().getRounding();
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        List<BaseResponse> baseResponses = new ArrayList<>();
        projectionsGroupedByGarden.entrySet().stream().forEach(gardenProjections -> {
            String gardenId = gardenProjections.getKey();
            Performance performance = new Performance();
            List<Map<String, String>> allowedProjections = gardenProjections.getValue().subList(0, Math.min(gardenProjections.getValue().size(), MAX_PROJECTIONS));
            List<String> projectionIds = allowedProjections.stream()
                    .map(m -> m.get(MONGO_SUBSCRIPTION_ID)).collect(Collectors.toList());
            double actualValue = getActualGardenProduction(gardenId, yearMonthString, projectionIds);
            for (Map<String, String> gardenProjection : allowedProjections) {
                String projectionString = gardenProjection.get(PROJECTION);
                if (projectionString.isEmpty()) {
                    LOGGER.error("Projection value is empty in mpJson");
                    baseResponses.add(BaseResponse.builder()
                            .message("PROJECTION value is empty in mpJson for subscription id " + gardenProjection.get(MONGO_SUBSCRIPTION_ID))
                            .code(HttpStatus.UNPROCESSABLE_ENTITY.value()).build());
                    continue;
                }
                double projection = Double.parseDouble(gardenProjection.get(PROJECTION));
                // Check threshold
                double gardenEfficiency = (actualValue / projection) * 100;
                double efficiencyDecrease = utility.round(100 - gardenEfficiency, rounding);
                // Log processing
                System.out.println(actualValue);
                if (efficiencyDecrease > threshold) {
                    logCalculation(mapper, writer, projection, actualValue, efficiencyDecrease,
                            AlertCalculationLog.EStatus.GENERATED.getName(), gardenId);
                    performance.getUnderPerformances().add(UnderPerformance.builder()
                            .projectName(gardenProjection.get(PRJTNM))
                            .projectedProduction(projection)
                            .actualProduction(utility.round(actualValue, rounding))
                            .decreaseInEfficiency(efficiencyDecrease)
                            .colorCode(colorCodes.get(performance.getUnderPerformances().size()))
                            .build());
                } else {
                    logCalculation(mapper, writer, projection, actualValue, efficiencyDecrease,
                            AlertCalculationLog.EStatus.NOT_GENERATED.getName(), gardenId);
                }
            }
            if (!performance.getUnderPerformances().isEmpty()) {
                performance.setHistoricalPerformances(getHistoricalData(projectionIds, gardenId, yearMonthString));
                addGardenInformation(performance, allowedProjections.get(0), yearMonthString);
                setHistoricalLegend(allowedProjections, performance);
                performance.setGardenId(gardenId);
                gardenUnderPerformances.put(gardenId, performance);
            }
        });
        return triggerEmailForUnderPerformance(gardenUnderPerformances, baseResponses);
    }

    private Double getThreshold() {
        Optional<TenantConfig> thresholdConfigOptional = tenantConfigRepository.findByParameter("UnderPerformanceThreshold");
        if (thresholdConfigOptional.isPresent()) {
            return Double.parseDouble(thresholdConfigOptional.get().getText());
        } else {
            LOGGER.error("UnderPerformanceThreshold is required for " + DBContextHolder.getTenantName());
            return null;
        }
    }

    private void logCalculation(ObjectMapper mapper, ObjectWriter writer, double projection, double actualValue,
                                double efficiencyDecrease, String status, String gardenId) {
        ObjectNode node = mapper.createObjectNode();
        node.put("projection", projection);
        node.put(ACTUAL_PRODUCTION, actualValue);
        node.put(EFFICIENCY_DECREASE, efficiencyDecrease);
        try {
            alertCalculationLogRepository.save(AlertCalculationLog.builder()
                    .valuesJson(writer.writeValueAsString(node))
                    .status(status)
                    .gardenId(gardenId)
                    .build());
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void addGardenInformation(Performance performance, Map<String, String> projection, String yearMonth) {
        SimpleDateFormat format = new SimpleDateFormat(Utility.YEAR_MONTH_FORMAT);
        SimpleDateFormat durationFormat = new SimpleDateFormat(DURATION_FORMAT);
        performance.setName(projection.get(GARDEN_NAME));
        performance.setSize(projection.get(SYSTEM_SIZE) != null && !projection.get(SYSTEM_SIZE).isEmpty() ? projection.get(SYSTEM_SIZE) : SIZE_NOT_SPECIFIED);
        try {
            performance.setDuration(durationFormat.format(format.parse(yearMonth)));
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
        }
        Optional<PhysicalLocation> physicalLocation = projection.get(SITE_LOCATION_ID) != null && !projection.get(SITE_LOCATION_ID).isEmpty()?
                physicalLocationRepository.findById(Long.valueOf(projection.get(SITE_LOCATION_ID))) : Optional.empty();
        performance.setLocations(physicalLocation.isPresent() ? physicalLocation.get().getAdd1() : LOCATION_NOT_SPECIFIED);
        performance.setPlatform(projection.get(MP) != null && !projection.get(MP).isEmpty() ? projection.get(MP) : PLATFORM_NOT_SPECIFIED);
        performance.setReportDate(String.valueOf(new Date()));
    }

    private List<HistoricalPerformance> getHistoricalData(List<String> projectionIds, String gardenId, String yearMonth) {
        List<HistoricalPerformance> historicalPerformances = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat(Utility.YEAR_MONTH_FORMAT);
        SimpleDateFormat monthFormat = new SimpleDateFormat(Utility.MONTH_FORMAT);
        try {
            Date pastYearMonth = Utility.addMonths(format.parse(yearMonth), -6);
            yearMonth = format.format(pastYearMonth);
            for (int i = 0; i < 6; i++) {
                historicalPerformances.add(HistoricalPerformance.builder()
                        .month(monthFormat.format(pastYearMonth))
                        .projectedProductions(getProjectedGardenProduction(yearMonth, projectionIds))
                        .actualProduction(getActualGardenProduction(gardenId, yearMonth, projectionIds))
                        .build());
                pastYearMonth = Utility.addMonths(format.parse(yearMonth), 1);
                yearMonth = format.format(pastYearMonth);
            }
            roundOffValues(historicalPerformances);
            setBarSizes(historicalPerformances);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return historicalPerformances;
    }

    void roundOffValues(List<HistoricalPerformance> historicalPerformances) {
        int rounding = utility.getCompanyPreference().getRounding();
        historicalPerformances.forEach(m -> m.setActualProduction(utility.round(m.getActualProduction(), rounding)));
        historicalPerformances.forEach(m -> m.getProjectedProductions().forEach(n -> n.setValue(utility.round(n.getValue(), rounding))));
    }

    private void setBarSizes(List<HistoricalPerformance> historicalPerformances) {
        List<Double> historicalValues = historicalPerformances.stream()
                .flatMap(m -> m.getProjectedProductions().stream())
                    .map(ProjectedProduction::getValue).collect(Collectors.toList());
        historicalValues.addAll(historicalPerformances.stream().map(HistoricalPerformance::getActualProduction).collect(Collectors.toList()));
        double maxScale = historicalValues.stream().filter(i -> i != null).max(Comparator.comparing(Double::doubleValue)).get();
        historicalPerformances.forEach(p -> p.setBarHeight(getBarSize(VERTICAL_BAR_HEIGHT, maxScale, p.getActualProduction())));
        int barWidth = VERTICAL_BAR_WIDTH_BOTH / (historicalPerformances.stream()
                .map(m -> m.getProjectedProductions()).findFirst().orElse(Collections.EMPTY_LIST).size() + 1);
        historicalPerformances.forEach(p -> p.setBarWidth(barWidth));
        historicalPerformances.stream().flatMap(m -> m.getProjectedProductions().stream()).forEach(p -> p.setBarHeight(getBarSize(VERTICAL_BAR_HEIGHT, maxScale, p.getValue())));
        historicalPerformances.stream().flatMap(m -> m.getProjectedProductions().stream()).forEach(p -> p.setBarWidth(barWidth));
        historicalPerformances.forEach(historicalPerformance -> {
            for (int i = 0; i < historicalPerformance.getProjectedProductions().size(); i++) {
                historicalPerformance.getProjectedProductions().get(i).setColorCode(colorCodesHistorical.get(i));
            }
        });
        historicalPerformances.forEach(p -> p.setColorCode(colorCodeActual));
    }

    private List<ProjectedProduction> getProjectedGardenProduction(String yearMonth, List<String> projectionIds) {
        List<ProjectedProduction> projectedProductions = new ArrayList<>();
        projectionIds.forEach(projectionId -> {
            projectedProductions.add(ProjectedProduction.builder()
                    .value(monitorReadingDailyRepository.getMonthlyYield(projectionId, yearMonth))
                    .build());
        });
        return projectedProductions;
    }

    private double getActualGardenProduction(String gardenId, String yearMonth, List<String> projectionIds) {
        double actualValue = 0.0;
        List<ExtDataStageDefinition> extDataStageDefinitions = extDataStageDefinitionRepository.findAllBySubsStatusAndRefIdIn(ACTIVE,
                List.of(gardenId));
        for (ExtDataStageDefinition subscription : extDataStageDefinitions.stream()
                .filter(m -> !projectionIds.contains(m.getSubsId())).collect(Collectors.toList())) {
            Double monthlyYield = monitorReadingDailyRepository.getMonthlyYield(subscription.getSubsId(), yearMonth);
            actualValue = actualValue + (monthlyYield != null ? monthlyYield : 0.0);
        }
        return actualValue;
    }

    void setHistoricalLegend(List<Map<String, String>> allowedProjections, Performance performance) {
        List<HistoricalLegendItem> historicalLegendItems = new ArrayList<>();
        for (int i = 0; i < allowedProjections.size(); i++) {
            historicalLegendItems.add(HistoricalLegendItem.builder()
                    .name(allowedProjections.get(i).get(PRJTNM))
                    .colorCode(colorCodesHistorical.get(i))
                    .build());
        }
        performance.setHistoricalLegend(historicalLegendItems);
    }

    private List<BaseResponse> triggerEmailForUnderPerformance(Map<String, Performance> gardenUnderPerformances, List<BaseResponse> baseResponses) {
        String emailTOs = "";
        Optional<TenantConfig> tenantConfig = tenantConfigRepository.findByParameter(UNDER_PERFORMANCE_TOEMAIL_TENANT_CONFIG_PARAM);
        emailTOs = "tos=" + tenantConfig.get().getText().replace(",", "&tos=");
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        for (Map.Entry<String, Performance> map : gardenUnderPerformances.entrySet()) {
            try {
                String formattedJson = writer.writeValueAsString(map.getValue());
                System.out.println(formattedJson);
                    BaseResponse response = alertService.superSendEmailTrigger(tenantConfigRepository
                            .findByParameter(UNDER_PERFORMANCE_TENANT_CONFIG_PARAM).orElse(null),
                                        UNDER_PERFORMANCE_EMAIL_SUBJECT, emailTOs, "", "",
                                            mapper.writeValueAsString(map.getValue()));
                    response.setMessage("Garden Id: " + map.getValue().getGardenId() + " " + response.getMessage());
                    baseResponses.add(response);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return baseResponses;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Performance {
        private String name;
        private String size;
        private String duration;
        private String locations;
        private String platform;
        @JsonProperty("report_date")
        private String reportDate;
        @JsonProperty("under_performances")
        private List<UnderPerformance> underPerformances = new ArrayList<>();
        @JsonProperty("historical_performances")
        List<HistoricalPerformance> historicalPerformances = new ArrayList<>();
        private List<HistoricalLegendItem> historicalLegend;
        @JsonIgnore
        private String gardenId;
    }

    @Builder
    @Getter
    @Setter
    private static class UnderPerformance {
        @JsonProperty("project_name")
        private String projectName;
        @JsonProperty("projected_production")
        private double projectedProduction;
        @JsonProperty("actual_production")
        private double actualProduction;
        @JsonProperty("decrease_in_efficiency")
        private double decreaseInEfficiency;
        @JsonProperty("color_code")
        private String colorCode;
    }

    @Builder
    @Getter
    @Setter
    private static class HistoricalPerformance {
        private String month;
        @JsonProperty("projected_productions")
        private List<ProjectedProduction> projectedProductions;
        @JsonProperty("actual_production")
        private double actualProduction;
        @JsonProperty("bar_height")
        private int barHeight;
        @JsonProperty("bar_width")
        private int barWidth;
        @JsonProperty("color_code")
        private String colorCode;
    }

    @Builder
    @Getter
    @Setter
    private static class ProjectedProduction {
        private Double value;
        @JsonProperty("bar_height")
        private int barHeight;
        @JsonProperty("bar_width")
        private int barWidth;
        @JsonProperty("color_code")
        private String colorCode;
    }

    @Builder
    @Getter
    @Setter
    private static class HistoricalLegendItem {
        private String name;
        @JsonProperty("color_code")
        private String colorCode;
    }
}
