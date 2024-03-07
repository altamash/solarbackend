package com.solar.api.tenant.service.process.billing;

import java.util.Arrays;

public enum EPaymentCode {

    PAYMENT_CODE("Payment Code"),
    BILLING("Billing");
    String code;

    EPaymentCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static EPaymentCode get(String code) {
        return Arrays.stream(values()).filter(value -> code.equalsIgnoreCase(value.code)).findFirst().orElse(null);
    }
}
