package com.solar.api.tenant.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.AuditResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditResponseWrapper {

    private Long total;
    private List<AuditResponse> auditResponses;
}
