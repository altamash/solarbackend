package com.solar.api.tenant.service.process.subscription;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.helper.Utility;
import com.solar.api.saas.model.process.rule.RuleHead;
import com.solar.api.saas.repository.RuleHeadRepository;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.saas.service.integration.mongo.response.subscription.SubscriptionMapping;
import com.solar.api.saas.service.job.EJobStatus;
import com.solar.api.saas.service.process.rule.RulesFactory;
import com.solar.api.saas.service.process.rule.RulesInitiator;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.tansStage.TransStageHead;
import com.solar.api.tenant.model.billing.tansStage.TransStageTemp;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHead;
import com.solar.api.tenant.repository.CustomerSubscriptionMappingRepository;
import com.solar.api.tenant.repository.CustomerSubscriptionRepository;
import com.solar.api.tenant.repository.JobManagerTenantRepository;
import com.solar.api.tenant.repository.SubscriptionRateMatrixHeadRepository;
import com.solar.api.tenant.service.CalculationTrackerService;
import com.solar.api.tenant.service.job.JobManagerTenantService;
import com.solar.api.tenant.service.process.billing.invoice.BillInvoiceService;
import com.solar.api.tenant.service.tansStage.TransStageHeadService;
import com.solar.api.tenant.service.tansStage.TransStageTempService;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SubscriptionBase {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    protected CustomerSubscriptionRepository subscriptionRepository;
    @Autowired
    protected CustomerSubscriptionMappingRepository subscriptionMapping;
    @Autowired
    protected SubscriptionRateMatrixHeadRepository headRepository;
    @Autowired
    private RuleHeadRepository ruleHeadRepository;
    @Autowired
    private JobManagerTenantService jobManagerTenantService;
    @Autowired
    private JobManagerTenantRepository repository;
    @Autowired
    private RulesFactory rulesFactory;
    @Autowired
    private DataExchange dataExchange;
    @Lazy
    @Autowired
    private BillInvoiceService billInvoiceService;
    @Autowired
    private CalculationTrackerService calculationTrackerService;
    @Autowired
    private TransStageHeadService transStageHeadService;
    @Autowired
    private TransStageTempService transStageTempService;

    protected void updateRolloverDate(LocalDate start, CustomerSubscriptionMapping rollDtMapping,
                                      Integer generateCycle) {
        rollDtMapping.setValue(Utility.formatLocalDate(start.plusMonths(generateCycle), Utility.SYSTEM_DATE_FORMAT));
        subscriptionMapping.save(rollDtMapping);
    }

    public void generateBills(List<BillingHead> billingHeads, Long jobId, Boolean isLegacy) {
        Long numberOfBills = (long) billingHeads.size();
        for (int i = 0; i < numberOfBills; i++) {
            generateBill(billingHeads.get(i), jobId, isLegacy);
        }
    }

    public void generateBill(BillingHead billingHead, Long jobId, Boolean isLegacy) {
        if (isLegacy) {
            Long subscriptionId = billingHead.getSubscriptionId();
            CustomerSubscription subscription = subscriptionRepository.findById(subscriptionId).orElse(null);
            LOGGER.info("Subscription Id: [" + billingHead.getSubscriptionId() + "]");
            LOGGER.info("BillingHead Id: [" + billingHead.getId() + "]");
            Long subscriptionRateMatrixId = subscription.getSubscriptionRateMatrixId();
            RuleHead ruleHead = ruleHeadRepository.findBySubscriptionCode(subscription.getSubscriptionType());
            RulesInitiator initiator = rulesFactory.getRulesInitiator(ruleHead.getMethod(), isLegacy);
            try {
                SubscriptionRateMatrixHead rateMatrixHead =
                        headRepository.findById(subscriptionRateMatrixId).orElse(null);
                Map<String, Object> valuesHashMap = initiator.generateHashMap(subscription, rateMatrixHead.getId(), // check usage of subs
                        billingHead, jobId);
                initiator.calculate(billingHead, rateMatrixHead.getId(), ruleHead.getId(), valuesHashMap, jobId);
            } catch (Exception e) {
                if (jobId != null) {
                    Optional<JobManagerTenant> jobManagerOptional = repository.findById(jobId);
                    if (jobManagerOptional.isPresent()) {
                        JobManagerTenant jobManager = jobManagerOptional.get();
                        jobManager.setErrors(true);
                        StringBuilder builder = new StringBuilder();
                        if (jobManager.getLog() != null) {
                            builder.append(new String(jobManager.getLog()));
                            builder.append("\n");
                        }
                        builder.append(e.getMessage());
                        jobManager.setLog(builder.toString().getBytes());
                        jobManager.setStatus(EJobStatus.FAILED.toString());
                        jobManagerTenantService.saveOrUpdate(jobManager);
                    }
                }
                ObjectNode messageJson = new ObjectMapper().createObjectNode();
                messageJson.put("job_id", jobId);
                messageJson.put("subscription_id", subscriptionId);
                messageJson.put("message", e.getMessage());
                LOGGER.error(messageJson.toPrettyString(), e);
            }
        } else {
            Long subscriptionId = billingHead.getSubscriptionId();
            CustomerSubscription subscription = subscriptionRepository.findById(subscriptionId).orElse(null);
            TransStageHead transStageHead = transStageHeadService.findBySubsId(subscription.getExtSubsId());
            if (transStageHead != null) {
                List<TransStageTemp> transStageTempList = transStageTempService.findAllByTJobId(transStageHead.getTjobId());
                LOGGER.info("Customer Product Id: [" + subscription.getExtSubsId() + "]");
                LOGGER.info("BillingHead Id: [" + billingHead.getId() + "]");

                RulesInitiator initiator = rulesFactory.getRulesInitiator(transStageHead.getParserCode(), isLegacy);
                try {
                    Map<String, Object> valuesHashMap = initiator.generateHashMap(transStageTempList, billingHead, subscription.getExtSubsId(), jobId);
                    initiator.calculate(billingHead, subscription, transStageTempList, null, valuesHashMap, jobId);
                    billInvoiceService.generateDraftHTML(billingHead);
                } catch (Exception e) {
                    if (jobId != null) {
                        Optional<JobManagerTenant> jobManagerOptional = repository.findById(jobId);
                        if (jobManagerOptional.isPresent()) {
                            JobManagerTenant jobManager = jobManagerOptional.get();
                            jobManager.setErrors(true);
                            StringBuilder builder = new StringBuilder();
                            if (jobManager.getLog() != null) {
                                builder.append(new String(jobManager.getLog()));
                                builder.append("\n");
                            }
                            builder.append(e.getMessage());
                            jobManager.setLog(builder.toString().getBytes());
                            jobManager.setStatus(EJobStatus.FAILED.toString());
                            jobManagerTenantService.saveOrUpdate(jobManager);
                        }
                    }
                    ObjectNode messageJson = new ObjectMapper().createObjectNode();
                    messageJson.put("job_id", jobId);
                    messageJson.put("subscription_id", subscriptionId);
                    messageJson.put("message", e.getMessage());
                    LOGGER.error(messageJson.toPrettyString(), e);
                }
            } else {
                calculationTrackerService.updateBillingLogError(billingHead.getId(), "Invalid subscription information");
            }

        }
    }
}
