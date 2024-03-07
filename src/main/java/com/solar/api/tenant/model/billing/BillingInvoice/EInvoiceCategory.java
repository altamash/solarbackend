package com.solar.api.tenant.model.billing.BillingInvoice;

import java.util.Arrays;

public enum EInvoiceCategory {
    INDIVIDUAL("INDIVIDUAL"),
    CORPORATE("CORPORATE");

    String category;

    EInvoiceCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public static EInvoiceCategory get(String type) {
        return Arrays.stream(values()).filter(value -> type.equalsIgnoreCase(value.category)).findFirst().orElse(null);
    }
}
