package com.solar.api.tenant.service.preferences;

import java.util.Arrays;

public enum EConfigParameter {
    EMAIL_HEADER("Email Header"),
    EMAIL_FOOTER("Email Footer"),
    REVERSAL_DAYS("Reversal Days"),
    AUTO_RECONCILE_DAYS("AutoReconcile Days"),
    AUTO_RECONCILE_IND("AutoReconcile Indicator");

    String name;

    EConfigParameter(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public static EConfigParameter get(String name) {
        return Arrays.stream(values()).filter(value -> name.equalsIgnoreCase(value.name)).findFirst().orElse(null);
    }
}
