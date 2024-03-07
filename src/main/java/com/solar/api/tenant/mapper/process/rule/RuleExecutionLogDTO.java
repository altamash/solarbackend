package com.solar.api.tenant.mapper.process.rule;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.saas.model.process.rule.RuleHead;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RuleExecutionLogDTO {

    private Long id;
    private Long billId; // Session unique to subscriptionBillingExecution
    private Long jobId;
    private RuleHead ruleExecuted; // FK: RuleHead
    private String rateCode;
    private Double returnedValue;
    private String subscriptionMatrixRef; // Concatenate(subscriptionID-subscriptionRateMatrixID)
    private Date jobExecutionDatetime;
    private Boolean exception;
    private String exceptionLog;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
