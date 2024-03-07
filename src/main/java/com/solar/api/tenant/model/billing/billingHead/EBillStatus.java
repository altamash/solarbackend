package com.solar.api.tenant.model.billing.billingHead;

import java.util.Arrays;

public enum EBillStatus {

    SCHEDULED("SCHEDULED"),
    PENDING("PENDING"),
    GENERATED("GENERATED"),
    CALCULATED("CALCULATED"),
    INVOICED("INVOICED"),
    PAID("PAID"),
    DEFERRED("DEFERRED"),
    LOCKED("LOCKED"),
    INVALID("INVALID"),
    DISCONTINUED("DISCONTINUED"),
    SKIPPED("SKIPPED"),
    UNSKIPPED("UNSKIPPED"),
    ERROR("ERROR"),
    IN_PAYMENT("IN-PAYMENT"),
    PUBLISHED("PUBLISHED"),
    RECALCULATING("RECALCULATING"),
    PROJECTION("PROJECTION");
    String status;

    EBillStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static EBillStatus get(String status) {
        return Arrays.stream(values()).filter(value -> status.equalsIgnoreCase(value.status)).findFirst().orElse(null);
    }

}
