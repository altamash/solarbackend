package com.solar.api.tenant.model.subscription;

import java.util.Date;

public interface SubscriptionTerminationTemplate {

    Long getId();

    String getSubscriptionStatus();

    Date getEndDate();

    Date getStartDate();

    Date getUpdateDate();

    Date getClosedDate();

    Date getTerminationDate();

    Long getAccountId();

    Date getNotifyDate();

    String getValue();

    Date getAutoDate();

}
