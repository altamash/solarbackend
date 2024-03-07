package com.solar.api.tenant.mapper.ca;

import com.solar.api.tenant.model.user.UserTemplate;

public class CaUserTemplateDTOMapper {
    public static CaUserTemplateDTO caUserTemplateDTO(UserTemplate userTemplate) {
        if (userTemplate == null) {
            return null;
        }
        return CaUserTemplateDTO.builder()
                .accountId(userTemplate.getAccountId())
                .entityId(userTemplate.getEntityId())
                .customerType(userTemplate.getCustomerType())
                .emailAddress(userTemplate.getEmailAddress())
                .firstName(userTemplate.getFirstName())
                .lastName(userTemplate.getLastName())
                .status(userTemplate.getStatus())
                .stateCustomer(userTemplate.getStateCustomer())
                .zipCode(userTemplate.getZipCode())
                .profileUrl(userTemplate.getProfileUrl())
                .region(userTemplate.getRegion())
                .phone(userTemplate.getPhone())
                .physicalLocId(userTemplate.getPhysicalLocId())
                .signedDocument(userTemplate.getSignedDocument())
                .registeredDate(userTemplate.getRegisteredDate() != null ? userTemplate.getRegisteredDate().toString() : "")
                .state(userTemplate.getState())
                .place(userTemplate.getPlace())
                .subscriptionTotal(userTemplate.getSubscriptionTotal() != null ? userTemplate.getSubscriptionTotal() : "0")
                .isChecked(userTemplate.getIsChecked())
                .referralId(userTemplate.getReferralId())
                .selfInitiative((userTemplate.getSelfInitiative()))
                .mongoProjectId(userTemplate.getMongoProjectId())
                .hasPassword(userTemplate.getHasPassword())
                .hasLogin(userTemplate.getHasLogin())
                .mobileAllowed(userTemplate.getMobileAllowed())
                .utilityInfoCount(userTemplate.getUtilityInfoCount())
                .supportTicketCount(userTemplate.getSupportTicketCount())
                .paymentInfoCount(userTemplate.getPaymentInfoCount())
                .contractCount(userTemplate.getContractCount())
                .addressCount(userTemplate.getAddressCount())
                .build();
    }

}
