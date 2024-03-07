package com.solar.api.saas.repository;

import com.solar.api.saas.model.preferences.TenantSystemConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantSystemConfigurationRepository extends JpaRepository<TenantSystemConfiguration, Long> {
}
