package com.solar.api.saas.service.integration.mongo.response.subscription;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class VariantSubscriptionMapping {

    private Variant variant;
    private List<Subscription> subscriptions;
}
