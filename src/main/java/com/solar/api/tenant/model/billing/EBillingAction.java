package com.solar.api.tenant.model.billing;

import java.util.Arrays;

public enum EBillingAction {
    SCHEDULE("SCHEDULE"),
    GENERATED("GENERATED"),
    CALCULATED("CALCULATED"),
    PUBLISH("PUBLISH"),
    RECALCULATE("RECALCULATE"),
    DEFER("DEFER");

    String action;

    EBillingAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public static EBillingAction get(String status) {
        return Arrays.stream(values()).filter(value -> status.equalsIgnoreCase(value.action)).findFirst().orElse(null);
    }
}
