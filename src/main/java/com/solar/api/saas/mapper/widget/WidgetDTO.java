package com.solar.api.saas.mapper.widget;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WidgetDTO {

    private Long id;
    private String widgetCode;
    private String name;
    private String type;
    private String category;
    private String helpUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
