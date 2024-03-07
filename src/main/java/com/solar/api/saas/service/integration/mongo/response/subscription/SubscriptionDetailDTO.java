package com.solar.api.saas.service.integration.mongo.response.subscription;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionDetailDTO implements Serializable {

    private String productId;
    private String subId;
    private String status;
    private String subLocation;
    private String subName;
    private String subAlias;
    private String accountId;
    private String variantId;
    private String variantName;
    private String createdAt;
    private String defaultValue;
    private String subValueCN;
    private String premiseNo;
}
