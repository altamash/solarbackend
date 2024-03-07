package com.solar.api.tenant.service.dashboardwidget;

import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.tenant.mapper.tiles.dashboardwidget.billing.BillingSummaryWidgetTemplate;
import com.solar.api.tenant.mapper.tiles.dashboardwidget.billing.history.BillingHistoryWrapperTile;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DashboardWidgetService {
    BaseResponse getDashboardSubscriptionWidgetData();

    BaseResponse getWelcomeWidgetData(Long companyKey);

    BaseResponse getEnviromentalWidgetData(String monthYear);

    BaseResponse getBillingSummaryWidgetData(Long compKey, String monthYear);

    BaseResponse getOutstandingAmountBillingWidgetData(Long compKey, String subscriptionIds);

    BaseResponse getBillingHistoryWidgetData(Long compKey, Boolean isQuarterly, Boolean isComparison, String year, String subscriptionIds);

    List<BillingSummaryWidgetTemplate> getBillingHistory(List<String> extSubsIds);
}
