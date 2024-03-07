package com.solar.api.tenant.model.billingCredits;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "billing_credits_temp_stage", indexes = {
        @Index(columnList = "creditCodeVal", name = "creditCodeVal_index"),
        @Index(columnList = "gardenId", name = "gardenId_index"),
        @Index(columnList = "calendarMonth", name = "calendarMonth_index")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingCreditsTempStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String importType;
    private Long jobId;
    @Column(length = 50)
    private String creditCodeType;
    @Column(length = 50)
    private String gardenId;
    private Double mpa;
    private Double tariffRate;
    private String creditCodeVal;
    private Double creditValue;
    private String creditForDate;
    private String subscriptionCode;
    private Integer lineSeqNo;
    private String calendarMonth;
    private Boolean imported;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
