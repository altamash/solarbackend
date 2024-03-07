package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.billing.billingPeriod.BillingPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingPeriodRepository extends JpaRepository<BillingPeriod, Long> {
}
