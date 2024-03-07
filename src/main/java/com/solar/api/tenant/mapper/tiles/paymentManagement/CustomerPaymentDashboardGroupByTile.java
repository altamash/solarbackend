package com.solar.api.tenant.mapper.tiles.paymentManagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.billing.calculation.CustomerDetailDTO;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerPaymentDashboardGroupByTile {
    private CustomerPaymentDashboardTile data;
    private List<CustomerPaymentDashboardDataTile> children;
}
