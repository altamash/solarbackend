package com.solar.api.tenant.service.extended.service;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.extended.service.WorkOrderDetail;
import com.solar.api.tenant.repository.service.WorkOrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkOrderDetailServiceImpl implements WorkOrderDetailService {

    @Autowired
    private WorkOrderDetailRepository repository;

    @Override
    public WorkOrderDetail save(WorkOrderDetail workOrderDetail) {
        return repository.save(workOrderDetail);
    }

    @Override
    public WorkOrderDetail update(WorkOrderDetail workOrderDetail) {
        return repository.save(workOrderDetail);
    }

    @Override
    public WorkOrderDetail findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(WorkOrderDetail.class, id));
    }

    @Override
    public List<WorkOrderDetail> findAll() {
        return repository.findAll();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
