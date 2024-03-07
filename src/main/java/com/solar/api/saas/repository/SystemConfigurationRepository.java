package com.solar.api.saas.repository;

import com.solar.api.saas.model.preferences.SystemConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemConfigurationRepository extends JpaRepository<SystemConfiguration, Long> {
}
