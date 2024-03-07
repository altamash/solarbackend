package com.solar.api.tenant.service.extended.service;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.extended.service.ServiceResources;
import com.solar.api.tenant.repository.service.ServiceResourcesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceResourcesServiceImpl implements ServiceResourcesService {

    @Autowired
    private ServiceResourcesRepository repository;

    @Override
    public ServiceResources save(ServiceResources serviceResources) {
        return repository.save(serviceResources);
    }

    @Override
    public ServiceResources update(ServiceResources serviceResources) {
        return repository.save(serviceResources);
    }

    @Override
    public ServiceResources findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(ServiceResources.class, id));
    }

    @Override
    public List<ServiceResources> findAll() {
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
