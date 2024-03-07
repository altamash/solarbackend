package com.solar.api.tenant.model.contract;

import java.util.Arrays;

public enum EAccountStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    String status;

    EAccountStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static EAccountStatus get(String status) {
        return Arrays.stream(values()).filter(value -> status.equalsIgnoreCase(value.status)).findFirst().orElse(null);
    }
}
