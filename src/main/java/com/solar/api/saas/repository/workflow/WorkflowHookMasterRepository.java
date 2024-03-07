package com.solar.api.saas.repository.workflow;

import com.solar.api.saas.model.WorkflowHookMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowHookMasterRepository extends JpaRepository<WorkflowHookMaster, Long> {

    WorkflowHookMaster findByHookConstant(String hookConstant);
}
