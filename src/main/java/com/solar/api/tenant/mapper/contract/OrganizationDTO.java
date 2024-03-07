package com.solar.api.tenant.mapper.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.saas.mapper.companyPreference.CompanyPreferenceDTO;
import com.solar.api.tenant.mapper.extended.physicalLocation.LocationMappingDTO;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationDTO {
    private Long id;
    private String organizationName;
    private String businessDescription;
    private String logoImage;
    private Boolean isDocAttached;
    private Boolean primaryIndicator;
    private String status;
    private List<EntityDTO> entityDTOList;
    private List<UserLevelPrivilegeDTO> userLevelPrivilegeDTOList;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String companyURL;
    private Long totalBusinessUnitCount;
    private EntityDTO contactPerson;
    private CompanyPreferenceDTO companyPreference;
    private List<CompanyOperatingTerritoryDTO> companyOperatingTerritoryDTOs;
    private String unitCategory;
    private String unitCategoryType;
    private Long parentOrgId;
    private Long officeCount;
    private Long assetCount;
    private Long customerCount;
    private List<PhysicalLocationDTO> physicalLocationDTOList;
    private String createdAtString;
    @Override
    public String toString() {
        return "OrganizationDTO{" +
                "id=" + id +
                ", organizationName='" + organizationName + '\'' +
                ", businessDescription='" + businessDescription + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public OrganizationDTO(Long id, String organizationName,String logoImage, String businessDescription, String status,Long totalBusinessUnitCount,
                           Long entityId,String entityImg,String contactPersonName, String contactPersonEmail,String contactPersonPhoneNo,String designation,
                           Long companyPreferenceId,String websiteURL, String youtubeURL, String twitterURL, String linkedInURL,String facebookURL,String companyTerms,String companyLanding,String adminWelcomeDescription, String customerWelcomeDescription) {
        this.id = id;
        this.logoImage = logoImage;
        this.organizationName = organizationName;
        this.companyURL = websiteURL;
        this.businessDescription = businessDescription;
        this.status = status;
        this.totalBusinessUnitCount=totalBusinessUnitCount;
        this.contactPerson = new EntityDTO(entityId, contactPersonName, contactPersonEmail, contactPersonPhoneNo, entityImg, designation);
        this.companyPreference = new CompanyPreferenceDTO(companyPreferenceId,websiteURL,youtubeURL,twitterURL,linkedInURL,facebookURL,companyTerms, companyLanding,adminWelcomeDescription,customerWelcomeDescription);
    }
    public OrganizationDTO(Long id, String organizationName,String logoImage, String businessDescription, String status,Long totalBusinessUnitCount,
                           Long entityId,String entityImg,String contactPersonName, String contactPersonEmail,String contactPersonPhoneNo,String designation,
                           Long companyPreferenceId,String websiteURL, String youtubeURL, String twitterURL, String linkedInURL,String facebookURL,String companyTerms,String companyPolicy) {
        this.id = id;
        this.logoImage = logoImage;
        this.organizationName = organizationName;
        this.companyURL = websiteURL;
        this.businessDescription = businessDescription;
        this.status = status;
        this.totalBusinessUnitCount=totalBusinessUnitCount;
        this.contactPerson = new EntityDTO(entityId, contactPersonName, contactPersonEmail, contactPersonPhoneNo, entityImg, designation);
        this.companyPreference = new CompanyPreferenceDTO(companyPreferenceId,youtubeURL,twitterURL,linkedInURL,facebookURL,companyTerms, companyPolicy);
    }

    public OrganizationDTO(Long id, String organizationName,String logoImage, String unitCategoryType,String unitCategory,
                          LocalDateTime createdAt,String businessDescription,
                          Long entityId,String entityImg,String contactPersonName,
                           String contactPersonEmail,String contactPersonPhoneNo) {
        this.id = id;
        this.organizationName = organizationName;
        this.logoImage = logoImage;
        this.unitCategoryType = unitCategoryType;
        this.unitCategory = unitCategory;
        this.createdAt = createdAt;
        this.businessDescription = businessDescription;
        this.contactPerson = new EntityDTO(entityId, contactPersonName, contactPersonEmail, contactPersonPhoneNo, entityImg);


    }
}
