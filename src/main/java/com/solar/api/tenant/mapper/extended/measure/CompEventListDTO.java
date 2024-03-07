package com.solar.api.tenant.mapper.extended.measure;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompEventListDTO {

    private Long id;
    private String eventName;
    private String componentId;
    private String module;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
