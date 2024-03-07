package com.solar.api.saas.service.integration.mongo.response.subscription.transStage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.solar.api.saas.service.integration.mongo.response.subscription.MeasureType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransStageTempDTO {
    @JsonProperty("_id")
    String subscriptionId;
    @JsonProperty("by_product")
    List<TransStageMeasureType> byProduct;
    @JsonProperty("by_customer")
    List<TransStageMeasureType> byCustomer;
    @JsonProperty("parser_code")
    String parserCode;
    @JsonProperty("variant_id")
    String variantId;

}
