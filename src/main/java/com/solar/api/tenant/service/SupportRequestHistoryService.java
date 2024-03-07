package com.solar.api.tenant.service;

import com.solar.api.tenant.model.support.SupportRequestHistory;

import java.util.List;

public interface SupportRequestHistoryService {

    public SupportRequestHistory addOrUpdate(SupportRequestHistory supportRequestHistory);

    public SupportRequestHistory findById(Long id);

    public List<SupportRequestHistory> findAll();

    List<SupportRequestHistory> findByResponderUserId(Long id);

}
