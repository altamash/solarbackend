package com.solar.api.tenant.service;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.support.SupportRequestHeadMapper;
import com.solar.api.tenant.mapper.support.SupportRequestHeadSearchDTO;
import com.solar.api.tenant.model.support.SupportRequestHead;
import com.solar.api.tenant.model.support.SupportRequestHistory;
import com.solar.api.tenant.repository.SupportRequestHeadRepository;
import com.solar.api.tenant.repository.SupportRequestHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class SupportRequestHeadServiceImpl implements SupportRequestHeadService {

    @Autowired
    SupportRequestHeadRepository supportRequestHeadRepository;
    @Autowired
    SupportRequestHistoryRepository supportRequestHistoryRepository;

    @Override
    public SupportRequestHead addOrUpdate(SupportRequestHead supportRequestHead) {
        if (supportRequestHead.getId() != null) {
            SupportRequestHead supportRequestHeadData = supportRequestHeadRepository.getOne(supportRequestHead.getId());
            if (supportRequestHeadData == null) {
                throw new NotFoundException(SupportRequestHead.class, supportRequestHead.getId());
            }
            supportRequestHeadData = SupportRequestHeadMapper.toUpdateSupportRequestHead(supportRequestHeadData,
                    supportRequestHead);
            return supportRequestHeadRepository.save(supportRequestHeadData);
        }

        return supportRequestHeadRepository.save(supportRequestHead);
    }

    @Override
    public SupportRequestHead findById(Long id) {

        SupportRequestHead supportRequestHead =
                supportRequestHeadRepository.findById(id).orElseThrow(() -> new NotFoundException(SupportRequestHead.class, id));
        List<SupportRequestHistory> supportRequestHistory =
                supportRequestHistoryRepository.findBySupportRequestHead(supportRequestHead);

        supportRequestHead.setSupportRequestHistories(supportRequestHistory);
        return supportRequestHead;
    }

    @Override
    public List<SupportRequestHead> findByAccountId(Long id) {
        return supportRequestHeadRepository.findByAccountId(id);
    }

    @Override
    public List<SupportRequestHead> findAll() {
        return supportRequestHeadRepository.findAll();
    }

    @Override
    public List<SupportRequestHead> findAllFetchSupportRequestHistories() {
        return supportRequestHeadRepository.findAllFetchSupportRequestHistories();
    }

    @Override
    public List<SupportRequestHead> findBySubscriptionId(Long id) {
        return supportRequestHeadRepository.findBySubscriptionId(id);
    }

    @Override
    public SupportRequestHead search(SupportRequestHeadSearchDTO supportRequestHeadSearchDTO) {

        System.out.println(supportRequestHeadSearchDTO);
        return null;
    }

    @Override
    public List<SupportRequestHead> findByStatus(String status) {
        return supportRequestHeadRepository.findByStatus(status);
    }
}
