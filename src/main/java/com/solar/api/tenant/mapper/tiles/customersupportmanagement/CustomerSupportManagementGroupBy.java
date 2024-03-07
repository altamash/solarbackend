package com.solar.api.tenant.mapper.tiles.customersupportmanagement;

import java.util.Arrays;

public enum CustomerSupportManagementGroupBy {
    NONE("NONE"),
    STATUS("STATUS"),
    TICKET_TYPE("TICKET TYPE"),
    REQUESTER("REQUESTER"),
    PRIORITY("PRIORITY"),
    CREATED_BY("CREATED BY");
    String type;

    CustomerSupportManagementGroupBy(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }
    public static CustomerSupportManagementGroupBy get(String type) {
        return Arrays.stream(values()).filter(value -> type.equalsIgnoreCase(value.type)).findFirst().orElse(null);
    }
}
