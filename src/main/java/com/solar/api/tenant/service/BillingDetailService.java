package com.solar.api.tenant.service;

import com.solar.api.tenant.model.billing.billingHead.BillingDetail;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;

import java.util.List;

public interface BillingDetailService {

    BillingDetail addOrUpdateBillingDetail(BillingDetail billingDetail);

    List<BillingDetail> saveAll(List<BillingDetail> billingDetails);

    Integer findByValue(Double value);

    BillingDetail findById(Long id);

    BillingDetail findByBillingHeadAndBillingCode(BillingHead billingHead, String billingCode);

    List<BillingDetail> findByBillingHeadIdAndAddToBillAmount(Long headId, Boolean addToBillAmount);

    List<BillingDetail> findAll();

    List<BillingDetail> findByBillingHeadId(Long id);

    void delete(Long id);

    void deleteAll();
}
