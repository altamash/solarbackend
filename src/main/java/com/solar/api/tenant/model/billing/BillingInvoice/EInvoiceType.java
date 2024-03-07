package com.solar.api.tenant.model.billing.BillingInvoice;

import java.util.Arrays;

public enum EInvoiceType {
    MONTHLY("MONTHLY"),
    TRUEUP("TRUEUP"),
    SETTLEMENT("SETTLEMENT"),
    REBATE("REBATE"),
    OTHERS("OTHERS");

    String type;

    EInvoiceType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static EInvoiceType get(String type) {
        return Arrays.stream(values()).filter(value -> type.equalsIgnoreCase(value.type)).findFirst().orElse(null);
    }
}
