package com.solar.api.tenant.model.report;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "true_up")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrueUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long acctId;
    private Long subscriptionId;
    private Long subscriptionRateMatrixId;
    private Date startDate;
    private Date endDate;
    private String period;
    private String subscriptionType;
    private String reportUrl;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
