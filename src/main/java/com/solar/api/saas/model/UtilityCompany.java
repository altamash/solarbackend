package com.solar.api.saas.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "utility_company")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtilityCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long utilityCompanyId;
    @Column(unique = true)
    private String companyName;
    private String utilityType;
    private String contactAddress;
    private String contactPhone;
    private String contactMobile;
    private String country;
    private String city;
    private String state;
    private String county;
    private Integer postalCode;
    private String poBox;
    private Double latitude;
    private Double longitude;
    private String contactPerson;
    private String email;
    private String subscriptionReferences;
    private Long parentCompanyId;
    private String field_1;
    private String field_2;
    private String field_3;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
