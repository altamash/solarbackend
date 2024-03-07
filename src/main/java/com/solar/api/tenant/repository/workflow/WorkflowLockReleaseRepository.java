package com.solar.api.tenant.repository.workflow;

import com.solar.api.tenant.model.workflow.WorkflowLockRelease;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowLockReleaseRepository extends JpaRepository<WorkflowLockRelease, Long> {
}
