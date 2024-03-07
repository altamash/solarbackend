package com.solar.api.tenant.mapper.extended.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EngagementRoleMapDTO {

    private Long id;
    private Long engagementRoleId;
    private Long prRateGroupId;
    private Long prRateId;
    private Long overrideRate;
    private Long overrideOtRate;
    private String sequence;
    private String status;
}
