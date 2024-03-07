package com.solar.api.tenant.mapper.tiles.workorder;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkOrderManagementTile {
    private Boolean isLeaf;
    private String groupBy;
    private String projectId;
    private String workOrderId;
    private Long conversationHeadId;
    private String workOrderTitle;
    private String workOrderType;
    private Long ticketId;
    private String businessUnitName;
    private String status;
    private Long requestorAcctId;
    private Long requestorEntityId;
    private String requestorName;
    private String requestorImage;
    private String requestorType;
    private Long supportAgentAcctId;
    private Long supportAgentEntityId;
    private String supportAgentName;
    private String supportAgentImage;
    private String plannedDate;
    private Double timeRequired;
    private Long assignedResources;
    private String billable;

    public WorkOrderManagementTile(String projectId, String workOrderId, Long conversationHeadId,
                                   String workOrderType, Long ticketId, String businessUnitName,
                                   String status, Long requestorAcctId, Long requestorEntityId,
                                   String requestorName, String requestorImage, String requestorType,
                                   Long supportAgentAcctId, Long supportAgentEntityId, String supportAgentName,
                                   String supportAgentImage, Double timeRequired, Long assignedResources, String billable) {
        this.projectId = projectId;
        this.workOrderId = workOrderId;
        this.conversationHeadId = conversationHeadId;
        this.workOrderType = workOrderType;
        this.ticketId = ticketId;
        this.businessUnitName = businessUnitName;
        this.status = status;
        this.requestorAcctId = requestorAcctId;
        this.requestorEntityId = requestorEntityId;
        this.requestorName = requestorName;
        this.requestorImage = requestorImage;
        this.requestorType = requestorType;
        this.supportAgentAcctId = supportAgentAcctId;
        this.supportAgentEntityId = supportAgentEntityId;
        this.supportAgentName = supportAgentName;
        this.supportAgentImage = supportAgentImage;
        this.timeRequired = timeRequired;
        this.assignedResources = assignedResources;
        this.billable = billable;
    }
}