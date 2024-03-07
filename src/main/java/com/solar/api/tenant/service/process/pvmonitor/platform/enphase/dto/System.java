package com.solar.api.tenant.service.process.pvmonitor.platform.enphase.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class System {

    @JsonProperty("connection_type")
    private ConnectionType connectionType;
    private String statusCode;


}
