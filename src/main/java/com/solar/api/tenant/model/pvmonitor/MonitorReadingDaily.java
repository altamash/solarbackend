package com.solar.api.tenant.model.pvmonitor;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "monitor_reading_daily")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitorReadingDaily {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Temporal(TemporalType.DATE)
    private Date day;
//    private String variantId; // (variant ID)

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Transient
    private String action;

}
