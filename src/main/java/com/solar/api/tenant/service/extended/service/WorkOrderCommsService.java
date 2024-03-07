package com.solar.api.tenant.service.extended.service;

import com.solar.api.tenant.model.extended.service.WorkOrderComms;

import java.util.List;

public interface WorkOrderCommsService {

    WorkOrderComms save(WorkOrderComms workOrderComms);

    WorkOrderComms update(WorkOrderComms workOrderComms);

    WorkOrderComms findById(Long id);

    List<WorkOrderComms> findAll();

    void delete(Long id);

    void deleteAll();
}
