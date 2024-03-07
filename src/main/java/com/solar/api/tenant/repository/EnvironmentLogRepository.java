package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.extended.EnvironmentLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvironmentLogRepository extends JpaRepository<EnvironmentLog, Long> {
}
