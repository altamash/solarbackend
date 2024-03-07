package com.solar.api.tenant.mapper.subscription.customerSubscription.mongo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeasuresDTO {
    ArrayList< RateCodesDTO > by_customer = new ArrayList < RateCodesDTO > ();
    ArrayList< RateCodesDTO > by_product = new ArrayList < RateCodesDTO > ();

}
