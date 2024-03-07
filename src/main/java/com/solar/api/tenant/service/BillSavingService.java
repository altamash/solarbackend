package com.solar.api.tenant.service;

import com.solar.api.tenant.model.billing.billingHead.BillSaving;

import java.util.List;

public interface BillSavingService {

    public BillSaving addOrUpdate(BillSaving billSaving);

    public BillSaving findById(Long id);

    public List<BillSaving> findAll();

    public void delete(Long id);

    public void deleteAll();
}
