package com.solar.api.tenant.model.pvmonitor;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "monitor_reading")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitorReading {
    //  For widget
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;
    private String durl;
    @Column(length = 1000)
    private String logs;
    @Transient
    private boolean isLastRecord = true;

    @Override
    public String toString() {
        return new com.google.gson.Gson().toJson(this);
    }

    @CreationTimestamp
    private LocalDateTime createdAt;
}
