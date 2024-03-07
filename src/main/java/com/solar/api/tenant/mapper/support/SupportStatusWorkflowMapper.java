package com.solar.api.tenant.mapper.support;

import com.solar.api.tenant.model.support.SupportStatusWorkflow;

import java.util.List;
import java.util.stream.Collectors;

public class SupportStatusWorkflowMapper {

    public static SupportStatusWorkflow toSupportStatusWorkflow(SupportStatusWorkflowDTO supportStatusWorkflowDTO) {
        return SupportStatusWorkflow.builder()
                .ssw_id(supportStatusWorkflowDTO.getSsw_id())
                .statusNow(supportStatusWorkflowDTO.getStatusNow())
                .possibleStatuses(supportStatusWorkflowDTO.getPossibleStatuses())
                .target(supportStatusWorkflowDTO.getTarget())
                .role(supportStatusWorkflowDTO.getRole())
                .build();
    }

    public static SupportStatusWorkflowDTO toSupportStatusWorkflowDTO(SupportStatusWorkflow supportStatusWorkflow) {
        if (supportStatusWorkflow == null) {
            return null;
        }

        return SupportStatusWorkflowDTO.builder()
                .ssw_id(supportStatusWorkflow.getSsw_id())
                .statusNow(supportStatusWorkflow.getStatusNow())
                .possibleStatuses(supportStatusWorkflow.getPossibleStatuses())
                .target(supportStatusWorkflow.getTarget())
                .role(supportStatusWorkflow.getRole())
                .build();
    }

    public static SupportStatusWorkflow toUpdateSupportStatusWorkflow(SupportStatusWorkflow supportStatusWorkflow,
                                                                      SupportStatusWorkflow supportStatusWorkflowUpdate) {
        supportStatusWorkflow.setStatusNow(supportStatusWorkflowUpdate.getStatusNow() == null ?
                supportStatusWorkflow.getStatusNow() : supportStatusWorkflowUpdate.getStatusNow());
        supportStatusWorkflow.setPossibleStatuses(supportStatusWorkflowUpdate.getPossibleStatuses() == null ?
                supportStatusWorkflow.getPossibleStatuses() : supportStatusWorkflowUpdate.getPossibleStatuses());
        supportStatusWorkflow.setTarget(supportStatusWorkflowUpdate.getTarget() == null ?
                supportStatusWorkflow.getTarget() : supportStatusWorkflowUpdate.getTarget());
        supportStatusWorkflow.setRole(supportStatusWorkflowUpdate.getRole() == null ?
                supportStatusWorkflow.getRole() : supportStatusWorkflowUpdate.getRole());
        return supportStatusWorkflow;
    }

    /**
     * @param supportStatusWorkflowDTOS
     * @return
     */
    public static List<SupportStatusWorkflow> toSupportStatusWorkflows(List<SupportStatusWorkflowDTO> supportStatusWorkflowDTOS) {
        return supportStatusWorkflowDTOS.stream().map(ssw -> toSupportStatusWorkflow(ssw)).collect(Collectors.toList());
    }

    /**
     * @param supportStatusWorkflows
     * @return
     */
    public static List<SupportStatusWorkflowDTO> toSupportStatusWorkflowDTOs(List<SupportStatusWorkflow> supportStatusWorkflows) {
        return supportStatusWorkflows.stream().map(ssw -> toSupportStatusWorkflowDTO(ssw)).collect(Collectors.toList());
    }
}
