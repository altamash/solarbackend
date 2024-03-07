package com.solar.api.tenant.mapper.extended.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrRateGroupDTO {

    private Long id;
    private String groupName;
    private Long description;
    private Long prRateId;
    private String sequenceNumber;
    private String overtimeApplicableInd;
    private String category;
    private String referenceFunction;
    private String notes;
}
