package com.solar.api.tenant.mapper.widgetconfiguration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserWidgetDTO {
    private Long id;
    private String widgetName;
    private ModuleWidgetDTO moduleWidgetDTO;
    private EndPointsDTO endPointsDTO;
    private Long acctId;
    private Long moduleWidgetId;
    private Long endpointId;

}
