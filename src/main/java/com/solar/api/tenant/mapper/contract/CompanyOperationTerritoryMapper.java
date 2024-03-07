package com.solar.api.tenant.mapper.contract;

import com.solar.api.tenant.model.contract.CompanyOperatingTerritory;

import java.util.List;
import java.util.stream.Collectors;

public class CompanyOperationTerritoryMapper {
    public static CompanyOperatingTerritory toCompanyOperatingTerritoryList(CompanyOperatingTerritoryDTO companyOperatingTerritoryDTO) {
        return CompanyOperatingTerritory.builder()
                .id(companyOperatingTerritoryDTO.getId())
                .name(companyOperatingTerritoryDTO.getName())
                .description(companyOperatingTerritoryDTO.getDescription())
                .organization(companyOperatingTerritoryDTO.getOrganizationDTO() != null ? OrganizationMapper.toOrganization(companyOperatingTerritoryDTO.getOrganizationDTO()) : null)
                .build();
    }

    public static CompanyOperatingTerritoryDTO toCompanyOperatingTerritoryDTO(CompanyOperatingTerritory companyOperatingTerritory) {
        if (companyOperatingTerritory == null) {
            return null;
        }

        return CompanyOperatingTerritoryDTO.builder()
                .id(companyOperatingTerritory.getId())
                .name(companyOperatingTerritory.getName())
                .description(companyOperatingTerritory.getDescription())
                .organizationDTO(OrganizationMapper.toOrganizationDTO(companyOperatingTerritory.getOrganization()))
                .build();
    }


    public static List<CompanyOperatingTerritory> toCompanyOperatingTerritoryList(List<CompanyOperatingTerritoryDTO> companyOperatingTerritoryDTOList) {
        return companyOperatingTerritoryDTOList.stream().map(CompanyOperationTerritoryMapper::toCompanyOperatingTerritoryList).collect(Collectors.toList());
    }

    public static List<CompanyOperatingTerritoryDTO> toCompanyOperatingTerritoryDTOList(List<CompanyOperatingTerritory> companyOperatingTerritoryList) {
        return companyOperatingTerritoryList.stream().map(CompanyOperationTerritoryMapper::toCompanyOperatingTerritoryDTO).collect(Collectors.toList());
    }

    public static CompanyOperatingTerritory toUpdateCompanyOperatingTerritory(CompanyOperatingTerritory companyOperatingTerritory,
                                                                              CompanyOperatingTerritory companyOperatingTerritoryUpdate) {
        companyOperatingTerritory.setName(companyOperatingTerritoryUpdate.getName() == null ? companyOperatingTerritory.getName() : companyOperatingTerritoryUpdate.getName());
        companyOperatingTerritory.setDescription(companyOperatingTerritoryUpdate.getDescription() == null ? companyOperatingTerritory.getDescription() : companyOperatingTerritoryUpdate.getDescription());
        return companyOperatingTerritory;
    }
}
