package com.solar.api.tenant.mapper.subscription.customerSubscription;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerSubscriptionMaintenanceDTO {

    private Long id;
    private Date lastMaintenanceDate;
    private Long maintenanceIntervalDays;
    private Long daysRemaining;
    private Date nextMaintenanceDate;


    @Override
    public String toString() {
        return "MaintenanceDTO{" +
                  "subscriptionId :" + id +
                ", lastMaintenanceDate :" + lastMaintenanceDate +
                ", maintenanceIntervalDays :" + maintenanceIntervalDays +
                ", daysRemaining :" + daysRemaining +
                ", nextMaintenanceDate :" + nextMaintenanceDate +
                '}';
    }
}
