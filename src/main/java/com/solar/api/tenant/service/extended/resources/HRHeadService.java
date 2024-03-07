package com.solar.api.tenant.service.extended.resources;

import com.solar.api.tenant.model.extended.resources.HRHead;

import java.util.List;

public interface HRHeadService {

    HRHead save(HRHead hrHead);

    HRHead update(HRHead hrHead);

    HRHead findById(Long id);

    List<HRHead> findAll();

    List<HRHead> findAllByRegisterId(Long registerId);

    HRHead findByExternalReferenceId(String externalReferenceId);

    HRHead findByLoginUser(Long userId);

    void delete(Long id);

    void deleteAll();

    List<HRHead> findAllByIdIn(List<Long> ids);
}
