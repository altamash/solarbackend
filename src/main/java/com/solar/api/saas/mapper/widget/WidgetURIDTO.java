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
public class WidgetURIDTO {

    private Long id;
    private String widgetCode;
    private String input;
    private String callingURL;
    private String retURI;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
