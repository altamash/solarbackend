package com.solar.api.tenant.mapper.tiles.organizationmanagement.LinkedSites.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LinkedSitesFiltersTile {
    private List<GardenOwnerDto> gardenOwner;
    private List<String> gardenType;
}
