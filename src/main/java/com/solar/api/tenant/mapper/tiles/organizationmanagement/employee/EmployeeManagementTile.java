package com.solar.api.tenant.mapper.tiles.organizationmanagement.employee;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeManagementTile {
    private Boolean isLeaf;
    private String entityName;
    private String entityType;
    private String entityImage;
    private String email;
    private String phoneNum;
    private String status;
    private Boolean hasLogin;
    private Boolean mobileAllowed;
    private String lastLogin;
    private String createdAt;
    private Long orgId;
    private Long entityId;
    private Long entityRoleId;
    private String designation;
    private String employmentType;
    private String employmentSubType;
    private String office;

    public EmployeeManagementTile(Long entityId,String entityName, String entityImage, String email, String phoneNum, String status, Boolean hasLogin, Boolean mobileAllowed, String createdAtString, Long orgId) {
        this.entityId=entityId;
        this.entityName = entityName;
        this.entityImage = entityImage;
        this.email = email;
        this.phoneNum = phoneNum;
        this.status = status;
        this.hasLogin = hasLogin;
        this.mobileAllowed = mobileAllowed;
        this.lastLogin = lastLogin;
        this.createdAt = createdAtString;
        this.orgId = orgId;
    }

    public EmployeeManagementTile(Long entityId, String entityName, String entityImage, String email, String phoneNum, String status, Boolean hasLogin, Boolean mobileAllowed, String createdAtString,LocalDateTime createdAt,Long orgId) {
        this.entityId = entityId;
        this.entityName = entityName;
        this.entityImage = entityImage;
        this.email = email;
        this.phoneNum = phoneNum;
        this.status = status;
        this.hasLogin = hasLogin;
        this.mobileAllowed = mobileAllowed;
        this.createdAt = createdAtString;
        this.orgId = orgId;
    }
    public EmployeeManagementTile(Long entityId, String entityName, String entityImage, String email, String phoneNum, String status, Boolean hasLogin, Boolean mobileAllowed, String createdAtString,
                                  String designation, String employmentType,String employmentSubType) {
        this.entityId = entityId;
        this.entityName = entityName;
        this.entityImage = entityImage;
        this.email = email;
        this.phoneNum = phoneNum;
        this.status = status;
        this.hasLogin = hasLogin;
        this.mobileAllowed = mobileAllowed;
        this.createdAt = createdAtString;
        this.designation = designation;
        this.employmentType = employmentType;
        this.employmentSubType = employmentSubType;
    }

    public EmployeeManagementTile(Long entityId, String entityName, String entityImage, String email, String phoneNum, String status, String createdAtString,
                                  String designation, String employmentType,String employmentSubType, String ext1,String ext2,String add3) {
        this.entityId = entityId;
        this.entityName = entityName;
        this.entityImage = entityImage;
        this.email = email;
        this.phoneNum = phoneNum;
        this.status = status;
        this.createdAt = createdAtString;
        this.designation = designation;
        this.employmentType = employmentType;
        this.employmentSubType = employmentSubType;
        StringBuilder officeBuilder = new StringBuilder();
        if (ext1 != null) {
            officeBuilder.append(ext1);
        }

        if (ext2 != null) {
            if (officeBuilder.length() > 0) {
                officeBuilder.append(",");
            }
            officeBuilder.append(ext2);
        }

        if (add3 != null) {
            if (officeBuilder.length() > 0) {
                officeBuilder.append(",");
            }
            officeBuilder.append(add3);
        }

        this.office = officeBuilder.toString();

    }

}
