package com.solar.api.tenant.mapper.customerSupport;

public interface ConversationHeadTemplateWoDTO {
    Long getId();
    String getSummary();
    String getMessage();
    String getCategory();
    String getSubCategory();
    String getPriority();
    String getStatus();
    String getSubscriptionId();
    Long getCustomerId();
    String getCustomerName();
    String getSourceId();
    String getCreatedBy();
    Boolean getInternal();
    String getCreatedByUri();
    String getCustomerUri();
    String getCustomerType();
    String getCustomerEmail();
    String getCustomerContact();
    String getCreatedByType();
    String getCreatedByEmail();
    String getCreatedByContact();
    String getVariantId();

}
