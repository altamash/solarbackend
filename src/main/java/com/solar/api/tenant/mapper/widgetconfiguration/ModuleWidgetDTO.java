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
public class ModuleWidgetDTO {
    private Long id;
    private String widgetName;
    private String widgetSize;
    private String widgetColor;
    private String widgetIcon;
    private String widgetUri;

}
