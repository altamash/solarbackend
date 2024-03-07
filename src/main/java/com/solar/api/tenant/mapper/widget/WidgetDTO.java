package com.solar.api.tenant.mapper.widget;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WidgetDTO {

    private long widgetId;
    private String name;
    private ObjectNode configJson;
    private String details;
}
