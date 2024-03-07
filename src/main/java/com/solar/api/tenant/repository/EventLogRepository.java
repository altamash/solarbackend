package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventLogRepository extends JpaRepository<EventLog, Long> {
}
