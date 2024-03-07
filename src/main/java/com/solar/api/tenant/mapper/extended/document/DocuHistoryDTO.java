package com.solar.api.tenant.mapper.extended.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocuHistoryDTO {

    private Long id;
    private Long docuId;
    private float version;
    private String uri;
    private Date createUpdateDatetime;
    private Long updatedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
