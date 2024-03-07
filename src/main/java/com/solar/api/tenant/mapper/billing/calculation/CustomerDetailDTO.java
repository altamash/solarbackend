package com.solar.api.tenant.mapper.billing.calculation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDetailDTO {
    private String customerName;
    private String customerType;
    private String customerEmail;
    private String customerPhone;
    private String profileUrl;
    private Boolean isSubmitted;
    private Boolean selfInitiative;

    public CustomerDetailDTO(String customerName, String customerType, String customerEmail, String customerPhone, String profileUrl) {
        this.customerName = customerName;
        this.customerType = customerType;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.profileUrl = profileUrl;
    }

    @Override
    public String toString() {
        return "CustomerDetailDTO{" +
                "customerName='" + customerName + '\'' +
                ", customerType='" + customerType + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", customerPhone='" + customerPhone + '\'' +
                ", profileUrl='" + profileUrl + '\'' +
                ", isSubmitted='" + isSubmitted + '\'' +
                ", selfInitiative='" + selfInitiative + '\'' +
                '}';
    }
}

