package com.solar.api.saas.service.process.upload.v2.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerUtility {

    private String action;
    @JsonProperty("utility_id")
    private Long utilityId;
    @JsonProperty("entity_id")
    private Long entityId;
    @JsonProperty("account_holder_name")
    private String accountHolderName;
    @JsonProperty("premise")
    private String premise;
    @JsonProperty("average_monthly_bill")
    private Long averageMonthlyBill;
    @JsonProperty("util_provider_id")
    private Long utilProviderId;
    @JsonProperty("reference_num")
    private String referenceNum;

    @JsonProperty("is_checked")
    private Boolean isChecked;

    @Override
    public String toString() {
        return "CustomerAddress{" +
                "action='" + action + '\'' +
                ", utility_id='" + utilityId + '\'' +
                ", entity_id='" + entityId + '\'' +
                ", account_holder_name='" + accountHolderName + '\'' +
                ", premise='" + premise + '\'' +
                ", average_monthly_bill='" + averageMonthlyBill + '\'' +
                ", util_provider_id='" + utilProviderId + '\'' +
                ", reference_num='" + referenceNum + '\'' +
                ", is_checked='" + isChecked + '\'' +
                '}';
    }
}
