package com.solar.api.saas.service.process.migration;

import com.solar.api.exception.NotFoundException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum EReconStatus {

    PENDING("PENDING", ""),
    COMPLETED("COMPLETED", ""),
    MANUAL("MANUAL", ""),
    REJECTED("REJECTED", ""),
    FAILED("FAILED", "");

    String status;
    String description;

    EReconStatus(String status, String description) {
        this.status = status;
        this.description = description;
    }

    public static EReconStatus get(String status) {
        return Arrays.stream(values()).filter(value -> status.equalsIgnoreCase(value.status)).findFirst().orElseThrow(
                () -> new NotFoundException(EReconStatus.class, "status", status));
    }
}
