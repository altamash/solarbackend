package com.solar.api.tenant.mapper.organization;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnitDTO {
    private Long id;
    private String name;
    private Long type;
    private String status;
    private int noOfLocations;
}
