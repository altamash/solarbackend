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
public class ProductType {

    private String name;
    private List<SolarGarden> solarGardens;
    private Long count;
}
