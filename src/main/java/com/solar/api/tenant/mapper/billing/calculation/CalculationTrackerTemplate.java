package com.solar.api.tenant.mapper.billing.calculation;


import java.util.Date;


public interface CalculationTrackerTemplate {

    Long getId();

    String getPeriod();

    Double getAmount();

    String getStatus();

    Date getDueDate();
    String getError();
    Long getAccountId();
    String getCustomerProdId();
    Integer getReAttemptCount();
    String getCustomerName();

    String getCustomerType();

    String getCustomerEmail();

    String getCustomerPhone();

    String getProfileUrl();

    Boolean getBillSkip();


}
