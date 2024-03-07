package com.solar.api.tenant.mapper.extended.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EngagementRoleRateOverrideDTO {

    private Long id;
    private Long projectEngagementId;
    private Long resourceId;
    private Long roleId;
    private Long prRateId;
    private boolean disabledIndicator;
    private Long overrideRate;
    private Long overrideOvertimeRate;
    private String state;
}
