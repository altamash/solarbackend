package com.solar.api.tenant.mapper.workOrder;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MongoWorkOrderDTO {
    private String workOrderId;
    private String workOrderName;
    private String WorkOrderSummary;
    private String customer;
    private String customerRefId;
    private String WorkOrderStatus;
    private String plannedDate;
    private String timeRequired;
    private String subscriptionId;
    private String subscriptionName;
    private String orgId;
    private String cusSuppRefId;
    private String ticketId;
    private String priority;
    private String typeId;
    private String customerName;
    private String customerType;
    private String customerEmail;
    private String customerPhone;
    private String customerImageUri;
    private String name;

    private String workOrderSeqId; // work order seq id

}

