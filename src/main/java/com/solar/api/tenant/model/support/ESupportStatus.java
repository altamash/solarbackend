package com.solar.api.tenant.model.support;

import java.util.Arrays;

public enum ESupportStatus {

    NEW("NEW"),
    IN_PROGRESS("IN PROGRESS"),
    REQUIRE_CLARIFICATION("REQUIRE CLARIFICATION");

    String status;

    ESupportStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static ESupportStatus get(String status) {
        return Arrays.stream(values()).filter(value -> status.equalsIgnoreCase(value.status)).findFirst().orElse(null);
    }

    public static void main(String[] args) {

        ESupportStatus.NEW.getStatus();
    }
}
