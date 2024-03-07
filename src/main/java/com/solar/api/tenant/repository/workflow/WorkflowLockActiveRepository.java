package com.solar.api.tenant.repository.workflow;

import com.solar.api.tenant.model.workflow.WorkflowLockActive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowLockActiveRepository extends JpaRepository<WorkflowLockActive, Long> {
}
