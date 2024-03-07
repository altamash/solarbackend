package com.solar.api.tenant.service.preferences;

import com.solar.api.tenant.model.TenantConfig;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TenantConfigService {
    TenantConfig add(TenantConfig tenantConfig) throws Exception;

    TenantConfig update(TenantConfig tenantConfig) throws Exception;

    TenantConfig findById(Long id) throws Exception;

    Optional<TenantConfig> findByParameter(String parameter) throws Exception;
    List<TenantConfig> findAllByParameterIn(List<String> parameter);
    List<TenantConfig> findAll() throws Exception;

    TenantConfig findByCategory(String category) throws Exception;

    Map findAllEmailDomain();
}
