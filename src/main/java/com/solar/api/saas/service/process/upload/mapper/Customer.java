package com.solar.api.saas.service.process.upload.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Customer {

//    private String externalId;
    private String rowNumber;
    @JsonProperty("acct_id")
    private Long acctId;
    private String action;
    @JsonProperty("company_id")
    private String companyId;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("user_name")
    private String userName;
    private String email;
    private String password;
    @JsonProperty("id_code")
    private String idCode;       //Authority ID
    @JsonProperty("authority_id")
    private String authorityId;  //e.g. SSN, Driving License
    private String gender;
    @JsonProperty("data_of_birth")
    private String dataOfBirth;
    @JsonProperty("register_date")
    private String registerDate;
    @JsonProperty("active_date")
    private String activeDate;
    private String status;
    @JsonProperty("prospect_status")
    private String prospectStatus;
    @JsonProperty("referral_email")
    private String referralEmail;
    @JsonProperty("deferred_contact_date")
    private String deferredContactDate;
    private String language;
    private String authentication;
    @JsonProperty("user_type")
    private String userType;
    private String roles; // csv
    private String category;
    @JsonProperty("group_id")
    private String groupId;
    private String photo;
    @JsonProperty("social_url")
    private String socialUrl;
    private String ccd;
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("lead_type")
    private  String leadType;
    @JsonProperty("business_company_name")
    private String businessCompanyName;
    @JsonProperty("business_website")
    private String businessWebsite;
    @JsonProperty("business_phone")
    private String businessPhone;
    @JsonProperty("business_email")
    private String businessEmail;
    @JsonProperty("country_code")
    private String countryCode;
    @JsonProperty("creation_date")
    private String creationDate;
    @JsonProperty("lead_source")
    private String leadSource;



    @Override
    public String toString() {
        return "Customer{" +
                "rowNumber='" + rowNumber + '\'' +
                ", acctId=" + acctId +
                ", action='" + action + '\'' +
                ", companyId='" + companyId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", idCode='" + idCode + '\'' +
                ", authorityId='" + authorityId + '\'' +
                ", gender='" + gender + '\'' +
                ", dataOfBirth='" + dataOfBirth + '\'' +
                ", registerDate='" + registerDate + '\'' +
                ", activeDate='" + activeDate + '\'' +
                ", status='" + status + '\'' +
                ", prospectStatus='" + prospectStatus + '\'' +
                ", referralEmail='" + referralEmail + '\'' +
                ", deferredContactDate='" + deferredContactDate + '\'' +
                ", language='" + language + '\'' +
                ", authentication='" + authentication + '\'' +
                ", userType='" + userType + '\'' +
                ", roles='" + roles + '\'' +
                ", category='" + category + '\'' +
                ", groupId='" + groupId + '\'' +
                ", photo='" + photo + '\'' +
                ", socialUrl='" + socialUrl + '\'' +
                ", ccd='" + ccd + '\'' +
                ", phoneNumber="+ phoneNumber+ '\'' +
                ", leadType="+ leadType+ '\'' +
                ", businessCompanyName="+ businessCompanyName+ '\'' +
                ", businessWebsite="+ businessWebsite+ '\'' +
                ", businessPhone="+ businessPhone+ '\'' +
                ", businessEmail="+ businessEmail+ '\'' +
                ", countryCode="+ countryCode+ '\'' +
                ", creationDate="+ creationDate+ '\'' +
                ", leadSource="+ leadSource+ '\'' +
                '}';
    }
}
