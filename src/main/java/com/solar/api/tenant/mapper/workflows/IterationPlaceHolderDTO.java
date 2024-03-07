package com.solar.api.tenant.mapper.workflows;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IterationPlaceHolderDTO {

    Long iterationId;
    String phName1;
    String phName2;
    String phName3;
    String phName4;
    String phName5;
    String billing_code;
    String billing_code_value;
}
