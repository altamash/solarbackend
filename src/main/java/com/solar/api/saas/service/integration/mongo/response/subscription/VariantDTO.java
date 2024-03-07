package com.solar.api.saas.service.integration.mongo.response.subscription;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VariantDTO implements Serializable {
    private String _id;
    private String variantAlias;
    private Integer sitePhysicalLocId;
    private Integer magingPhysicalLocId;
    private List<String> subscriptionIds;
    private String variantName;
    private String variantType;
    private String status;
    private String SCSG;
    private String SCSGN;
    private Long gardenOwnerEntityId;
    private String gardenOwnerEntityName;
    private Long gardenOwnerAcctId;
    private String gardenImageUrl;
    private String utilityCompany;
}
