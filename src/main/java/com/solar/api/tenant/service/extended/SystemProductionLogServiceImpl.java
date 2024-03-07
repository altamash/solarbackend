package com.solar.api.tenant.service.extended;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.extended.SystemProductionLog;
import com.solar.api.tenant.repository.SystemProductionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class SystemProductionLogServiceImpl implements SystemProductionLogService {

    @Autowired
    private SystemProductionLogRepository repository;

    @Override
    public SystemProductionLog save(SystemProductionLog systemProductionLog) {
        return repository.save(systemProductionLog);
    }

    @Override
    public SystemProductionLog update(SystemProductionLog systemProductionLog) {
        return repository.save(systemProductionLog);
    }

    @Override
    public SystemProductionLog findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(SystemProductionLog.class, id));
    }

    @Override
    public List<SystemProductionLog> findAll() {
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
