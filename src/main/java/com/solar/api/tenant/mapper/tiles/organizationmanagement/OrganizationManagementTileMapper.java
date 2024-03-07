package com.solar.api.tenant.mapper.tiles.organizationmanagement;

import com.solar.api.tenant.mapper.contract.EntityDTO;
import com.solar.api.tenant.mapper.contract.OrganizationDTO;
import com.solar.api.tenant.mapper.customerSupport.CustomerDTO;
import com.solar.api.tenant.mapper.tiles.customersupportmanagement.CustomerSupportFiltersTemplate;
import com.solar.api.tenant.mapper.tiles.customersupportmanagement.CustomerSupportFiltersTile;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.LinkedSites.LinkedSiteTile;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.LinkedSites.LinkedSitesTemplate;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.LinkedSites.filter.GardenOwnerDto;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.LinkedSites.filter.LinkedSitesFiltersTemplate;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.LinkedSites.filter.LinkedSitesFiltersTile;
import com.solar.api.tenant.mapper.tiles.workorder.filter.WorkOrderManagementFilterDTO;
import com.solar.api.tenant.model.dataexport.employee.EmployeeDataDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.solar.api.helper.ValidationUtils.isNumeric;

@Component
public class OrganizationManagementTileMapper {

    public static OrganizationManagementTile toOrganizationManagementTile(OrganizationManagementTemplate organizationManagementTemplate) {
        if (organizationManagementTemplate == null) {
            return null;
        }
        return OrganizationManagementTile.builder()
                .orgId(organizationManagementTemplate.getOrgId())
                .unitName(organizationManagementTemplate.getUnitName())
                .unitImg(organizationManagementTemplate.getUnitImg())
                .unitType(organizationManagementTemplate.getUnitType())
                .unitCategory(organizationManagementTemplate.getUnitCategory())
                .unitManagerName(organizationManagementTemplate.getUnitManagerName())
                .unitManagerImg(organizationManagementTemplate.getUnitManagerImg())
                .officeCount(organizationManagementTemplate.getOfficeCount())
                .assetCount(organizationManagementTemplate.getAssetCount())
                .customerCount(organizationManagementTemplate.getCustomerCount())
                .createdAt(organizationManagementTemplate.getCreatedAt())
                .updatedAt(organizationManagementTemplate.getUpdatedAt())
                .status(organizationManagementTemplate.getStatus())
                .linkedSiteCount(organizationManagementTemplate.getLinkedSiteCount())
                .build();
    }

    public static OrganizationDTO toOrganizationDTO(OrganizationManagementTemplate organizationManagementTemplate) {
        if (organizationManagementTemplate == null) {
            return null;
        }
        return OrganizationDTO.builder()
                .id(organizationManagementTemplate.getOrgId())
                .organizationName(organizationManagementTemplate.getUnitName())
                .logoImage(organizationManagementTemplate.getUnitImg())
                .unitCategoryType(organizationManagementTemplate.getUnitType())
                .unitCategory(organizationManagementTemplate.getUnitCategory())
                .contactPerson(new EntityDTO(organizationManagementTemplate.getUnitManagerId(),
                        organizationManagementTemplate.getUnitManagerName(), organizationManagementTemplate.getUnitManagerEmail(),
                        organizationManagementTemplate.getUnitManagerPhone(), organizationManagementTemplate.getUnitManagerImg()))
                .officeCount(organizationManagementTemplate.getOfficeCount())
                .assetCount(organizationManagementTemplate.getAssetCount())
                .customerCount(organizationManagementTemplate.getCustomerCount())
                .businessDescription(organizationManagementTemplate.getUnitDescription())
                .createdAtString(organizationManagementTemplate.getCreatedAt())
                .status(organizationManagementTemplate.getStatus())
                .build();
    }

    public static List<OrganizationManagementTile> toOrganizationManagementTiles(List<OrganizationManagementTemplate> organizationManagementTemplates) {
        return organizationManagementTemplates.stream().map(ut -> toOrganizationManagementTile(ut)).collect(Collectors.toList());
    }

    public static List<LinkedSiteTile> toLinkedListTiles(List<LinkedSitesTemplate> linkedSitesTemplate) {
        return linkedSitesTemplate.stream().map(ut -> toLinkedListTile(ut)).collect(Collectors.toList());
    }

    public static LinkedSiteTile toLinkedListTile(LinkedSitesTemplate linkedSitesTemplate) {
        if (linkedSitesTemplate == null) {
            return null;
        }
        return LinkedSiteTile.builder().
                refId(linkedSitesTemplate.getRefId()).
                gardenName(linkedSitesTemplate.getGardenName())
                .gardenType(linkedSitesTemplate.getGardenType())
                .gardenOwner(linkedSitesTemplate.getGardenOwner())
                .gardenRegistrationDate(linkedSitesTemplate.getGardenRegistrationDate())
                .gardenLiveDate(linkedSitesTemplate.getGoLiveDate())
                .build();
    }

    public static List<LinkedSiteTile> toPhysicalLocationOMTilesGroupBy(List<LinkedSitesTemplate> linkedSitesTemplates) {
        return linkedSitesTemplates.stream().map(ut -> toPhysicalLocationOMTileGroupBy(ut)).collect(Collectors.toList());
    }

    public static LinkedSiteTile toPhysicalLocationOMTileGroupBy(LinkedSitesTemplate linkedSitesTemplate) {
        if (linkedSitesTemplate == null) {
            return null;
        }
        return LinkedSiteTile.builder()
                .groupBy(linkedSitesTemplate.getGroupBy())
                .build();
    }

    public static LinkedSitesFiltersTile toLinkedSitesFiltersTile(LinkedSitesFiltersTemplate linkedSitesFiltersTemplate) {
        if (linkedSitesFiltersTemplate == null) {
            return null;
        }

        List<GardenOwnerDto> gardenOwners = linkedSitesFiltersTemplate.getGardenOwner() != null ?
                Arrays.stream(linkedSitesFiltersTemplate.getGardenOwner().split(","))
                        .map(s -> s.split(";"))
                        .filter(parts -> parts.length == 2 && isNumeric(parts[1]))
                        .map(parts -> new GardenOwnerDto(Long.parseLong(parts[1]), parts[0]))
                        .collect(Collectors.toList()) : Collections.emptyList();

        List<String> gardenTypes = linkedSitesFiltersTemplate.getGardenType() != null ?
                Arrays.asList(linkedSitesFiltersTemplate.getGardenType().split(",")) : Collections.emptyList();

        return LinkedSitesFiltersTile.builder()
                .gardenOwner(gardenOwners)
                .gardenType(gardenTypes)
                .build();
    }
}