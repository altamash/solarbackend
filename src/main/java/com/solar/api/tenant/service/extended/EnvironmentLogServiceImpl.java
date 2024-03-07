package com.solar.api.tenant.service.extended;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.extended.EnvironmentLog;
import com.solar.api.tenant.repository.EnvironmentLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class EnvironmentLogServiceImpl implements EnvironmentLogService {

    @Autowired
    private EnvironmentLogRepository repository;

    @Override
    public EnvironmentLog save(EnvironmentLog environmentLog) {
        return repository.save(environmentLog);
    }

    @Override
    public EnvironmentLog update(EnvironmentLog environmentLog) {
        return repository.save(environmentLog);
    }

    @Override
    public EnvironmentLog findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(EnvironmentLog.class, id));
    }

    @Override
    public List<EnvironmentLog> findAll() {
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
