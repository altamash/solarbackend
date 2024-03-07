package com.solar.api.tenant.mapper.contract;

import com.solar.api.saas.mapper.companyPreference.CompanyPreferenceMapper;
import com.solar.api.tenant.model.contract.Organization;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrganizationMapper {
    public static Organization toOrganization(OrganizationDTO organizationDTO) {
        return Organization.builder()
                .id(organizationDTO.getId())
                .organizationName(organizationDTO.getOrganizationName())
                .businessDescription(organizationDTO.getBusinessDescription())
                .logoImage(organizationDTO.getLogoImage())
                .isDocAttached(organizationDTO.getIsDocAttached())
                .primaryIndicator(organizationDTO.getPrimaryIndicator())
                .parentOrgId(organizationDTO.getParentOrgId())
                .status(organizationDTO.getStatus())
//                .entities(organizationDTO.getEntityDTOList() != null ?
//                        EntityMapper.toEntityList(organizationDTO.getEntityDTOList()) : null)
                .userLevelPrivileges(organizationDTO.getUserLevelPrivilegeDTOList() != null ?
                        UserLevelPrivilegeMapper.toUserLevelPrivilegeList(organizationDTO.getUserLevelPrivilegeDTOList()) : null)
                .createdAt(organizationDTO.getCreatedAt())
                .updatedAt(organizationDTO.getUpdatedAt())
                .unitCategory(organizationDTO.getUnitCategory())
                .unitCategoryType(organizationDTO.getUnitCategoryType())
                .build();
    }

    public static OrganizationDTO toOrganizationDTO(Organization organization) {
        if (organization == null) {
            return null;
        }

        return OrganizationDTO.builder()
                .id(organization.getId())
                .organizationName(organization.getOrganizationName())
                .businessDescription(organization.getBusinessDescription())
                .logoImage(organization.getLogoImage())
                .isDocAttached(organization.getIsDocAttached())
                .primaryIndicator(organization.getPrimaryIndicator())
                .status(organization.getStatus())
//                .entityDTOList(organization.getEntities() != null ?
//                        EntityMapper.toEntityDTOList(organization.getEntities()) : null)
                .userLevelPrivilegeDTOList(organization.getUserLevelPrivileges() != null ?
                        UserLevelPrivilegeMapper.toUserLevelPrivilegeDTOList(organization.getUserLevelPrivileges()) : null)
                .createdAt(organization.getCreatedAt())
                .updatedAt(organization.getUpdatedAt())
                .build();
    }

    public static OrganizationDTO toOrganizationSummaryDTO(Organization organization) {
        if (organization == null) {
            return null;
        }

        return OrganizationDTO.builder()
                .id(organization.getId())
                .organizationName(organization.getOrganizationName())
                .businessDescription(organization.getBusinessDescription())
                .logoImage(organization.getLogoImage())
                .primaryIndicator(organization.getPrimaryIndicator())
                .status(organization.getStatus())
                .build();
    }

    public static List<Organization> toOrganizationList(List<OrganizationDTO> organizationDTOList) {
        return organizationDTOList.stream().map(OrganizationMapper::toOrganization).collect(Collectors.toList());
    }

    public static List<OrganizationDTO> toOrganizationDTOList(List<Organization> organizationList) {
        return organizationList.stream().map(OrganizationMapper::toOrganizationDTO).collect(Collectors.toList());
    }

    public static List<OrganizationDTO> toOrganizationSummaryDTOList(List<Organization> organizationList) {
        return organizationList.stream().map(OrganizationMapper::toOrganizationSummaryDTO).collect(Collectors.toList());
    }

    public static Organization toOrganization(OrganizationDetailDTO organizationDTO) {
        String orgType = "";
        if(organizationDTO.getParentId() != null){
            orgType="Business Unit";
        } else {
            orgType ="Master";
        }
        return Organization.builder()
                .id(organizationDTO.getId())
                .organizationName(organizationDTO.getUnitName())
                .businessDescription(organizationDTO.getDetails())
                .subType(organizationDTO.getUnitTypeId())
                .organizationType(orgType)
//                .organizationSubType(organizationDTO.getUnitSubType())
                .logoImage(organizationDTO.getLogoImage())
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .parentOrgId(organizationDTO.getParentId())
                .build();

    }

    public static OrganizationDetailDTO toOrganizationDetailDTO(Organization organization) {
        if (organization == null) {
            return null;
        }

        return OrganizationDetailDTO.builder()
                .id(organization.getId())
                .unitName(organization.getOrganizationName())
                .details(organization.getBusinessDescription())
                .logoImage(organization.getLogoImage())
                .status(organization.getStatus())

//                .entityDTOList(organization.getEntities() != null ?
//                        EntityMapper.toEntityDTOList(organization.getEntities()) : null)

                .build();
    }

    /**
     * Used in mapper of physical location
     * @param organization
     * @return
     */
    public static OrganizationDTO toOrganizationDTOPhysicalLocation(Organization organization) {
        if (organization == null) {
            return null;
        }

        return OrganizationDTO.builder()
                .id(organization.getId())
                .organizationName(organization.getOrganizationName())
                .businessDescription(organization.getBusinessDescription())
                .logoImage(organization.getLogoImage())
                .isDocAttached(organization.getIsDocAttached())
                .primaryIndicator(organization.getPrimaryIndicator())
                .status(organization.getStatus())
                .createdAt(organization.getCreatedAt())
                .updatedAt(organization.getUpdatedAt())
                .build();
    }
    public static Organization toUpdateOrganization(Organization organization, Organization organizationUpdate) {
        if (organizationUpdate.getOrganizationName() != null) {
            organization.setOrganizationName(organizationUpdate.getOrganizationName());
        }
        if (organizationUpdate.getBusinessDescription() != null) {
            organization.setBusinessDescription(organizationUpdate.getBusinessDescription());
        }
        if (organizationUpdate.getOrganizationType() != null) {
            organization.setOrganizationType(organizationUpdate.getOrganizationType());
        }
        if (organizationUpdate.getParentOrgId() != null) {
            organization.setParentOrgId(organizationUpdate.getParentOrgId());
        }
        if (organizationUpdate.getLogoImage() != null) {
            organization.setLogoImage(organizationUpdate.getLogoImage());
        }
        if (organizationUpdate.getIsDocAttached() != null) {
            organization.setIsDocAttached(organizationUpdate.getIsDocAttached());
        }
        if (organizationUpdate.getPrimaryIndicator() != null) {
            organization.setPrimaryIndicator(organizationUpdate.getPrimaryIndicator());
        }
        if (organizationUpdate.getStatus() != null) {
            organization.setStatus(organizationUpdate.getStatus());
        }
        if (organizationUpdate.getSubType() != null) {
            organization.setSubType(organizationUpdate.getSubType());
        }
        if (organizationUpdate.getOrganizationSubType() != null) {
            organization.setOrganizationSubType(organizationUpdate.getOrganizationSubType());
        }
        if (organizationUpdate.getCompanyPreference() != null) {
            organization.setCompanyPreference(organizationUpdate.getCompanyPreference());
        }
        if (organizationUpdate.getContactPerson() != null) {
            organization.setContactPerson(organizationUpdate.getContactPerson());
        }
        if (organizationUpdate.getUnitCategory() != null) {
            organization.setUnitCategory(organizationUpdate.getUnitCategory());
        }
        if (organizationUpdate.getUnitCategoryType() != null) {
            organization.setUnitCategoryType(organizationUpdate.getUnitCategoryType());
        }

        return organization;
    }

}
