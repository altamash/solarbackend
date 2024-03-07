package com.solar.api.tenant.mapper.billing.calculation;

public interface CalTrackerGraphTemplate {

     String getPeriod();
     String getBillStatus();
     Double getAmount();
     Integer getStatusCount();

}
