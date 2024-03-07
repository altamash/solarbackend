package com.solar.api.tenant.mapper.billingCredits;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingCreditsManualDTO {
    private String subsId;
    private String premiseNo;
    private String gardenSrc;
    private Double creditValue;
    private String creditType;
    private String period;

}
