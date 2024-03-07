package com.solar.api.tenant.mapper.tiles.paymentManagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerPaymentDashboardDataTile {
    private CustomerPaymentDashboardTile data;

}
