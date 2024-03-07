package com.solar.api.tenant.service.extended.service;

import com.solar.api.tenant.model.extended.service.WorkOrderDetail;

import java.util.List;

public interface WorkOrderDetailService {

    WorkOrderDetail save(WorkOrderDetail workOrderDetail);

    WorkOrderDetail update(WorkOrderDetail workOrderDetail);

    WorkOrderDetail findById(Long id);

    List<WorkOrderDetail> findAll();

    void delete(Long id);

    void deleteAll();
}
