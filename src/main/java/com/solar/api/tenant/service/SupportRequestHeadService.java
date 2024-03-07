package com.solar.api.tenant.service;

import com.solar.api.tenant.mapper.support.SupportRequestHeadSearchDTO;
import com.solar.api.tenant.model.support.SupportRequestHead;

import java.util.List;

public interface SupportRequestHeadService {

    SupportRequestHead addOrUpdate(SupportRequestHead supportRequestHead);

    SupportRequestHead findById(Long id);

    List<SupportRequestHead> findByAccountId(Long id);

    List<SupportRequestHead> findAll();

    List<SupportRequestHead> findAllFetchSupportRequestHistories();

    List<SupportRequestHead> findBySubscriptionId(Long id);

    SupportRequestHead search(SupportRequestHeadSearchDTO supportRequestHeadSearchDTO);

    List<SupportRequestHead> findByStatus(String status);

}
