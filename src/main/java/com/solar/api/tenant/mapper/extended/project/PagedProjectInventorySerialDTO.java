package com.solar.api.tenant.mapper.extended.project;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@Builder
public class PagedProjectInventorySerialDTO {
    long totalItems;
    List<ProjectInventorySerialDTO> inventorySerials;
}
