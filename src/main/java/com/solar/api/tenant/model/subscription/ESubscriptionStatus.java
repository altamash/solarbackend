package com.solar.api.tenant.model.subscription;

import java.util.Arrays;

public enum ESubscriptionStatus {

    ACTIVE("ACTIVE"),
    TERMINATED("TERMINATED"),
    INACTIVE("INACTIVE"),
    INVALID("INVALID");

    String status;

    ESubscriptionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static ESubscriptionStatus get(String status) {
        return Arrays.stream(values()).filter(value -> status.equalsIgnoreCase(value.status)).findFirst().orElse(null);
    }
}
