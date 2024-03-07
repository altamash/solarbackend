package com.solar.api.tenant.model.pvmonitor;

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
public class MonitorReadingDailyDTO {

    private Long id;
    private Long userId;
    private Long subscriptionId;
    private String subscriptionIdMongo;
    private String site;
    private String inverterNumber;
    private Double currentValue;
    private Double currentValueRunning;
    private Double peakValue;
    private Double yieldValue;
    private Double yieldValueRunning;
    private Date day;
    private LocalDateTime createdAt;
}
