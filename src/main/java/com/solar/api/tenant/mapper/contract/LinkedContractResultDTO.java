package com.solar.api.tenant.mapper.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LinkedContractResultDTO {
    private String productId;
    private String _id;
    private String name;
    private String description;
    private Boolean flag;
    private String variant_alias;
}
