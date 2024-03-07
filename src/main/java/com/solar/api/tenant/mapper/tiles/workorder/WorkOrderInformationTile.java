package com.solar.api.tenant.mapper.tiles.workorder;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkOrderInformationTile {
    private String subscriptionId;
    private String message;
    private String category;
    private String parentRequestId;
    private String status;
    private String organizationName;
    private String plannedDate;
    private Double estimatedHours;
    private Long assignedResource;
    private String billable;
    private String updatedAt;
    private String entityName;
    private String uri;

    public WorkOrderInformationTile(String subscriptionId, String message, String category,
                                 String parentRequestId, String status, String organizationName,
                                 String plannedDate, Double estimatedHours, Long assignedResource,
                                 String billable, String updatedAt, String entityName, String uri) {
        this.subscriptionId = subscriptionId;
        this.message = message;
        this.category = category;
        this.parentRequestId = parentRequestId;
        this.status = status;
        this.organizationName = organizationName;
        this.plannedDate = plannedDate;
        this.estimatedHours = estimatedHours;
        this.assignedResource = assignedResource;
        this.billable = billable;
        this.updatedAt = updatedAt;
        this.entityName = entityName;
        this.uri = uri;
    }

}
