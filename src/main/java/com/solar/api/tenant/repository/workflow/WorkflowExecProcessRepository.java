package com.solar.api.tenant.repository.workflow;

import com.solar.api.tenant.model.workflow.WorkflowExecProcess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowExecProcessRepository extends JpaRepository<WorkflowExecProcess, Long> {
}
