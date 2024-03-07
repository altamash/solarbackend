package com.solar.api.tenant.model.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.model.user.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "customer_detail")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) 	//  ignore all null fields
public class CustomerDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long entityId;
    private String customerType; //  Commercial, Individual
    private String states; //PROSPECT, LEAD, CUSTOMER
    private String prefix;
    private String category;
    private boolean isActive;
    private boolean hasLogin;
    private boolean mobileAllowed;
    private Date date;
    private String phoneNo;
    private String altPhoneNo;
    private String altEmail;
    private Date signUpDate;
    private Long linkedCompany;
    private String status;
    private boolean priorityIndicator;
    private Long ratingNum;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private boolean isCustomer;
    private boolean isContractSign;
    private String notes;
    private String leadSource;
    @Column(columnDefinition = "boolean default false")
    private Boolean isSubmitted;
    @Column(columnDefinition = "boolean default false")
    private Boolean selfInitiative;
    private String landingDefaultUrl;
}
