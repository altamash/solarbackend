package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.AlertLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertLogRepository extends JpaRepository<AlertLog, Long> {
}
