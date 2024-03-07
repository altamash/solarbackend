package com.solar.api.tenant.service.extended;

import com.solar.api.tenant.model.extended.EnvironmentLog;

import java.util.List;

public interface EnvironmentLogService {

    EnvironmentLog save(EnvironmentLog environmentLog);

    EnvironmentLog update(EnvironmentLog environmentLog);

    EnvironmentLog findById(Long id);

    List<EnvironmentLog> findAll();

    void delete(Long id);

    void deleteAll();
}
