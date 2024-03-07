package com.solar.api.saas.service.process.migration.parser.vista.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Customer {

    private String externalId;
    private String compKey;
    private String firstName;
    private String lastName;
    private String user;
    private String email;
    private String password;
    private String IdCode;       //Authority ID
    private String authorityId;  //e.g. SSN, Driving License
    private String gender;
    private String dataOfBirth;
    private String registerDate;
    private String activeDate;
    private String status;
    private String prospectStatus;
    private String referralEmail;
    private String deferredContactDate;
    private String language;
    private String authentication;
    private String userType;
    private String roles; // csv
    private String category;
    private String groupId;
    private String photo;
    private String socialUrl;

    @Override
    public String toString() {
        return "Customer{" +
                "externalId='" + externalId + '\'' +
                ", compKey='" + compKey + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", user='" + user + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", IdCode='" + IdCode + '\'' +
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
                '}';
    }
}
