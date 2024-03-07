package com.solar.api.tenant.mapper.subscription.customerSubscription;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionCountDTO {

    BigInteger count;

    String gardenName;
}
