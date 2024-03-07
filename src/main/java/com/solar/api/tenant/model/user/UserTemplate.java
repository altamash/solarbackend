package com.solar.api.tenant.model.user;

import java.util.Date;

public interface UserTemplate {
    Long getAccountId();

    String getFirstName();

    String getLastName();

    String getEmailAddress();

    Date getGeneratedAt();

    Date getRegisteredDate();

    String getState();

    String getPlace();

    String getSubscriptionTotal();

    String getCustomerType();

    String getStatus();

    String getStateCustomer();

    String getPhone();

    String getZipCode();

    String getRegion();

    Long getPhysicalLocId();

    Long getEntityId();

    String getSignedDocument();

    String getIsChecked();

    String getSubscriptionId();

    String getVarientId();

    String getProfileUrl();

    String getResidential();

    String getIndividual();

    String getCommercial();

    String getNonProfit();

    String getAgentName();

    String getAgentStatus();

    Long getReferralId();

    Boolean getSelfInitiative();

    String getMongoProjectId();

    String getHasPassword();

    Boolean getHasLogin();

    Boolean getMobileAllowed();

    Long getUtilityInfoCount();

    Long getSupportTicketCount();

    Long getPaymentInfoCount();

    Long getContractCount();

    Long getAddressCount();

    Long getIsLeaf();

    String getGroupBy();

    String getAgentImage();

    String getCreatedAt();
}
