package com.solar.api.tenant.service.extended.service;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.extended.service.ServiceDetail;
import com.solar.api.tenant.repository.service.ServiceDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class ServiceDetailServiceImpl implements ServiceDetailService {

    @Autowired
    private ServiceDetailRepository repository;

    @Override
    public ServiceDetail save(ServiceDetail serviceDetail) {
        return repository.save(serviceDetail);
    }

    @Override
    public ServiceDetail update(ServiceDetail serviceDetail) {
        return repository.save(serviceDetail);
    }

    @Override
    public ServiceDetail findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(ServiceDetail.class, id));
    }

    @Override
    public List<ServiceDetail> findAll() {
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
