package com.solar.api.saas.service.integration.mongo.response.subscription;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Subscription {

    @JsonProperty("_id")
    private Id id;
    @JsonProperty("account_id")
    private Long userAccountId;
    @JsonProperty("product_group")
    private Group productGroup;
    @JsonProperty("variant_group")
    private Group variantGroup;
    private Measure measures;
//    @JsonProperty("data_blocks")
//    List<DataBlock> dataBlocks;
    @JsonProperty("status")
    private String active; // TODO: Should be string, Ref ESubscriptionStatus
    @JsonProperty("start_date")
    private Date startDate;
    @JsonProperty("end_date")
    private Date endDate;


}
