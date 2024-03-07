package com.solar.api.tenant.service.process.pvmonitor;

import org.springframework.http.HttpMethod;

public interface APIConstants {
    HttpMethod getMethod();
    String getUrl();
    String getUrlSuffix();
}
