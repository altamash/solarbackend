package com.solar.api.tenant.model.process.rule;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "rule_execution_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleExecutionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long billId; // Session unique to subscriptionBillingExecution
    private Long jobId;
    private String rateCode;
    private Double returnedValue;
    private String subscriptionMatrixRef;
    private Date jobExecutionDatetime;
    private Boolean exception;
    private String exceptionLog;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
