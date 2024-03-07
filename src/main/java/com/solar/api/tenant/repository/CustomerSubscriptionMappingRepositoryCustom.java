package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerSubscriptionMappingRepositoryCustom {

    List<CustomerSubscriptionMapping> getMappingsForCalculationOrderedBySequence(@Param("subscription") CustomerSubscription subscription,
                                                                                 @Param("subscriptionRateMatrixHeadId"
                                                                                 ) Long subscriptionRateMatrixHeadId);

    List<CustomerSubscriptionMapping> getMappingsWithStaticValues(@Param("subscription") CustomerSubscription subscription,
                                                                  @Param("subscriptionRateMatrixHeadId") Long subscriptionRateMatrixHeadId);

    List<CustomerSubscriptionMapping> findCustomerSubscriptionMappingIncludingVaryByCustomerZero(Long customerSubscriptionId, Long subscriptionRateMatrixId);
}
