package com.solar.api.tenant.service.process.billing.postBillilng;

import com.solar.api.saas.model.process.rule.RuleHead;
import com.solar.api.saas.service.process.calculation.RateCodeParser;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.model.process.rule.RuleExecutionLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class PostBillingCalculation extends RateCodeParser {

    protected List<RuleExecutionLog> executionLogs = new ArrayList<>();

    abstract public void calculate(String subscriptionCode, String billingMonth,
                                   Long ruleHeadId, String type, JobManagerTenant jobManagerTenant);

    abstract public void calculate(String subscriptionCode, List<Long> rateMatrixHeadIds, String billingMonth,
                                   Long ruleHeadId, String type, JobManagerTenant jobManagerTenant);

    protected RuleExecutionLog getRuleExecutionLog(Long billId, Long jobId, RuleHead ruleExecuted, String rateCode,
                                                   Double returnedValue, String subscriptionMatrixRef) {
        return RuleExecutionLog.builder()
                .billId(billId)
                .jobId(jobId)
//                .ruleExecuted(ruleExecuted)
                .rateCode(rateCode)
                .returnedValue(returnedValue)
                .subscriptionMatrixRef(subscriptionMatrixRef)
                .jobExecutionDatetime(new Date())
                .build();
    }

}
