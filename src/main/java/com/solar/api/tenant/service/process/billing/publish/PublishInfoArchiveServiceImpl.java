package com.solar.api.tenant.service.process.billing.publish;

import com.solar.api.tenant.model.billing.BillingInvoice.PublishInfoArchive;
import com.solar.api.tenant.repository.PublishInfoArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PublishInfoArchiveServiceImpl implements PublishInfoArchiveService {

    @Autowired
    PublishInfoArchiveRepository publishInfoArchiveRepository;

    @Override
    public PublishInfoArchive save(PublishInfoArchive publishInfoArchive) {
        return publishInfoArchiveRepository.save(publishInfoArchive);
    }
}
