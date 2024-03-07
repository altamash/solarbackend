package com.solar.api.tenant.mapper.extended.project.activity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityDetailDTO {

    private Long id;
    private Long activityId;
    private Long measureId;
    private String value;
    private Long lastUpdateBy;
    private String validationRule;
    private String validationParams;
}
