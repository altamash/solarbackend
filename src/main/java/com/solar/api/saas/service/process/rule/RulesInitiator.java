package com.solar.api.saas.service.process.rule;

import com.solar.api.saas.model.process.rule.RuleHead;
import com.solar.api.saas.service.integration.mongo.response.subscription.Subscription;
import com.solar.api.saas.service.integration.mongo.response.subscription.SubscriptionMapping;
import com.solar.api.saas.service.process.ExecuteParams;
import com.solar.api.saas.service.process.calculation.RateCodeParser;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.tansStage.TransStageHead;
import com.solar.api.tenant.model.billing.tansStage.TransStageTemp;
import com.solar.api.tenant.model.process.rule.RuleExecutionLog;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public abstract class RulesInitiator extends RateCodeParser {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public abstract Map<String, Object> generateHashMap(CustomerSubscription subscription,
                                                        Long subscriptionMatrixHeadId, BillingHead billingHead,
                                                        Long jobId);
    public abstract Map<String, Object> generateHashMap(SubscriptionMapping subscriptionMapping, BillingHead billingHead, Long jobId);
    public abstract Map<String, Object> generateHashMap(List<TransStageTemp> transSageTempList, BillingHead billingHead,String subscriptionId, Long jobId);
    public abstract void calculate(BillingHead billingHead, Long rateMatrixHeadId, Long ruleHeadId,
                                   Map<String, Object> valuesHashMap, Long jobId);
    public abstract void calculate(BillingHead billingHead, Subscription subscription, Long ruleHeadId,
                                   Map<String,Object> valuesHashMap, Long jobId);
    public abstract void calculate(BillingHead billingHead, CustomerSubscription subscription, List<TransStageTemp> transStageTempList, Long ruleHeadId,
                                   Map<String,Object> valuesHashMap, Long jobId);
    public void rollover() {
    }

    public void adjustment() {
    }

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

    protected RuleExecutionLog getRuleExecutionLog(Long billId, Long jobId, RuleHead ruleExecuted, String rateCode,
                                                   Double returnedValue) {
        return getRuleExecutionLog(billId, jobId, ruleExecuted, rateCode, returnedValue, null);
    }



    public void execute(ExecuteParams params, CustomerSubscription subscription, BillingHead billingHead, Long jobId) {
    }

    public void execute(ExecuteParams params, SubscriptionMapping subscriptionMapping, BillingHead billingHead, Long jobId) {
    }

    protected Double getDouble(Object value) {
        return value instanceof String ? Double.valueOf((String) value) : (Double) value;
    }

    protected Integer getInteger(Object value) {
        return value instanceof String ? Integer.valueOf((String) value) : (Integer) value;
    }

}
