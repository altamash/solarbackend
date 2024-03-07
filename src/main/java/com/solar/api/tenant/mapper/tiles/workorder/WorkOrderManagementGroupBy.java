package com.solar.api.tenant.mapper.tiles.workorder;

import com.solar.api.tenant.mapper.tiles.organizationmanagement.office.PhysicalLocationOMGroupBy;

import java.util.Arrays;

public enum WorkOrderManagementGroupBy {
    NONE("NONE"),
    STATUS("STATUS"),
    BOARD("BOARD"),
    TYPE("TYPE"),
    REQUESTER("REQUESTER"),
    REQUESTER_TYPE("REQUESTER TYPE"),
    BILLABLE("BILLABLE"),
    SUPPORT_AGENT("SUPPORT AGENT");
    String type;

    WorkOrderManagementGroupBy(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static WorkOrderManagementGroupBy get(String type) {
        return Arrays.stream(values()).filter(value -> type.equalsIgnoreCase(value.type)).findFirst().orElse(null);
    }
}
