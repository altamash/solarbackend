package com.solar.api.tenant.repository.workflow;

import com.solar.api.tenant.model.workflow.WorkflowGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowGroupRepository extends JpaRepository<WorkflowGroup, Long> {
}
