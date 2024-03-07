package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.AppConstants;
import com.solar.api.helper.Utility;
import com.solar.api.helper.service.SubscriptionRateCodes;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.process.rule.RuleHead;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.saas.service.integration.mongo.response.subscription.MeasureType;
import com.solar.api.saas.service.integration.mongo.response.subscription.Variant;
import com.solar.api.saas.service.job.EJobName;
import com.solar.api.saas.service.job.EJobStatus;
import com.solar.api.saas.service.process.rule.RuleService;
import com.solar.api.tenant.model.billing.BillingInvoice.BillingInvoice;
import com.solar.api.tenant.model.billing.EBillingAction;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.billingHead.EBillStatus;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.service.BillingHeadService;
import com.solar.api.tenant.service.SubscriptionService;
import com.solar.api.tenant.service.process.billing.BillingService;
import com.solar.api.tenant.service.process.billing.invoice.BillInvoiceService;
import com.solar.api.tenant.service.process.billing.invoice.BillingUtilityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("BillingController")
public class BillingController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final SubscriptionService subscriptionService;
    private final RuleService ruleService;
    private final BillingService billingService;
    private final BillInvoiceService billInvoiceService;
    private final BillingUtilityService billingUtilityService;
    private final DataExchange dataExchange;
    @Lazy
    @Autowired
    private BillingHeadService billingHeadService;

    BillingController(SubscriptionService subscriptionService, RuleService ruleService, BillingService billingService,
                      BillInvoiceService billInvoiceService, BillingUtilityService billingUtilityService,
                      DataExchange dataExchange) {
        this.subscriptionService = subscriptionService;
        this.ruleService = ruleService;
        this.billingService = billingService;
        this.billInvoiceService = billInvoiceService;
        this.billingUtilityService = billingUtilityService;
        this.dataExchange = dataExchange;
    }

    @PostMapping("/ruleHead")
    public RuleHead addRuleHead(@RequestBody RuleHead ruleHead) {
        return ruleService.addOrUpdate(ruleHead);
    }

    // Bulk processing
    @PostMapping("/billingBySubscriptionType/{subscriptionCode}/{subscriptionRateMatrixIdsCSV}/{billingMonth}/{type}")
    public ObjectNode billingByType(@PathVariable("subscriptionCode") String subscriptionCode,
                                    @PathVariable(value = "subscriptionRateMatrixIdsCSV", required = false) String subscriptionRateMatrixIdsCSV,
                                    @PathVariable("billingMonth") String billingMonth,
                                    @PathVariable("type") String type,
                                    @RequestParam(value = "date", required = false) String dateString,
                                    @RequestHeader("Comp-Key") Long compKey,
                                    @RequestParam(value = "isLegacy", required = false, defaultValue = "true") Boolean isLegacy) throws Exception {
        List<Long> subscriptionRateMatrixIds = null;
        List<String> variantIds = null;
        String jobName;
        JobManagerTenant jobManager;
        if (isLegacy) {
            subscriptionRateMatrixIds =
                    Arrays.stream(subscriptionRateMatrixIdsCSV.split(",")).map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
            jobName = billingService.getJobNameLegacy(subscriptionRateMatrixIds, type, subscriptionCode, billingMonth);
            jobManager = billingService.addJobManagerTenantLegacy(jobName, subscriptionCode, subscriptionRateMatrixIds,
                    billingMonth, type, compKey);
        } else {
            variantIds =
                    Arrays.stream(subscriptionRateMatrixIdsCSV.split(",")).map(id -> id.trim()).collect(Collectors.toList());
            jobName = billingService.getJobName(variantIds, type, subscriptionCode, billingMonth);
            jobManager = billingService.addJobManagerTenant(jobName, subscriptionCode, variantIds, billingMonth, type, compKey);
        }
        String message;
//        message = billingService.checkForRunningJob(subscriptionRateMatrixIds, type, subscriptionCode, billingMonth);
//        if (message != null) {
//            ObjectNode messageJson = new ObjectMapper().createObjectNode();
//            messageJson.put("message", message);
//            return messageJson;
//        }
        Date date = null;
        if (dateString != null) {
            List<String> monthYear = Utility.getMonthYearFromDate(new Date());
            dateString = monthYear.get(0) + "-" + monthYear.get(1) + "-" + dateString;
            date = dateString != null ? new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT).parse(dateString) : null;
        }
        billingService.enqueJobBillingByType(subscriptionCode, subscriptionRateMatrixIds, variantIds, billingMonth,
                date, type, compKey, jobManager, isLegacy);
        ObjectNode messageJson = new ObjectMapper().createObjectNode();
        messageJson.put("message", AppConstants.JOB_SUCCESS_SUBMISSION);
        return messageJson;
    }

    // Get due date
    @GetMapping("/dueDate/{subscriptionType}/{billingMonth}/{dayOfMonth}")
    public ObjectNode getDueDate(@PathVariable("subscriptionType") String subscriptionType,
                                 @PathVariable("billingMonth") String billingMonth,
                                 @PathVariable(value = "dayOfMonth") String dayOfMonth) throws ParseException {
        List<String> monthYear = Utility.getMonthYearFromDate(new Date());
        dayOfMonth = monthYear.get(0) + "-" + monthYear.get(1) + "-" + dayOfMonth;
        Date date = dayOfMonth != null ? new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT).parse(dayOfMonth) : null;
        ObjectNode messageJson = new ObjectMapper().createObjectNode();
        messageJson.put("message",
                "Your estimated due date is " + Utility.readableDateFormat(billInvoiceService.getDueDate(subscriptionType, date),
                        Utility.INVOICE_SHORT_MONTH_DATE_FORMAT));
        return messageJson;
    }

    // Individual bill generation/regeneration
    @GetMapping("/billingById/{action}/{id}")
    public ObjectNode billingById(@PathVariable String action, @PathVariable Long id,
                                  @RequestHeader("Comp-Key") Long compKey,
                                  @RequestParam(value = "isLegacy", required = false, defaultValue = "true") Boolean isLegacy) {
        String message;
        message = billingService.checkForRunningJob(EBillingAction.get(action) == EBillingAction.CALCULATED ?
                "CALCULATED" : "RECALCULATE" + "_" + id);
        if (message != null) {
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("message", message);
            return messageJson;
        }
        ObjectNode messageJson = new ObjectMapper().createObjectNode();
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("billingHeadId", id);
        String jobName = EBillingAction.get(action) == EBillingAction.CALCULATED ? "CALCULATED" : "RECALCULATE";
        requestMessage.put("type", jobName);
        JobManagerTenant jobManager = billingService.addJobManagerTenant(jobName + "_" + id, requestMessage,
                EJobStatus.RUNNING.toString());
        billingService.billingByAction(id, action, jobManager, isLegacy);
        messageJson.put("message", AppConstants.JOB_SUCCESS_SUBMISSION);
        return messageJson;
    }

    /**
     * Individual bill calculate/recalculate
     *
     * @param action
     * @param id
     * @param compKey
     * @param isLegacy
     * @return
     */
    @GetMapping("/billingById/v1/{action}/{id}")
    public Map billingByIdV1(@PathVariable String action, @PathVariable Long id,
                             @RequestHeader("Comp-Key") Long compKey,
                             @RequestParam(value = "isLegacy", required = false, defaultValue = "false") Boolean isLegacy) {

        Map response = new HashMap();
        if (action == null && id == null) {
            response = Utility.generateResponseMap(response, HttpStatus.PRECONDITION_FAILED.toString(), "Parameters cannot be null", null);
        }else{
            response = billingService.billingByActionV1(response,id, action, isLegacy);
        }
        return response;
    }

    /**
     * bulk process of CALCULATED/RECALCULATE bills
     *
     * @param action
     * @param compKey
     * @param isLegacy
     * @param billHeadIds
     * @return
     */
    @GetMapping("/billingByIds/bulk/v1/{action}")
    public Map bulkBillingByActionV1(@PathVariable String action,
                              @RequestHeader("Comp-Key") Long compKey,
                              @RequestParam(value = "isLegacy", required = false, defaultValue = "false") Boolean isLegacy,
                              @RequestParam(value = "billHeadIds", required = true) String billHeadIds) {
        Map response = new HashMap();
        List<Long> rowIds =
                Arrays.stream(billHeadIds.split(",")).map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
        if (action == null && billHeadIds == null) {
            response = Utility.generateResponseMap(response, HttpStatus.PRECONDITION_FAILED.toString(), "Parameters cannot be null", null);
        } else {
            billingService.bulkBillingByActionV1(rowIds, action, isLegacy);
            response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), AppConstants.JOB_SUCCESS_SUBMISSION, null);
        }
        return response;
    }

    // Individual subscription activation and bill generation
    @GetMapping("/generateBillsOnActivation")
    public ObjectNode generateBillsOnActivation(@RequestParam("userAccountId") Long userAccountId,
                                                @RequestParam("subscriptionId") String subscriptionId,
                                                @RequestParam("start") String start,
                                                @RequestParam(value = "isLegacy", required = false, defaultValue =
                                                        "true") Boolean isLegacy) throws ParseException {
        return billingService.generateBillsOnActivation(userAccountId, subscriptionId, start, isLegacy);
    }

    // Bulk subscriptions activation
    @GetMapping("/activateSubscriptions")
    public ObjectNode bulkActivation(@RequestParam(value = "customToDate", required = false) String customToDate,
                                     @RequestParam(value = "startDate", required = false) String startDate,
                                     @RequestParam(value = "subscriptionRateMatrixId", required = false) String variantId,
                                     @RequestParam(value = "isLegacy", required = false, defaultValue = "true") Boolean isLegacy) throws
            ParseException {
        return billingService.bulkActivation(customToDate, startDate, variantId, isLegacy);
    }

    @GetMapping("/getAllBillingInvoices")
    public List<BillingInvoice> getAllBillingInvoices() {
        return billInvoiceService.getAll();
    }

    @GetMapping("/billingInvoiceTest/{billHeadId}")
    public BillingInvoice billingInvoice(@PathVariable Long billHeadId, @RequestHeader("Comp-Key") Long compKey) {
        Date invoiceDate = Utility.getDate(new Date(), Utility.SYSTEM_DATE_FORMAT);
        BillingInvoice bi = null;
        try {
            billingUtilityService.invoicing(billHeadId, invoiceDate, compKey);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return bi;
    }

    @GetMapping("/getBillingCustomerTypeGraphData")
    public Map getBillingCustomerTypeGraphData(@RequestParam("period") String period) {
        Map response = new HashMap();
        if (period == null) {
            response = Utility.generateResponseMap(response, HttpStatus.PRECONDITION_FAILED.toString(), "Parameters cannot be null", null);
        } else {
            List<String> periodList = null;
            if (period.contains(",")) {
                periodList = Arrays.stream(period.split(",")).map(id -> id.trim()).collect(Collectors.toList());
            } else {
                periodList = Arrays.asList(period);
            }
            response = billingHeadService.getCustomerTypeGroupByBillingStatusData(response,periodList);
        }
        return response;
    }

    @GetMapping("/getBillingStatusGraphData")
    public Map getBillingStatusGraph(@RequestParam("period") String period) {
        Map response = new HashMap();
        if (period == null) {
            response = Utility.generateResponseMap(response, HttpStatus.PRECONDITION_FAILED.toString(), "Parameters cannot be null", null);
        } else {
            List<String> periodList = null;
            if (period.contains(",")) {
                periodList = Arrays.stream(period.split(",")).map(id -> id.trim()).collect(Collectors.toList());
            } else {
                periodList = Arrays.asList(period);
            }
            response = billingHeadService.getBillingByStatusData(response,periodList);
        }
        return response;
    }

    @GetMapping("/getBillingStatusComparisonGraphData")
    public Map getBillingStatusComparisonGraphData() {
        Map response = new HashMap();
            List<String> periodList = null;
            response = billingHeadService.getBillingStatusComparisonData(response);

        return response;
    }
}
