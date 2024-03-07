package com.solar.api.tenant.service.extended.service;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.extended.service.WorkOrderComms;
import com.solar.api.tenant.repository.service.WorkOrderCommsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkOrderCommsServiceImpl implements WorkOrderCommsService {

    @Autowired
    private WorkOrderCommsRepository repository;

    @Override
    public WorkOrderComms save(WorkOrderComms workOrderComms) {
        return repository.save(workOrderComms);
    }

    @Override
    public WorkOrderComms update(WorkOrderComms workOrderComms) {
        return repository.save(workOrderComms);
    }

    @Override
    public WorkOrderComms findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(WorkOrderComms.class, id));
    }

    @Override
    public List<WorkOrderComms> findAll() {
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
