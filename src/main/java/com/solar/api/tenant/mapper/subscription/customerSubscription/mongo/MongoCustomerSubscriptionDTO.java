package com.solar.api.tenant.mapper.subscription.customerSubscription.mongo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MongoCustomerSubscriptionDTO {

    private Long kwRequested;
    private String customerName;
    private String contractId;
    private String requestStatus;
    private String requestId;
    private String requestType;
    private String entityId;
    private List<String> subscriptions;
    private List<MongoCustomerSubscriptionDetailDTO> subscriptionsDetail;
    private String subscriptionId;
    private String variantId;
    private String productId;
    private Long kwdc;
    private Integer premiseNumber;
    private Date activeDate;
    private Date expiryDate;
    private String status;
    private Long accountId;

    private String entityType;
    private String subscriptionName;
// projection measures
    private String projectionImpactedAdj ;
    private String projectionPeriod;
    private String projectionTenure;
    private String projectionUOM ;
    private String projectionEff;
    private String projectionSource ;
    private String projectionName ;
    private Boolean projectionYearly ;
    private Boolean projectionMonthly ;
    private Boolean projectionDaily ;
    private Boolean projectionQuarterly ;
}

