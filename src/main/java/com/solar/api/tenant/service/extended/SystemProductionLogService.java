package com.solar.api.tenant.service.extended;

import com.solar.api.tenant.model.extended.SystemProductionLog;

import java.util.List;

public interface SystemProductionLogService {

    SystemProductionLog save(SystemProductionLog systemProductionLog);

    SystemProductionLog update(SystemProductionLog systemProductionLog);

    SystemProductionLog findById(Long id);

    List<SystemProductionLog> findAll();

    void delete(Long id);

    void deleteAll();
}
