package com.solar.api.tenant.service.dashboardwidget;

import com.solar.api.AppConstants;
import com.solar.api.helper.Utility;
import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.tenant.mapper.dashboardwidget.DashboardWidgetMapper;
import com.solar.api.tenant.mapper.tiles.dashboardwidget.DashboardSubscriptionWidget;
import com.solar.api.tenant.mapper.tiles.dashboardwidget.DashboardSubscriptionWidgetTile;
import com.solar.api.tenant.mapper.tiles.dashboardwidget.EnviromentalWidgetTile;
import com.solar.api.tenant.mapper.tiles.dashboardwidget.billing.BillingSummaryWidgetTemplate;
import com.solar.api.tenant.mapper.tiles.dashboardwidget.billing.BillingSummaryWidgetTile;
import com.solar.api.tenant.mapper.tiles.dashboardwidget.billing.BillingWidgetMapper;
import com.solar.api.tenant.mapper.tiles.dashboardwidget.billing.history.BillingHistoryWrapperTile;
import com.solar.api.tenant.model.companyPreference.CompanyPreference;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.repository.CustomerSubscriptionRepository;
import com.solar.api.tenant.repository.MonitoringDashboardWidgetRepository;
import com.solar.api.tenant.repository.dashboardwidget.DashboardWidgetRepository;
import com.solar.api.tenant.service.CompanyPreferenceService;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.process.pvmonitor.ExtDataStageDefinitionService;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.MonitoringDashboardWidgetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class DashboardWidgetServiceImpl implements DashboardWidgetService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final DashboardWidgetRepository dashboardWidgetRepository;
    private final UserService userService;
    private final DashboardWidgetMapper dashboardWidgetMapper;
    private final CompanyPreferenceService companyPreferenceService;
    private final CustomerSubscriptionRepository customerSubscriptionRepository;
    private final MonitoringDashboardWidgetService monitoringDashboardWidgetService;
    private final Utility utility;
    private final ExtDataStageDefinitionService extDataStageDefinitionService;
    private final MonitoringDashboardWidgetRepository monitoringDashboardWidgetRepository;

    public DashboardWidgetServiceImpl(DashboardWidgetRepository dashboardWidgetRepository, UserService userService, DashboardWidgetMapper dashboardWidgetMapper,
                                      CompanyPreferenceService companyPreferenceService, CustomerSubscriptionRepository customerSubscriptionRepository,
                                      MonitoringDashboardWidgetService monitoringDashboardWidgetService, Utility utility, ExtDataStageDefinitionService extDataStageDefinitionService,
                                      MonitoringDashboardWidgetRepository monitoringDashboardWidgetRepository) {
        this.dashboardWidgetRepository = dashboardWidgetRepository;
        this.userService = userService;
        this.dashboardWidgetMapper = dashboardWidgetMapper;
        this.companyPreferenceService = companyPreferenceService;
        this.customerSubscriptionRepository = customerSubscriptionRepository;
        this.monitoringDashboardWidgetService = monitoringDashboardWidgetService;
        this.utility = utility;
        this.extDataStageDefinitionService = extDataStageDefinitionService;
        this.monitoringDashboardWidgetRepository = monitoringDashboardWidgetRepository;
    }

    @Override
    public BaseResponse getDashboardSubscriptionWidgetData() {
        DashboardSubscriptionWidget dashboardSubscriptionWidget = null;
        DashboardSubscriptionWidgetTile dashboardSubscriptionWidgetTile = null;
        try {
            User currentUser = userService.getLoggedInUser();
            dashboardSubscriptionWidget = dashboardWidgetRepository.getDashboardSubscriptionWidgetData(currentUser.getAcctId());
            dashboardSubscriptionWidgetTile = dashboardWidgetMapper.convertIntoTiles(dashboardSubscriptionWidget);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message(AppConstants.DashboardWidgets.ERROR + e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(dashboardSubscriptionWidgetTile).build();
    }

    @Override
    public BaseResponse getWelcomeWidgetData(Long companyKey) {
        try {
            User currentUser = userService.getLoggedInUser();
            String userName = currentUser.getFirstName() + " " + currentUser.getLastName();
            String databaseMessage = AppConstants.DashboardWidgets.WELCOME_TEXT + userName;
            CompanyPreference companyPreference = companyPreferenceService.findByCompanyKey(companyKey);
            if (currentUser.getUserType().getName().getName().equalsIgnoreCase(AppConstants.DashboardWidgets.HEAD_OFFICE)) {
                if (companyPreference != null && companyPreference.getAdminWelcomeWidgetText() != null) {
                    databaseMessage += ", " + companyPreference.getAdminWelcomeWidgetText();
                }
            } else if (currentUser.getUserType().getName().getName().equalsIgnoreCase(AppConstants.DashboardWidgets.CUSTOMER)) {
                if (companyPreference != null && companyPreference.getCustomerWelcomeWidgetText() != null) {
                    databaseMessage += ", " + companyPreference.getCustomerWelcomeWidgetText();
                }
            }
            return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(databaseMessage).build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.UNPROCESSABLE_ENTITY.value()).message("Error").data(null).build();
        }
    }

    @Override
    public BaseResponse getEnviromentalWidgetData(String monthYear) {
        try {
            User currentUser = userService.getLoggedInUser();
            List<ExtDataStageDefinition> extDataStageDefinitionList = dashboardWidgetRepository.findExtDataStageDefinitionByUserId(currentUser.getAcctId());
            int rounding = utility.getCompanyPreference().getRounding();
            Double treesPlanted = monitoringDashboardWidgetService.getTreesPlanted(extDataStageDefinitionList, monthYear);
            Double co2Reduction = monitoringDashboardWidgetService.getCO2Reduction(extDataStageDefinitionList, monthYear);
            Double barrels = monitoringDashboardWidgetService.getBarrels(extDataStageDefinitionList, monthYear);
            Double carCharges = monitoringDashboardWidgetService.getCarCharges(extDataStageDefinitionList, monthYear);
            Double milesCover = monitoringDashboardWidgetService.getMilesCover(extDataStageDefinitionList, monthYear);
            Double phoneCharges = monitoringDashboardWidgetService.getPhoneCharges(extDataStageDefinitionList, monthYear);
            EnviromentalWidgetTile enviromentalWidgetTile = EnviromentalWidgetTile.builder()
                    .treesPlanted(utility.roundAndFormat(treesPlanted, rounding))
                    .co2Reduction(utility.roundAndFormat(co2Reduction, rounding))
                    .barrels(utility.roundAndFormat(barrels, rounding))
                    .carCharges(utility.roundAndFormat(carCharges, rounding))
                    .milesCover(utility.roundAndFormat(milesCover, rounding))
                    .phoneCharges(utility.roundAndFormat(phoneCharges, rounding))
                    .build();
            return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(enviromentalWidgetTile).build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.UNPROCESSABLE_ENTITY.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).build();
        }
    }

    @Override
    public BaseResponse getBillingSummaryWidgetData(Long compKey, String monthYear) {
        BillingSummaryWidgetTile result = null;
        try {
            User currentUser = userService.getLoggedInUser();
            result = BillingWidgetMapper.toBillingSummaryWidgetTile(dashboardWidgetRepository
                    .getBillingSummaryWidgetByMonthYearTile(currentUser.getAcctId(), monthYear));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.UNPROCESSABLE_ENTITY.value()).message(AppConstants.DashboardWidgets.ERROR + e.getMessage()).data(null).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(result).build();
    }

    @Override
    public BaseResponse getOutstandingAmountBillingWidgetData(Long compKey, String subscriptionIds) {
        BillingSummaryWidgetTile result = null;
        try {
            Boolean subscriptionIdsPresent = subscriptionIds != null ? true : false;
            List<String> subscriptionIdsList = subscriptionIdsPresent ? Arrays.asList(subscriptionIds.split(",")) : Collections.emptyList();
            User currentUser = userService.getLoggedInUser();
            result = BillingWidgetMapper.toBillingSummaryWidgetTile(dashboardWidgetRepository.getOutstandingBillingAmountWidgetTile(currentUser.getAcctId(),
                    subscriptionIdsList, subscriptionIdsPresent));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.UNPROCESSABLE_ENTITY.value()).message(AppConstants.DashboardWidgets.ERROR + e.getMessage()).data(null).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(result).build();
    }

    @Override
    public BaseResponse getBillingHistoryWidgetData(Long compKey, Boolean isQuarterly, Boolean isComparison, String year, String subscriptionIds) {
        BillingHistoryWrapperTile result;
        try {
            List<String> subscriptionIdsList = (subscriptionIds != null)
                    ? Arrays.asList(subscriptionIds.split(","))
                    : Collections.emptyList();

            if (isComparison) {
                result = handleComparisonData(isQuarterly, year, subscriptionIdsList);
            } else {
                result = handleCumulativeData(isQuarterly, year, subscriptionIdsList);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.UNPROCESSABLE_ENTITY.value()).message(AppConstants.DashboardWidgets.ERROR + e.getMessage()).data(null).build();
        }

        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(result).build();
    }

    @Override
    public List<BillingSummaryWidgetTemplate> getBillingHistory(List<String> extSubsIds) {
        return dashboardWidgetRepository.getBillingHistoryYearlyCumulativeData(extSubsIds);
    }

    private BillingHistoryWrapperTile handleComparisonData(boolean isQuarterly, String year, List<String> subscriptionIdsList) {
        return BillingWidgetMapper.toBillingHistoryComparativeWrapperTile(
                isQuarterly
                        ? dashboardWidgetRepository.getBillingHistoryQuarterlyComparativeData(year, subscriptionIdsList)
                        : dashboardWidgetRepository.getBillingHistoryYearlyComparativeData(year, subscriptionIdsList));
    }

    private BillingHistoryWrapperTile handleCumulativeData(boolean isQuarterly, String year, List<String> subscriptionIdsList) {
        return BillingWidgetMapper.toBillingHistoryCumulativeWrapperTile(
                isQuarterly
                        ? dashboardWidgetRepository.getBillingHistoryQuarterlyCumulativeData(year, subscriptionIdsList)
                        : dashboardWidgetRepository.getBillingHistoryYearlyCumulativeData(year, subscriptionIdsList));
    }

}
