package com.solar.api.tenant.service.extended.service;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.extended.service.ServiceHead;
import com.solar.api.tenant.repository.service.ServiceHeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceHeadServiceImpl implements ServiceHeadService {

    @Autowired
    private ServiceHeadRepository repository;

    @Override
    public ServiceHead save(ServiceHead serviceHead) {
        return repository.save(serviceHead);
    }

    @Override
    public ServiceHead update(ServiceHead serviceHead) {
        return repository.save(serviceHead);
    }

    @Override
    public ServiceHead findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(ServiceHead.class, id));
    }

    @Override
    public List<ServiceHead> findAll() {
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
