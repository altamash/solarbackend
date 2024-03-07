package com.solar.api.tenant.service;

import com.solar.api.tenant.model.billing.billingPeriod.BillingPeriod;

import java.util.List;

public interface BillingPeriodService {

    BillingPeriod addOrUpdate(BillingPeriod billingPeriod);

    BillingPeriod findById(Long id);

    List<BillingPeriod> findAll();

    void delete(Long id);

    void deleteAll();

}
