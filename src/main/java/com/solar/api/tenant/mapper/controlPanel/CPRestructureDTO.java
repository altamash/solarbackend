package com.solar.api.tenant.mapper.controlPanel;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CPRestructureDTO {

    private String name;
    private List<ProductType> productTypes;
    private Long count;


}
