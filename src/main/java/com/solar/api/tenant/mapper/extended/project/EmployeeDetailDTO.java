package com.solar.api.tenant.mapper.extended.project;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDetailDTO {

    private Long id;
    private String gender;
    private String ethnicity;
    private String phoneNumber;
    private String personalEmail;
    private String emergencyContactName;
    private String emergencyContactRelation;
    private String emergencyContactNumber;
    private String nextOfKinName;
    private String nextOfKinRelation;
    private String nextOfKinContactNumber;

    private String salutation;
    private String firstName;
    private String lastName; //add in entity name
    private String department;
    private String designation;
    private String employmentType;
    private String reportingManager;
    private Date dateOfJoining;
    private String primaryOffice;
    private Long entityId;
    private String hierarchyLevel;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Boolean hasLogin;
    private Boolean mobileAllowed;
    private Date dateOfBirth;
    private Date terminationDate;

}
