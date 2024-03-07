package com.solar.api.tenant.service.process.subscription.billHead;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Utility;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.JobComponents;
import com.solar.api.saas.model.process.rule.RuleHead;
import com.solar.api.saas.repository.JobComponentsRepository;
import com.solar.api.saas.repository.RuleHeadRepository;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.saas.service.integration.mongo.response.subscription.*;
import com.solar.api.saas.service.job.EJobStatus;
import com.solar.api.saas.service.process.ExecuteParams;
import com.solar.api.saas.service.process.rule.RulesFactory;
import com.solar.api.saas.service.process.rule.RulesInitiator;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.billingHead.EBillStatus;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.subscription.ESubscriptionStatus;
import com.solar.api.tenant.model.subscription.SubscriptionType;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHead;
import com.solar.api.tenant.repository.BillingHeadRepository;
import com.solar.api.tenant.repository.CustomerSubscriptionRepository;
import com.solar.api.tenant.service.BillingHeadService;
import com.solar.api.tenant.service.SubscriptionService;
import com.solar.api.tenant.service.job.JobManagerTenantService;
import com.solar.api.tenant.service.process.subscription.SubscriptionBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
//@Transactional("masterTransactionManager")
public class BillHeadActionImpl extends SubscriptionBase implements BillHeadAction {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private BillingHeadService billingHeadService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private JobManagerTenantService jobManagerTenantService;
    @Autowired
    private RulesFactory rulesFactory;
    @Autowired
    private BillingHeadRepository billingHeadRepository;
    @Autowired
    private CustomerSubscriptionRepository customerSubscriptionRepository;
    @Autowired
    private RuleHeadRepository ruleHeadRepository;
    @Autowired
    private JobComponentsRepository componentsRepository;
    @Autowired
    private DataExchange dataExchange;

    @Override
    public String lockForInvalidation(Long billHeadId, Boolean isLegacy) {
        try {
            BillingHead billingHeadToInvalidate = billingHeadService.findById(billHeadId);
            if (!billingHeadToInvalidate.getBillStatus().equals(EBillStatus.GENERATED.getStatus()) && !billingHeadToInvalidate.getBillStatus().equals(EBillStatus.SCHEDULED.getStatus())) {
                return "Bill Status Should be GENERATED or SCHEDULED to INVALIDATE the bill.";
            }
            invalidate(billingHeadToInvalidate, isLegacy);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return "Your bill invalidate job request is successfully submitted.";
    }

    @Transactional
    @Async
    @Override
    public void invalidate(BillingHead billingHeadToInvalidate, Boolean isLegacy) {
        if (isLegacy) {
            try {
                //change bill status to LOCKED
                billingHeadToInvalidate.setBillStatus(EBillStatus.LOCKED.getStatus());
                billingHeadRepository.save(billingHeadToInvalidate);

                //change bill status to INVALID
                CustomerSubscription subscription =
                        subscriptionService.findCustomerSubscriptionById(billingHeadToInvalidate.getSubscriptionId());
                SubscriptionType subscriptionType =
                        subscriptionService.findSubscriptionTypeByCode(subscription.getSubscriptionType());
                Integer billingCycle = subscriptionType.getBillingCycle();
                billingHeadToInvalidate.setBillStatus(EBillStatus.INVALID.getStatus());
                billingHeadRepository.save(billingHeadToInvalidate);

                // Add number of BillingHeads based on GenerateCycle and BillingCycle in SubscriptionTypes
                //String invalidatedMonthYear = billingHeadToInvalidate.getBillingMonthYear();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Utility.MONTH_YEAR_FORMAT);
                BillingHead invalidatedMonthYear =
                        billingHeadRepository.getBillHeadMonth(billingHeadToInvalidate.getSubscriptionId());
                Date invalidatedMonthDate =
                        Utility.addMonths(simpleDateFormat.parse(invalidatedMonthYear.getBillingMonthYear()), billingCycle);

                BillingHead billingHead = BillingHead.builder()
                        .userAccountId(billingHeadToInvalidate.getUserAccountId())
                        .subscriptionId(billingHeadToInvalidate.getSubscriptionId())
                        .billingMonthYear(simpleDateFormat.format(invalidatedMonthDate))
                        .billStatus(EBillStatus.SCHEDULED.getStatus())
                        .build();
                billingHeadRepository.save(billingHead);


                if (subscriptionType.getPreGenerate()) {
                    generateBill(billingHead, null, true);
                }

                CustomerSubscriptionMapping ssdtMapping =
                        subscriptionService.findCustomerSubscriptionMappingByRateCodeAndSubscription("SSDT", subscription);
                String ssdtDate = ssdtMapping.getValue();
                SimpleDateFormat simpleDateFormatForNew = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
                Date newMonthDate = Utility.addMonths(simpleDateFormatForNew.parse(ssdtDate), billingCycle);
                ssdtMapping.setValue(simpleDateFormatForNew.format(newMonthDate));
                subscriptionService.addOrUpdateCustomerSubscriptionMapping(ssdtMapping);

                //update startDate and endDate
                CustomerSubscription customerSubscription =
                        subscriptionService.findCustomerSubscriptionById(billingHeadToInvalidate.getSubscriptionId());
                customerSubscription.setStartDate(newMonthDate);

                Date updateEndDate = Utility.addMonths(customerSubscription.getEndDate(), billingCycle);
                customerSubscription.setEndDate(updateEndDate);
                customerSubscriptionRepository.save(customerSubscription);

                CustomerSubscriptionMapping rolloverDateMapping = subscriptionService.getRolloverDate(subscription);
                Date rollOverNewDate = Utility.addMonths(simpleDateFormatForNew.parse(rolloverDateMapping.getValue()),
                        billingCycle);
                rolloverDateMapping.setValue(simpleDateFormatForNew.format(rollOverNewDate));
                subscriptionService.addOrUpdateCustomerSubscriptionMapping(rolloverDateMapping);

            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                LOGGER.error(e.getMessage(), e);
            }
            LOGGER.info("Bill {} Invalidated", billingHeadToInvalidate.getId());
        } else {
            try {
                //change bill status to LOCKED
                billingHeadToInvalidate.setBillStatus(EBillStatus.LOCKED.getStatus());
                billingHeadRepository.save(billingHeadToInvalidate);
                //change bill status to INVALID
                String subscriptionId = billingHeadToInvalidate.getCustProdId();
                SubscriptionMapping subscriptionMapping = dataExchange.getSubscriptionMapping(subscriptionId, DBContextHolder.getTenantName());
                Variant variant = subscriptionMapping.getVariant();

                Integer billingCycle = variant.getBillingFrequency();
                billingHeadToInvalidate.setBillStatus(EBillStatus.INVALID.getStatus());
                billingHeadRepository.save(billingHeadToInvalidate);

                // Add number of BillingHeads based on GenerateCycle and BillingCycle in SubscriptionTypes
                //String invalidatedMonthYear = billingHeadToInvalidate.getBillingMonthYear();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Utility.MONTH_YEAR_FORMAT);
                BillingHead invalidatedMonthYear =
                        billingHeadRepository.getBillHeadMonth(billingHeadToInvalidate.getSubscriptionId());
                Date invalidatedMonthDate =
                        Utility.addMonths(simpleDateFormat.parse(invalidatedMonthYear.getBillingMonthYear()), billingCycle);

                BillingHead billingHead = BillingHead.builder()
                        .userAccountId(billingHeadToInvalidate.getUserAccountId())
//                        .subscriptionId(billingHeadToInvalidate.getSubscriptionId())
                        .custProdId(subscriptionId)
                        .billingMonthYear(simpleDateFormat.format(invalidatedMonthDate))
                        .billStatus(EBillStatus.SCHEDULED.getStatus())
                        .build();
                billingHeadRepository.save(billingHead);


                if (variant.getPreGenerate()) {
                    generateBill(billingHead, null, false);
                }

                Subscription subscription = subscriptionMapping.getSubscription();
                MeasureType ssdtMeasure =
                        subscription.getMeasures().getByCustomer().stream().filter(m -> m.getCode().equals("S_SSDT")).findFirst().orElse(null);
                SimpleDateFormat simpleDateFormatForNew = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
                if (ssdtMeasure == null) {
                    LOGGER.error("S_SSDT not found for subscription " + subscriptionId);
                } else {
                    String ssdtDate = ssdtMeasure.getDefaultValue().toString();
                    Date newMonthDate = Utility.addMonths(simpleDateFormatForNew.parse(ssdtDate), billingCycle);
                    ssdtMeasure.setDefaultValue(simpleDateFormatForNew.format(newMonthDate));
                    subscription.setStartDate(newMonthDate);
                }

                //update startDate and endDate
                Date updateEndDate = Utility.addMonths(subscription.getEndDate(), billingCycle);
                subscription.setEndDate(updateEndDate);

                MeasureType rolloverMeasure =
                        subscription.getMeasures().getByCustomer().stream().filter(m -> m.getCode().equals("S_ROLLDT")).findFirst().orElse(null);
                if (rolloverMeasure == null) {
                    LOGGER.error("S_ROLLDT not found for subscription " + subscriptionId);
                } else {
                    Date rollOverNewDate = Utility.addMonths(simpleDateFormatForNew.parse(rolloverMeasure.getDefaultValue().toString()),
                            billingCycle);
                    rolloverMeasure.setDefaultValue(simpleDateFormatForNew.format(rollOverNewDate));
                }
                dataExchange.updateSubscription(subscription, DBContextHolder.getTenantName());
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            LOGGER.info("Bill {} Invalidated", billingHeadToInvalidate.getId());
        }
    }

    @Override
    // TODO: Pass variant code as subscriptionCode when isLegacy is false
    public void billingBySubscriptionType(String subscriptionCode, String billingMonth, String type,
                                          JobManagerTenant jobManagerTenant, Boolean isLegacy) {
        if (isLegacy) {
            List<CustomerSubscription> activeSubscriptions =
                    subscriptionService.findBySubscriptionStatusAndSubscriptionType("ACTIVE", subscriptionCode);
            activeSubscriptions.forEach(subscription -> {
                SubscriptionRateMatrixHead matrixHead =
                        subscriptionService.findSubscriptionRateMatrixHeadById(subscription.getSubscriptionRateMatrixId());
                RuleHead ruleHead = ruleHeadRepository.findByBillingCodeAndRuleDependency("MBILLFN",
                        matrixHead.getSubscriptionCode());
                if (ruleHead == null) {
                    throw new NotFoundException("RuleHead not found for action " + subscription.getSubscriptionType());
                }
                RulesInitiator initiator = rulesFactory.getRulesInitiator(ruleHead.getMethod(), isLegacy);
                ExecuteParams executeParams = ExecuteParams.builder()
                        .rateMatrixHeadId(subscription.getSubscriptionRateMatrixId())
                        .billingMonth(billingMonth)
                        .ruleHeadId(ruleHead.getId())
                        .build();
                billingBySubscriptionType(subscription, billingMonth, initiator, executeParams, jobManagerTenant);
            });
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), null, LOGGER);
        } else {
            VariantSubscriptionMapping activeMappings = getActiveSubscriptionsByVariantCode(subscriptionCode); // TODO: get by variant code
            if (activeMappings == null) {
                return;
            }
            Variant variant = activeMappings.getVariant();
            RuleHead ruleHead = ruleHeadRepository.findByBillingCode(variant.getParserCode());
            if (ruleHead == null) {
                throw new NotFoundException("RuleHead not found for action " + variant.getParserCode());
            }
            activeMappings.getSubscriptions().forEach(subscription -> {
                RulesInitiator initiator = rulesFactory.getRulesInitiator(ruleHead.getBillingCode(), isLegacy);
                ExecuteParams executeParams = ExecuteParams.builder()
//                        .rateMatrixHeadId(subscription.getSubscriptionRateMatrixId())
                        .billingMonth(billingMonth)
                        .ruleHeadId(ruleHead.getId())
                        .build();
                billingBySubscriptionType(SubscriptionMapping.builder().variant(variant).subscription(subscription).build(),
                        billingMonth, initiator, executeParams, jobManagerTenant);
            });
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), null, LOGGER);
        }
    }

    @Override
    public void billingBySubscriptionType(String subscriptionCode, List<Long> rateMatrixHeadIds, List<String> variantIds,
                                          String billingMonth, String type, JobManagerTenant jobManagerTenant, Boolean isLegacy) {
        if (isLegacy) {
            List<JobComponents> jobComponents = new ArrayList<>();
            List<CustomerSubscription> subscriptions =
                    subscriptionService.findActiveBySubscriptionRateMatrixIds(rateMatrixHeadIds);
            RuleHead ruleHead = ruleHeadRepository.findByBillingCodeAndRuleDependency("MBILLFN", subscriptionCode);
            if (ruleHead == null) {
                throw new NotFoundException("RuleHead not found for action " + type);
            }
            RulesInitiator initiator = rulesFactory.getRulesInitiator(ruleHead.getMethod(), isLegacy);
            subscriptions.forEach(subscription -> {
                ExecuteParams params = ExecuteParams.builder()
                        .rateMatrixHeadId(subscription.getSubscriptionRateMatrixId())
                        .billingMonth(billingMonth)
                        .ruleHeadId(ruleHead.getId())
                        .build();
                /**
                 * To be changed
                 * findBySubscriptionIdAndBillingMonthYear
                 * return type is List
                 * for testing purposes
                 */
                billingBySubscriptionType(subscription, billingMonth, initiator, params, jobManagerTenant);
            });
            //        pending, running, failed, completed, scheduled

            componentsRepository.saveAll(jobComponents);
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), null, LOGGER);
        } else {
            VariantSubscriptionMapping activeMappings = getActiveSubscriptionsByVariantIdsIn(variantIds); // TODO: get by variant Ids
            if (activeMappings == null) {
                return;
            }
            Variant variant = activeMappings.getVariant();
            RuleHead ruleHead = ruleHeadRepository.findByBillingCode(variant.getParserCode());
            if (ruleHead == null) {
                throw new NotFoundException("RuleHead not found for action " + variant.getParserCode());
            }
            activeMappings.getSubscriptions().forEach(subscription -> {
                RulesInitiator initiator = rulesFactory.getRulesInitiator(ruleHead.getBillingCode(), isLegacy);
                ExecuteParams executeParams = ExecuteParams.builder()
//                        .rateMatrixHeadId(subscription.getSubscriptionRateMatrixId())
                        .billingMonth(billingMonth)
                        .ruleHeadId(ruleHead.getId())
                        .build();
                billingBySubscriptionType(SubscriptionMapping.builder().variant(variant).subscription(subscription).build(),
                        billingMonth, initiator, executeParams, jobManagerTenant);
            });
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), null, LOGGER);
        }
    }

    private VariantSubscriptionMapping getActiveSubscriptionsByVariantCode(String variantCode) {
        VariantSubscriptionMapping subscriptionMappings =
                dataExchange.getSubscriptionMappingsByVariantCode(variantCode, DBContextHolder.getTenantName());
        if (!subscriptionMappings.getVariant().getVariantActive()) {
            return null;
        }
        subscriptionMappings.getSubscriptions().removeIf(s -> !ESubscriptionStatus.ACTIVE.getStatus().equals(s.getActive()));
        return subscriptionMappings;
    }

    private VariantSubscriptionMapping getActiveSubscriptionsByVariantIdsIn(List<String> variantIds) {
        VariantSubscriptionMapping subscriptionMappings =
                dataExchange.getSubscriptionMappingsByVariantIdsIn(variantIds, DBContextHolder.getTenantName());
        if (!subscriptionMappings.getVariant().getVariantActive()) {
            return null;
        }
        subscriptionMappings.getSubscriptions().removeIf(s -> !ESubscriptionStatus.ACTIVE.getStatus().equals(s.getActive()));
        return subscriptionMappings;
    }

    private void billingBySubscriptionType(CustomerSubscription subscription, String billingMonth,
                                           RulesInitiator initiator, ExecuteParams params,
                                           JobManagerTenant jobManagerTenant) {
        BillingHead billingHead = billingHeadService.findBySubscriptionIdAndBillingMonthYear(subscription.getId(),
                billingMonth);
        List<JobComponents> jobComponents = new ArrayList<>();
        if (billingHead != null) {
            jobComponents.add(JobComponents.builder()
                    .componentId(billingHead.getId())
                    .componentTypeCode("BILL") // From system attribute
                    .jobId(jobManagerTenant.getId()).build());
            try {
                initiator.execute(params, subscription, billingHead, jobManagerTenant.getId());
            } catch (Exception e) {
                ObjectNode messageJson = new ObjectMapper().createObjectNode();
                messageJson.put("job_id", jobManagerTenant.getId());
                messageJson.put("subscription_id", subscription.getId());
                messageJson.put("billing_head_id", billingHead.getId());
                messageJson.put("billing_month", billingMonth);
                LOGGER.error(messageJson.toPrettyString(), e);
            }
        } else {
            LOGGER.info("BillingHead not found for subscriptionId " + subscription.getId() + " and billing month " + billingMonth);
        }
    }

    private void billingBySubscriptionType(SubscriptionMapping subscriptionMapping, String billingMonth,
                                           RulesInitiator initiator, ExecuteParams params,
                                           JobManagerTenant jobManagerTenant) {
        String subscriptionId = subscriptionMapping.getSubscription().getId().getOid();
        BillingHead billingHead = billingHeadService.findByCustProdIdAndBillingMonthYear(subscriptionId, billingMonth);
        List<JobComponents> jobComponents = new ArrayList<>();
        if (billingHead != null) {
            jobComponents.add(JobComponents.builder()
                    .componentId(billingHead.getId())
                    .componentTypeCode("BILL") // From system attribute
                    .jobId(jobManagerTenant.getId()).build());
            try {
                initiator.execute(params, subscriptionMapping, billingHead, jobManagerTenant.getId());
            } catch (Exception e) {
                ObjectNode messageJson = new ObjectMapper().createObjectNode();
                messageJson.put("job_id", jobManagerTenant.getId());
                messageJson.put("subscription_id", subscriptionId);
                messageJson.put("billing_head_id", billingHead.getId());
                messageJson.put("billing_month", billingMonth);
                LOGGER.error(messageJson.toPrettyString(), e);
            }
        } else {
            LOGGER.info("BillingHead not found for subscriptionId " + subscriptionId + " and billing month " + billingMonth);
        }
    }
}
