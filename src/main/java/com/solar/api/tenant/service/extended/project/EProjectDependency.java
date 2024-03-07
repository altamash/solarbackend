package com.solar.api.tenant.service.extended.project;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum EProjectDependency {

    RELATED_PROJECT("related.project"),
    RELATED_ACTIVITY("related.activity"),
    RELATED_TASK("related.task");

    String action;

    EProjectDependency(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public static List<String> getActions() {
        return Arrays.stream(com.solar.api.tenant.service.extended.project.EProjectDependency.values()).map(m -> m.action).collect(Collectors.toList());
    }

    public static com.solar.api.tenant.service.extended.project.EProjectDependency get(String status) {
        return Arrays.stream(values()).filter(value -> status.equalsIgnoreCase(value.action)).findFirst().orElse(null);
    }
}