package com.solar.api.tenant.service;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.billing.billingHead.BillSavingMapper;
import com.solar.api.tenant.model.billing.billingHead.BillSaving;
import com.solar.api.tenant.repository.BillSavingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class BillSavingServiceImpl implements BillSavingService {


    @Autowired
    BillSavingRepository billSavingRepository;

    @Override
    public BillSaving addOrUpdate(BillSaving billSaving) {
        if (billSaving.getBillId() != null) {
            BillSaving billSavingData = billSavingRepository.getOne(billSaving.getBillId());
            if (billSavingData == null) {
                throw new NotFoundException(BillSaving.class, billSaving.getBillId());
            }
            billSavingData = BillSavingMapper.toUpdatedBillSaving(billSavingData, billSaving);
            return billSavingRepository.save(billSavingData);
        }
        return billSavingRepository.save(billSaving);
    }

    @Override
    public BillSaving findById(Long id) {
        return billSavingRepository.findById(id).orElseThrow(() -> new NotFoundException(BillSaving.class, id));
    }

    @Override
    public List<BillSaving> findAll() {
        return billSavingRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        billSavingRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        billSavingRepository.deleteAll();
    }
}
