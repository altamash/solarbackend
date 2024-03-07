package com.solar.api.tenant.service.extended.service;

import com.solar.api.tenant.model.extended.service.WorkOrderHead;

import java.util.List;

public interface WorkOrderHeadService {

    WorkOrderHead save(WorkOrderHead workOrderHead);

    WorkOrderHead update(WorkOrderHead workOrderHead);

    WorkOrderHead findById(Long id);

    List<WorkOrderHead> findAll();

    void delete(Long id);

    void deleteAll();
}
