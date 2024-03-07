package com.solar.api.tenant.repository.workflow;

import com.solar.api.tenant.model.workflow.WorkflowHookMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkflowHookMapRepository extends JpaRepository<WorkflowHookMap, Long> {
    Optional<WorkflowHookMap> findByHookId(Long hookId);

    List<WorkflowHookMap> findListByHookId(Long hookId);
}
