package com.solar.api.tenant.repository.workflow;

import com.solar.api.tenant.model.workflow.WorkflowNotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowNotificationLogRepository extends JpaRepository<WorkflowNotificationLog, Long> {

    List<WorkflowNotificationLog> findByCommTypeAndStatus(String commType, String status);
}
