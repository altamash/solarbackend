package com.solar.api.tenant.mapper.subscription.customerSubscription;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerSubscriptionRateCodeDTO {

    private Long id;
    private Double latitude;
    private Double longitude;
    private String inverterNumber;

    @Override
    public String toString() {
        return "CustomerSubscriptionDetail{" +
                "subscriptionId=" + id +
                ", latitude:'" + latitude + '\'' +
                ", longitude:'" + longitude + '\'' +
                ", inverterNumber:'" + inverterNumber + '\'' +
                '}';
    }
}
