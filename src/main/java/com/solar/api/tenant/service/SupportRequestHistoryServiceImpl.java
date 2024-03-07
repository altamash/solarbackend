package com.solar.api.tenant.service;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.support.SupportRequestHead;
import com.solar.api.tenant.model.support.SupportRequestHistory;
import com.solar.api.tenant.repository.SupportRequestHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class SupportRequestHistoryServiceImpl implements SupportRequestHistoryService {

    @Autowired
    SupportRequestHistoryRepository supportRequestHistoryRepository;
    @Autowired
    SupportRequestHeadService supportRequestHeadService;

    @Override
    public SupportRequestHistory addOrUpdate(SupportRequestHistory supportRequestHistory) {
        SupportRequestHead supportRequestHeadData = supportRequestHeadService.findById(supportRequestHistory.getSrId());
        if (supportRequestHistory.getId() != null) {
            SupportRequestHistory supportRequestHistoryData =
                    supportRequestHistoryRepository.getOne(supportRequestHistory.getId());
            if (supportRequestHistoryData == null) {
                throw new NotFoundException(SupportRequestHistory.class, supportRequestHistory.getId());
            }
            if (supportRequestHeadData == null) {
                throw new NotFoundException(SupportRequestHead.class, supportRequestHistory.getSrId());
            }
            supportRequestHistory.setSupportRequestHead(supportRequestHeadData);
            return supportRequestHistoryRepository.save(supportRequestHistory);
        }
        supportRequestHistory.setSupportRequestHead(supportRequestHeadData);
        return supportRequestHistoryRepository.save(supportRequestHistory);
    }

    @Override
    public SupportRequestHistory findById(Long id) {
        return supportRequestHistoryRepository.findById(id).orElseThrow(() -> new NotFoundException(SupportRequestHistory.class, id));
    }

    @Override
    public List<SupportRequestHistory> findAll() {
        return supportRequestHistoryRepository.findAll();
    }

    @Override
    public List<SupportRequestHistory> findByResponderUserId(Long id) {
        return supportRequestHistoryRepository.findByResponderUserId(id);
    }

}
