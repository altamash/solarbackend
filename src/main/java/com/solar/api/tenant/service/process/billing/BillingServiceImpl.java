package com.solar.api.tenant.service.process.billing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.mchange.util.AlreadyExistsException;
import com.solar.api.AppConstants;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Utility;
import com.solar.api.helper.service.SubscriptionRateCodes;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.process.rule.RuleHead;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.module.com.solar.batch.service.BatchService;
import com.solar.api.saas.repository.RuleHeadRepository;
import com.solar.api.saas.service.MasterTenantService;
import com.solar.api.saas.service.StorageService;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.saas.service.integration.mongo.response.subscription.MeasureType;
import com.solar.api.saas.service.integration.mongo.response.subscription.SubscriptionMapping;
import com.solar.api.saas.service.integration.mongo.response.subscription.Variant;
import com.solar.api.saas.service.integration.mongo.response.subscription.VariantSubscriptionMapping;
import com.solar.api.saas.service.integration.mongo.response.subscription.transStage.TransStageMeasureType;
import com.solar.api.saas.service.integration.mongo.response.subscription.transStage.TransStageTempDTO;
import com.solar.api.saas.service.job.EJobName;
import com.solar.api.saas.service.job.EJobStatus;
import com.solar.api.saas.service.process.calculation.RateFunctions;
import com.solar.api.tenant.mapper.billing.billingHead.ACHFileDTO;
import com.solar.api.tenant.model.billing.EBillingAction;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.billingHead.EBillStatus;
import com.solar.api.tenant.model.billing.calculation.CalculationDetails;
import com.solar.api.tenant.model.billing.calculation.CalculationTracker;
import com.solar.api.tenant.model.billing.tansStage.TransStageHead;
import com.solar.api.tenant.model.billing.tansStage.TransStageTemp;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.ESubscriptionStatus;
import com.solar.api.tenant.model.subscription.SubscriptionType;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHead;
import com.solar.api.tenant.repository.BillingHeadRepository;
import com.solar.api.tenant.repository.CustomerSubscriptionRepository;
import com.solar.api.tenant.repository.UserRepository;
import com.solar.api.tenant.service.*;
import com.solar.api.tenant.service.job.JobManagerTenantService;
import com.solar.api.tenant.service.process.billing.invoice.BillInvoiceService;
import com.solar.api.tenant.service.process.billing.postBillilng.PostBillingCalculation;
import com.solar.api.tenant.service.process.billing.postBillilng.PostBillingParserFactory;
import com.solar.api.tenant.service.process.subscription.activation.SubscriptionActivation;
import com.solar.api.tenant.service.process.subscription.billHead.BillHeadAction;
import com.solar.api.tenant.service.tansStage.TransStageHeadService;
import com.solar.api.tenant.service.tansStage.TransStageTempService;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BillingServiceImpl implements BillingService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private RuleHeadRepository ruleHeadRepository;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private BillingHeadService billingHeadService;
    @Lazy
    @Autowired
    private BillInvoiceService billInvoiceService;
    @Autowired
    private JobManagerTenantService jobManagerService;
    @Autowired
    private SubscriptionActivation subscriptionActivation;
    @Autowired
    private RateFunctions rateFunctions;
    @Autowired
    private PostBillingParserFactory postBillingParserFactory;
    @Autowired
    @Lazy
    private BatchService batchService;
    @Autowired
    private BillHeadAction billHeadAction;
    @Autowired
    private BillingHeadRepository billingHeadRepository;
    @Autowired
    private JobManagerTenantService jobManagerTenantService;
    @Autowired
    private MasterTenantService masterTenantService;
    @Autowired
    private CalculationTrackerService calculationTrackerService;
    @Autowired
    private StorageService storageService;
    @Value("${app.profile}")
    private String appProfile;
    @Autowired
    private Utility utility;
    @Autowired
    private DataExchange dataExchange;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransStageTempService transStageTempService;
    @Autowired
    private TransStageHeadService transStageHeadService;
    @Autowired
    private CustomerSubscriptionRepository customerSubscriptionRepository;
    @Autowired
    private CalculationDetailsService calculationDetailsService;

    @Override
    public String checkForRunningJob(List<Long> rateMatrixHeadIds, List<String> variantIds, String type, String subscriptionCode,
                                     String billingMonth, boolean isLegacy) {
        String jobName;
        if (isLegacy) {
            jobName = getJobNameLegacy(rateMatrixHeadIds, type, subscriptionCode, billingMonth);
        } else {
            jobName = getJobName(variantIds, type, subscriptionCode, billingMonth);
        }
        if (type.equals(EBillingByType.INVOICE_PDF.getType())) {
            String getMessage = checkForLastJobInstance(rateMatrixHeadIds, variantIds, type, subscriptionCode, billingMonth, isLegacy);
            if (getMessage != null) {
                return getMessage;
            }
        }
        return checkForRunningJob(jobName);
    }

    @Override
    public String checkForRunningJob(String jobName) {
        List<JobManagerTenant> jobManagerTenants = jobManagerService.findByJobNameAndStatus(
                jobName, EJobStatus.RUNNING.toString());
        if (!jobManagerTenants.isEmpty()) {
            JobManagerTenant job = jobManagerTenants.get(jobManagerTenants.size() - 1);
            return job.getJobName() + " with id " + job.getId() + " is already running!";
        }
        return null;
    }

    @Override
    public String checkForRunningJobIndividualInvoice(String jobName) {
        //Get now time
        LocalTime now = LocalTime.now();
        LocalTime elapsedTime = LocalTime.fromMillisOfDay(checkForLastJobInstance(jobName).getTime());
        //Get last run time - stored in db
        LocalTime updatedTime = now.minusHours(elapsedTime.getHourOfDay())
                .minusMinutes(elapsedTime.getMinuteOfHour())
                .minusSeconds(elapsedTime.getSecondOfMinute());
        if (updatedTime.getMinuteOfHour() > 30) {
            return null;
        } else {
            int remainingTime = 30 - Integer.parseInt(String.valueOf(updatedTime.getMinuteOfHour()));
            return "Your invoices will be available after " + String.valueOf(remainingTime) + " minute(s)";
        }
    }

    //Strictly for INVOICE_PDF job
    @Override
    public String checkForLastJobInstance(List<Long> rateMatrixHeadIds, List<String> variantIds, String type,
                                          String subscriptionCode, String billingMonth, boolean isLegacy) {
        String jobName;
        if (isLegacy) {
            jobName = getJobNameLegacy(rateMatrixHeadIds, EJobName.INVOICE.toString(), subscriptionCode, billingMonth);
        } else {
            jobName = getJobName(variantIds, EJobName.INVOICE.toString(), subscriptionCode, billingMonth);
        }
        //Get now time
        LocalTime now = LocalTime.now();
        LocalTime elapsedTime = LocalTime.fromMillisOfDay(checkForLastJobInstance(jobName).getTime());
        //Get last run time - stored in db
        LocalTime updatedTime = now.minusHours(elapsedTime.getHourOfDay())
                .minusMinutes(elapsedTime.getMinuteOfHour())
                .minusSeconds(elapsedTime.getSecondOfMinute());
        if (updatedTime.getMinuteOfHour() > 30) {
            return null;
        } else {
            int remainingTime = 30 - Integer.parseInt(String.valueOf(updatedTime.getMinuteOfHour()));
            return "Your invoices will be available after " + String.valueOf(remainingTime) + " minute(s)";
        }
    }

    //Strictly for INVOICE_PDF job
    @Override
    public Date checkForLastJobInstance(String jobName) {
        JobManagerTenant jobManagerTenant = jobManagerTenantService.findById(jobManagerService.findIdOfLastJobByJobNameAndStatus(
                jobName, EJobStatus.COMPLETED.toString()));
        if (jobManagerTenant != null) {
            return jobManagerTenant.getEndDatetime();
        }
        return null;
    }

    @Override
    public String getJobNameLegacy(List<Long> rateMatrixHeadIds, String type, String subscriptionCode, String
            billingMonth) {
        EBillingByType billingType = EBillingByType.get(type);
        String jobNameSuffix = "_" + subscriptionCode + "_" + billingMonth.toUpperCase();
        switch (billingType) {
            case GENERATE:
                if (rateMatrixHeadIds.isEmpty()) {
                    return EJobName.GENERATE_BY_CODE + jobNameSuffix;
                } else {
                    return EJobName.GENERATE + jobNameSuffix;
                }
            case INVOICE:
                if (rateMatrixHeadIds.isEmpty()) {
                    return EJobName.INVOICE_BY_CODE + jobNameSuffix;
                } else {
                    return EJobName.INVOICE + jobNameSuffix;
                }
            case SCHEDULED_INVOICING:
                if (rateMatrixHeadIds.isEmpty()) {
                    return EJobName.INVOICE_BY_CODE + jobNameSuffix;
                } else {
                    return EJobName.SCHEDULED_INVOICING + jobNameSuffix;
                }
            case INVOICE_PDF:
                if (rateMatrixHeadIds.isEmpty()) {
                    return EJobName.INVOICE_PDF_BY_CODE + jobNameSuffix;
                } else {
                    return EJobName.INVOICE_PDF + jobNameSuffix;
                }
            case PUBLISH_INVOICE:
                return EJobName.PUBLISH_INVOICE + jobNameSuffix;
            case POST_BILLING_CALCULATIONS:
                if (rateMatrixHeadIds.isEmpty()) {
                    return EJobName.POST_BILLING_CALCULATIONS_BY_CODE + jobNameSuffix;
                } else {
                    return EJobName.POST_BILLING_CALCULATIONS + jobNameSuffix;
                }
        }
        return null;
    }

    @Override
    public String getJobName(List<String> variantIds, String type, String subscriptionCode, String billingMonth) {
        EBillingByType billingType = EBillingByType.get(type);
        String jobNameSuffix = "_" + subscriptionCode + "_" + billingMonth.toUpperCase();
        switch (billingType) {
            case GENERATE:
                if (variantIds.isEmpty()) {
                    return EJobName.GENERATE_BY_CODE + jobNameSuffix;
                } else {
                    return EJobName.GENERATE + jobNameSuffix;
                }
            case INVOICE:
                if (variantIds.isEmpty()) {
                    return EJobName.INVOICE_BY_CODE + jobNameSuffix;
                } else {
                    return EJobName.INVOICE + jobNameSuffix;
                }
            case SCHEDULED_INVOICING:
                if (variantIds.isEmpty()) {
                    return EJobName.INVOICE_BY_CODE + jobNameSuffix;
                } else {
                    return EJobName.SCHEDULED_INVOICING + jobNameSuffix;
                }
            case INVOICE_PDF:
                if (variantIds.isEmpty()) {
                    return EJobName.INVOICE_PDF_BY_CODE + jobNameSuffix;
                } else {
                    return EJobName.INVOICE_PDF + jobNameSuffix;
                }
            case PUBLISH_INVOICE:
                return EJobName.PUBLISH_INVOICE + jobNameSuffix;
            case POST_BILLING_CALCULATIONS:
                if (variantIds.isEmpty()) {
                    return EJobName.POST_BILLING_CALCULATIONS_BY_CODE + jobNameSuffix;
                } else {
                    return EJobName.POST_BILLING_CALCULATIONS + jobNameSuffix;
                }
        }
        return null;
    }

    public JobManagerTenant addJobManagerTenantLegacy(String jobName, String
            subscriptionCode, List<Long> rateMatrixHeadIds
            , String billingMonth, String type, Long compKey) {
        EBillingByType billingType = EBillingByType.get(type);
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("subscriptionCode", subscriptionCode);
        requestMessage.put("billingMonth", billingMonth);
        requestMessage.put("type", type);
        switch (billingType) {
            case GENERATE:
            case POST_BILLING_CALCULATIONS:
                if (!rateMatrixHeadIds.isEmpty()) {
                    requestMessage.put("rateMatrixHeadIds", Joiner.on(", ").join(rateMatrixHeadIds));
                }
                break;
            case INVOICE:
                requestMessage.put("rateMatrixHeadIds", Joiner.on(", ").join(rateMatrixHeadIds));
                break;
            case PUBLISH_INVOICE:
                requestMessage.put("startTime", System.currentTimeMillis());
                requestMessage.put("BatchName", "PUBLISH_INVOICES_EMAIL");
                break;
        }
        return jobManagerService.add(jobName, requestMessage, EJobStatus.RUNNING.toString(), null, LOGGER);
    }

    @Override
    public JobManagerTenant addJobManagerTenant(String jobName, String subscriptionCode, List<String> variantIds,
                                                String billingMonth, String type, Long compKey) {
        EBillingByType billingType = EBillingByType.get(type);
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("subscriptionCode", subscriptionCode);
        requestMessage.put("billingMonth", billingMonth);
        requestMessage.put("type", type);
        switch (billingType) {
            case GENERATE:
            case POST_BILLING_CALCULATIONS:
                if (!variantIds.isEmpty()) {
                    requestMessage.put("rateMatrixHeadIds", Joiner.on(", ").join(variantIds));
                }
                break;
            case INVOICE:
                requestMessage.put("rateMatrixHeadIds", Joiner.on(", ").join(variantIds));
                break;
            case PUBLISH_INVOICE:
                requestMessage.put("startTime", System.currentTimeMillis());
                requestMessage.put("BatchName", "PUBLISH_INVOICES_EMAIL");
                break;
        }
        return jobManagerService.add(jobName, requestMessage, EJobStatus.RUNNING.toString(), null, LOGGER);
    }

    @Override
    public JobManagerTenant addJobManagerTenant(String jobName, ObjectNode requestMessage, String jobStatus) {
        return jobManagerService.add(jobName, requestMessage, jobStatus, null, LOGGER);
    }

    @Async
    @Override
    public void enqueJobBillingByType(String subscriptionCode, List<Long> rateMatrixHeadIds, List<String> variantIds,
                                      String billingMonth, Date date, String type, Long compKey, JobManagerTenant jobManager,
                                      Boolean isLegacy) throws Exception {
        EBillingByType billingType = EBillingByType.get(type);
        DBContextHolder.setLegacy(true);
        switch (billingType) {
            case CALCULATE:
                if (rateMatrixHeadIds.isEmpty()) {
                    billHeadAction.billingBySubscriptionType(subscriptionCode, billingMonth, type, jobManager, isLegacy);
                } else {
                    billHeadAction.billingBySubscriptionType(subscriptionCode, rateMatrixHeadIds, variantIds,
                            billingMonth, type, jobManager, isLegacy);
                }
                break;
            case INVOICE:
                billInvoiceService.invoiceByMatrixId(subscriptionCode, rateMatrixHeadIds, billingMonth, date, type,
                        jobManager, compKey);
                break;
            case INVOICE_PDF:
                billInvoiceService.generatePDFByMatrixId(subscriptionCode, rateMatrixHeadIds, billingMonth, type,
                        jobManager);
                break;
            case PUBLISH_INVOICE:
                String subscriptionRateMatrixIds =
                        Joiner.on(",").join(rateMatrixHeadIds.stream().map(id -> String.valueOf(id)).collect(Collectors.toList()));
                batchService.publishInvoiceByMonth(subscriptionCode, subscriptionRateMatrixIds, billingMonth, type,
                        jobManager);
                break;
            case POST_BILLING_CALCULATIONS:
                RuleHead ruleHead = ruleHeadRepository.findByBillingCodeAndRuleDependency("PICAL", subscriptionCode);
                if (ruleHead == null) {
                    throw new NotFoundException("RuleHead not found for action " + type);
                }
                PostBillingCalculation postBillingCalculation =
                        postBillingParserFactory.getParser(ruleHead.getMethod());
                if (rateMatrixHeadIds.isEmpty()) {
                    postBillingCalculation.calculate(subscriptionCode, billingMonth, ruleHead.getId(), type,
                            jobManager);
                } else {
                    postBillingCalculation.calculate(subscriptionCode, rateMatrixHeadIds, billingMonth,
                            ruleHead.getId(), type, jobManager);
                }
        }
    }

    //    @Async
    @Override
    public void enqueActivation(Long userAccountId, String subscriptionId, String startDate, JobManagerTenant
            jobManager, Boolean isLegacy) throws ParseException {
        if (isLegacy) {
            CustomerSubscription subscription = subscriptionService.findCustomerSubscriptionById(Long.parseLong(subscriptionId));
            if (subscription.getSubscriptionStatus().equals(ESubscriptionStatus.INACTIVE.getStatus())) {
                SubscriptionType subscriptionType =
                        subscriptionService.findSubscriptionTypeByCode(subscription.getSubscriptionType());
                if (subscriptionType.getGenerateCycle() != -1) {
                    subscriptionActivation.activate(userAccountId, subscriptionId, startDate, jobManager.getId(), true);
                } else if (subscriptionType.getGenerateCycle() == -1) {
                    subscriptionActivation.activateOnly(userAccountId, subscriptionId, startDate, jobManager.getId(), true);
                }
            }
        } else {
            SubscriptionMapping subscriptionMapping = dataExchange.getSubscriptionMapping(subscriptionId, DBContextHolder.getTenantName());
            if (subscriptionMapping.getSubscription().getActive().equalsIgnoreCase(ESubscriptionStatus.INACTIVE.getStatus())) { // TODO Change active type to string
                Variant variant = subscriptionMapping.getVariant();
                if (variant.getBillingCycle() != -1) {
                    subscriptionActivation.activate(userAccountId, subscriptionId, startDate, jobManager.getId(), false);
                } else if (variant.getBillingCycle() == -1) {
                    subscriptionActivation.activateOnly(userAccountId, subscriptionId, startDate, jobManager.getId(), false);
                }
            }
        }
        jobManagerService.setCompleted(jobManager, LOGGER);
    }

    @Override
    public ObjectNode generateBillsOnActivation(Long userAccountId, String subscriptionId, String start, Boolean isLegacy) throws ParseException {

        if (isLegacy) {
            CustomerSubscription subscription = subscriptionService.findCustomerSubscriptionById(Long.parseLong(subscriptionId));
            if (subscriptionService.findBySubscriptionRateMatrixIdAndRateCode(subscription.getSubscriptionRateMatrixId(),
                    SubscriptionRateCodes.GARDEN_START_DATE).getDefaultValue() == null) {
                return new ObjectMapper().createObjectNode()
                        .put("warning", "The garden is either inactive or scheduled for a future date");
            }
        } else {
            Variant variant = dataExchange.getSubscriptionMapping(subscriptionId,
                    DBContextHolder.getTenantName()).getVariant();
            MeasureType gardenStartDate =
                    variant.getMeasures().getByProduct().stream()
                            .filter(m -> m.getCode().equals(SubscriptionRateCodes.S_GARDEN_START_DATE))
                            .findFirst().orElse(null);
            if (gardenStartDate == null || gardenStartDate.getDefaultValue() == null) {
                return new ObjectMapper().createObjectNode().put("warning", "The garden is either inactive or scheduled for a future date");
            }
        }
        String message = checkForRunningJob(EJobName.ACTIVATION + "_" + userAccountId + "_" + subscriptionId);

        if (message != null) {
            return new ObjectMapper().createObjectNode().put("message", message);
        }

        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("userAccountId", userAccountId);
        requestMessage.put("subscriptionId", subscriptionId);
        requestMessage.put("type", "activate");

        JobManagerTenant jobManager = addJobManagerTenant(EJobName.ACTIVATION + "_" +
                userAccountId + "_" + subscriptionId, requestMessage, EJobStatus.RUNNING.toString());

        enqueActivation(userAccountId, subscriptionId, null, jobManager, isLegacy);

        return new ObjectMapper().createObjectNode().put("message", AppConstants.JOB_SUCCESS_SUBMISSION);
    }

    @Override
    public ObjectNode bulkActivation(String customToDate, String startDate, String variantId, Boolean isLegacy) throws ParseException {
        String message;
        message = checkForRunningJob(EJobName.ACTIVATION.toString());
        if (message != null) {
            return new ObjectMapper().createObjectNode().put("message", message);
        }
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("customToDate", customToDate);
        String jobName = EJobName.ACTIVATION.toString() + "-" + variantId;
        JobManagerTenant jobManager = addJobManagerTenant(jobName,
                requestMessage, EJobStatus.RUNNING.toString());
        enqueActivation(customToDate, startDate, variantId, jobManager, isLegacy);
        return new ObjectMapper().createObjectNode().put("message", AppConstants.JOB_SUCCESS_SUBMISSION);
    }

    @Async
    @Override
    public void enqueActivation(String customToDate, String startDate, String variantId, JobManagerTenant jobManager,
                                Boolean isLegacy) throws ParseException {
        LocalDate toDate;
        LocalDate period;
        if (customToDate != null && !customToDate.isEmpty()) {
            toDate = rateFunctions.parseDateFormat("yyyy-MM-dd", customToDate);
        } else {
            toDate = rateFunctions.systemDate();
        }
        if (isLegacy) {
            List<CustomerSubscription> subscriptions;
            if (variantId != null) {
                subscriptions = subscriptionService.findInactiveSubscriptionsTillTodayForGarden(
                        new SimpleDateFormat("yyyy-MM-dd").parse(toDate.toString()), Long.parseLong(variantId));
            } else {
                subscriptions = subscriptionService.findInactiveSubscriptionsTillToday(
                        new SimpleDateFormat("yyyy-MM-dd").parse(toDate.toString()));
            }
            subscriptions.forEach(subscription -> {
                try {
                    enqueActivation(subscription.getUserAccount().getAcctId(), String.valueOf(subscription.getId()), startDate, jobManager, isLegacy);
                } catch (ParseException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            });
        } else {
            VariantSubscriptionMapping subscriptions;
            period = rateFunctions.parseDateFormat("yyyy-MM-dd", startDate);
            if (variantId != null) {
                //Creating Calculation Tracker Item
//                    CalculationTracker calculationTracker = calculationTrackerService.addOrUpdate(CalculationTracker.builder()
//                            .refId(variantId)
//                            .calcRefType(AppConstants.REF_TYPE_VARIANT)
//                            .billingPeriod(rateFunctions.getMonthYear(period))
//                            .state(EJobStatus.RUNNING.toString()).build());
                subscriptions = dataExchange.getSubscriptionMappingsByVariantCode(variantId, DBContextHolder.getTenantName());
                subscriptions.getSubscriptions().stream().forEach(subscription -> {
                    try {
                        enqueActivation(subscription.getUserAccountId(),
                                String.valueOf(subscription.getId().getOid()), startDate, jobManager, isLegacy);
                    } catch (ParseException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                });

            }
//            else {
//                subscriptions = subscriptionService.findInactiveSubscriptionsTillToday(
//                        new SimpleDateFormat("yyyy-MM-dd").parse(toDate.toString()));
//            }

        }
        jobManagerService.setCompleted(jobManager, LOGGER);
    }

    @Async
    @Override
    public void billingByAction(Long billingHeadId, String action, JobManagerTenant jobManager, Boolean isLegacy) {
        BillingHead billingHead = billingHeadService.findById(billingHeadId);
        if (EBillingAction.get(action) == EBillingAction.CALCULATED || EBillingAction.get(action) == EBillingAction.RECALCULATE) {
            subscriptionActivation.generateBills(Arrays.asList(billingHead), null, isLegacy);
            LOGGER.info("Bill [{}] generated for subscription [{}]", billingHeadId, billingHead.getSubscriptionId());
        }
        jobManagerService.setCompleted(jobManager, LOGGER);
    }

    @Override
    public Map billingByActionV1(Map response, Long id, String action, Boolean isLegacy) {
        try {
            BillingHead billingHead = billingHeadService.findById(id);
            if (!billingHead.getBillStatus().equalsIgnoreCase(EBillStatus.INVOICED.getStatus())) {
                if (EBillingAction.get(action) == EBillingAction.CALCULATED || EBillingAction.get(action) == EBillingAction.RECALCULATE) {
                    CalculationDetails calculationDetails = calculationDetailsService.findBySourceId(billingHead.getId());
                    calculationDetails.setState(EBillStatus.PENDING.getStatus());
                    calculationDetailsService.addOrUpdate(calculationDetails);
                    response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), AppConstants.JOB_SUCCESS_SUBMISSION, null);
                }
            } else {
                response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), "Bill " + id + "is already invoiced", null);
            }
        } catch (Exception e) {
            response = Utility.generateResponseMap(response, HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error occurred", null);
        }
        return response;
    }

    @Async
    @Override
    public void bulkBillingByActionV1(List<Long> billingHeadIds, String action, Boolean isLegacy) {
        List<BillingHead> billingHeads = billingHeadService.findAllByIds(billingHeadIds);
        List<Long> billingHeadIdList = billingHeads.stream()
                .filter(billhead -> (billhead.getBillStatus().equalsIgnoreCase(EBillStatus.PENDING.getStatus())
                        || billhead.getBillStatus().equalsIgnoreCase(EBillStatus.CALCULATED.getStatus()))).map(BillingHead::getId).collect(Collectors.toList());
        if (EBillingAction.get(action) == EBillingAction.CALCULATED || EBillingAction.get(action) == EBillingAction.RECALCULATE) {
            billingHeads.stream().forEach(billingHead -> {
                billingHead.setBillStatus(EBillStatus.RECALCULATING.getStatus());
            });
            List<CalculationDetails> calculationDetailsList = calculationDetailsService.findAllBySourceIds(billingHeadIdList);
            calculationDetailsList.stream().forEach(calcDetail -> {
                calcDetail.setState(EBillStatus.PENDING.getStatus());
            });
            billingHeadService.saveAll(billingHeads);
            calculationDetailsService.saveAll(calculationDetailsList);
            batchService.runBillingADHOCBatch(EJobName.BULK_CALCULATE.name(), utility.getCompKey(), null);
        }
    }

    @Override
    public List<String> generateACHCSV(List<Long> subscriptionRateMatrixIdsCSV, Long ppdOrCcd, String
            billingMonthYear, Long compKey,
                                       String subscriptionCode, String userName) {

        List<String> blobUrls = new ArrayList<>();

        try {

            String subs = "";
            List<SubscriptionRateMatrixHead> subsRateMatrixHeads = null;
            MasterTenant company = masterTenantService.findByCompanyKey(compKey);

            if (subscriptionRateMatrixIdsCSV.size() == 1 && subscriptionRateMatrixIdsCSV.get(0) == -1) {
                subsRateMatrixHeads = subscriptionService.findSubscriptionRateMatrixHeadBySubscriptionCodeAndActive(subscriptionCode, true);
                subscriptionRateMatrixIdsCSV = subsRateMatrixHeads.stream().map(SubscriptionRateMatrixHead::getId).collect(Collectors.toList());
                subs = "ALL";
            } else {
                subsRateMatrixHeads = subscriptionService.findSubscriptionRateMatrixHeadsByIdsIn(subscriptionRateMatrixIdsCSV);
                subs = subsRateMatrixHeads.stream().map(SubscriptionRateMatrixHead::getSubscriptionTemplate)
                        .collect(Collectors.joining(";" + (char) 32));
            }

            StringBuilder groupName = new StringBuilder();
            if (subscriptionCode.equals("-1")) {
                List<String> subCodes = Lists.newArrayList("CSGF", "CSGR");
                List<SubscriptionType> subsCodes = subscriptionService.findSubscriptionTypeByCodeIn(subCodes);
                groupName.append(subsCodes.get(0).getAlias()).append(" and ").append(subsCodes.get(1).getAlias()).append(" Collections");

            } else {
                SubscriptionType subsCode = subscriptionService.findSubscriptionTypeByCode(subscriptionCode);
                groupName.append(subsCode.getAlias()).append(" Collections");
            }

            String[] fieldDetails = {"Novel Energy Services", "ACH Payments", groupName.toString(), subs,
                    company.getCompanyName(), userName};

            if (ppdOrCcd == 0 || ppdOrCcd == -1) {
                blobUrls.add(achPPDJob(subscriptionRateMatrixIdsCSV, billingMonthYear, fieldDetails));
            }

            if (ppdOrCcd == 1 || ppdOrCcd == -1) {
                blobUrls.add(achCCDJob(subscriptionRateMatrixIdsCSV, billingMonthYear, fieldDetails));
            }

        } catch (Exception ex) {
            ex.getStackTrace();
        }
        return blobUrls;
    }

    @Override
    public SubscriptionRateMatrixHead findGardenByBillIdLegacy(Long billHeadId) {
        BillingHead billingHead = billingHeadService.findById(billHeadId);
        CustomerSubscription customerSubscription = subscriptionService.findCustomerSubscriptionById(billingHead.getSubscriptionId() == null ?
                billingHead.getSubscription().getId() : billingHead.getSubscriptionId());
        return subscriptionService.findSubscriptionRateMatrixHeadById(customerSubscription.getSubscriptionRateMatrixId() == null ?
                customerSubscription.getSubscriptionRateMatrixHead().getId() : customerSubscription.getSubscriptionRateMatrixId());
    }

    @Override
    public SubscriptionMapping findVariantByBillId(Long billHeadId) {
        BillingHead billingHead = billingHeadService.findById(billHeadId);
        return dataExchange.getSubscriptionMapping(billingHead.getCustProdId(), DBContextHolder.getTenantName());
    }

    private String achPPDJob(List<Long> subscriptionRateMatrixIdsCSV, String billingMonthYear, String[]
            fieldDetails) {

        String blobUrl = "";
        JobManagerTenant jobManagerTenant = null;
        try {
            ObjectNode messageJson = new ObjectMapper().createObjectNode();

            jobManagerTenant =
                    jobManagerTenantService.add(EJobName.ACH_CSV_RESIDENT.toString(), null,
                            EJobStatus.RUNNING.toString(), null, LOGGER);
            messageJson.put("msg", "File Is In Process");

            List<ACHFileDTO> achCsvResidential = billingHeadRepository.generateACHCSV(subscriptionRateMatrixIdsCSV,
                    false, billingMonthYear);
            LOGGER.info("achCsvResidential data:" + achCsvResidential);
            blobUrl = downloadACHFile(achCsvResidential, "PPD(resident)", jobManagerTenant.getId(), fieldDetails);

            messageJson.put("msg", "File Generated Successfully");
            messageJson.put("blobUrl", blobUrl);
            jobManagerTenant.setRequestMessage(messageJson.toString());

            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), null, LOGGER);

        } catch (Exception ex) {
            LOGGER.error("ACH Residential Job Id:" + jobManagerTenant.getId(), ex);
        }
        return blobUrl;
    }

    private String achCCDJob(List<Long> subscriptionRateMatrixIdsCSV, String billingMonthYear, String[]
            fieldDetails) {

        String blobUrl = "";
        JobManagerTenant jobManagerTenant = null;
        try {
            ObjectNode messageJson = new ObjectMapper().createObjectNode();

            jobManagerTenant =
                    jobManagerTenantService.add(EJobName.ACH_CSV_COMMERCIAL.toString(), null,
                            EJobStatus.RUNNING.toString(), null, LOGGER);
            messageJson.put("msg", "File Is In process");
            List<ACHFileDTO> achCsvCommercial = billingHeadRepository.generateACHCSV(subscriptionRateMatrixIdsCSV,
                    true, billingMonthYear);
            LOGGER.info("achCsvCommercial data:" + achCsvCommercial);

            blobUrl = downloadACHFile(achCsvCommercial, "CCD(commercial)", jobManagerTenant.getId(), fieldDetails);
            messageJson.put("msg", "File Generated Successfully");
            messageJson.put("blobUrl", blobUrl);
            jobManagerTenant.setRequestMessage(messageJson.toString());

            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), null, LOGGER);

        } catch (Exception ex) {
            LOGGER.error("ACH Commercial Job Id:" + jobManagerTenant.getId(), ex);
        }
        return blobUrl;
    }

    private String downloadACHFile(List<ACHFileDTO> achCsvData, String ppdOrCcd, Long jobId, String[]
            fieldDetails) {

        String blobUrl = "";
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateTime = dateFormatter.format(new Date());
        String achFileName = "ACH_[" + ppdOrCcd + "]_REQID[" + jobId + "]_[" + currentDateTime + "].csv";

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            OutputStreamWriter osNew = new OutputStreamWriter(stream, "UTF-8");
            PrintWriter writer1 = new PrintWriter(osNew);
            CsvPreference preferences = new CsvPreference.Builder('"', ';', "\n").build();
            ICsvBeanWriter csvWriter = new CsvBeanWriter(writer1, preferences);

            String[] csvHeader = {"Account Number", "Amount", "Discretionary Data",
                    "Effective Date", "Identification", "Name", "Payment Information",
                    "Routing Transit", "Transaction Code", "Transaction Code(Custom)"};

            csvWriter.writeHeader("Tenant Name: " + fieldDetails[0]);
            csvWriter.writeHeader("Template Description: " + fieldDetails[1]);
            csvWriter.writeHeader("Group Name: " + fieldDetails[2]);
            csvWriter.writeHeader("Subscription: " + fieldDetails[3]);
            csvWriter.writeHeader("Company Name: " + fieldDetails[4]);
            csvWriter.writeHeader("Created By: " + fieldDetails[5]);
            csvWriter.writeHeader("");
            csvWriter.writeHeader(csvHeader);

            for (ACHFileDTO data : achCsvData) {
                String[] nameMapping = {"accountNumber", "amount", "discretionaryData",
                        "effectiveDate", "identification", "name", "paymentInformation",
                        "routingNumber", "transactionCode", "transactionCodeCustom"};
                try {
                    csvWriter.write(data, nameMapping);

                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }

            csvWriter.close();

            byte[] byteArray = stream.toByteArray();
            blobUrl = Utility.uploadToStorage(storageService, byteArray, appProfile, "tenant/" + utility.getCompKey()
                            + "/billing/ach", achFileName,
                    utility.getCompKey(), false);

        } catch (Exception ex) {
            LOGGER.error(ppdOrCcd, ex);
        }
        return blobUrl;
    }

    @Override
    public void fillTransStageTables(List<BillingHead> billingHeads) {
        List<TransStageHead> transStageHeadList = new ArrayList<>();

        List<Long> custSubIds = billingHeads.stream().map(BillingHead::getSubscriptionId).collect(Collectors.toList());
        List<String> subIds = customerSubscriptionRepository.findExtSubsIdListById(custSubIds).stream().distinct().collect(Collectors.toList());

        if (subIds.size() > 0) {
            List<TransStageTempDTO> transStageTempDTOList = dataExchange.getCustomerAndProductMeasuresBySubIds(String.join(",", subIds));
            transStageHeadList.addAll(saveTransStageHeadData(transStageTempDTOList, transStageHeadList));
            transStageTempDTOList.forEach(transStageTempDTO -> {
                transStageHeadList.stream().forEach(transStageHead -> {
                    if (transStageHead.getSubsId().equalsIgnoreCase(transStageTempDTO.getSubscriptionId())) {
                        List<TransStageTemp> transStageTempList = new ArrayList<>();
                        transStageTempList.addAll(saveTransStageTempData(transStageTempDTO.getByCustomer(), transStageHead, true));
                        transStageTempList.addAll(saveTransStageTempData(transStageTempDTO.getByProduct(), transStageHead, false));
                        transStageTempService.saveAll(transStageTempList);
                    }
                });
            });
        }
    }

    private List<TransStageHead> saveTransStageHeadData(List<TransStageTempDTO> transStageTempDTOList, List<TransStageHead> transStageHeadList) {
        transStageTempDTOList.stream().forEach(transStageTempDTO -> {
            TransStageHead transStageHeadTemp = transStageHeadService.findBySubsId(transStageTempDTO.getSubscriptionId());
            if (transStageHeadTemp == null) {
                transStageHeadList.add(TransStageHead.builder()
                        .varId(transStageTempDTO.getVariantId())
                        .subsId(transStageTempDTO.getSubscriptionId())
                        .parserCode(transStageTempDTO.getParserCode())
                        .status(EJobStatus.RUNNING.toString())
                        .build());
            } else {
                transStageHeadList.add(transStageHeadTemp);
            }

        });
        return transStageHeadService.saveAll(transStageHeadList);
    }

    private List<TransStageTemp> saveTransStageTempData(List<TransStageMeasureType> transStageMeasureTypeList, TransStageHead transStageHead, Boolean byCustomer) {
        List<TransStageTemp> transStageTempList = new ArrayList<>();
        transStageMeasureTypeList.forEach(transStageMeasureType -> {
            transStageTempList.add(TransStageTemp.builder()
                    .tJob_id(transStageHead.getTjobId())
                    .measId(transStageMeasureType.getId())
                    .measCode(transStageMeasureType.getCode())
                    .value(transStageMeasureType.getDefaultValue())
                    .format(transStageMeasureType.getFormat())
                    .level(transStageMeasureType.getLevel())
                    .seqNo(transStageMeasureType.getSeq())
                    .byCustomer(byCustomer)
                    .build());
        });
        return transStageTempList;
    }

    @Async
    @Override
    public void calculatePendingBillsInCalTracker(Long jobId, String period) {
        List<String> periodList = new ArrayList<>();
        if (period != null) {
            periodList = Arrays.stream(period.split(",")).filter(s -> s != null && !s.equalsIgnoreCase("null")).collect(Collectors.toList());
        }
        List<Long> billingHeadIds = periodList.size() > 0 ? calculationDetailsService.findAllByStatusAndPeriods(EBillStatus.PENDING.getStatus(),periodList)
                .stream().map(CalculationDetails::getSourceId).distinct().collect(Collectors.toList()) :
                calculationDetailsService.findAllByStatus(EBillStatus.PENDING.getStatus())
                        .stream().map(CalculationDetails::getSourceId).distinct().collect(Collectors.toList());
        List<BillingHead> billingHeads = billingHeadService.findAllByIds(billingHeadIds);
        billingHeads = billingHeads.stream().filter(billhead -> (billhead.getBillStatus().equals(EBillStatus.PENDING.getStatus())
                || billhead.getBillStatus().equals(EBillStatus.CALCULATED.getStatus()) || billhead.getBillStatus().equals(EBillStatus.RECALCULATING.getStatus()))).collect(Collectors.toList());
        fillTransStageTables(billingHeads);
        subscriptionActivation.generateBills(billingHeads, jobId, false);
        transStageTempService.deleteAll();

    }
}
