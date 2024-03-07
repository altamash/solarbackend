package com.solar.api.tenant.service.process.subscription.activation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.solar.api.AppConstants;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Utility;
import com.solar.api.helper.service.SubscriptionRateCodes;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.saas.service.integration.mongo.response.subscription.MeasureType;
import com.solar.api.saas.service.integration.mongo.response.subscription.Subscription;
import com.solar.api.saas.service.integration.mongo.response.subscription.SubscriptionMapping;
import com.solar.api.saas.service.integration.mongo.response.subscription.Variant;
import com.solar.api.saas.service.job.EJobStatus;
import com.solar.api.saas.service.process.calculation.RateFunctions;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.billingHead.EBillStatus;
import com.solar.api.tenant.model.billing.calculation.CalculationDetails;
import com.solar.api.tenant.model.billing.calculation.CalculationTracker;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.subscription.ESubscriptionStatus;
import com.solar.api.tenant.model.subscription.SubscriptionType;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.repository.CustomerSubscriptionMappingRepository;
import com.solar.api.tenant.repository.CustomerSubscriptionRepository;
import com.solar.api.tenant.repository.SubscriptionRateMatrixDetailRepository;
import com.solar.api.tenant.repository.SubscriptionTypeRepository;
import com.solar.api.tenant.service.BillingHeadService;
import com.solar.api.tenant.service.CalculationDetailsService;
import com.solar.api.tenant.service.CalculationTrackerService;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.job.JobManagerTenantService;
import com.solar.api.tenant.service.process.billing.BillingService;
import com.solar.api.tenant.service.process.subscription.SubscriptionBase;
import com.solar.api.tenant.service.tansStage.TransStageTempService;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class SubscriptionActivation extends SubscriptionBase {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private BillingHeadService billingHeadService;
    @Autowired
    private CustomerSubscriptionRepository subscriptionRepository;
    @Autowired
    private CustomerSubscriptionMappingRepository subscriptionMappingRepository;
    @Autowired
    private SubscriptionRateMatrixDetailRepository rateMatrixDetailRepository;
    @Autowired
    private SubscriptionTypeRepository subscriptionTypeRepository;
    @Autowired
    private CalculationDetailsService calculationDetailsService;
    @Autowired
    private RateFunctions rateFunctions;
    @Autowired
    private JobManagerTenantService jobManagerService;
    @Autowired
    private DataExchange dataExchange;
    @Autowired
    private CalculationTrackerService calculationTrackerService;
    @Lazy
    @Autowired
    private BillingService billingService;
    @Autowired
    private UserService userService;
    @Autowired
    private TransStageTempService transStageTempService;


    @Async
//    @Transactional
    public List<BillingHead> activate(Long userAccountId, String subscriptionId, String start, Long jobId, Boolean isLegacy) throws ParseException {
        List<BillingHead> billingHeads = new ArrayList<>();
        if (isLegacy) {
            Long subsId = Long.parseLong(subscriptionId);
            CustomerSubscription subscription = subscriptionRepository.findById(subsId).orElse(null);
            try {
                if (rateMatrixDetailRepository.findBySubscriptionRateMatrixIdAndRateCode(subscription.getSubscriptionRateMatrixId(),
                        SubscriptionRateCodes.GARDEN_START_DATE).getDefaultValue() == null) {
                    LOGGER.warn("The garden is either inactive or scheduled for a future date (subscription id: {})",
                            subsId);
                    return billingHeads;
                }
                SubscriptionType subscriptionType =
                        subscriptionTypeRepository.findByCode(subscription.getSubscriptionType());
                Integer generateCycle = subscriptionType.getGenerateCycle();
                if (generateCycle == -1) {
                    return billingHeads;
                }
                Integer billingCycle = subscriptionType.getBillingCycle();

                LocalDate startDate;
                if (start == null) {
                    startDate = rateFunctions.parseDateFormat("yyyy-MM-dd", rateFunctions.getSSDT(subscription));
                } else {
                    startDate = rateFunctions.parseDateFormat("yyyy-MM-dd", start);
                }
                Integer billingPeriod = generateCycle / billingCycle;
                // Add number of BillingHeads based on GenerateCycle and BillingCycle in SubscriptionTypes
                BillingHead billingHead = BillingHead.builder()
                        .userAccountId(userAccountId)
                        .subscriptionId(subsId)
                        .billingMonthYear(rateFunctions.getMonthYear(startDate))
                        .billStatus(EBillStatus.PENDING.getStatus())
                        .build();
                billingHeads.add(billingHead);
                for (int i = 1; i < billingPeriod; i++) {
                    billingHead = BillingHead.builder()
                            .userAccountId(userAccountId)
                            .subscriptionId(subsId)
                            .billingMonthYear(rateFunctions.getMonthYear(startDate.plusMonths(billingCycle * i)))
                            .billStatus(EBillStatus.PENDING.getStatus())
                            .build();
                    billingHeads.add(billingHead);
                }
                subscription.setSubscriptionStatus(ESubscriptionStatus.ACTIVE.getStatus());
                Long userId = subscription.getUserAccount().getAcctId();
                subscription.setUserAccountId(userId);
                Date activationDate = new SimpleDateFormat("yyyy-MM-dd").parse(
                        start == null ? rateFunctions.getSSDT(subscription) : start);
                subscription.setStartDate(activationDate);
            /*try {
                if (billingHeads.size() > 0) {
                    subscription.setEndDate(new SimpleDateFormat("MM-yyyy").parse(billingHeads.get(billingHeads.size
                    () - 1).getBillingMonthYear()));
                }
            } catch (ParseException e) {
                LOGGER.error("Error parsing end date", e);
            }*/
                subscription.setEndDate(startDate.plusMonths(generateCycle).minusDays(1).toDate());
                subscriptionRepository.save(subscription);
                billingHeads = billingHeadService.addBillingHeadsBySubscriptionAndMonth(billingHeads);
                CustomerSubscriptionMapping rolloverDateMapping = subscriptionMappingRepository.getRolloverDate(subscription);
                updateRolloverDate(startDate, rolloverDateMapping, generateCycle);
                if (start != null) {
                    CustomerSubscriptionMapping mappingSSDT =
                            subscriptionMappingRepository.findByRateCodeAndSubscription("SSDT", subscription);
                    mappingSSDT.setValue(start);
                    subscriptionMappingRepository.save(mappingSSDT);
                }
            } catch (Exception e) {
                JobManagerTenant jobManagerTenant = jobManagerService.findById(jobId);
                jobManagerTenant.setStatus(EJobStatus.FAILED.toString());
                jobManagerTenant.setErrors(true);
                jobManagerTenant.setLog(e.getMessage() != null ? e.getMessage().getBytes() : null);
                jobManagerService.saveOrUpdate(jobManagerTenant);
                ObjectNode messageJson = new ObjectMapper().createObjectNode();
                messageJson.put("job_id", jobId);
                messageJson.put("user_account_id", userAccountId);
                messageJson.put("subscription_id", subsId);
                messageJson.put("start", start);
                messageJson.put("message", e.getMessage());
                LOGGER.error(messageJson.toPrettyString(), e);
            }
            SubscriptionType subscriptionType =
                    subscriptionTypeRepository.findByCode(subscription.getSubscriptionType());
            if (subscriptionType.getPreGenerate()) {
                generateBills(billingHeads, jobId, isLegacy);
            }
            LOGGER.info("{} subscription [{}] activated", subscription.getSubscriptionType(), subsId);
        } else {
            SubscriptionMapping subscriptionMapping = dataExchange.getSubscriptionMapping(String.valueOf(subscriptionId),
                    DBContextHolder.getTenantName());
            Variant variant = subscriptionMapping.getVariant();
            try {
                Subscription subscription = subscriptionMapping.getSubscription();
                MeasureType gardenStartDate =
                        variant.getMeasures().getByProduct().stream()
                                .filter(m -> m.getCode().equals(SubscriptionRateCodes.S_GARDEN_START_DATE))
                                .findFirst().orElse(null);
                if (gardenStartDate == null || gardenStartDate.getDefaultValue() == null) {
                    LOGGER.warn("The variant is either inactive or scheduled for a future date (variant id: {})",
                            variant.getId().getOid());
                    return billingHeads;
                }
                Integer generateCycle = variant.getBillingCycle();
                if (generateCycle == -1) {
                    return billingHeads;
                }
                Integer billingCycle = variant.getBillingFrequency();

                LocalDate startDate;
                MeasureType ssdtMapping = subscription.getMeasures().getByCustomer().stream()
                        .filter(m -> m.getCode().equals("S_SSDT"))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException(CustomerSubscriptionMapping.class, "rateCode", "SSDT"));
                if (start == null) {
                    startDate = rateFunctions.parseDateFormat("yyyy-MM-dd", ssdtMapping.getDefaultValue().toString().split("T")[0].replace("{\"$date\":\"", ""));
                    //.replace("{$date=",""));
                } else {
                    startDate = rateFunctions.parseDateFormat("yyyy-MM-dd", start);
                }
                Integer billingPeriod = generateCycle / billingCycle;
                // Add number of BillingHeads based on GenerateCycle and BillingCycle in SubscriptionTypes
                CustomerSubscription customerSubscription = subscriptionRepository.findByExtSubsId(subscriptionId);
                User user = userService.findById(userAccountId);
                BillingHead billingHead = BillingHead.builder()
                        .userAccountId(userAccountId)
                        .userAccount(user)
                        .subscriptionId(customerSubscription.getId())
                        .billingMonthYear(rateFunctions.getMonthYear(startDate))
                        .billStatus(EBillStatus.PENDING.getStatus())
                        .build();
                billingHeads.add(billingHead);
                for (int i = 1; i < billingPeriod; i++) {
                    billingHead = BillingHead.builder()
                            .userAccountId(userAccountId)
                            .userAccount(user)
                            .subscriptionId(customerSubscription.getId())
                            .billingMonthYear(rateFunctions.getMonthYear(startDate.plusMonths(billingCycle * i)))
                            .billStatus(EBillStatus.PENDING.getStatus())
                            .build();
                    billingHeads.add(billingHead);
                }
                subscription.setActive(ESubscriptionStatus.ACTIVE.getStatus());
//                Long userId = subscription.getUserAccount().getAcctId();
//                subscription.setUserAccountId(userId);
                Date activationDate = new SimpleDateFormat("yyyy-MM-dd")
                        .parse(start == null ? ssdtMapping.getDefaultValue().toString().split("T")[0].replace("{\"$date\":\"", "") : start);
                subscription.setStartDate(activationDate);
                subscription.setEndDate(startDate.plusMonths(generateCycle).minusDays(1).toDate());


//                subscriptionRepository.save(subscription);
                billingHeads = billingHeadService.addBillingHeadsBySubscriptionAndMonth(billingHeads);

//                CustomerSubscriptionMapping rolloverDateMapping = subscriptionMappingRepository.getRolloverDate(subscription);
                MeasureType rolldtMapping = subscription.getMeasures().getByCustomer().stream()
                        .filter(m -> m.getCode().equals("ROLLDT") || m.getCode().equals("S_ROLLDT"))
                        .findFirst()
                        .orElse(null);
                if (rolldtMapping != null) {
                    rolldtMapping.setDefaultValue(Utility.formatLocalDate(startDate.plusMonths(generateCycle), Utility.SYSTEM_DATE_FORMAT));
                }
//                updateRolloverDate(startDate, rolloverDateMapping, generateCycle);
                if (start != null) {
//                    CustomerSubscriptionMapping mappingSSDT =
//                            subscriptionMappingRepository.findByRateCodeAndSubscription("SSDT", subscription);
                    ssdtMapping.setDefaultValue(start);
//                    subscriptionMappingRepository.save(mappingSSDT);
                }
                //TODO : Subscription Activation to be updated on mongo & staging table
//                dataExchange.updateSubscription(dataExchange.measuresDefaultValuesCastToGivenFormat(subscription), DBContextHolder.getTenantName());
//                CalculationTracker calculationTracker = calculationTrackerService.addOrUpdate(CalculationTracker.builder()
//                        .refId(variant.getId().getOid())
//                        .calcRefType(AppConstants.REF_TYPE_VARIANT)
//                        .billingPeriod(rateFunctions.getMonthYear(startDate))
//                        .state(EJobStatus.RUNNING.toString()).build());
//                billingHeads.forEach(billingHeadTemp -> {  //Creating Calculation Detail Items
//                    calculationDetailsService.addOrUpdate(CalculationDetails.builder().source("BILLING")
//                            .sourceId(billingHeadTemp.getId())
//                            .calculationTracker(calculationTracker)
//                            .state(billingHeadTemp.getBillStatus()).build());
//                });
//                calculationTracker.setState(EJobStatus.COMPLETED.toString());
//                calculationTrackerService.addOrUpdate(calculationTracker);
            } catch (Exception e) {
                JobManagerTenant jobManagerTenant = jobManagerService.findById(jobId);
                jobManagerTenant.setStatus(EJobStatus.FAILED.toString());
                jobManagerTenant.setErrors(true);
                jobManagerTenant.setLog(e.getMessage() != null ? e.getMessage().getBytes() : null);
                jobManagerService.saveOrUpdate(jobManagerTenant);
                ObjectNode messageJson = new ObjectMapper().createObjectNode();
                messageJson.put("job_id", jobId);
                messageJson.put("user_account_id", userAccountId);
                messageJson.put("subscription_id", subscriptionId);
                messageJson.put("start", start);
                messageJson.put("message", e.getMessage());
                LOGGER.error(messageJson.toPrettyString(), e);
            }
//            SubscriptionType subscriptionType =
//                    subscriptionTypeRepository.findByCode(subscription.getSubscriptionType());
//            if (variant.getPreGenerate()) {
//                billingService.fillTransStageTables(billingHeads);
//                generateBills(billingHeads, jobId, isLegacy);
//                transStageTempService.deleteAll();
//            }
            LOGGER.info("{} subscription [{}] activated", variant.getCategory(), subscriptionId);
        }
        return billingHeads;
    }

    @Async
    public void activateOnly(Long userAccountId, String subscriptionId, String start, Long jobId, Boolean isLegacy) throws ParseException {
        try {
            if (isLegacy) {
                CustomerSubscription subscription = subscriptionRepository.findById(Long.parseLong(subscriptionId)).orElse(null);
                SubscriptionType subscriptionType =
                        subscriptionTypeRepository.findByCode(subscription.getSubscriptionType());
                if (subscriptionType.getGenerateCycle() == -1) {
                    subscription.setSubscriptionStatus(ESubscriptionStatus.ACTIVE.getStatus());
                    Long userId = subscription.getUserAccount().getAcctId();
                    subscription.setUserAccountId(userId);
                    Date activationDate = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT).parse(
                            start == null ? rateFunctions.getSSDT(subscription) : start);
                    subscription.setStartDate(activationDate);
                    subscriptionRepository.save(subscription);
                }
                LOGGER.info("{} subscription [{}] activated", subscription.getSubscriptionType(), subscriptionId);
            } else {
                SubscriptionMapping subscriptionMapping = dataExchange.getSubscriptionMapping(subscriptionId, DBContextHolder.getTenantName());
                if (subscriptionMapping.getVariant().getBillingCycle() == -1) {
                    Subscription subscription = subscriptionMapping.getSubscription();
                    subscription.setActive(ESubscriptionStatus.ACTIVE.getStatus());
//                    Long userId = subscription.getUserAccount().getAcctId();
//                    subscription.setUserAccountId(userId);
                    MeasureType ssdtMapping = subscription.getMeasures().getByCustomer().stream()
                            .filter(m -> m.getCode().equals("S_SSDT"))
                            .findFirst()
                            .orElseThrow(() -> new NotFoundException(CustomerSubscriptionMapping.class, "rateCode", "SSDT"));
                    Date activationDate = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT).parse(
                            start == null ? ssdtMapping.getDefaultValue().toString() : start);
                    subscription.setStartDate(activationDate);
//                    dataExchange.updateSubscription(dataExchange.measuresDefaultValuesCastToGivenFormat(subscription), DBContextHolder.getTenantName());
                }
                LOGGER.info("{} subscription [{}] activated", subscriptionMapping.getVariant().getCategory(), subscriptionId);
            }
        } catch (Exception e) {
            JobManagerTenant jobManagerTenant = jobManagerService.findById(jobId);
            jobManagerTenant.setStatus(EJobStatus.FAILED.toString());
            jobManagerTenant.setErrors(true);
            jobManagerTenant.setLog(e.getMessage() != null ? e.getMessage().getBytes() : null);
            jobManagerService.saveOrUpdate(jobManagerTenant);
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("job_id", jobId);
            messageJson.put("user_account_id", userAccountId);
            messageJson.put("subscription_id", subscriptionId);
            messageJson.put("start", start);
            messageJson.put("message", e.getMessage());
            LOGGER.error(messageJson.toPrettyString(), e);
        }
    }

}
