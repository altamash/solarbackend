package com.solar.api.tenant.mapper.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LinkedContractDTO {
    private Long id;
    private String productId;
    private String variantId;
    private String gardenName;
    private String gardenDescription;
    private Long subType;
    private String type;
    private Boolean flag;

    public LinkedContractDTO(Long id, String productId, String variantId, Long subType, String type) {
        this.productId = productId;
        this.variantId = variantId;
        this.id = id;
        this.subType = subType;
        this.type = type;
    }

    public LinkedContractDTO(Long id, String productId, String variantId, String gardenName, String gardenDescription) {
        this.productId = productId;
        this.variantId = variantId;
        this.id = id;
        this.gardenName = gardenName;
        this.gardenDescription = gardenDescription;
    }

    public LinkedContractDTO(Long id, String productId, String variantId) {
        this.productId = productId;
        this.variantId = variantId;
        this.id = id;
    }


}
