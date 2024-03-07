package com.solar.api.tenant.mapper.controlPanel;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SolarGarden {

    private Long id;
    private String name;
    private Long noOfSubscriptions;
    private Double size;
    private Double active;
    private Double inactive;
    private Double reserved;
    private Double unallocated;
}
