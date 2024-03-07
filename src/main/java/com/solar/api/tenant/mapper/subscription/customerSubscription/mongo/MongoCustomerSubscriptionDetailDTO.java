package com.solar.api.tenant.mapper.subscription.customerSubscription.mongo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MongoCustomerSubscriptionDetailDTO {
    @JsonProperty("_id")
    private IdDTO idDTO;
    @JsonProperty("product_group")
    private ProductGroupDTO productGroupDTO;
    @JsonProperty("variant_group")
    private VariantGroupDTO variantGroupDTO;
    @JsonProperty("measures")
    private MeasuresDTO measuresDTOObject;
    private String active;
    private String active_date;
    private String status;
    private String requestId;
    private String data_blocks = null;

}

