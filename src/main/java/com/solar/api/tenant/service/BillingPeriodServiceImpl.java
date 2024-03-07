package com.solar.api.tenant.service;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.billing.billingPeriod.BillingPeriodMapper;
import com.solar.api.tenant.model.billing.billingPeriod.BillingPeriod;
import com.solar.api.tenant.repository.BillingPeriodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class BillingPeriodServiceImpl implements BillingPeriodService {

    @Autowired
    BillingPeriodRepository billingPeriodRepository;

    @Override
    public BillingPeriod addOrUpdate(BillingPeriod billingPeriod) {
        if (billingPeriod.getId() != null) {
            BillingPeriod billingPeriodData = billingPeriodRepository.getOne(billingPeriod.getId());
            if (billingPeriodData == null) {
                throw new NotFoundException(BillingPeriod.class, billingPeriod.getId());
            }
            billingPeriodData = BillingPeriodMapper.toUpdatedBillingPeriod(billingPeriodData, billingPeriod);
            return billingPeriodRepository.save(billingPeriodData);
        }
        return billingPeriodRepository.save(billingPeriod);
    }

    @Override
    public BillingPeriod findById(Long id) {
        return billingPeriodRepository.findById(id).orElseThrow(() -> new NotFoundException(BillingPeriod.class, id));
    }

    @Override
    public List<BillingPeriod> findAll() {
        return billingPeriodRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        billingPeriodRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        billingPeriodRepository.deleteAll();
    }

}
