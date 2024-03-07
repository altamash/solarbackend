package com.solar.api.tenant.model.extended.project;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "employee_detail")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    /*    @OneToOne(cascade = CascadeType.MERGE)
        @JoinColumn(name = "entity_id")
        private Entity entity;*/
    private Long entityId;
    private String hierarchyLevel;

    @Column(columnDefinition = "boolean default false")
    private Boolean isActive;
    private Date dateOfBirth;
    private Date terminationDate;
    private Boolean hasLogin;
    private Boolean mobileAllowed;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private String landingDefaultUrl;
}
