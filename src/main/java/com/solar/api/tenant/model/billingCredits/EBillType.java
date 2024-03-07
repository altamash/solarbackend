package com.solar.api.tenant.model.billingCredits;

import java.util.Arrays;

public enum EBillType {

    REGULAR("REGULAR"),
    SETTLEMENT("SETTLEMENT"),
    TRUEUP("TRUEUP");

    String type;

    EBillType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static EBillType get(String type) {
        return Arrays.stream(values()).filter(value -> type.equalsIgnoreCase(value.type)).findFirst().orElse(null);
    }

}
