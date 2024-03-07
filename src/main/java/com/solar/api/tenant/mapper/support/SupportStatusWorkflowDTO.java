package com.solar.api.tenant.mapper.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SupportStatusWorkflowDTO {

    private Long ssw_id;
    private String statusNow;
    private String possibleStatuses;
    private String target;
    private String role;
}
