package com.solar.api.tenant.service.extended.service;

import com.solar.api.tenant.model.extended.service.ServiceResources;

import java.util.List;

public interface ServiceResourcesService {

    ServiceResources save(ServiceResources serviceResources);

    ServiceResources update(ServiceResources serviceResources);

    ServiceResources findById(Long id);

    List<ServiceResources> findAll();

    void delete(Long id);

    void deleteAll();
}
