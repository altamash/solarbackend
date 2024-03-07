package com.solar.api.tenant.mapper.billingCredits;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingCreditsPMDTO {
    private String subsId;
    private Double mpa;
    private String gardenSrc;
    private String mpJson;
    private String premiseNo;

    private String date;

    public BillingCreditsPMDTO(String subsId, Double mpa, String gardenSrc, String mpJson) {
        this.subsId = subsId;
        this.mpa = mpa;
        this.gardenSrc = gardenSrc;
        this.mpJson = mpJson;
    }
}
