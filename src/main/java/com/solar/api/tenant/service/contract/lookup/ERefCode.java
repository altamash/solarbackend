package com.solar.api.tenant.service.contract.lookup;

import java.util.Arrays;

public enum ERefCode {
    ORGANIZATION("ORG"),
    ENTITY("ENT"),
    CONTRACT("CONT"),
    ;

    String refCode;

    ERefCode(String refCode) {
        this.refCode = refCode;
    }

    public String getRefCode() {
        return refCode;
    }

    public static ERefCode get(String refCode) {
        return Arrays.stream(values()).filter(value -> refCode.equalsIgnoreCase(value.refCode)).findFirst().orElse(null);
    }
}
