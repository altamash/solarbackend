package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.billingCredits.BillingCreditsCsv;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillingCreditsCsvRepository extends JpaRepository<BillingCreditsCsv, Long> {

    List<BillingCreditsCsv> findBillingCreditsCsvByPaymentType(String paymentType);
}
