package com.solar.api;

import com.solar.api.exception.NotFoundException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ETypePackage {

    BILLING_HEAD("BillingHead", "com.solar.api.tenant.model.billing.billingHead.BillingHead"),
    BILLING_DETAIL("BillingDetail", "com.solar.api.tenant.model.billing.billingHead.BillingDetail"),
    PAYMENT_TRANSACTION_HEAD("PaymentTransactionHead", "com.solar.api.tenant.model.payment.billing" +
            ".PaymentTransactionHead"),
    PAYMENT_TRANSACTION_DETAIL("PaymentTransactionDetail", "com.solar.api.tenant.model.payment.billing" +
            ".PaymentTransactionDetail");
    String type;
    String pakage;

    ETypePackage(String type, String pakage) {
        this.type = type;
        this.pakage = pakage;
    }

    public static ETypePackage get(String type) {
        return Arrays.stream(values()).filter(value -> type.equalsIgnoreCase(value.type)).findFirst().orElseThrow(
                () -> new NotFoundException(ETypePackage.class, "type", type));
    }
}
