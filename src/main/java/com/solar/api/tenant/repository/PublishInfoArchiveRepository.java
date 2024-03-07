package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.billing.BillingInvoice.PublishInfoArchive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublishInfoArchiveRepository extends JpaRepository<PublishInfoArchive, Long> {
}
