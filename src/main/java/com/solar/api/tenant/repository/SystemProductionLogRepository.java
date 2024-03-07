package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.extended.SystemProductionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemProductionLogRepository extends JpaRepository<SystemProductionLog, Long> {
}
