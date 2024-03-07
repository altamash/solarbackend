package com.solar.api.saas.service.process.upload.v2.customer.mapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerV2 {

    // Common columns for Individual and Commercial
    @JsonProperty("First Name")
    private String firstName;       // mongo + mysql (user, entity (concat))
    @JsonProperty("Last Name")
    private String lastName;        // mongo + mysql (user, entity (concat))
    @JsonProperty("Phone Number")
    private String phoneNumber;     // mongo + mysql (customer_detail)
    @JsonProperty("Email")
    private String email;           // mongo + mysql (user, entity)
    @JsonProperty("Utility Provider")
    private String utilityProvider; // mongo
    @JsonProperty("Location Name")
    private String locationName;    // mysql (physical_location, location_mapping)
    @JsonProperty("Organization")
    private String organization;    // mysql (physical_location)
    @JsonProperty("Location Type")
    private String locationType;    // mysql (physical_location)
    @JsonProperty("Address 1")
    private String address1;        // mysql (physical_location)
    @JsonProperty("Address 2")
    private String address2;        // mysql (physical_location)
    @JsonProperty("City")
    private String city;            // mysql (physical_location)
    @JsonProperty("State")
    private String state;           // mysql (physical_location)
    @JsonProperty("Country")
    private String country;         // mysql (physical_location)
    @JsonProperty("Zip Code")
    private String zipCode;         // mysql (physical_location)
    @JsonProperty("Account Holder Name")
    private String accountHolderName;   // mysql (ca_utility)
    @JsonProperty("Premise Number")
    private String premiseNumber;       // mysql (ca_utility)
    @JsonProperty("Reference Number / ID")
    private String referenceNumberId;   // mysql (ca_utility)
    @JsonProperty("Average Monthly Bill")
    private Long averageMonthlyBill;  // mysql (ca_utility)
    @JsonProperty("Payment Method")
    private String paymentMethod;       // mysql (payment_info)
    @JsonProperty("Account Title")
    private String accountTitle;        // mysql (payment_info)
    @JsonProperty("Bank")
    private String bank;                // mysql (payment_info)
    @JsonProperty("Account Type")
    private String accountType;         // mysql (payment_info)
    @JsonProperty("Soft Credit Check")
    private String softCreditCheck;     // boolean
    @JsonProperty("Soft Credit Reference")
    private String softCreditReference;           // mysql (soft_credit_check) (mandatory on softCreditCheck true)
    @JsonProperty("Soft Credit Source")
    private String softCreditSource;              // mysql (soft_credit_check) (mandatory on softCreditCheck true)
    @JsonProperty("How did you hear about us?")
    private String howDidYouHearAboutUs;          // mysql (ca_referral_info)
    @JsonProperty("Promo Code")
    private String promoCode;                     // mysql (ca_referral_info)
    @JsonProperty("User Type")
    private String userType;                        // mysql (ca_referral)
    // Additional columns for Commercial
    @JsonProperty("Legal Business Name")
    private String legalBusinessName;           // mongo
    @JsonProperty("Contact Person Designation")
    private String contactPersonDesignation;    // mongo
    @JsonProperty("Designation")
    private String designation;                 // mongo
    @JsonProperty("Upload Type")
    private String uploadType; //lead, customer, prospect
    @JsonProperty("Customer Type")
    private String customerType; //individual, commercial
//    @Override
//    public String toString() {
//        return "CustomerV2{" +
//                ", firstName='" + firstName + '\'' +
//                ", lastName='" + lastName + '\'' +
//                ", phoneNumber='" + phoneNumber + '\'' +
//                ", email='" + email + '\'' +
//                ", utilityProvider='" + utilityProvider + '\'' +
//                ", locationName='" + locationName + '\'' +
//                ", organization='" + organization + '\'' +
//                ", locationType='" + locationType + '\'' +
//                ", address1='" + address1 + '\'' +
//                ", address2='" + address2 + '\'' +
//                ", city='" + city + '\'' +
//                ", state='" + state + '\'' +
//                ", country='" + country + '\'' +
//                ", zipCode='" + zipCode + '\'' +
//                ", accountHolderName='" + accountHolderName + '\'' +
//                ", utilityProvide='" + utilityProvider + '\'' +
//                ", premiseNumber='" + premiseNumber + '\'' +
//                ", referenceNumberId='" + referenceNumberId + '\'' +
//                ", averageMonthlyBill='" + averageMonthlyBill + '\'' +
//                ", paymentMethod='" + paymentMethod + '\'' +
//                ", accountTitle='" + accountTitle + '\'' +
//                ", bank='" + bank + '\'' +
//                ", accountType='" + accountType + '\'' +
//                ", softCreditCheck='" + softCreditCheck + '\'' +
//                ", softCreditReference='" + softCreditReference + '\'' +
//                ", softCreditSource='" + softCreditSource + '\'' +
//                ", howDidYouHearAboutUs='" + howDidYouHearAboutUs + '\'' +
//                ", promoCode='" + promoCode + '\'' +
//                ", legalBusinessName='" + legalBusinessName + '\'' +
//                ", contactPersonDesignation='" + contactPersonDesignation + '\'' +
//                ", designation='" + designation + '\'' +
//                ", uploadType='" + uploadType + '\'' +
//                ", customerType='" + customerType + '\'' +
//
//                '}';
//    }
}
