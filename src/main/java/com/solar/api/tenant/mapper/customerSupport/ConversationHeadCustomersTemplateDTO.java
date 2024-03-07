package com.solar.api.tenant.mapper.customerSupport;

public interface ConversationHeadCustomersTemplateDTO {

    String getSourceId();
    String getHeadIds();
    Long getTicketCount();
    String getSourceType();
    String getStatus();
    String getRequesterName();

    String getRequesterImgUri();

    String getRequesterEmail();

    String getRequesterPhone();

    String getRequesterType();

}
