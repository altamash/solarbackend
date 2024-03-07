package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.billing.BillingInvoice.PublishInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PublishInfoRepository extends JpaRepository<PublishInfo, Long> {

    List<PublishInfo> findByReferenceId(Long referenceId);

    PublishInfo findByStatus(String status);
}
