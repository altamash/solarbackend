package com.solar.api.tenant.service.extended;

import com.solar.api.tenant.model.extended.AllocHead;

import java.util.List;

public interface AllocHeadService {

    AllocHead save(AllocHead allocHead);

    AllocHead update(AllocHead allocHead);

    AllocHead findById(Long id);

    List<AllocHead> findAll();

    void delete(Long id);

    void deleteAll();
}
