package com.solar.api.tenant.mapper.tiles.organizationmanagement.office.filter;

import com.solar.api.tenant.mapper.tiles.organizationmanagement.office.PhysicalLocationOMTemplate;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.office.PhysicalLocationOMTile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PhysicalLocationOMFilterMapper {

    public static PhysicalLocationOMFilterDTO toPhysicalLocationOMFilterDTO(PhysicalLocationOMFilterTemplate physicalLocationOMFilterTemplate) {
        if (physicalLocationOMFilterTemplate == null) {
            return null;
        }
        return PhysicalLocationOMFilterDTO.builder()
                .locationCategory(physicalLocationOMFilterTemplate.getLocationCategory() != null ? Arrays.stream(physicalLocationOMFilterTemplate.getLocationCategory().split(",")).collect(Collectors.toList()) : null)
                .locationType(physicalLocationOMFilterTemplate.getLocationType() != null ? Arrays.stream(physicalLocationOMFilterTemplate.getLocationType().split(",")).collect(Collectors.toList()) : null)
                .businessUnit(physicalLocationOMFilterTemplate.getBusinessUnit() != null ? Arrays.stream(physicalLocationOMFilterTemplate.getBusinessUnit().split(",")).collect(Collectors.toList()) : null)
                .build();
    }

}
