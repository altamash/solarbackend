package com.solar.api.tenant.model.user;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "address")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //User table join column many_to_one
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "account_id")
    private User userAccount;
    @Transient
    private Long acctId;
    @Column(nullable = false)
    private String addressType;
    @Column(nullable = false)
    private String address1;
    private String address2;
    private String address3;
    private String area;
    private String city;
    private String state;
    private String county;
    private String country;
    private String postalCode;
    private String defaultInd;
    private String alias;
    private String countryCode;
    private String Phone;
    private String alternateContactPhone;
    private String contactPhone;
    private String contactPerson;
    private String alternateEmail;
    @Transient
    private String externalId;
    @Transient
    private String action;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
