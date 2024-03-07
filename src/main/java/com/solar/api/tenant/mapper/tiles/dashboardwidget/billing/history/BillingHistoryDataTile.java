package com.solar.api.tenant.mapper.tiles.dashboardwidget.billing.history;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingHistoryDataTile {
    private Double totalInvoicedAmount;
    private Double totalPaidAmount;
    private String dateTime;

}
