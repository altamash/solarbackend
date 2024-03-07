package com.solar.api.tenant.repository.workflow;

import com.solar.api.tenant.model.workflow.WorkflowGroupAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WorkflowGroupAssignmentRepository extends JpaRepository<WorkflowGroupAssignment, Long> {

    @Query("FROM WorkflowGroupAssignment wga LEFT JOIN FETCH wga.assignees WHERE id in :ids")
    List<WorkflowGroupAssignment> findAllByIdFetchAssignees(List<Long> ids);
}
