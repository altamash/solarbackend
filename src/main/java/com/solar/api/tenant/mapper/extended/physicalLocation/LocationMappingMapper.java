package com.solar.api.tenant.mapper.extended.physicalLocation;

import com.solar.api.tenant.model.extended.physicalLocation.LocationMapping;

import java.util.List;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationMapper.toPhysicalLocationDTO;

public class LocationMappingMapper {

    public static LocationMapping toSiteLocation(LocationMappingDTO siteLocationDTO) {
        if (siteLocationDTO == null) {
            return null;
        }
        return LocationMapping.builder()
                .id(siteLocationDTO.getId())
                //.siteId(siteLocationDTO.getSiteId())
                .sourceId(siteLocationDTO.getSourceId())
                .sourceType(siteLocationDTO.getSourceType())
                .locationId(siteLocationDTO.getLocationId())
                .primaryInd(siteLocationDTO.getPrimaryInd())
                .status(siteLocationDTO.getStatus())
                .build();
    }

    public static LocationMappingDTO toLocationMappingDTO(LocationMapping siteLocation) {
        if (siteLocation == null) {
            return null;
        }
        return LocationMappingDTO.builder()
                .id(siteLocation.getId())
                //.siteId(siteLocation.getSiteId())
                .sourceId(siteLocation.getSourceId())
                .sourceType(siteLocation.getSourceType())
                .locationId(siteLocation.getLocationId())
                .primaryInd(siteLocation.getPrimaryInd())
                .status(siteLocation.getStatus())
                .physicalLocation(siteLocation.getPhysicalLocation() != null ? toPhysicalLocationDTO(siteLocation.getPhysicalLocation()) : null)
                .build();
    }

    public static LocationMapping toUpdatedLocationMapping(LocationMapping siteLocation, LocationMapping siteLocationUpdate) {
        //siteLocation.setSiteId(siteLocationUpdate.getSiteId() == null ? siteLocation.getSiteId() : siteLocationUpdate.getSiteId());
        siteLocation.setSourceId(siteLocationUpdate.getSourceId() == null ? siteLocation.getSourceId() : siteLocationUpdate.getSourceId());
        siteLocation.setSourceType(siteLocationUpdate.getSourceType() == null ? siteLocation.getSourceType() : siteLocationUpdate.getSourceType());
        siteLocation.setLocationId(siteLocationUpdate.getLocationId() == null ? siteLocation.getSiteId() : siteLocationUpdate.getLocationId());
        siteLocation.setPrimaryInd(siteLocationUpdate.getPrimaryInd() == null ? siteLocation.getPrimaryInd() : siteLocationUpdate.getPrimaryInd());
        siteLocation.setStatus(siteLocationUpdate.getStatus() == null ? siteLocation.getStatus() : siteLocationUpdate.getStatus());
        return siteLocation;
    }

    public static List<LocationMapping> toLocationMappings(List<LocationMappingDTO> siteLocationDTOS) {
        return siteLocationDTOS.stream().map(a -> toSiteLocation(a)).collect(Collectors.toList());
    }

    public static List<LocationMappingDTO> toLocationMappingDTOs(List<LocationMapping> siteLocations) {
        return siteLocations.stream().map(a -> toLocationMappingDTO(a)).collect(Collectors.toList());
    }
}
