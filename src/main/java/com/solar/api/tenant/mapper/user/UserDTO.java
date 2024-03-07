package com.solar.api.tenant.mapper.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.ca.CaReferralInfoDTO;
import com.solar.api.tenant.mapper.ca.CaSoftCreditCheckDTO;
import com.solar.api.tenant.mapper.ca.CaUtilityDTO;
import com.solar.api.tenant.mapper.contract.UserLevelPrivilegeDTO;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO;
import com.solar.api.tenant.mapper.subscription.customerSubscription.CustomerSubscriptionDTO;
import com.solar.api.tenant.mapper.tiles.signingRequestTracker.SigningReqTrackerTile;
import com.solar.api.tenant.mapper.user.address.AddressDTO;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO implements Serializable {

    private Long acctId;
    private String jwtToken;
    private Long compKey;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private String IdCode;       //Authority ID
    private String authorityId;  //e.g. SSN, Driving License
    private String gender;
    private Date dataOfBirth;
    private Date registerDate;
    private Date activeDate;
    private String status;
    private String notes;
    private String prospectStatus;
    private String referralEmail;
    private Date deferredContactDate;
    private String language;
    private String authentication;
    private String userType;
    private Set<String> roles;
    //    private Set<String> privileges;
    private String category;
    private String groupId;
    private byte[] photo;
    private String photoBase64;
    private String socialUrl;
    private String emailAddress;
    private Boolean ccd;
    private String businessLogoBase64;
    private String businessCompanyName;
    private String businessWebsite;
    private String businessPhone;
    private String businessEmail;
    private String phone;
    private String customerState; //lead/ prospect  entityState previously
    private String customerType; // individual / commercial
    private List<CustomerSubscriptionDTO> customerSubscriptions;
    private Set<AddressDTO> addresses;
    private List<PhysicalLocationDTO> physicalLocations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isAttachment;
    private Integer privLevel;
    private List<CaUtilityDTO> caUtility;
    private CaReferralInfoDTO caReferralInfo;
    private CaSoftCreditCheckDTO caSoftCreditCheck;
    private String profileUrl;
    private  String entityType; //customer / employee
    private  Long entityId;
    List<UserLevelPrivilegeDTO> UserLevelPrivilegeDTOList;
    private String agentName;
    private String agentStatus;
    private String isChecked;
    private String countryCode;
    private String creationDate;
    private String leadSource;
    private String zipCode;
    private String region;
    private Date generatedAt;
    private String referralId;
    private String companyLogo;
    private String companyCode;
    private String companyName;
    private String loginUrl;
    private String isSubmitted;
    private String confirmPassword;
    private String landingPageUrl;
    private String designation;
    private Long organizationId;

    private List<Object> signingReqTrackerTiles;
    private String caseStatus; // closed, deffered, resolved
    private String legalBusinessName;
    private String defaultLandingPageUrl;
    private String entityProfileUri;
    private Long entityDetailId;


}
