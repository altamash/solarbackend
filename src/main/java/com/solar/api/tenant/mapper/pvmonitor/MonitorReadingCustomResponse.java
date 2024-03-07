package com.solar.api.tenant.mapper.pvmonitor;

public interface MonitorReadingCustomResponse {
//     Long getUserId();

     String getCustomerName();

     Double getSytemSize();

     Double getCurrentValue();

     Double getPeakValue();

     Double getDailyYield();

     Double getMonthlyYield();

     Double getAnnualYield();

     Double getGrossYield();

     String getSubscriptionIds();

}
