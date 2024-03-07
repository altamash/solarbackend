package com.solar.api.tenant.service.process.billing.publish;

import java.util.Arrays;

public enum EPublishBillType {
    INVOICE("INVOICE"),
    NOTIFICATION("NOTIFICATION");

    String type;

    EPublishBillType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static EPublishBillType get(String type) {
        return Arrays.stream(values()).filter(value -> type.equalsIgnoreCase(value.type)).findFirst().orElse(null);
    }
}
