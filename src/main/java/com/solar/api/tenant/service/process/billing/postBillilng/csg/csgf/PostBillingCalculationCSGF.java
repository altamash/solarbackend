package com.solar.api.tenant.service.process.billing.postBillilng.csg.csgf;

import com.solar.api.saas.model.process.rule.RuleHead;
import com.solar.api.saas.repository.RuleHeadRepository;
import com.solar.api.saas.service.job.EJobStatus;
import com.solar.api.tenant.model.billing.billingHead.BillingDetail;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixDetail;
import com.solar.api.tenant.service.BillingDetailService;
import com.solar.api.tenant.service.BillingHeadService;
import com.solar.api.tenant.service.SubscriptionService;
import com.solar.api.tenant.service.job.JobManagerTenantService;
import com.solar.api.tenant.service.process.billing.postBillilng.PostBillingCalculation;
import com.solar.api.tenant.service.process.rule.RuleExecutionLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PostBillingCalculationCSGF extends PostBillingCalculation {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final static String POST_CALCULATION_CODE = "PICAL";

    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private BillingHeadService billingHeadService;
    @Autowired
    private BillingDetailService billingDetailService;
    @Autowired
    private RuleExecutionLogService ruleExecutionLogService;
    @Autowired
    private JobManagerTenantService jobManagerTenantService;
    @Autowired
    private RuleHeadRepository ruleHeadRepository;

    @Override
    public void calculate(String subscriptionCode, String billingMonth, Long ruleHeadId, String type,
                          JobManagerTenant jobManagerTenant) {
        List<CustomerSubscription> activeSubscriptions =
                subscriptionService.findBySubscriptionStatusAndSubscriptionType("ACTIVE", subscriptionCode);
        activeSubscriptions.forEach(subscription -> {
            calculate(subscription, billingMonth, ruleHeadId, jobManagerTenant.getId());
        });
        jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), null, LOGGER);
    }

    // ABCRE,MPA,ABSAV
    @Override
    public void calculate(String subscriptionCode, List<Long> rateMatrixHeadIds, String billingMonth, Long ruleHeadId
            , String type, JobManagerTenant jobManagerTenant) {
        subscriptionService.findActiveBySubscriptionRateMatrixIds(rateMatrixHeadIds).forEach(subscription -> {
            calculate(subscription, billingMonth, ruleHeadId, jobManagerTenant.getId());
        });
        jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), null, LOGGER);
    }

    private void calculate(CustomerSubscription subscription, String billingMonth, Long ruleHeadId, Long jobId) {
        BillingHead billingHead = billingHeadService.findBySubscriptionIdAndBillingMonthYear(subscription.getId(),
                billingMonth);
        if (billingHead == null) {
            return;
        }
        String subscriptionCode = subscription.getSubscriptionType();
        Long rateMatrixHeadId = subscription.getSubscriptionRateMatrixId();
        LOGGER.info("\tInitiated post billing calculation for subscription code " + subscriptionCode + ", rate matrix" +
                " head id " + rateMatrixHeadId);
        try {
            Map<String, Object> valuesHashMap = generateHashMap(subscription, rateMatrixHeadId, billingHead, jobId);
            SubscriptionRateMatrixDetail detail =
                    subscriptionService.findBySubscriptionRateMatrixIdAndRateCode(rateMatrixHeadId,
                            POST_CALCULATION_CODE);
            String postBillingCodes[] = detail.getDefaultValue().split(",");
            List<BillingDetail> billingDetailsToSave = new ArrayList<>();
            AtomicInteger lineSequence =
                    new AtomicInteger(billingDetailService.findByBillingHeadId(billingHead.getId()).size());
            Long billId = billingHead.getId();
            Long subscriptionId = billingHead.getSubscriptionId();
            String subscriptionMatrixRef = subscriptionId + rateMatrixHeadId.toString();
            RuleHead ruleHead = ruleHeadRepository.findById(ruleHeadId).get();
            for (String code : Arrays.asList(postBillingCodes)) {
                BillingDetail billingDetail = addLineSequence(
                        valuesHashMap,
                        rateMatrixHeadId,
                        ruleHeadId,
                        jobId,
                        billingHead,
                        code,
                        lineSequence.incrementAndGet());
                if (billingDetail != null) {
                    billingDetailsToSave.add(billingDetail);
                    executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, code, billingDetail.getValue(),
                            subscriptionMatrixRef));
                } else {
                    LOGGER.error("{} not found for subscription {}", code, subscription.getId());
                }
            }
            billingDetailService.saveAll(billingDetailsToSave);
            ruleExecutionLogService.save(executionLogs);
        } catch (Exception e) {
            LOGGER.error("Subscription id: {}, exception: {}", subscription.getId(), e.getMessage());
        }
        LOGGER.info("\tPost billing calculation completed for subscription code " + subscriptionCode + ", rate matrix" +
                " head id " + rateMatrixHeadId);
    }

    private BillingDetail addLineSequence(Map<String, Object> valuesHashMap, Long rateMatrixHeadId, Long ruleHeadId,
                                          Long jobId,
                                          BillingHead billingHead, String rateCode, int lineSequence) {
        switch (rateCode) {
            case "ABCRE":
                return getAbcreOrMpa(valuesHashMap, billingHead, "ABCRE");
            case "MPA":
                return getAbcreOrMpa(valuesHashMap, billingHead, "MPA");
            case "ABSAV":
                return getABSAV(valuesHashMap, rateMatrixHeadId, ruleHeadId, jobId, billingHead, rateCode,
                        lineSequence);
        }
        return null;
    }

    private BillingDetail getABSAV(Map<String, Object> valuesHashMap, Long rateMatrixHeadId, Long ruleHeadId,
                                   Long jobId,
                                   BillingHead billingHead, String rateCode, int lineSequence) {
        Long billId = billingHead.getId();
        Long subscriptionId = billingHead.getSubscriptionId();
        String subscriptionMatrixRef = subscriptionId + rateMatrixHeadId.toString();
        RuleHead ruleHead = ruleHeadRepository.findById(ruleHeadId).get();
        Double KWDC = Double.valueOf((String) valuesHashMap.get("KWDC"));
        Double YLD = Double.valueOf((String) valuesHashMap.get("YLD"));
        Double DEP = Double.valueOf((String) valuesHashMap.get("DEP")); // not found
        Double DEPM = 1 - DEP;
        Integer OPYR = (Integer) valuesHashMap.get("OPYR");
        Double SRTE = (Double) valuesHashMap.get("SRTE");
        Double DSCM = (Double) valuesHashMap.get("DSCM");
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "KWDC", KWDC, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "YLD", YLD, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "DEP", DEP, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "DEPM", DEPM, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "OPYR", (double) OPYR, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "SRTE", SRTE, subscriptionMatrixRef));
        executionLogs.add(getRuleExecutionLog(billId, jobId, ruleHead, "DSCM", DSCM, subscriptionMatrixRef));
        LOGGER.info("\t\tCalculations completed and saved to execution logs");
        Double mbillValue = (KWDC * YLD * Math.pow(DEPM, (OPYR - 1)) * (SRTE * DSCM)) / 12;
        BillingDetail abcreDetails = getAbcreOrMpa(valuesHashMap, billingHead, "ABCRE");
        Double abcre = abcreDetails == null ? 0d : abcreDetails.getValue();
        return getBillingDetail(billingHead,
                null,
                abcre - mbillValue,
                lineSequence,
                "ABSAV",
                false);
    }
}
