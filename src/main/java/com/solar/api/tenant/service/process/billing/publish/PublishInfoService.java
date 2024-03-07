package com.solar.api.tenant.service.process.billing.publish;

import com.solar.api.tenant.model.billing.BillingInvoice.PublishInfo;

import java.util.List;

public interface PublishInfoService {

    PublishInfo findByStatus(String status);

    PublishInfo save(Long headId);

    Long count(PublishInfo publishInfo);

    PublishInfo update(Long headId, int status);

    List<PublishInfo> getAll();

    List<PublishInfo> findByReferenceId(Long referenceId);

    PublishInfo findById(Long id);

    void deleteAll();
}
