package com.solar.api.tenant.mapper.tiles.organizationmanagement.office;

import java.util.List;
import java.util.stream.Collectors;

public class PhysicalLocationOMTileMapper {

    public static PhysicalLocationOMTile toPhysicalLocationOMTile(PhysicalLocationOMTemplate physicalLocationOMTemplate) {
        if (physicalLocationOMTemplate == null) {
            return null;
        }
        return PhysicalLocationOMTile.builder()
                .locationId(physicalLocationOMTemplate.getLocationId())
                .orgId(physicalLocationOMTemplate.getOrgId())
                .locationName(physicalLocationOMTemplate.getLocationName())
                .locationCategory(physicalLocationOMTemplate.getLocationCategory())
                .locationType(physicalLocationOMTemplate.getLocationType())
                .businessUnit(physicalLocationOMTemplate.getBusinessUnit())
                .address(physicalLocationOMTemplate.getAddress())
                .contactPersonName(physicalLocationOMTemplate.getContactPersonName())
                .contactPersonImage(physicalLocationOMTemplate.getContactPersonImage())
                .isLeaf((physicalLocationOMTemplate.getIsLeaf() != null && physicalLocationOMTemplate.getIsLeaf() == 1))
                .build();
    }
    public static PhysicalLocationOMTile toPhysicalLocationOMTileGroupBy(PhysicalLocationOMTemplate physicalLocationOMTemplate) {
        if (physicalLocationOMTemplate == null) {
            return null;
        }
        return PhysicalLocationOMTile.builder()
                .groupBy(physicalLocationOMTemplate.getGroupBy())
                .isLeaf((physicalLocationOMTemplate.getIsLeaf() != null && physicalLocationOMTemplate.getIsLeaf() == 1))
                .build();
    }
    public static List<PhysicalLocationOMTile> toPhysicalLocationOMTiles(List<PhysicalLocationOMTemplate> physicalLocationOMTemplates) {
        return physicalLocationOMTemplates.stream().map(ut -> toPhysicalLocationOMTile(ut)).collect(Collectors.toList());
    }
    public static List<PhysicalLocationOMTile> toPhysicalLocationOMTilesGroupBy(List<PhysicalLocationOMTemplate> physicalLocationOMTemplates) {
        return physicalLocationOMTemplates.stream().map(ut -> toPhysicalLocationOMTileGroupBy(ut)).collect(Collectors.toList());
    }
}
