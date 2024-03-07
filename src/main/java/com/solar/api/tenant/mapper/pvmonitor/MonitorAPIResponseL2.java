package com.solar.api.tenant.mapper.pvmonitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.helper.Utility;
import lombok.*;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonitorAPIResponseL2 {

    private String inverterSn;

    // Solax
    private Double powerdc1;
    private Double powerdc2;
    private Double powerdc3;
    private Double powerdc4;
    private Double pac1;
    private Double pac2;
    private Double pac3;
    private Double pvPower;
    private Double gridpower;
    private Double feedinpower;
    private String hour;
    private Integer minute;
    private String fiveMinuteVal;
    private String uploadTimeValue;
    private Double consumeEnergyMeter2;
    private Double feedinPowerMeter2;
    private Double relayPower;
    private Double batPower1;
    private Double batteryCapacity;
    private String pileSn;
    private Double totalChargePower;
    private Double epspower;
    private Double meter2ComState;
    private Double acpower;
    private Double yieldtoday;
    private Double yieldtotal;

    //Solis
    private String time;
    private Double pac;
    private Double eToday;
    private Double eMonth;
    private Double eYear;
    private Double eTotal;
    private Double powerFactor;
    private Double apparentPower;

    // MonitorReading mapping fields
    private String subscriptionIdMongo;
    private String site;
    private String inverterNumber;
    private Long userId;
    private Long subscriptionId;
    private Double sytemSize;
    private Double currentValue;
    private Double currentValueToday;
    private Double currentValueRunning;
    private Double yieldValue;
    private Double yieldValueRunning;
    private Double peakValue;
    private Double dailyYield;
    private Double monthlyYield;
    private Double annualYield;
    private Double grossYield;
    private Date dateTime;
}
