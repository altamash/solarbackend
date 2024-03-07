package com.solar.api.tenant.model.trueup;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CsgBillcreReconTemplate {

    private Double totalCredits;
    private Double totalBilled;
    private Double totalPayment;
    private Double balance;
    private Double subscriptionCost;
    private Long subscriptionId;
    private String gardenName;
    private String gardenId;
    private String premiseNo;
}
