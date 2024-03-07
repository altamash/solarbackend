package com.solar.api.tenant.mapper.tiles.dashboardwidget.billing;

public interface BillingSummaryWidgetTemplate {

    Double getTotalPendingAmount();
    Double getTotalCalculatedAmount();
    Double getTotalInvoicedAmount();
    Double getTotalPaidAmount();
    Double getTotalOutstandingAmount();
    String getDateTime();
    String getSubsId();

}
