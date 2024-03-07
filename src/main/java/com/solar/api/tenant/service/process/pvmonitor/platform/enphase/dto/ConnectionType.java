package com.solar.api.tenant.service.process.pvmonitor.platform.enphase.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConnectionType {
    private String key;
    private String name;

    @Override
    public String toString() {
        return "ConnectionType{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
