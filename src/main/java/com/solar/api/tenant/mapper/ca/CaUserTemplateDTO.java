package com.solar.api.tenant.mapper.ca;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.workOrder.MongoCustomerDetailWoDTO;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaUserTemplateDTO {
    private Long entityId;

    private Long accountId;

    private String firstName;

    private String lastName;

    private String emailAddress;

    private String generatedAt;

    private String registeredDate;

    private String state;

    private String place;

    private String subscriptionTotal;

    private String customerType;

    private String status;
    private String stateCustomer;

    private String phone;

    private String zipCode;

    private String region;

    private Long physicalLocId;

    private String profileUrl;

    private String signedDocument;

    private String isChecked;

    private String subId;
    private String variantId;

    private List<MongoCustomerDetailWoDTO> mongoCustomerDetailWoDTO;

    private String residential;
    private String individual;
    private String commercial;
    private String nonProfit;

    private String agentName;
    private String agentStatus;
    private Long referralId;
    private String agentDesignation;

    private String project;
    private String mongoProjectId;
    private String agentImage;
    private String updatedAt;
    private Long correspondencCount;
    private Boolean selfInitiative;
    private String leadSource;
//    private Boolean selfInitiative;
//    private String mongoProjectId;
    private String hasPassword;
    private  Boolean hasLogin;
    private Boolean mobileAllowed;
    private Long utilityInfoCount;
    private Long supportTicketCount;
    private Long paymentInfoCount;
    private Long contractCount;
    private Long addressCount;
    private Date dateOfBirth;
    private String refId;
    private Boolean isSoftCreditCheckIconVisibility;


    public CaUserTemplateDTO(CaUserTemplateDTO caUserTemplateDTO) {
        this(caUserTemplateDTO.entityId, caUserTemplateDTO.accountId, caUserTemplateDTO.firstName, caUserTemplateDTO.lastName, caUserTemplateDTO.emailAddress
                , caUserTemplateDTO.generatedAt, caUserTemplateDTO.registeredDate, caUserTemplateDTO.state, caUserTemplateDTO.place, caUserTemplateDTO.subscriptionTotal
                , caUserTemplateDTO.customerType, caUserTemplateDTO.status, caUserTemplateDTO.stateCustomer, caUserTemplateDTO.phone, caUserTemplateDTO.zipCode
                , caUserTemplateDTO.region, caUserTemplateDTO.physicalLocId, caUserTemplateDTO.profileUrl, caUserTemplateDTO.signedDocument, caUserTemplateDTO.isChecked
                , caUserTemplateDTO.subId, caUserTemplateDTO.variantId, caUserTemplateDTO.mongoCustomerDetailWoDTO, caUserTemplateDTO.residential, caUserTemplateDTO.individual, caUserTemplateDTO.commercial
                , caUserTemplateDTO.nonProfit, caUserTemplateDTO.agentName, caUserTemplateDTO.agentStatus, caUserTemplateDTO.referralId, caUserTemplateDTO.agentDesignation, caUserTemplateDTO.project, caUserTemplateDTO.mongoProjectId,
                caUserTemplateDTO.agentImage, caUserTemplateDTO.updatedAt, caUserTemplateDTO.correspondencCount, caUserTemplateDTO.selfInitiative, caUserTemplateDTO.leadSource,
                caUserTemplateDTO.hasPassword, caUserTemplateDTO.hasLogin, caUserTemplateDTO.mobileAllowed, caUserTemplateDTO. utilityInfoCount, caUserTemplateDTO.supportTicketCount,
                caUserTemplateDTO.paymentInfoCount, caUserTemplateDTO. contractCount, caUserTemplateDTO.addressCount,caUserTemplateDTO.dateOfBirth, caUserTemplateDTO.refId, caUserTemplateDTO.isSoftCreditCheckIconVisibility
        );
    }

    public CaUserTemplateDTO(Long entityId, Long accountId, String firstName, String lastName, String emailAddress,
                             String generatedAt, String customerType, String status, String phone, String zipCode, String region,
                             Long physicalLocId, String profileUrl, String signedDocument, String isChecked,
                             String mongoProjectId,String updatedAt, Long correspondencCount, Boolean selfInitiative) {
        this.entityId = entityId;
        this.accountId = accountId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.generatedAt = generatedAt;
        this.customerType = customerType;
        this.status = status;
        this.phone = phone;
        this.zipCode = zipCode;
        this.region = region;
        this.physicalLocId = physicalLocId;
        this.profileUrl = profileUrl;
        this.signedDocument = signedDocument;
        this.isChecked = isChecked;
        this.mongoProjectId = mongoProjectId;
        this.updatedAt=updatedAt;
        this.correspondencCount=correspondencCount;
        this.selfInitiative = selfInitiative == null? false : selfInitiative;

    }
    public CaUserTemplateDTO(Long entityId, Long accountId, String firstName, String lastName, String emailAddress,
                             String generatedAt, String customerType, String status, String phone, String zipCode, String region,
                             Long physicalLocId, String profileUrl, String isChecked,
                             String mongoProjectId, String updatedAt, Long correspondencCount, Boolean selfInitiative , String leadSource, Date dateOfBirth) {
        this.entityId = entityId;
        this.accountId = accountId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.generatedAt = generatedAt;
        this.customerType = customerType;
        this.status = status;
        this.phone = phone;
        this.zipCode = zipCode;
        this.region = region;
        this.physicalLocId = physicalLocId;
        this.profileUrl = profileUrl;
        this.isChecked = isChecked;
        this.mongoProjectId = mongoProjectId;
        this.updatedAt=updatedAt;
        this.correspondencCount = correspondencCount;
        this.selfInitiative = selfInitiative == null ? false : selfInitiative;
        this.leadSource = leadSource;
        this.dateOfBirth = dateOfBirth;

    }
    public CaUserTemplateDTO(Long entityId, Long accountId, String firstName, String lastName, String emailAddress,
                             String generatedAt, String customerType, String status, String phone, String zipCode, String region,
                             String profileUrl, String isChecked,
                             String mongoProjectId,Long correspondencCount) {
        this.entityId = entityId;
        this.accountId = accountId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.generatedAt = generatedAt;
        this.customerType = customerType;
        this.status = status;
        this.phone = phone;
        this.zipCode = zipCode;
        this.region = region;
        this.profileUrl = profileUrl;
        this.isChecked = isChecked;
        this.mongoProjectId = mongoProjectId;
        this.correspondencCount = correspondencCount;
    }

    public CaUserTemplateDTO(Long entityId, Long accountId, String firstName, String lastName, String emailAddress, String customerType, String states, String contactPersonPhone, String refId) {
        this.entityId = entityId;
        this.accountId = accountId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.customerType = customerType;
        this.status = states;
        this.phone = contactPersonPhone;
        this.refId = refId;
    }
    public CaUserTemplateDTO(Long entityId, Long accountId, String firstName, String lastName, String emailAddress,
                             String generatedAt, String customerType, String status, String phone, String zipCode, String region,
                             Long physicalLocId, String profileUrl, String isChecked,
                             String mongoProjectId, String updatedAt, Long correspondencCount, Boolean selfInitiative , String leadSource) {
        this.entityId = entityId;
        this.accountId = accountId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.generatedAt = generatedAt;
        this.customerType = customerType;
        this.status = status;
        this.phone = phone;
        this.zipCode = zipCode;
        this.region = region;
        this.physicalLocId = physicalLocId;
        this.profileUrl = profileUrl;
        this.isChecked = isChecked;
        this.mongoProjectId = mongoProjectId;
        this.updatedAt=updatedAt;
        this.correspondencCount = correspondencCount;
        this.selfInitiative = selfInitiative == null ? false : selfInitiative;
        this.leadSource = leadSource;

    }
}
