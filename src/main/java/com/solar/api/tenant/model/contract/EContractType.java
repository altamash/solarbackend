package com.solar.api.tenant.model.contract;

import java.util.Arrays;

public enum EContractType {
    MASTER_AGREEMENT("MASTER_AGREEMENT"),
    SERVICE_CONTRACT("SERVICE_CONTRACT");

    String contractType;

    EContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getContractType() {
        return contractType;
    }

    public static EContractType get(String contractType) {
        return Arrays.stream(values()).filter(value -> contractType.equalsIgnoreCase(value.contractType)).findFirst().orElse(null);
    }
}
