package com.solar.api.tenant.mapper.tiles.dashboardwidget.billing;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingSummaryWidgetTile {
    private Double totalPendingAmount;
    private Double totalCalculatedAmount;
    private Double totalInvoicedAmount;
    private Double totalPaidAmount;
    private Double totalOutstandingAmount;

}
