package com.solar.api.tenant.mapper.tiles.organizationmanagement;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationManagementTile {
    private Boolean isLeaf;
    private String unitName;
    private String unitImg;
    private String unitType; //unit_category_type
    private String unitCategory; //unit_category
    private String unitManagerName;
    private String unitManagerImg;
    private Long officeCount;
    private Long assetCount;
    private Long customerCount;
    private String status;
    private String createdAt;
    private String updatedAt;
    private Long totalBusinessUnitCount;
    private Long orgId;
    private String companyUrl;
    private Long linkedSiteCount;


    public OrganizationManagementTile(Long orgId, String unitName, String unitImg, String status, String unitType, Long totalBusinessUnitCount,String companyUrl) {
        this.orgId = orgId;
        this.unitName = unitName;
        this.unitImg = unitImg;
        this.status = status;
        this.unitType = unitType;
        this.companyUrl=companyUrl;
        this.totalBusinessUnitCount=totalBusinessUnitCount;
    }

    public OrganizationManagementTile(Long orgId, String unitName, String unitImg, String unitType, String unitCategory, String unitManagerName, String unitManagerImg, String status, String createdAt, String updatedAt) {
        this.orgId = orgId;
        this.unitName = unitName;
        this.unitImg = unitImg;
        this.unitType = unitType;
        this.unitCategory = unitCategory;
        this.unitManagerName = unitManagerName;
        this.unitManagerImg = unitManagerImg;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

    }

}