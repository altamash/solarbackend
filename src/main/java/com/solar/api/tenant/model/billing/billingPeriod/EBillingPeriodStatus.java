package com.solar.api.tenant.model.billing.billingPeriod;

import java.util.Arrays;

public enum EBillingPeriodStatus {

    OPEN("Open"),
    CLOSED("Closed"),
    NOT_STARTED("Not Started");

    String status;

    EBillingPeriodStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static EBillingPeriodStatus get(String status) {
        return Arrays.stream(values()).filter(value -> status.equalsIgnoreCase(value.status)).findFirst().orElse(null);
    }

}
