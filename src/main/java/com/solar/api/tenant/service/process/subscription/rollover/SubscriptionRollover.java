package com.solar.api.tenant.service.process.subscription.rollover;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Utility;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.repository.MasterTenantRepository;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.saas.service.integration.mongo.response.subscription.MeasureType;
import com.solar.api.saas.service.integration.mongo.response.subscription.Subscription;
import com.solar.api.saas.service.integration.mongo.response.subscription.SubscriptionMapping;
import com.solar.api.saas.service.integration.mongo.response.subscription.Variant;
import com.solar.api.saas.service.job.EJobName;
import com.solar.api.saas.service.job.EJobStatus;
import com.solar.api.saas.service.process.calculation.RateFunctions;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.billingHead.EBillStatus;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.subscription.ESubscriptionStatus;
import com.solar.api.tenant.model.subscription.SubscriptionType;
import com.solar.api.tenant.repository.CustomerSubscriptionRepository;
import com.solar.api.tenant.repository.SubscriptionTypeRepository;
import com.solar.api.tenant.service.BillingHeadService;
import com.solar.api.tenant.service.job.JobManagerTenantService;
import com.solar.api.tenant.service.process.subscription.SubscriptionBase;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SubscriptionRollover extends SubscriptionBase {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private MasterTenantRepository masterTenantRepository;
    @Autowired
    private BillingHeadService billingHeadService;
    @Autowired
    private JobManagerTenantService jobManagerTenantService;
    @Autowired
    private CustomerSubscriptionRepository subscriptionRepository;
    @Autowired
    private SubscriptionTypeRepository subscriptionTypeRepository;
    @Autowired
    private RateFunctions rateFunctions;
    @Autowired
    private DataExchange dataExchange;

    @Async
    public void rollover(Long compKey) {
        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(EJobName.ROLLOVER.toString(), null,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(compKey);
        DBContextHolder.setTenantName(masterTenant.getDbName());
        DBContextHolder.setLegacy(masterTenant.getLegacyBilling());
        subscriptionRepository.findIdsBySubscriptionStatus(ESubscriptionStatus.ACTIVE.getStatus())
                .forEach(id -> rollover(String.valueOf(id), jobManagerTenant.getId(), true));
        jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), null, LOGGER);
    }

    // Apply to rollover month
    @Transactional
    public void rollover(String subscriptionId, Long jobId, Boolean isLegacy) {
        if (isLegacy) {
            try {
                CustomerSubscription subscription = subscriptionRepository.findById(Long.parseLong(subscriptionId)).orElse(null);
                CustomerSubscriptionMapping rolloverDateMapping = subscriptionMapping.getRolloverDate(subscription);
                LocalDate startDate = rateFunctions.parseDateFormat("yyyy-MM-dd", rolloverDateMapping.getValue());
                if (!shouldRollover(subscription, startDate, Long.parseLong(subscriptionId), jobId)) {
                    LOGGER.info("Rollover for subscription id {} is not applicable.", subscription.getId());
                    return;
                }
                LOGGER.info("Rollover for subscription id {} started.", subscription.getId());
                SubscriptionType subscriptionType =
                        subscriptionTypeRepository.findByCode(subscription.getSubscriptionType());
                List<BillingHead> billingHeads = rollover(subscription.getUserAccount().getAcctId(), subscription,
                        startDate, rolloverDateMapping, subscriptionType, jobId);
                if (subscriptionType.getPreGenerate()) {
                    generateBills(billingHeads, jobId, true);
                }
                LOGGER.info("Rollover for subscription id {} completed.", subscription.getId());
            } catch (Exception e) {
                ObjectNode messageJson = new ObjectMapper().createObjectNode();
                messageJson.put("job_id", jobId);
                messageJson.put("subscription_id", subscriptionId);
                messageJson.put("message", e.getMessage());
                LOGGER.error(messageJson.toPrettyString(), e);
            }
        } else {
            try {
                SubscriptionMapping subscriptionMapping = dataExchange.getSubscriptionMapping(subscriptionId,
                        DBContextHolder.getTenantName());
                Variant variant = subscriptionMapping.getVariant();
                Subscription subscription = subscriptionMapping.getSubscription();
                MeasureType rolloverMeasure =
                        subscription.getMeasures().getByCustomer().stream().filter(m -> m.getCode().equals("S_ROLLDT"))
                                .findFirst()
                                .orElseThrow(() -> new NotFoundException(CustomerSubscriptionMapping.class, "rateCode", "S_ROLLDT"));
                LocalDate startDate = rateFunctions.parseDateFormat("yyyy-MM-dd", rolloverMeasure.getDefaultValue());
                if (!shouldRollover(subscription, startDate, subscriptionId, jobId)) {
                    LOGGER.info("Rollover for subscription id {} is not applicable.", subscriptionId);
                    return;
                }
                LOGGER.info("Rollover for subscription id {} started.", subscriptionId);
                List<BillingHead> billingHeads = rollover(subscription.getUserAccountId(), subscription, variant,
                        startDate, jobId);
                if (variant.getPreGenerate()) {
                    generateBills(billingHeads, jobId, false);
                }
                LOGGER.info("Rollover for subscription id {} completed.", subscription.getId());
            } catch (Exception e) {
                ObjectNode messageJson = new ObjectMapper().createObjectNode();
                messageJson.put("job_id", jobId);
                messageJson.put("subscription_id", subscriptionId);
                messageJson.put("message", e.getMessage());
                LOGGER.error(messageJson.toPrettyString(), e);
            }
        }
    }

    // Subscription is active
    // Rollover is yes
    // Rollover date is past current date
    private boolean shouldRollover(CustomerSubscription subscription, LocalDate startDate, Long subscriptionId,
                                   Long jobId) {
        CustomerSubscriptionMapping rollMapping =
                subscriptionMapping.findByRateCodeAndSubscription("ROLL", subscription);
        if (rollMapping == null) {
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("job_id", jobId);
            messageJson.put("subscription_id", subscriptionId);
            LOGGER.error(messageJson.toPrettyString(), new NotFoundException(CustomerSubscriptionMapping.class,
                    "rate_code", "ROLL"));
            return false;
        }
        return ESubscriptionStatus.ACTIVE.getStatus().equals(subscription.getSubscriptionStatus())
                && "YES".equals(rollMapping.getValue().toUpperCase())
                && Utility.isBefore(startDate.toDate(), new Date());
    }
    private boolean shouldRollover(Subscription subscription, LocalDate startDate, String subscriptionId, Long jobId) {
        MeasureType rollMeasure =
                subscription.getMeasures().getByCustomer().stream().filter(m -> m.getCode().equals("ROLL"))
                        .findFirst()
                        .orElse(null);
        if (rollMeasure == null) {
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("job_id", jobId);
            messageJson.put("subscription_id", subscriptionId);
            LOGGER.error(messageJson.toPrettyString(), new NotFoundException(CustomerSubscriptionMapping.class,
                    "rate_code", "ROLL"));
            return false;
        }
        return ESubscriptionStatus.ACTIVE.getStatus().equals(subscription.getActive())
                && "YES".equalsIgnoreCase(rollMeasure.getDefaultValue())
                && Utility.isBefore(startDate.toDate(), new Date());
    }

    // Set start to rollover
    // And rollover to rollover + generateCycle
    private List<BillingHead> rollover(Long userAccountId, CustomerSubscription subscription, LocalDate start,
                                      CustomerSubscriptionMapping rollDtMapping, SubscriptionType subscriptionType,
                                      Long jobId) {
        List<BillingHead> billingHeads = new ArrayList<>();
        try {
            Integer generateCycle = subscriptionType.getGenerateCycle();
            Integer billingCycle = subscriptionType.getBillingCycle();

            Integer billingPeriod = generateCycle / billingCycle;
            // Add number of BillingHeads based on GenerateCycle and BillingCycle in SubscriptionTypes
            BillingHead billingHead = BillingHead.builder()
                    .userAccountId(userAccountId)
                    .subscriptionId(subscription.getId())
                    .billingMonthYear(rateFunctions.getMonthYear(start))
                    .billStatus(EBillStatus.SCHEDULED.getStatus())
                    .build();
            billingHeads.add(billingHead);
            for (int i = 1; i < billingPeriod; i++) {
                billingHead = BillingHead.builder()
                        .userAccountId(userAccountId)
                        .subscriptionId(subscription.getId())
                        .billingMonthYear(rateFunctions.getMonthYear(start.plusMonths(billingCycle * i)))
                        .billStatus(EBillStatus.SCHEDULED.getStatus())
                        .build();
                billingHeads.add(billingHead);
            }
            subscription.setSubscriptionStatus(ESubscriptionStatus.ACTIVE.getStatus());
            Long userId = subscription.getUserAccount().getAcctId();
            subscription.setUserAccountId(userId);
            subscription.setStartDate(Utility.localDateToUtilDate(start));
            /*try {
                if (billingHeads.size() > 0) {
                    subscription.setEndDate(new SimpleDateFormat("MM-yyyy").parse(billingHeads.get(billingHeads.size
                    () - 1).getBillingMonthYear()));
                }
            } catch (ParseException e) {
                LOGGER.error("Error parsing end date", e);
            }*/
            subscription.setEndDate(start.plusMonths(generateCycle).minusDays(1).toDate());
            subscriptionRepository.save(subscription);
            billingHeads = billingHeadService.addBillingHeadsBySubscriptionAndMonth(billingHeads);
            updateRolloverDate(start, rollDtMapping, generateCycle);
        } catch (Exception e) {
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("job_id", jobId);
            messageJson.put("user_account_id", userAccountId);
            messageJson.put("subscription_id", subscription.getId());
            messageJson.put("start", start.toString());
            messageJson.put("message", e.getMessage());
            LOGGER.error(messageJson.toPrettyString(), e);
        }
        return billingHeads;
    }

    private List<BillingHead> rollover(Long userAccountId, Subscription subscription, Variant variant, LocalDate start,
                                       Long jobId) {
        List<BillingHead> billingHeads = new ArrayList<>();
        try {
            Integer generateCycle = variant.getBillingCycle();
            Integer billingCycle = variant.getBillingFrequency();

            Integer billingPeriod = generateCycle / billingCycle;
            // Add number of BillingHeads based on GenerateCycle and BillingCycle in SubscriptionTypes
            BillingHead billingHead = BillingHead.builder()
                    .userAccountId(userAccountId)
                    .custProdId(subscription.getId().getOid())
                    .billingMonthYear(rateFunctions.getMonthYear(start))
                    .billStatus(EBillStatus.SCHEDULED.getStatus())
                    .build();
            billingHeads.add(billingHead);
            for (int i = 1; i < billingPeriod; i++) {
                billingHead = BillingHead.builder()
                        .userAccountId(userAccountId)
                        .custProdId(subscription.getId().getOid())
                        .billingMonthYear(rateFunctions.getMonthYear(start.plusMonths(billingCycle * i)))
                        .billStatus(EBillStatus.SCHEDULED.getStatus())
                        .build();
                billingHeads.add(billingHead);
            }
            subscription.setActive(ESubscriptionStatus.ACTIVE.getStatus());
//            Long userId = subscription.getUserAccount().getAcctId();
//            subscription.setUserAccountId(userId);
            subscription.setStartDate(Utility.localDateToUtilDate(start));
            subscription.setEndDate(start.plusMonths(generateCycle).minusDays(1).toDate());
            billingHeads = billingHeadService.addBillingHeadsBySubscriptionAndMonth(billingHeads);
//            updateRolloverDate(start, rollDtMapping, generateCycle);
            MeasureType rolldtMapping = subscription.getMeasures().getByCustomer().stream()
                    .filter(m -> m.getCode().equals("ROLLDT"))
                    .findFirst()
                    .orElse(null);
            if (rolldtMapping != null) {
                rolldtMapping.setDefaultValue(Utility.formatLocalDate(start.plusMonths(generateCycle), Utility.SYSTEM_DATE_FORMAT));
            }
            dataExchange.updateSubscription(subscription, DBContextHolder.getTenantName());
        } catch (Exception e) {
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("job_id", jobId);
            messageJson.put("user_account_id", userAccountId);
            messageJson.put("subscription_id", subscription.getId().getOid());
            messageJson.put("start", start.toString());
            messageJson.put("message", e.getMessage());
            LOGGER.error(messageJson.toPrettyString(), e);
        }
        return billingHeads;
    }

}
