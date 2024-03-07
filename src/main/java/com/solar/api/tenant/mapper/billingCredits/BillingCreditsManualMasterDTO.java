package com.solar.api.tenant.mapper.billingCredits;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingCreditsManualMasterDTO {
    private List<BillingCreditsManualDTO> credits;

}
