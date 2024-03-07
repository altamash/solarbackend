package com.solar.api.tenant.mapper.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityResponseDTO {
    private Long id;
    private String entityName;
    private String entityType;  // customer, employee
    private String status;
    private Boolean isDocAttached;
    private Boolean isDeleted;
    private String companyName;
    private String contactPersonEmail;
    private String contactPersonPhone;
    private String website;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private  Boolean isActive;
    private String registerType; //project/partners..
    private Long registerId;
    private String imageUri;

    public EntityResponseDTO(Long id, String entityName, String entityType, String status, Boolean isDocAttached, Boolean isDeleted, String companyName, String contactPersonEmail, String contactPersonPhone, String website, Boolean isActive, String imageUri) {
        this.id = id;
        this.entityName = entityName;
        this.entityType = entityType;
        this.status = status;
        this.isDocAttached = isDocAttached;
        this.isDeleted = isDeleted;
        this.companyName = companyName;
        this.contactPersonEmail = contactPersonEmail;
        this.contactPersonPhone = contactPersonPhone;
        this.website = website;
        this.isActive = isActive;
        this.imageUri = imageUri;
    }
}
