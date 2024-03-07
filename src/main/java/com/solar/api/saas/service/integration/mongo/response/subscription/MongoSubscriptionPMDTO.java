package com.solar.api.saas.service.integration.mongo.response.subscription;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MongoSubscriptionPMDTO<T> implements Serializable {
    private String subscriptionId;
    private String subscriptionStatus;
    private String variantId;
    private String variantAlias;
    private String productId;
    private String filteredMeasure;
    private String subscriptionName;
    private String systemSize;
    private Long custAdd;
    private Long siteLocationId;

    private String productName;

    private String extJson;


}