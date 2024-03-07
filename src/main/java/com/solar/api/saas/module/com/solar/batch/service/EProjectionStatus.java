package com.solar.api.saas.module.com.solar.batch.service;

import java.util.Arrays;

public enum EProjectionStatus {

    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");
    String status;




    EProjectionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static EProjectionStatus get(String status) {
        return Arrays.stream(values()).filter(value -> status.equalsIgnoreCase(value.status)).findFirst().orElse(null);
    }
}
