package com.solar.api.tenant.service.process.billing.invoice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Utility;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.repository.workflow.WorkflowHookMasterRepository;
import com.solar.api.saas.service.PortalAttributeSAASService;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.saas.service.integration.mongo.response.subscription.SubscriptionMapping;
import com.solar.api.saas.service.job.EJobStatus;
import com.solar.api.saas.service.reporting.powerBI.ReportingService;
import com.solar.api.saas.service.workflow.HookValidator;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeValueTenantDTO;
import com.solar.api.tenant.model.billing.BillingInvoice.BillingInvoice;
import com.solar.api.tenant.model.billing.BillingInvoice.EInvoiceCategory;
import com.solar.api.tenant.model.billing.BillingInvoice.EInvoiceType;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.billingHead.EBillStatus;
import com.solar.api.tenant.model.billing.calculation.CalculationDetails;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import com.solar.api.tenant.model.payment.billing.PaymentTransactionHead;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHead;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRatesDerived;
import com.solar.api.tenant.repository.*;
import com.solar.api.tenant.service.*;
import com.solar.api.tenant.service.extended.document.DocuLibraryService;
import com.solar.api.tenant.service.job.JobManagerTenantService;
import com.solar.api.tenant.service.override.portalAttribute.PortalAttributeOverrideService;
import com.solar.api.tenant.service.preferences.TenantConfigService;
import com.solar.api.tenant.service.process.billing.BillingService;
import com.solar.api.tenant.service.process.billing.EBillingByType;
import com.solar.api.tenant.service.process.billing.EPaymentCode;
import com.solar.api.tenant.service.process.billing.publish.BillingInvoicePublishService;
import com.solar.api.tenant.service.process.billing.publish.PublishInfoService;
import com.solar.api.tenant.service.process.pvmonitor.ExtDataStageDefinitionService;
import com.solar.api.tenant.service.process.reporting.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
//@Transactional("masterTransactionManager")
public class BillInvoiceServiceImpl implements BillInvoiceService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private BillingHeadRepository billingHeadRepository;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private PortalAttributeSAASService attributeService;
    @Autowired
    private PortalAttributeOverrideService attributeOverrideService;
    @Autowired
    private JobManagerTenantService jobManagerTenantService;
    @Autowired
    private ReportingService reportingService;
    @Autowired
    private BillingInvoiceRepository billingInvoiceRepository;
    @Autowired
    private PaymentTransactionHeadRepository paymentTransactionHeadRepository;
    @Autowired
    private Generator generator;
    @Autowired
    private BillingService billingService;
    @Autowired
    private BillingHeadService billingHeadService;
    @Lazy
    @Autowired
    private BillInvoiceService billInvoiceService;
    @Autowired
    private CalculationTrackerService calculationTrackerService;
    @Autowired
    private DataExchange dataExchange;
    @Autowired
    private HookValidator hookValidator;
    @Autowired
    private CalculationDetailsService calculationDetailsService;
    @Autowired
    private TenantConfigService tenantConfigService;
    @Autowired
    private WorkflowHookMasterRepository workFlowHookMasterRepository;
    @Autowired
    private PublishInfoService publishInfoService;
    @Autowired
    private CustomerSubscriptionRepository customerSubscriptionRepository;
    @Autowired
    private BillingInvoicePublishService billingInvoicePublishService;
    @Autowired
    private Utility utility;
    @Autowired
    private ExtDataStageDefinitionService extDataStageDefinitionService;
    @Autowired
    private DocuLibraryService docuLibraryService;


    @Override
    public List<BillingInvoice> getAll() {
        return billingInvoiceRepository.findAll();
    }

    /*
            1) Recalculate
            2) Prompt for confirmation
            3) Invoice ID, (PDF)
            4) Bill status = Invoiced
            Invoicing will be in sequence
            Last bill id of the same subscription type should be invoiced
        */
    @Override
    public BillingInvoice invoice(BillingHead billingHead, Date invoiceDate) throws Exception {
        try {
            BillingHead lastBillingHead = billingHeadRepository.findLastBillHead(billingHead.getSubscriptionId(),
                    billingHead.getId());
            if (lastBillingHead != null &&
                    (lastBillingHead.getBillStatus().equals(EBillStatus.SCHEDULED.getStatus()) ||
                            lastBillingHead.getBillStatus().equals(EBillStatus.GENERATED.getStatus()))) {
                LOGGER.warn("The previous month's bill is still open for invoicing. " +
                        "Kindly invoice all the previous bills before proceeding with the current month.");
                return null;
            }
            if (!(billingHead.getBillStatus().equals(EBillStatus.GENERATED.getStatus()) || billingHead.getBillStatus().equals(EBillStatus.INVOICED.getStatus()))) {
                LOGGER.warn("The bill is already invoiced.");
                return null;
            }
            if (invoiceDate == null) {
                invoiceDate = new Date();
            }
            // find and replace invoice
            BillingInvoice billingInvoice;
            CustomerSubscription subscription;
            subscription = subscriptionService.findCustomerSubscriptionById(billingHead.getSubscriptionId());
            String subscriptionType = subscription.getSubscriptionType();
            /*List<SubscriptionRatesDerived> ratesDeriveds = subscriptionService.findBySubscriptionCodeAndCalcGroup
            (subscriptionType, "DUE_PERIOD");
            Date dueDate = null;
            invoiceDate = Utility.addMonths(invoiceDate, 1);
            if (!ratesDeriveds.isEmpty()) {
                int daysToAdd = ratesDeriveds.get(0).getValue().intValue();
                // If the due date is greater that or equal to 28th of February and the month is February then due
                date is equal to the end of the month
                // else if the due date is greater that or equal to 30th of the month and month is not February then
                the due date is 30th of the month
                Calendar cal = Calendar.getInstance();
                cal.setTime(invoiceDate);
                int totalDays = (cal.get(Calendar.DAY_OF_MONTH) + daysToAdd);
                if (totalDays >= 28 && cal.get(Calendar.MONTH) == 1) {
                    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                    dueDate = cal.getTime();
                } else if (totalDays >= 30 && cal.get(Calendar.MONTH) != 1) {
                    cal.set(Calendar.DAY_OF_MONTH, 30);
                    dueDate = cal.getTime();
                } else {
                    dueDate = Utility.addDays(invoiceDate, daysToAdd);
                }
            }*/
            Date dueDate = getDueDate(subscriptionType, invoiceDate);
            if (billingHead.getInvoice() != null) {
                billingInvoice = billingHead.getInvoice();
                billingInvoice.setType(EInvoiceType.MONTHLY.getType()); // MONTHLY (default), TRUEUP, SETTLEMENT,
                // REBATE, OTHERS
                billingInvoice.setCategory(EInvoiceCategory.INDIVIDUAL.getCategory()); // Individual, Corporate
                billingInvoice.setDateOfInvoice(invoiceDate);
                billingInvoice.setDueDate(dueDate);
                // billingInvoice.setPublishIndicator()
                // billingInvoice.setPublishDate()
                // billingInvoice.setPublishUrl() // = publish_indicator ? seturl of pdf : null
            } else {
                billingInvoice = BillingInvoice.builder()
                        .type(EInvoiceType.MONTHLY.getType()) // MONTHLY (default), TRUEUP, SETTLEMENT, REBATE, OTHERS
                        .category(EInvoiceCategory.INDIVIDUAL.getCategory()) // Individual, Corporate
                        .dateOfInvoice(invoiceDate)
                        .dueDate(dueDate)
                        // .publishIndicator()
                        // .publishDate()
                        // .publishUrl() // = publish_indicator ? seturl of pdf : null
                        .build();
            }

            billingHead.setInvoice(billingInvoice);
            billingHead.setInvoiceDate(invoiceDate);
            billingHead.setBillStatus(EBillStatus.INVOICED.getStatus());
            billingHead.setDueDate(dueDate);
            billingHead = billingHeadRepository.save(billingHead);
        } catch (Exception e) {
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("billing_head_id", billingHead.getId());
            LOGGER.error(messageJson.toPrettyString(), e);
        }
        return billingHead.getInvoice();
    }

    @Override
    public BillingInvoice invoiceV1(BillingHead billingHead, Date invoiceDate, SubscriptionMapping subscriptionMapping) {
        try {

            if (invoiceDate == null) {
                invoiceDate = new Date();
            }
            // find and replace invoice
            BillingInvoice billingInvoice;
            String subscriptionType = subscriptionMapping.getVariant().getCode();
            Date dueDate = getDueDate(subscriptionType, invoiceDate);
            if (billingHead.getInvoice() != null) {
                billingInvoice = billingHead.getInvoice();
                billingInvoice.setType(EInvoiceType.MONTHLY.getType()); // MONTHLY (default), TRUEUP, SETTLEMENT,

                billingInvoice.setCategory(EInvoiceCategory.INDIVIDUAL.getCategory()); // Individual, Corporate
                billingInvoice.setDateOfInvoice(invoiceDate);
                billingInvoice.setDueDate(dueDate);

            } else {
                billingInvoice = BillingInvoice.builder()
                        .type(EInvoiceType.MONTHLY.getType()) // MONTHLY (default), TRUEUP, SETTLEMENT, REBATE, OTHERS
                        .category(EInvoiceCategory.INDIVIDUAL.getCategory()) // Individual, Corporate
                        .dateOfInvoice(invoiceDate)
                        .dueDate(dueDate)
                        .build();
            }

            billingHead.setInvoice(billingInvoice);
            billingHead.setInvoiceDate(invoiceDate);
            billingHead.setBillStatus(EBillStatus.INVOICED.getStatus());
            billingHead.setDueDate(dueDate);
            billingHead = billingHeadRepository.save(billingHead);
            calculationTrackerService.updateBillingLogInvoice(billingHead.getId(), billingHead.getBillStatus(), billingHead.getInvoice().getId(), invoiceDate, dueDate);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return billingHead.getInvoice();
    }

    /**
     * Individual Invoicing
     *
     * @param billingHeadId
     * @param invoiceDate
     * @param compKey
     * @return
     * @throws Exception
     */
    @Override
    public BillingInvoice individualInvoice(Long billingHeadId, Date invoiceDate, Long compKey) throws Exception {
        BillingHead billingHead = billingHeadRepository.findById(billingHeadId).orElseThrow(() -> new NotFoundException(BillingHead.class, billingHeadId));
        BillingInvoice billingInvoice = invoice(billingHead, invoiceDate);
        if (billingHead.getInvoice() != null) {

//            PowerBi Invoice Template
//             String invoiceUrl = getInvoiceURL(billingHead);

            /**
             * Jasper Invoice Template
             * @param billingHeadId
             */
//            String invoiceUrl = generator.generatePDF(billingHead.getId(), compKey);
//            billingInvoice.setInvoiceUrl(invoiceUrl);
//            billingInvoiceRepository.save(billingInvoice);
            addPaymentTransactionHeadForInvoice(billingHead, billingInvoice.getId(),
                    getBillingCode(EPaymentCode.BILLING.getCode()), null);
        }
        return billingHead.getInvoice();
    }

    @Override
    public BillingInvoice individualInvoiceV1(BillingHead billingHead, Date invoiceDate, Long compKey, SubscriptionMapping subscriptionMapping) {

        BillingInvoice billingInvoice = new BillingInvoice();
        try {
            billingInvoice = invoiceV1(billingHead, invoiceDate, subscriptionMapping);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return billingInvoice;
    }


    @Override
    public List<BillingInvoice> invoiceByMatrixId(String subscriptionCode, List<Long> rateMatrixHeadIds,
                                                  String billingMonthYear, Date invoiceDate, String type,
                                                  JobManagerTenant jobManagerTenant, Long compKey) throws Exception {
        List<BillingInvoice> billingInvoices = new ArrayList<>();

        List<Long> lastMonthNotInvoiced = new ArrayList<>();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();

        //Get billingHeads loop
        for (BillingHead billingHead : findForInvoicing(billingMonthYear, subscriptionCode,
                rateMatrixHeadIds,
                Arrays.asList(EBillStatus.GENERATED.getStatus(), EBillStatus.INVOICED.getStatus()))) {
            BillingHead lastBillingHead = billingHeadRepository.findLastBillHead(billingHead.getSubscriptionId(),
                    billingHead.getId());
            if (lastBillingHead != null &&
                    (lastBillingHead.getBillStatus().equals(EBillStatus.SCHEDULED.getStatus()) ||
                            lastBillingHead.getBillStatus().equals(EBillStatus.GENERATED.getStatus()))) {
                lastMonthNotInvoiced.add(lastBillingHead.getSubscriptionId());
            }
            BillingInvoice billingInvoice = invoice(billingHead, invoiceDate);
            if (billingInvoice != null) {
                String invoiceUrl = null;
                try {

                    billingInvoices.add(billingInvoice);
                    /**
                     * Jasper Invoice
                     */
//                    invoiceUrl = generator.generatePDF(billingHead.getId(), compKey);
                    /**
                     * PowerBI Invoice
                     */
//                    invoiceUrl = getInvoiceURL(billingHead);
//                    billingInvoice.setInvoiceUrl(invoiceUrl);
//                    billingInvoiceRepository.save(billingInvoice);                 //send email async
                } catch (Exception e) {
                    ObjectNode messageJson = new ObjectMapper().createObjectNode();
                    messageJson.put("job_id", jobManagerTenant.getId());
                    messageJson.put("subscriptionCode", subscriptionCode);
                    messageJson.put("garden Ids", Joiner.on(", ").join(rateMatrixHeadIds));
                    messageJson.put("billingMonthYear", billingMonthYear);
                    messageJson.put("type", type);
                    LOGGER.error(messageJson.toPrettyString(), e);
                }
//                addPaymentTransactionHeadForInvoice(billingHead, billingInvoice.getId(),
//                        getBillingCode(EPaymentCode.BILLING.getCode()), jobManagerTenant.getId());
            }
        }
        String lastMonthNotInvoicedStrings = Joiner.on(", ").join(lastMonthNotInvoiced.stream().map(String::valueOf).collect(Collectors.toList()));
        responseMessage.put("Previous months' invoicing required for subscription ID(s): ", lastMonthNotInvoicedStrings);
        jobManagerTenant.setResponseMessage(responseMessage.toPrettyString());
        jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobManagerTenant.getBatchId(), LOGGER);
        return billingInvoices;
    }

    @Override
    public Date getDueDate(String subscriptionType, Date invoiceDate) {
        List<SubscriptionRatesDerived> ratesDeriveds =
                subscriptionService.findBySubscriptionCodeAndCalcGroup(subscriptionType, "DUE_PERIOD");
        Date dueDate = null;
        // invoiceDate = Utility.addMonths(invoiceDate, 1);
        if (!ratesDeriveds.isEmpty()) {
            int daysToAdd = ratesDeriveds.get(0).getValue().intValue();
            // If the due date is greater that or equal to 28th of February and the month is February then due date
            // is equal to the end of the month
            // else if the due date is greater that or equal to 30th of the month and month is not February then the
            // due date is 30th of the month
            Calendar cal = Calendar.getInstance();
            cal.setTime(invoiceDate);
            int totalDays = (cal.get(Calendar.DAY_OF_MONTH) + daysToAdd);
            if (totalDays >= 28 && cal.get(Calendar.MONTH) == 1) {
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                dueDate = cal.getTime();
            } else if (totalDays >= 30 && cal.get(Calendar.MONTH) != 1) {
                cal.set(Calendar.DAY_OF_MONTH, 30);
                dueDate = cal.getTime();
            } else {
                dueDate = Utility.addDays(invoiceDate, daysToAdd);
            }
        }
        return dueDate;
    }

    @Override
    public BillingInvoice findById(Long invoiceId) {
        return billingInvoiceRepository.findById(invoiceId).orElseThrow(() -> new NotFoundException(BillingInvoice.class, invoiceId));
    }

    @Override
    public List<BillingHead> findForInvoicing(String billingMonthYear, String subscriptionCode,
                                              List<Long> rateMatrixIds, List<String> billStatuses) {
        if (rateMatrixIds.isEmpty()) {
            return billingHeadRepository.findByMonthYearAndSubscriptionType(billingMonthYear, subscriptionCode,
                    billStatuses);
        } else {
            return billingHeadRepository.findByMonthYearAndRateMatrixIds(billingMonthYear, rateMatrixIds, billStatuses);
        }
    }

    @Override
    public void addPaymentTransactionHeadsForInvoices() {
        List<PaymentTransactionHead> transactionHeads = new ArrayList<>();
        try {
            billingHeadRepository.findAllWithoutPaymentTransactionHead().forEach(billingHead -> {
                PaymentTransactionHead transactionHead = getPaymentTransactionHeadForInvoice(billingHead,
                        getBillingCode(EPaymentCode.BILLING.getCode()));
                if (transactionHead != null) {
                    transactionHeads.add(transactionHead);
                }
            });
            paymentTransactionHeadRepository.saveAll(transactionHeads);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public BillingInvoice generatePDF(Long billingHeadId) {
        BillingHead billingHead = billingHeadRepository.findById(billingHeadId).orElseThrow(() -> new NotFoundException(BillingHead.class, billingHeadId));
        BillingInvoice billingInvoice = billingHead.getInvoice();
        if (billingInvoice != null) {
//            String invoiceUrl = generator.generatePDF(billingHead.getId(), compKey);
            String invoiceUrl = getInvoiceURL(billingHead);
            billingInvoice.setInvoiceUrl(invoiceUrl);
            billingInvoiceRepository.save(billingInvoice);
        }
        return billingInvoice;
    }

    @Override
    public List<BillingInvoice> generatePDFByMatrixId(String subscriptionCode, List<Long> rateMatrixHeadIds,
                                                      String billingMonthYear, String type,
                                                      JobManagerTenant jobManagerTenant) {
        List<BillingInvoice> billingInvoices = new ArrayList<>();
        //Get billingHeads loop
        for (BillingHead billingHead : findForInvoicing(billingMonthYear, subscriptionCode, rateMatrixHeadIds,
                Arrays.asList(EBillStatus.INVOICED.getStatus()))) {
            BillingInvoice billingInvoice = billingHead.getInvoice();
            String invoiceUrl = null;
            if (billingInvoice != null) {
                try {
                    billingInvoices.add(billingInvoice);
//                    invoiceUrl = generator.generatePDF(billingHead.getId(), compKey);
                    invoiceUrl = getInvoiceURL(billingHead);
                    billingInvoice.setInvoiceUrl(invoiceUrl);
                    billingInvoiceRepository.save(billingInvoice);
                    //send email async
                } catch (Exception e) {
                    ObjectNode messageJson = new ObjectMapper().createObjectNode();
                    messageJson.put("job_id", jobManagerTenant.getId());
                    messageJson.put("subscriptionCode", subscriptionCode);
                    messageJson.put("rateMatrixHeadIds", Joiner.on(", ").join(rateMatrixHeadIds));
                    messageJson.put("billingMonthYear", billingMonthYear);
                    messageJson.put("type", type);
                    messageJson.put("InvoiceUrl", invoiceUrl);
                    LOGGER.error(messageJson.toPrettyString(), e);
                }
            }
        }
        jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), null, LOGGER);
        return billingInvoices;
    }

    @Override
    public List<Long> findBySubscriptionTypesIn(List<String> subscriptionType) {
        return subscriptionService.findBySubscriptionTypesIn(subscriptionType);
    }

    private String getInvoiceURL(BillingHead billingHead) {
        CustomerSubscriptionMapping pnMapping =
                subscriptionService.findCustomerSubscriptionMappingByRateCodeAndSubscription("PN",
                        subscriptionService.findCustomerSubscriptionById(billingHead.getSubscriptionId()));
        return reportingService.generatePBInvoiceReport(billingHead, pnMapping.getValue());
    }

    private PaymentTransactionHead getPaymentTransactionHeadForInvoice(BillingHead billingHead, String billingCode) {
        billingCode = getBillingCode(billingCode);
        if (paymentTransactionHeadRepository.findByPaymentCodeAndInvoice(billingCode, billingHead.getInvoice()).isEmpty()) {
            try {
                return PaymentTransactionHead.builder()
                        .paymentCode(billingCode)
                        .custAccountId(billingHead.getUserAccountId())
                        .invoice(billingHead.getInvoice())
                        .subsId(billingHead.getSubscriptionId())
                        .net(0.00d)
                        .build();
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
        return null;
    }

    @Override
    public void addPaymentTransactionHeadForInvoice(BillingHead billingHead, Long billingInvoiceId,
                                                    String billingCode, Long jobId) {
        try {
            BillingInvoice billingInvoice = billingInvoiceRepository.findById(billingInvoiceId).get();
            billingCode = getBillingCode(billingCode);
            if (paymentTransactionHeadRepository.findByPaymentCodeAndInvoice(billingCode, billingInvoice).isEmpty()) {
                try {
                    paymentTransactionHeadRepository.save(PaymentTransactionHead.builder()
                            .paymentCode(billingCode)
                            .custAccountId(billingHead.getUserAccountId())
                            .invoice(billingInvoice)
//                            .subsId(billingHead.getSubscriptionId())
                            .net(0.00d)
                            .build());
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
        } catch (Exception e) {
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("job_id", jobId);
            messageJson.put("billing_head_id", billingHead.getId());
            messageJson.put("billing_invoice_id", billingInvoiceId);
            messageJson.put("billingCode", billingCode);
            LOGGER.error(messageJson.toPrettyString(), e);
        }
    }

    // from portal attr
    @Override
    public String getBillingCode(String billingCode) {
        String paymentCode = EPaymentCode.PAYMENT_CODE.getCode();
        List<PortalAttributeValueTenantDTO> attributes =
                attributeOverrideService.findByPortalAttributeName(paymentCode);
        return attributes.stream().filter(attrib -> attrib.getAttributeValue().equals(billingCode)).findFirst().orElse(null).getAttributeValue();
    }

    @Override
    public Map generateInvoiceV1(Long billingHeadId, Long compKey, Boolean isLegacy) {
        Map response = new HashMap();
        String jobName = EBillingByType.INVOICE.getType();
        SubscriptionRateMatrixHead subscriptionRateMatrixHead;
        SubscriptionMapping subscriptionMapping = new SubscriptionMapping();
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("billingHeadId", billingHeadId);
        requestMessage.put("type", jobName);
        JobManagerTenant jobManagerTenant;
        try {
            BillingHead billingHead = billingHeadService.findById(billingHeadId);
            if (!billingHead.getBillStatus().equals(EBillStatus.INVOICED.getStatus())) {
                if (isLegacy) {
                    subscriptionRateMatrixHead = billingService.findGardenByBillIdLegacy(billingHeadId);
                    jobManagerTenant = billingService.addJobManagerTenant(jobName + "_" + subscriptionRateMatrixHead.getSubscriptionCode() + "_"
                                    + billingHead.getBillingMonthYear(), requestMessage,
                            EJobStatus.RUNNING.toString());
                } else {
                    CustomerSubscription customerSubscription = customerSubscriptionRepository.findById(billingHead.getSubscriptionId()).get();
                    subscriptionMapping = dataExchange.getSubscriptionMapping(customerSubscription.getExtSubsId(), DBContextHolder.getTenantName());
                    if (subscriptionMapping != null) {

                        jobManagerTenant = billingService.addJobManagerTenant(jobName + "_" + subscriptionMapping.getVariant().getCode() + "_"
                                        + billingHead.getBillingMonthYear(), requestMessage,
                                EJobStatus.RUNNING.toString());
                        BillingHead lastBillingHead = billingHeadRepository.findLastBillHead(customerSubscription.getId(),
                                billingHead.getId());
                        if (lastBillingHead != null &&
                                (lastBillingHead.getBillStatus().equals(EBillStatus.PENDING.getStatus()) ||
                                        lastBillingHead.getBillStatus().equals(EBillStatus.CALCULATED.getStatus()))) {
                            response = Utility.generateResponseMap(response, HttpStatus.CONFLICT.name(), "Error The previous month's bill is still open for invoicing", null);

                        } else {
                            BillingInvoice billingInvoice = billInvoiceService.individualInvoiceV1(billingHead, null, compKey, subscriptionMapping);
                            if (billingInvoice != null) {
                                response = Utility.generateResponseMap(response, HttpStatus.OK.name(), "Invoice generated for Bill: " + billingHead.getId(), billingInvoice);
                            } else {
                                response = Utility.generateResponseMap(response, HttpStatus.EXPECTATION_FAILED.name(), "Cannot generate invoice for Bill: " + billingHead.getId(), null);
                            }
                            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), null, LOGGER);
                        }
                    } else {
                        response = Utility.generateResponseMap(response, HttpStatus.FAILED_DEPENDENCY.name(), "Error Invalid subscription information", null);
                    }
                }
            } else {
                response = Utility.generateResponseMap(response, HttpStatus.ALREADY_REPORTED.name(), "Error Bill is already invoiced", null);
            }

        } catch (Exception e) {
            e.getMessage();
        }

        return response;
    }

    /**
     * To generate invoice html after invoice
     *
     * @param billingHead
     */
    @Async
    @Override
    public void generateInvoiceHTML(BillingHead billingHead) {
        try {
            if (billingHead != null && billingHead.getUserAccountId() != null) {
                String[][] subjectParams = new String[][]{{billingHead.getInvoice().getId().toString(), billingHead.getBillingMonthYear(), billingHead.getId().toString()}};
                Map<String, String> placeholderValues = billingHeadService.getInvoicePlaceholders(billingHead);
                if (billingHead.getInvoice() != null && (placeholderValues.get("hookConstant") != null || !placeholderValues.get("hookConstant").isEmpty())) {
                    hookValidator.hookFinder(null, placeholderValues.get("hookConstant"), 1L, billingHead.getUserAccount().getAcctId(),
                            placeholderValues, billingHead, subjectParams);
                }
            }
        } catch (Exception ex) {
            CalculationDetails calculationDetailsDB = calculationDetailsService.findBySourceId(billingHead.getId());
            calculationDetailsDB.setErrorInd("Y");
            calculationDetailsDB.setErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR + ": Invoice html not generated.");
            calculationDetailsService.addOrUpdate(calculationDetailsDB);
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void generateDraftHTML(BillingHead billingHead) {
        LOGGER.info("Entering generateDraftHTML with Billing Head: ", billingHead.getId());
        try {
            if (billingHead != null && billingHead.getUserAccountId() != null) {
                String[][] subjectParams = new String[][]{{null, billingHead.getBillingMonthYear(), billingHead.getId().toString()}};
                Map<String, String> placeholderValues = billingHeadService.getInvoicePlaceholders(billingHead);
                if (placeholderValues.get("hookConstant") != null || !placeholderValues.get("hookConstant").isEmpty()) {
                    hookValidator.hookFinder(null, placeholderValues.get("hookConstant"), 1L, billingHead.getUserAccountId(),
                            placeholderValues, billingHead, subjectParams);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        LOGGER.info("Exiting generateDraftHTML with Billing Head: ", billingHead.getId());
        //logger.info("after execution of {}", joinPoint);
    }

    @Override
    @Async
    public void generateInvoiceBulkV1(String billingHeadIds, Long compKey) {
        Map response = new HashMap();
        List<Long> rowIds =
                Arrays.stream(billingHeadIds.split(",")).map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
        List<BillingHead> billingHeads = billingHeadService.findAllByIds(rowIds);
        billingHeads = billingHeads.stream().filter(billhead -> (billhead.getBillStatus().equals(EBillStatus.PENDING.getStatus())
                || billhead.getBillStatus().equals(EBillStatus.CALCULATED.getStatus()))).collect(Collectors.toList());
        try {
            billingHeads.stream().forEach(billingHead -> {
                SubscriptionMapping subscriptionMapping = new SubscriptionMapping();
                CustomerSubscription customerSubscription = customerSubscriptionRepository.findById(billingHead.getSubscriptionId()).get();
                subscriptionMapping = dataExchange.getSubscriptionMapping(customerSubscription.getExtSubsId(), DBContextHolder.getTenantName());
                if (subscriptionMapping != null) {
                    BillingHead lastBillingHead = billingHeadRepository.findLastBillHeadV1(billingHead.getCustProdId(),
                            billingHead.getId());
                    if (!(lastBillingHead != null &&
                            (lastBillingHead.getBillStatus().equals(EBillStatus.PENDING.getStatus()) ||
                                    lastBillingHead.getBillStatus().equals(EBillStatus.CALCULATED.getStatus())))) {
                        BillingInvoice billingInvoice = billInvoiceService.individualInvoiceV1(billingHead, null, compKey, subscriptionMapping);
                    }
                }
            });

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    @Override
    public Map publishInvoice(Map response, Long billHeadId, Boolean isLegacy) {
        ObjectNode publishString = new ObjectMapper().createObjectNode();
        try {
            BillingHead billingHead = billingHeadService.findById(billHeadId);
            CalculationDetails calculationDetails = calculationDetailsService.findBySourceId(billHeadId);
            publishString = billingInvoicePublishService.publishIndividualHTMLInvoice(billingHead, calculationDetails);
            if (publishString.get("message") != null) {
                response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), "Invoice Published Successfully", null);
                billingHead = getPublishIndicator(billingHead);
                billingHead.setBillStatus(EBillStatus.PUBLISHED.getStatus());
                calculationDetails.setState(EBillStatus.PUBLISHED.getStatus());
                calculationDetails.setPublishState("COMPLETED");
                billingHeadService.save(billingHead);
                calculationDetailsService.addOrUpdate(calculationDetails);

            } else {
                response = Utility.generateResponseMap(response, HttpStatus.NOT_IMPLEMENTED.name(), "Error Publishing Invoice", null);
            }
        } catch (Exception e) {
            response = Utility.generateResponseMap(response, HttpStatus.NOT_IMPLEMENTED.name(), "Error Publishing Invoice", null);
            LOGGER.error(e.getMessage(), e);
        }
        return response;
    }

    @Async
    @Override
    public void publishBulkInvoice(String billHeadIds) {
        List<Long> headIds =
                Arrays.stream(billHeadIds.split(",")).map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
        List<BillingHead> billingHeadList = billingHeadRepository.findAllById(headIds);
        ObjectNode publishString = null;
        if (billingHeadList.size() != 0) {
            for (BillingHead bh : billingHeadList) {
                try {
                    publishString = new ObjectMapper().createObjectNode();
                    CalculationDetails calculationDetails = calculationDetailsService.findBySourceId(bh.getId());
                    publishString = billingInvoicePublishService.publishIndividualHTMLInvoice(bh, calculationDetails);
                    if (publishString.get("message") != null) {
                        bh = getPublishIndicator(bh);
                        bh.setBillStatus(EBillStatus.PUBLISHED.getStatus());
                        calculationDetails.setPublishState("COMPLETED");
                        calculationDetails.setPublishState(EBillStatus.PUBLISHED.getStatus());
                        billingHeadService.save(bh);
                        calculationDetailsService.addOrUpdate(calculationDetails);
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    private BillingHead getPublishIndicator(BillingHead billingHead) {
        if (billingHead.getPublishIndicator() != null && billingHead.getPublishIndicator() < 2) {
            billingHead.setPublishIndicator(billingHead.getPublishIndicator() + 1);
        }
        if (billingHead.getPublishIndicator() == null) {
            billingHead.setPublishIndicator(0);
        }
        return billingHead;
    }

    @Override
    public void generateDraftHTMLProjection(List<BillingHead> billingHeads, List<String> months) {
        billingHeads.stream().forEach(billingHead -> {
            LOGGER.info("Entering generateDraftHTMLProjection with Billing Head: ", billingHead.getId());
            try {
                if (billingHead != null) {
                    String[][] subjectParams = new String[][]{{null, billingHead.getBillingMonthYear(), billingHead.getId().toString()}};
                    Map<String, String> placeholderValues = billingHeadService.getProjectionPlaceholders(billingHead, months);
                    if (placeholderValues.get("hookConstant") != null || !placeholderValues.get("hookConstant").isEmpty()) {
                        hookValidator.hookFinder(null, placeholderValues.get("hookConstant"), 1L, null,
                                placeholderValues, billingHead, subjectParams);
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            LOGGER.info("Exiting generateDraftHTMLProjection with Billing Head: ", billingHead.getId());
        });

    }

    @Override
    public void convertHTMLToPDF(List<BillingHead> billingHeadList) {
        billingHeadList.stream().forEach(billingHead -> {
            try {
                CalculationDetails calculationDetails = calculationDetailsService.findBySourceId(billingHead.getId());
                if (calculationDetails.getPrevInvHtmlView() != null) {
                    ExtDataStageDefinition extDataStageDefinition = extDataStageDefinitionService.findByCustomerSubscriptionId(billingHead.getSubscriptionId());
                    String monthYear = Utility.getCurrentMonthYear();
                    Long compKey = utility.getCompKey();
                    String fileName = Constants.PROJECTION_REVENUE.REVENUE_TYPE + "-" + extDataStageDefinition.getRefId() + "-" + monthYear + AppConstants.MIME_TYPE_PDF;
                    String url = dataExchange.htmlToPDFConversion(calculationDetails.getPrevInvHtmlView(), fileName, compKey);
                    String refType = Constants.PROJECTION_REVENUE.CODE_REF_TYPE;
                    DocuLibrary existingDocument = docuLibraryService.findByCodeRefIdAndCodeRefTypeAndNotes(extDataStageDefinition.getRefId(), refType, monthYear);
                    if (existingDocument == null) {
                        existingDocument = new DocuLibrary();
                    }
                    existingDocument.setDocuName(fileName);
                    existingDocument.setUri(url);
                    existingDocument.setCodeRefId(extDataStageDefinition.getRefId());
                    existingDocument.setCodeRefType(refType);
                    existingDocument.setNotes(monthYear);
                    existingDocument.setCompKey(compKey);
                    existingDocument.setFormat("pdf");
                    existingDocument.setVisibilityKey(true);
                    docuLibraryService.saveOrUpdate(existingDocument);
                }

            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
    }
}
