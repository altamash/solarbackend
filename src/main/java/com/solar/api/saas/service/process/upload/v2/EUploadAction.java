package com.solar.api.saas.service.process.upload.v2;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum EUploadAction {

    INSERT("INSERT"),
    UPDATE("UPDATE");

    String action;

    EUploadAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public static List<String> getActions() {
        return Arrays.stream(EUploadAction.values()).map(m -> m.action).collect(Collectors.toList());
    }

    public static EUploadAction get(String status) {
        return Arrays.stream(values()).filter(value -> status.equalsIgnoreCase(value.action)).findFirst().orElse(null);
    }
}
