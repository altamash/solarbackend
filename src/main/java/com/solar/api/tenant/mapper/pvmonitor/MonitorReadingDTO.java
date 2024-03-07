package com.solar.api.tenant.mapper.pvmonitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonitorReadingDTO {
    private Long id;
    private Long userId;
    private Long subscriptionId;
    private String subscriptionIdMongo;
    private String site;
    private String inverterNumber;
    private Double sytemSize;
    private Double currentValueToday;
    private Double currentValue;
    private Double currentValueRunning;
    private Double yieldValue;
    private Double yieldValueRunning;
    private Double peakValue;
    private Double dailyYield;
    private Double monthlyYield;
    private Double annualYield;
    private Double grossYield;
    private Double rawYield;
    private Date time;
    private String durl;
    private String logs;
    private boolean isLastRecord = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return new com.google.gson.Gson().toJson(this);
    }
}
