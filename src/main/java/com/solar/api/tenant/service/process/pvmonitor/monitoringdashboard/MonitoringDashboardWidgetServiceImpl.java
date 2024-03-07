package com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard;

import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.helper.Utility;
import com.solar.api.tenant.mapper.pvmonitor.*;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.model.pvmonitor.MonitorReading;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingDayWise;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingMonthWise;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingYearWise;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.repository.*;
import com.solar.api.tenant.service.preferences.TenantConfigService;
import com.solar.api.tenant.service.process.pvmonitor.ExtDataStageDefinitionService;
import com.solar.api.tenant.model.dataexport.powermonitoring.DataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class MonitoringDashboardWidgetServiceImpl implements MonitoringDashboardWidgetService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private MonitoringDashboardWidgetRepository monitoringDashboardWidgetRepository;
    @Autowired
    private Utility utility;
    @Autowired
    private MonitorReadingRepository readingRepository;

    @Autowired
    private ExtDataStageDefinitionService extDataStageDefinitionService;

    @Autowired
    private TenantConfigService tenantConfigService;
    @Autowired
    private MonitoringDashboardYearWiseRepository monitoringDashboardYearWiseRepository;

    @Autowired
    private MonitoringDashboardMonthWiseRepository monitoringDashboardMonthWiseRepository;

    @Autowired
    private MonitoringDashboardDailyWiseRepository monitoringDashboardDailyWiseRepository;

    /*
       @created by : sana
       @time stamp  : 14/08/2023, 10:09 PM EDT
       @return  : ResponseEntity<List<WidgetDataResult>>
       @Description  : This method is used to get the widget data for selected project
     */
    @Override
    public ResponseEntity<?> getWidgetData(MonitorAPIAuthBody body) {
        try {
            WidgetWeatherDataResult widgetWeatherDataResult = null;
            List<ExtDataStageDefinition> extDataStageDefinitionList = extDataStageDefinitionService.findAllByRefIdIn(Arrays.asList(body.getVariantId()), "ACTIVE");
            List<WidgetDataResult> widgetDataResults = monitoringDashboardWidgetRepository.findWidgetDataByVariantIds(Arrays.asList(body.getVariantId()));
            List<WidgetWeatherDataResult> widgetWeatherDataResults = monitoringDashboardWidgetRepository.findWidgetWeatherDataByVariantIds(Arrays.asList(body.getVariantId()));
            int rounding = utility.getCompanyPreference().getRounding();
            if (widgetWeatherDataResults.size() > 0) {
                widgetWeatherDataResult = widgetWeatherDataResults.get(0);
            }
            if (widgetDataResults.size() > 0) {
                Double treesPlanted = getTreesPlanted(extDataStageDefinitionList);
                Double co2Reduction = getCO2Reduction(extDataStageDefinitionList);
                Double barrels = getBarrels(extDataStageDefinitionList);
                Double carCharges = getCarCharges(extDataStageDefinitionList);
                Double milesCover = getMilesCover(extDataStageDefinitionList);
                Double phoneCharges = getPhoneCharges(extDataStageDefinitionList);
                WidgetDataResult widgetDataResult = widgetDataResults.get(0);
                WidgetDataDTO widgetDataDTO = WidgetDataDTO.builder().refId(widgetDataResult.getRefId())
                        .refType(widgetDataResult.getRefType()).address(widgetDataResult.getAddress())
                        .sunrise(widgetWeatherDataResult != null ? widgetWeatherDataResult.getSunrise() + " " + widgetWeatherDataResult.getTimeZone() : "")
                        .sunset(widgetWeatherDataResult != null ? widgetWeatherDataResult.getSunset() + " " + widgetWeatherDataResult.getTimeZone() : "")
                        .systemSize(widgetDataResult.getSystemSize()).mp(widgetDataResult.getMp())
                        .state(widgetDataResult.getState())
                        .geoLong(widgetDataResult.getGeoLong()).geoLat(widgetDataResult.getGeoLat())
                        .googleCoordinates(widgetDataResult.getGoogleCoordinates())
                        .installationType(widgetDataResult.getInstallationType())
                        .treesPlanted(utility.roundAndFormat(treesPlanted, rounding) + " Tress")
                        .co2Reduction(utility.roundAndFormat(co2Reduction, rounding) + " TONs")
                        .barrels(utility.roundAndFormat(barrels, rounding) + " Barrels")
                        .carCharges(utility.roundAndFormat(carCharges, rounding) + " Cars")
                        .milesCover(utility.roundAndFormat(milesCover, rounding) + " Miles")
                        .phoneCharges(utility.roundAndFormat(phoneCharges, rounding) + " Phones")
                        .build();
                return ResponseEntity.ok(widgetDataDTO);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return ResponseEntity.ok(WidgetDataDTO.builder().build());
    }

    /*
       @created by : sana
       @time stamp  : 4/09/2023, 10:09 PM EDT
       @return  : ResponseEntity<List<WidgetDataResult>>
       @Description  : This method is used to get the widget data for site comparison mode.
     */
    @Override
    public ResponseEntity<?> getSitesWidgetData(MonitorAPIAuthBody body) {
        try {
            List<ExtDataStageDefinition> extDataStageDefinitionList = extDataStageDefinitionService.findAllByRefIdIn(body.getVariantIds(), "ACTIVE");
            List<WidgetDataResult> widgetDataResults = monitoringDashboardWidgetRepository.findWidgetDataByVariantIds(body.getVariantIds());
            int rounding = utility.getCompanyPreference().getRounding();
            if (widgetDataResults.size() > 0) {
                Double treesPlanted = getTreesPlanted(extDataStageDefinitionList);
                Double co2Reduction = getCO2Reduction(extDataStageDefinitionList);
                Double barrels = getBarrels(extDataStageDefinitionList);
                Double carCharges = getCarCharges(extDataStageDefinitionList);
                Double milesCover = getMilesCover(extDataStageDefinitionList);
                Double phoneCharges = getPhoneCharges(extDataStageDefinitionList);
                WidgetSiteDataDTO widgetDataDTO = WidgetSiteDataDTO.builder()
                        .totalSites(widgetDataResults.stream().count() + AppConstants.DashboardWidgets.SITES)
                        .totalPlatforms(widgetDataResults.stream().map(WidgetDataResult::getMp).distinct().count() + AppConstants.DashboardWidgets.PLATFORMS)
                        .totalSystemSize(widgetDataResults.stream().mapToDouble(data -> Double.parseDouble(data.getSystemSize().replace("kWh", ""))).sum() + " kWh")
                        .totalLocation(widgetDataResults.stream().map(WidgetDataResult::getAddress).count() + AppConstants.DashboardWidgets.LOCATIONS)
                        .treesPlanted(utility.roundAndFormat(treesPlanted, rounding) + AppConstants.DashboardWidgets.TREES)
                        .co2Reduction(utility.roundAndFormat(co2Reduction, rounding) + AppConstants.DashboardWidgets.TONS)
                        .barrels(utility.roundAndFormat(barrels, rounding) + AppConstants.DashboardWidgets.BARRELS)
                        .carCharges(utility.roundAndFormat(carCharges, rounding) + AppConstants.DashboardWidgets.CARS)
                        .milesCover(utility.roundAndFormat(milesCover, rounding) + AppConstants.DashboardWidgets.MILES)
                        .phoneCharges(utility.roundAndFormat(phoneCharges, rounding) + AppConstants.DashboardWidgets.PHONES)
                        .build();
                return ResponseEntity.ok(widgetDataDTO);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return ResponseEntity.ok(WidgetDataDTO.builder().build());
    }

    @Override
    public ResponseEntity<?> getSitesWidgetDataDetail(MonitorAPIAuthBody body) {
        try {
            List<DataDTO> extSubsCountList = extDataStageDefinitionService.findSubCountBySubsStatusAndRefIdIn(body.getVariantIds(), "ACTIVE");
            List<WidgetDataResult> widgetDataResults = monitoringDashboardWidgetRepository.findWidgetDataByVariantIds(body.getVariantIds());
            List<WidgetWeatherDetailDataResult> widgetWeatherDetailDataResults = monitoringDashboardWidgetRepository.findWidgetWeatherDetailByVariantIds(body.getVariantIds());
            if (widgetDataResults.size() > 0) {
                WidgetSiteDataDTO widgetDataDTO = WidgetSiteDataDTO.builder()
                        .sitesData(MonitoringDashboardMapper.toWidgetDataDTOs(widgetDataResults, widgetWeatherDetailDataResults, extSubsCountList))
                        .totalSystemSize(widgetDataResults.stream().mapToDouble(data -> Double.parseDouble(data.getSystemSize().replace("kWh", ""))).sum() + " kWh")
                        .build();
                return ResponseEntity.ok(widgetDataDTO);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return ResponseEntity.ok(WidgetDataDTO.builder().build());
    }

    @Override
    public ResponseEntity<?> validateSitesSelectionCountAllowed(Long count) {
        Optional<TenantConfig> numberOfSitesAllowed = null;
        try {
            numberOfSitesAllowed = tenantConfigService.findByParameter(Constants.MONITORING_DASHBOARD_CONSTANTS.NUMBER_OF_SITES_ALLOWED);
            if (numberOfSitesAllowed.isPresent()) {
                if (count > Integer.parseInt(numberOfSitesAllowed.get().getText())) {
                    return utility.buildErrorResponse(HttpStatus.BAD_REQUEST, "Number of sites selected is more than allowed " + numberOfSitesAllowed.get().getText());
                } else {
                    return utility.buildSuccessResponse(HttpStatus.OK, null, null);
                }
            } else {
                return utility.buildErrorResponse(HttpStatus.BAD_REQUEST, "Number of sites allowed not configured");
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Failed to parse the number of sites allowed", e);
            return utility.buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to parse the number of sites allowed");
        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred", e);
            return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        }
    }

    @Override
    public ResponseEntity<?> getYieldWidgetData(MonitorAPIAuthBody body) {
        try {
            int rounding = utility.getCompanyPreference().getRounding();
            List<ExtDataStageDefinition> extDataStageDefinitionList = extDataStageDefinitionService.findAllByRefIdIn(body.getVariantIds(), "ACTIVE");
            YieldDataDTO yearlyYieldSum = getYearlyYield(extDataStageDefinitionList);
            YieldDataDTO monthlyYieldSum = getMonthlyYield(extDataStageDefinitionList);
            YieldDataDTO dailyYieldSum = getDailyYield(extDataStageDefinitionList);
            YieldDataDTO lifeTimeYieldSum = getLifeTimeYield(body.getVariantIds());
            YieldWidgetDataDTO yieldWidgetDataDTO = YieldWidgetDataDTO.builder()
                    .yearlyYield(Double.parseDouble(utility.roundAndFormat(yearlyYieldSum.getYield(), rounding))).currentYear(yearlyYieldSum.getDay())
                    .monthlyYield(Double.parseDouble(utility.roundAndFormat(monthlyYieldSum.getYield(), rounding))).currentMonth(monthlyYieldSum.getDay())
                    .dailyYield(Double.parseDouble(utility.roundAndFormat(dailyYieldSum.getYield(), rounding))).currentDay(dailyYieldSum.getDay())
                    .lifeTimeYield(Double.parseDouble(utility.roundAndFormat(lifeTimeYieldSum.getYield(), rounding))).lifeTime("total").uom("kWh").build();
            return utility.buildSuccessResponse(HttpStatus.OK, "successfully returned data", yieldWidgetDataDTO);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Double getTreesPlanted(List<ExtDataStageDefinition> extDataStageDefinitionList, String... monthYear) {
        List<MonitorReading> lastGrossRecords;
        if (monthYear == null) {
            lastGrossRecords = readingRepository.getLastGrossYieldRecords(extDataStageDefinitionList.stream().map(ExtDataStageDefinition::getSubsId).collect(Collectors.toList()));
        } else {
            lastGrossRecords = readingRepository.getLastGrossYieldRecordsByMonthYear(extDataStageDefinitionList.stream().map(ExtDataStageDefinition::getSubsId).collect(Collectors.toList()), monthYear[0]);
        }
        Double treesPlantedFactor = 0d;
        Double treesPlantedMultiplier = AppConstants.WidgetData.TRESS_PLANTED_MULTIPLIER;
        treesPlantedFactor = lastGrossRecords.stream()
                .mapToDouble(reading -> reading.getGrossYield() * treesPlantedMultiplier)
                .sum();
        return treesPlantedFactor;
    }

    @Override
    public Double getCO2Reduction(List<ExtDataStageDefinition> extDataStageDefinitionList, String... monthYear) {
        List<MonitorReading> lastGrossRecords;
        if (monthYear == null) {
            lastGrossRecords = readingRepository.getLastGrossYieldRecords(extDataStageDefinitionList.stream().map(ExtDataStageDefinition::getSubsId).collect(Collectors.toList()));
        } else {
            lastGrossRecords = readingRepository.getLastGrossYieldRecordsByMonthYear(extDataStageDefinitionList.stream().map(ExtDataStageDefinition::getSubsId).collect(Collectors.toList()), monthYear[0]);
        }
        Double co2Reduction = 0d;
        Double co2ReductionMultiplier = AppConstants.WidgetData.CO2_REDUCTION_IN_TONS_MULTIPLIER;
        co2Reduction = lastGrossRecords.stream()
                .mapToDouble(reading -> reading.getGrossYield() * co2ReductionMultiplier)
                .sum();
        return co2Reduction;
    }

    @Override
    public Double getBarrels(List<ExtDataStageDefinition> extDataStageDefinitionList, String... monthYear) {
        List<MonitorReading> lastGrossRecords;
        if (monthYear == null) {
            lastGrossRecords = readingRepository.getLastGrossYieldRecords(extDataStageDefinitionList.stream().map(ExtDataStageDefinition::getSubsId).collect(Collectors.toList()));
        } else {
            lastGrossRecords = readingRepository.getLastGrossYieldRecordsByMonthYear(extDataStageDefinitionList.stream().map(ExtDataStageDefinition::getSubsId).collect(Collectors.toList()), monthYear[0]);
        }
        Double barrels = 0d;
        Double barrelsMultiplier = AppConstants.WidgetData.BARRELS_COVER_MULTIPLIER;
        barrels = lastGrossRecords.stream()
                .mapToDouble(reading -> reading.getGrossYield() * barrelsMultiplier)
                .sum();
        return barrels;
    }

    @Override
    public Double getMilesCover(List<ExtDataStageDefinition> extDataStageDefinitionList, String... monthYear) {
        List<MonitorReading> lastGrossRecords;
        if (monthYear == null) {
            lastGrossRecords = readingRepository.getLastGrossYieldRecords(extDataStageDefinitionList.stream().map(ExtDataStageDefinition::getSubsId).collect(Collectors.toList()));
        } else {
            lastGrossRecords = readingRepository.getLastGrossYieldRecordsByMonthYear(extDataStageDefinitionList.stream().map(ExtDataStageDefinition::getSubsId).collect(Collectors.toList()), monthYear[0]);
        }
        Double milesCover = 0d;
        Double milesCoverMultiplier = AppConstants.WidgetData.MILES_COVER_MULTIPLIER;
        milesCover = lastGrossRecords.stream()
                .mapToDouble(reading -> reading.getGrossYield() * milesCoverMultiplier)
                .sum();
        return milesCover;
    }

    @Override
    public Double getPhoneCharges(List<ExtDataStageDefinition> extDataStageDefinitionList, String... monthYear) {
        List<MonitorReading> lastGrossRecords;
        if (monthYear == null) {
            lastGrossRecords = readingRepository.getLastGrossYieldRecords(extDataStageDefinitionList.stream().map(ExtDataStageDefinition::getSubsId).collect(Collectors.toList()));
        } else {
            lastGrossRecords = readingRepository.getLastGrossYieldRecordsByMonthYear(extDataStageDefinitionList.stream().map(ExtDataStageDefinition::getSubsId).collect(Collectors.toList()), monthYear[0]);
        }
        Double phoneCharges = 0d;
        Double phoneChargesMultiplier = AppConstants.WidgetData.PHONE_CHARGE_MULTIPLIER;
        phoneCharges = lastGrossRecords.stream()
                .mapToDouble(reading -> reading.getGrossYield() * phoneChargesMultiplier)
                .sum();
        return phoneCharges;
    }

    @Override
    public Double getCarCharges(List<ExtDataStageDefinition> extDataStageDefinitionList, String... monthYear) {
        List<MonitorReading> lastGrossRecords;
        if (monthYear == null) {
            lastGrossRecords = readingRepository.getLastGrossYieldRecords(extDataStageDefinitionList.stream().map(ExtDataStageDefinition::getSubsId).collect(Collectors.toList()));
        } else {
            lastGrossRecords = readingRepository.getLastGrossYieldRecordsByMonthYear(extDataStageDefinitionList.stream().map(ExtDataStageDefinition::getSubsId).collect(Collectors.toList()), monthYear[0]);
        }
        Double carCharges = 0d;
        Double carChargesMultiplier = AppConstants.WidgetData.CAR_CHARGE_MULTIPLIER;
        carCharges = lastGrossRecords.stream()
                .mapToDouble(reading -> reading.getGrossYield() * carChargesMultiplier)
                .sum();
        return carCharges;
    }

    /*this method is used to get the yearly yield sum for the given list of subscriptions*/
    @Override
    public YieldDataDTO getYearlyYield(List<ExtDataStageDefinition> extDataStageDefinitionList) {
        int currentYear = Year.now().getValue();
        List<MonitorReadingYearWise> yearWiseList = monitoringDashboardYearWiseRepository.findByYearAndSubIds(String.valueOf(currentYear), extDataStageDefinitionList.stream().map(ExtDataStageDefinition::getSubsId).collect(Collectors.toList()));
        Double yearlyYieldSum = 0d;
        yearlyYieldSum = yearWiseList.stream()
                .mapToDouble(reading -> reading.getYield())
                .sum();
        return YieldDataDTO.builder().yield(yearlyYieldSum).day(String.valueOf(currentYear)).build();
    }

    /*this method is used to get the monthly yield sum for the given list of subscriptions*/
    @Override
    public YieldDataDTO getMonthlyYield(List<ExtDataStageDefinition> extDataStageDefinitionList) {
        String monthYear = Utility.getCurrentMonthYear();
        List<MonitorReadingMonthWise> monthWiseList = monitoringDashboardMonthWiseRepository.findByMonthYearAndSubIds(monthYear, extDataStageDefinitionList.stream().map(ExtDataStageDefinition::getSubsId).collect(Collectors.toList()));
        Double monthlyYieldSum = 0d;
        monthlyYieldSum = monthWiseList.stream()
                .mapToDouble(reading -> reading.getYield())
                .sum();
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM");
        String monthAbbreviation = YearMonth.now().format(outputFormatter);
        return YieldDataDTO.builder().yield(monthlyYieldSum).day(monthAbbreviation).build();
    }

    /* this method is used to get the daily yield sum for the given list of subscriptions */
    @Override
    public YieldDataDTO getDailyYield(List<ExtDataStageDefinition> extDataStageDefinitionList) {
        String currentYearMonthPrvDay = Utility.getCurrentYearMonthAndPreviousDay();
        List<MonitorReadingDayWise> dayWiseList = monitoringDashboardDailyWiseRepository.findByYearMonthDayAndSubIds(currentYearMonthPrvDay, extDataStageDefinitionList.stream().map(ExtDataStageDefinition::getSubsId).collect(Collectors.toList()));
        Double dailyYieldSum = 0d;
        dailyYieldSum = dayWiseList.stream()
                .mapToDouble(reading -> reading.getYield())
                .sum();
        LocalDate localDate = LocalDate.parse(currentYearMonthPrvDay, DateTimeFormatter.ofPattern(Utility.SYSTEM_DATE_FORMAT));
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM");
        String monthAbbreviation = localDate.format(monthFormatter);
        int day = localDate.getDayOfMonth();
        return YieldDataDTO.builder().yield(dailyYieldSum).day(day + "-" + monthAbbreviation).build();
    }

    /*this method is used to get the yearly yield sum for the given list of subscriptions*/
    @Override
    public YieldDataDTO getLifeTimeYield(List<String> refIds) {
        int currentYear = Year.now().getValue();
        Double yearlyYieldSum = 0d;

        for (String refId : refIds) {
            int startYear = Year.now().getValue(), endYear = Year.now().getValue(), gardenStartYear = Year.now().getValue();
            List<ExtDataStageDefinition> extDataStageDefinitionList = extDataStageDefinitionService.findAllByRefIdIn(Arrays.asList(refId), "ACTIVE");
            Optional<ExtDataStageDefinition> ext = extDataStageDefinitionList.stream().findFirst();
            if (ext.isPresent()) {
                String gardenStartDate = Utility.getMeasureAsJson(ext.get().getMpJson(), Constants.RATE_CODES.GARDEN_START_DATE);
                if (gardenStartDate != null) {
                    gardenStartYear = getYearFromDateString(gardenStartDate);
                }
                if (gardenStartYear >= currentYear) {
                    startYear = currentYear;
                    endYear = gardenStartYear;
                } else {
                    startYear = gardenStartYear;
                    endYear = currentYear;
                }
            }
            List<MonitorReadingYearWise> yearWiseList = monitoringDashboardYearWiseRepository.findByStartAndEndYearAndSubIds(String.valueOf(startYear), String.valueOf(endYear), extDataStageDefinitionList.stream().map(ExtDataStageDefinition::getSubsId).collect(Collectors.toList()));
            yearlyYieldSum += yearWiseList.stream()
                    .mapToDouble(reading -> reading.getYield())
                    .sum();
        }
        return YieldDataDTO.builder().yield(yearlyYieldSum).day("TOTAL").build();
    }

    private int getYearFromDateString(String dateString) {
        String[] parts = dateString.split("-");
        if (parts.length >= 1) {
            try {
                return Integer.parseInt(parts[0]);
            } catch (NumberFormatException e) {
                // Handle parsing error
                System.err.println("Error parsing the year: " + e.getMessage());
            }
        }
        return -1; // Default value or error indicator
    }

    @Override
    public YieldWidgetDataDTO getYieldWidgetDataBySubscriptionId(String subscriptionId) {
        try {
            int rounding = utility.getCompanyPreference().getRounding();
            List<ExtDataStageDefinition> extDataStageDefinitionList = extDataStageDefinitionService.findAllBySubsIdIn(Arrays.asList(subscriptionId));
            YieldDataDTO yearlyYieldSum = getYearlyYield(extDataStageDefinitionList);
            YieldDataDTO monthlyYieldSum = getMonthlyYield(extDataStageDefinitionList);
            YieldDataDTO dailyYieldSum = getDailyYield(extDataStageDefinitionList);
            YieldDataDTO lifeTimeYieldSum = getLifeTimeYield((extDataStageDefinitionList != null && !extDataStageDefinitionList.isEmpty() ? Arrays.asList(extDataStageDefinitionList.get(0).getRefId()) : Collections.emptyList()));
            YieldWidgetDataDTO yieldWidgetDataDTO = YieldWidgetDataDTO.builder()
                    .yearlyYield(Double.parseDouble(utility.roundAndFormat(yearlyYieldSum.getYield(), rounding))).currentYear(yearlyYieldSum.getDay())
                    .monthlyYield(Double.parseDouble(utility.roundAndFormat(monthlyYieldSum.getYield(), rounding))).currentMonth(monthlyYieldSum.getDay())
                    .dailyYield(Double.parseDouble(utility.roundAndFormat(dailyYieldSum.getYield(), rounding))).currentDay(dailyYieldSum.getDay())
                    .lifeTimeYield(Double.parseDouble(utility.roundAndFormat(lifeTimeYieldSum.getYield(), rounding))).lifeTime("total").uom("kWh").build();
            return yieldWidgetDataDTO;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public WidgetDataDTO getSystemInformationBySubscriptionId(String subscriptionId) {
        WidgetDataDTO widgetDataDTO = null;
        try {
            WidgetWeatherDataResult widgetWeatherDataResult = null;
            List<WidgetDataResult> widgetDataResults = monitoringDashboardWidgetRepository.findWidgetDataBySubscriptionId(subscriptionId);
            List<WidgetWeatherDataResult> widgetWeatherDataResults = monitoringDashboardWidgetRepository.findWidgetWeatherDataBySubscriptionId(subscriptionId);
            if (widgetWeatherDataResults.size() > 0) {
                widgetWeatherDataResult = widgetWeatherDataResults.get(0);
            }
            if (widgetDataResults.size() > 0) {
                WidgetDataResult widgetDataResult = widgetDataResults.get(0);
                widgetDataDTO = WidgetDataDTO.builder()
                        .refId(widgetDataResult.getRefId())
                        .refType(widgetDataResult.getRefType())
                        .address(widgetDataResult.getAddress())
                        .sunrise(widgetWeatherDataResult != null ? widgetWeatherDataResult.getSunrise() + " " + widgetWeatherDataResult.getTimeZone() : "")
                        .sunset(widgetWeatherDataResult != null ? widgetWeatherDataResult.getSunset() + " " + widgetWeatherDataResult.getTimeZone() : "")
                        .systemSize(widgetDataResult.getSystemSize())
                        .mp(widgetDataResult.getMp())
                        .state(widgetDataResult.getState())
                        .geoLong(widgetDataResult.getGeoLong()).geoLat(widgetDataResult.getGeoLat())
                        .googleCoordinates(widgetDataResult.getGoogleCoordinates())
                        .installationType(widgetDataResult.getInstallationType())
                        .timeZone((widgetWeatherDataResult != null && widgetWeatherDataResult.getTimeZone() != null) ? widgetWeatherDataResult.getTimeZone() : " ")
                        .build();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
        return widgetDataDTO;
    }
}
