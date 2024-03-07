package com.solar.api.tenant.mapper.tiles.customermanagement;

import com.solar.api.tenant.service.process.billing.EBillingByType;

import java.util.Arrays;

public enum CustomerManagementGroupBy {
    NONE("NONE"),
    CUSTOMER_TYPE("CUSTOMER TYPE"),
    REGION("REGION"),
    SOURCE("SOURCE");
    String type;

    CustomerManagementGroupBy(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static CustomerManagementGroupBy get(String type) {
        return Arrays.stream(values()).filter(value -> type.equalsIgnoreCase(value.type)).findFirst().orElse(null);
    }
}
