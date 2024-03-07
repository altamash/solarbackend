package com.solar.api.saas.service.integration.mongo.response.subscription;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Measure {

    @JsonProperty("by_product")
    List<MeasureType> byProduct;
    @JsonProperty("by_customer")
    List<MeasureType> byCustomer;

    @JsonProperty("allMeasures")
    List<MeasureType> allMeasures;// for invoicing
}
