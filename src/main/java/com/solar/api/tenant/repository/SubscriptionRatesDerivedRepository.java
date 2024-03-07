package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRatesDerived;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRatesDerivedRepository extends JpaRepository<SubscriptionRatesDerived, Long> {

    List<SubscriptionRatesDerived> findBySubscriptionCodeAndCalcGroup(String subscriptionCode, String calcGroup);

    List<SubscriptionRatesDerived> findBySubscriptionRateMatrixIdAndSubscriptionCodeAndCalcGroup(Long subscriptionRateMatrixId, String subscriptionCode, String calcGroup);

    SubscriptionRatesDerived findByConditionExprAndSubscriptionCodeAndCalcGroup(String conditionExpr,
                                                                                String subscriptionCode,
                                                                                String calcGroup);

    List<SubscriptionRatesDerived> findByCalcGroup(String calcGroup);

}
