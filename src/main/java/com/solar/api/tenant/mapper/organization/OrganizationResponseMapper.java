package com.solar.api.tenant.mapper.organization;

import com.solar.api.tenant.model.contract.Organization;
import com.solar.api.tenant.model.extended.physicalLocation.Site;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrganizationResponseMapper {

    public static OrganizationResponseDTO toOrganizationDTO(Organization org) {
        if (org == null) {
            return null;
        }

        return OrganizationResponseDTO.builder()
                .id(org.getId())
                .name(org.getOrganizationName())
                .type(org.getOrganizationType())
                .subType(org.getOrganizationSubType())
                .status(org.getStatus())
                .noOfUnits(org.getSites().size())
                .units(toUnitDTO(org.getSites()))
                .build();
    }

    public static List<OrganizationResponseDTO> toOrganizationDTOs(List<Organization> organizations) {
        return organizations.stream().map(OrganizationResponseMapper::toOrganizationDTO).collect(Collectors.toList());
    }

    private static List<UnitDTO> toUnitDTO(List<Site> sites) {
        List<UnitDTO> unitDTOs = new ArrayList<>();

        sites.forEach(site -> unitDTOs.add(
                UnitDTO.builder()
                        .id(site.getId())
                        .name(site.getSiteName())
                        .noOfLocations(site.getPhysicalLocations() != null ? site.getPhysicalLocations().size() : 0)
                        .build()));

        return unitDTOs;
    }
}
