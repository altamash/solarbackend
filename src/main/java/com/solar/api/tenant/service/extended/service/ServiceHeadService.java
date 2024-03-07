package com.solar.api.tenant.service.extended.service;

import com.solar.api.tenant.model.extended.service.ServiceHead;

import java.util.List;

public interface ServiceHeadService {

    ServiceHead save(ServiceHead serviceHead);

    ServiceHead update(ServiceHead serviceHead);

    ServiceHead findById(Long id);

    List<ServiceHead> findAll();

    void delete(Long id);

    void deleteAll();
}
