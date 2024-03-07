package com.solar.api.tenant.mapper.tiles.organizationmanagement.LinkedSites.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GardenOwnerDto {
    private Long id;
    private String name;
}
