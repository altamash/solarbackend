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
public class ReportTemplateDTO {

    private Long id;
    private String templateName;
    private String category;
    private String type;
    private String outputFormat;
    private String templateURI;
    private String permission;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
