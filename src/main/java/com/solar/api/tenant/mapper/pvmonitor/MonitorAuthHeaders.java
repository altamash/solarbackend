package com.solar.api.tenant.mapper.pvmonitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonitorAuthHeaders {

    private String content;
    private String time;
    private String auth;

    @Override
    public String toString() {
        return "PVMonitorAuthData{" +
                "content='" + content + '\'' +
                ", time='" + time + '\'' +
                ", auth='" + auth + '\'' +
                '}';
    }
}
