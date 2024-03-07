package com.solar.api.configuration;

import java.util.HashMap;
import java.util.Map;

public class UserTenantInformation {

    private Map<String, String> map = new HashMap<>();

    public UserTenantInformation() {
    }

    public Map<String, String> getMap() {
        return map;
    }

    public UserTenantInformation setMap(Map<String, String> map) {
        this.map = map;
        return this;
    }
}
