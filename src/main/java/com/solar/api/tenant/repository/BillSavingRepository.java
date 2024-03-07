package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.billing.billingHead.BillSaving;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillSavingRepository extends JpaRepository<BillSaving, Long> {
}
