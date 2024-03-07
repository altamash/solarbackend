package com.solar.api.tenant.service.process.billing.publish;

import java.util.Arrays;

public enum EPublishBillStatus {

    NEW("NEW"),
    FAILED("FAILED"),
    CORRUPTED("CORRUPTED"),
    SUCCESS("SUCCESS");

    String status;

    EPublishBillStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static EPublishBillStatus get(String status) {
        return Arrays.stream(values()).filter(value -> status.equalsIgnoreCase(value.status)).findFirst().orElse(null);
    }

}
