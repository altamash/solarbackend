package com.solar.api.tenant.mapper.extended;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CodeTypeRefMapDTO {

    private Long id;
    private String regCode;
    private String refTable;
    private Long regModuleId;
    private String type;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
