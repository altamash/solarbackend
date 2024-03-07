package com.solar.api.tenant.model.paymentDetailView;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchParams {

    private Long attValue;
    private String attDependentValue;
    private String month;
    private String billingStatus;
    private String source;
}
