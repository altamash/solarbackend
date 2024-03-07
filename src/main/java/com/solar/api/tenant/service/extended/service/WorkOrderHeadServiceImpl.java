package com.solar.api.tenant.service.extended.service;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.extended.service.WorkOrderHead;
import com.solar.api.tenant.repository.service.WorkOrderHeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkOrderHeadServiceImpl implements WorkOrderHeadService {

    @Autowired
    private WorkOrderHeadRepository repository;

    @Override
    public WorkOrderHead save(WorkOrderHead workOrderHead) {
        return repository.save(workOrderHead);
    }

    @Override
    public WorkOrderHead update(WorkOrderHead workOrderHead) {
        return repository.save(workOrderHead);
    }

    @Override
    public WorkOrderHead findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(WorkOrderHead.class, id));
    }

    @Override
    public List<WorkOrderHead> findAll() {
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
