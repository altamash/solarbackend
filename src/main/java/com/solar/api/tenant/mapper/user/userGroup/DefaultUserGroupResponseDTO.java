package com.solar.api.tenant.mapper.user.userGroup;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DefaultUserGroupResponseDTO {

    private Long entityRoleId;
    private Long entityId;
    private Long entityGroupId;
    private Long userGroupId;
    private Long noOfTasks;
    private String employeeName;
    private String taskId;
    private String employeeEmail;
    private String employeeDesignation;
    private String employeePhone;
    private String employmentType;
    private boolean status;
    private Date employeeJoiningDate;
    private String employmentSubType;
    private String employeeJoiningDateString;
    private String imageUrl;
    private Long id;


    public DefaultUserGroupResponseDTO(Long entityRoleId, Long entityId, String employeeName, String employeeEmail,
                                       boolean status, String employeeDesignation, String employeePhone, String employmentType,
                                       String employmentSubType, String employeeJoiningDateString,String imageUrl) {
        this.entityRoleId = entityRoleId;
        this.id = entityId;
        this.employeeName = employeeName;
        this.employeeEmail = employeeEmail;
        this.employeeDesignation = employeeDesignation;
        this.employeePhone = employeePhone;
        this.employmentType = employmentType;
        this.status = status;
        this.employmentSubType = employmentSubType;
        this.employeeJoiningDateString = employeeJoiningDateString;
        this.imageUrl = imageUrl;
    }
}
