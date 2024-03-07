package com.solar.api.tenant.mapper.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportIteratorDefinitionDTO {

    private Long id;
    private String iteratorName;
    private String filterCodes;
    private String templateName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
