package com.solar.api.tenant.model.user;

import java.util.Arrays;

public enum EUserStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    INVALID("INVALID"),
    REGISTERED("REGISTERED"),
    DISABLED("DISABLED"),
    PROSPECT("PROSPECT");

    String status;

    EUserStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static EUserStatus get(String status) {
        return Arrays.stream(values()).filter(value -> status.equalsIgnoreCase(value.status)).findFirst().orElse(null);
    }
}
