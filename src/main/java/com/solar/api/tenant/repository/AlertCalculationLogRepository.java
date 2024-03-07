package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.AlertCalculationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertCalculationLogRepository extends JpaRepository<AlertCalculationLog, Long> {
}
