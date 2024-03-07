package com.solar.api.tenant.mapper.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityDTO {
    private Long id;
    private String entityName;
    private String entityType;  // customer, employee
    private String status;
    private Boolean isDocAttached;
    private Boolean isDeleted;
    //added for business info
    private String companyName;
    private String contactPersonEmail;
    private String contactPersonPhone;
    private String website;
    private List<ContractDTO> contractDTOList;
    private List<UserLevelPrivilegeDTO> userLevelPrivilegeDTOList;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private  Boolean isActive;
    private String registerType; //project/partners..
    private Long registerId;
    private String entityImg;
    private String designation;

    public EntityDTO(Long id, String entityName, String contactPersonEmail, String contactPersonPhone, String entityImg, String designation) {
        this.id = id;
        this.entityName = entityName;
        this.contactPersonEmail = contactPersonEmail;
        this.contactPersonPhone = contactPersonPhone;
        this.entityImg = entityImg;
        this.designation = designation;
    }

    public EntityDTO(Long id, String entityName, String contactPersonEmail, String contactPersonPhone, String entityImg) {
        this.id = id;
        this.entityName = entityName;
        this.contactPersonEmail = contactPersonEmail;
        this.contactPersonPhone = contactPersonPhone;
        this.entityImg = entityImg;
    }
}
