package com.solar.api.tenant.mapper.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SupportRequestHeadSearchDTO {

    Long sr_id;
    Long subscriptionId;
    Long userAccountId;
    String status;
}
