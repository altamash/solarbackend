package com.solar.api.tenant.service.process.billing;

import java.util.Arrays;

public enum EBillingByType {

    GENERATE("GENERATE"),
    CALCULATE("CALCULATE"),
    INVOICE("INVOICE"),
    INVOICE_PDF("INVOICE_PDF"),
    SCHEDULED_INVOICING("SCHEDULED_INVOICING"),
    INVOICE_PDF_SCHEDULED("INVOICE_PDF_SCHEDULED"),
    INVOICE_ALL("INVOICE_ALL"),
    PUBLISH_INVOICE("PUBLISH_INVOICE"),
    POST_BILLING_CALCULATIONS("POST_BILLING_CALCULATIONS");

    String type;

    EBillingByType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static EBillingByType get(String type) {
        return Arrays.stream(values()).filter(value -> type.equalsIgnoreCase(value.type)).findFirst().orElse(null);
    }
}
