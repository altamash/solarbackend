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
public class WidgetGlobalPermissionDTO {

    private Long id;
    private String widgetCode;
    private String validInput;
    private Boolean nullInputAllowed;
    private Boolean enabled;
    private String roleId;
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
