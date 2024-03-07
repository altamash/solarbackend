package com.solar.api.tenant.model.billingCredits;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchParamsBillingCredits {

    private String creditCodeVal;
    private String gardenId;
    private String calendarMonth;
    private int pageNumber;
    private int noOfPages;
}
