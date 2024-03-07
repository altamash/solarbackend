package com.solar.api.tenant.mapper.billingCredits;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PagedBillingCreditsDTO {
    long totalItems;
    List<BillingCreditsDTO> billingCreditsDTOList;
}
