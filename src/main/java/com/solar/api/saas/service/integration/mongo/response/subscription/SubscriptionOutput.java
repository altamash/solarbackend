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
public class SubscriptionOutput {

    @JsonProperty("id")
    private IdOutput _id;
    @JsonProperty("userAccountId")
    private Long account_id;
    @JsonProperty("productGroup")
    private GroupOutput product_group;
    @JsonProperty("variantGroup")
    private GroupOutput variant_group;
    private MeasureOutput measures;
    @JsonProperty("status")
    private String active; // TODO: Should be string, Ref ESubscriptionStatus
//    @JsonProperty("startDate")
//    private Date start_date;
//    @JsonProperty("endDate")
//    private Date end_date;

}
