package com.solar.api.tenant.mapper.extended.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocuLibraryDTO {

    private Long docuId;
    private String docuType;
    private String codeRefType;
    private String codeRefId;
    private String docuName;
    private String notes;
    private String tags;
    private String format;
    private String uri;
    private String status;
    private Boolean visibilityKey;
    private Long compKey;
    private Long resourceInterval;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
