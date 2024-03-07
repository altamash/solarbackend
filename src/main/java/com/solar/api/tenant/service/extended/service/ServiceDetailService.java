package com.solar.api.tenant.service.extended.service;

import com.solar.api.tenant.model.extended.service.ServiceDetail;

import java.util.List;

public interface ServiceDetailService {

    ServiceDetail save(ServiceDetail serviceDetail);

    ServiceDetail update(ServiceDetail serviceDetail);

    ServiceDetail findById(Long id);

    List<ServiceDetail> findAll();

    void delete(Long id);

    void deleteAll();
}
