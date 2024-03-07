package com.solar.api.tenant.service;

import com.mchange.util.AlreadyExistsException;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRatesDerived;

import java.util.List;

public interface RateManagementService {

    SubscriptionRatesDerived addOrUpdate(SubscriptionRatesDerived subscriptionRatesDerived) throws AlreadyExistsException;

    SubscriptionRatesDerived findById(Long id);

    List<SubscriptionRatesDerived> findByCalculationGroup(String calculationGroup);

    List<SubscriptionRatesDerived> findAll();

    void delete(Long id);

    void deleteAll();
}
