package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.azure.storage.StorageException;
import com.solar.api.AppConstants;
import com.solar.api.helper.Utility;
import com.solar.api.saas.service.integration.mongo.response.subscription.SubscriptionMapping;
import com.solar.api.saas.service.job.EJobStatus;
import com.solar.api.saas.service.widget.InfoService;
import com.solar.api.tenant.mapper.billing.billingHead.*;
import com.solar.api.tenant.model.billing.BillingInvoice.BillingInvoice;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.billingHead.EBillStatus;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHead;
import com.solar.api.tenant.repository.BillingHeadRepository;
import com.solar.api.tenant.service.BillingHeadService;
import com.solar.api.tenant.service.job.JobManagerTenantService;
import com.solar.api.tenant.service.process.billing.BillingService;
import com.solar.api.tenant.service.process.billing.EBillingByType;
import com.solar.api.tenant.service.process.billing.invoice.BillInvoiceService;
import com.solar.api.tenant.service.process.customer.CustomerPaymentInfo;
import com.solar.api.tenant.service.process.subscription.billHead.BillHeadAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.*;

import static com.solar.api.tenant.mapper.billing.billingHead.BillingHeadMapper.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("BillingHeadController")
@RequestMapping(value = "/billing")
public class BillingHeadController {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Value("${app.storage.blobService}")
    private String blobService;
    @Value("${app.storage.container}")
    private String storageContainer;
    @Value("${app.storage.tenantDirectory}")
    private String tenant;

    private final BillingHeadService billingHeadService;
    private final InfoService infoService;
    private final BillInvoiceService billInvoiceService;
    private final CustomerPaymentInfo customerPaymentInfo;
    private final BillHeadAction billHeadAction;

    private final BillingService billingService;

    private final JobManagerTenantService jobManagerTenantService;
    private final BillingHeadRepository billingHeadRepository;

    BillingHeadController(BillingHeadService billingHeadService, BillInvoiceService billInvoiceService,
                          InfoService infoService, CustomerPaymentInfo customerPaymentInfo,
                          BillHeadAction billHeadAction, BillingService billingService, JobManagerTenantService jobManagerTenantService,
                          BillingHeadRepository billingHeadRepository) {
        this.billingHeadService = billingHeadService;
        this.billInvoiceService = billInvoiceService;
        this.infoService = infoService;
        this.customerPaymentInfo = customerPaymentInfo;
        this.billHeadAction = billHeadAction;
        this.billingService = billingService;
        this.jobManagerTenantService = jobManagerTenantService;
        this.billingHeadRepository = billingHeadRepository;
    }

    @PostMapping("/billingHead")
    public BillingHeadDTO add(@RequestBody BillingHeadDTO billingHeadDTO, @RequestHeader("Comp-Key") Long compKey) {
        return toBillingHeadDTO(billingHeadService.addOrUpdateBillingHead(toBillingHead(billingHeadDTO)), false,
                blobService + "/" + storageContainer + "/" + tenant + "/" + compKey);
    }

    @PutMapping("/billingHead")
    public BillingHeadDTO update(@RequestBody BillingHeadDTO billingHeadDTO, @RequestHeader("Comp-Key") Long compKey) {
        return toBillingHeadDTO(billingHeadService.addOrUpdateBillingHead(toBillingHead(billingHeadDTO)), false,
                blobService + "/" + storageContainer + "/" + tenant + "/" + compKey);
    }

    @GetMapping("/billingHead/{id}")
    public BillingHeadDTO findById(@PathVariable Long id, @RequestHeader("Comp-Key") Long compKey) {
        return toBillingHeadDTO(billingHeadService.findById(id), false,
                blobService + "/" + storageContainer + "/" + tenant + "/" + compKey);
    }

    @GetMapping("/billingHead/user/{userId}")
    public List<BillingHeadDTO> findByUserAccountId(@PathVariable Long id, @RequestHeader("Comp-Key") Long compKey) {
        return toBillingHeadDTOs(billingHeadService.findByUserAccountId(id), false,
                blobService + "/" + storageContainer + "/" + tenant + "/" + compKey);
    }

    @GetMapping("/billingHead/subscriptionStatus/{subscriptionStatus}")
    public List<BillingHeadDTO> findBySubscriptionStatus(@PathVariable String subscriptionStatus, @RequestHeader(
            "Comp-Key") Long compKey) {
        return toBillingHeadDTOs(billingHeadService.findBySubscriptionStatus(subscriptionStatus), false,
                blobService + "/" + storageContainer + "/" + tenant + "/" + compKey);
    }

    @GetMapping("/billingHead/subscriptionId/{subscriptionId}")
    public List<BillingHeadDTO> findBySubscriptionId(@PathVariable Long subscriptionId,
                                                     @RequestHeader("Comp-Key") Long compKey) {
        return toBillingHeadDTOs(billingHeadService.findBySubscriptionIdFetchBillingDetails(subscriptionId), true,
                blobService + "/" + storageContainer + "/" + tenant + "/" + compKey);
    }

    /**
     * Description: New method to return calculation Tracker List
     *
     * @param tenantName
     * @param compKey
     * @return
     * @throws Exception
     */
    @GetMapping("/billingHead/v1/calcultionTrackerList")
    public Map calcultionTrackerList(@RequestHeader("Comp-Key") Long compKey) throws Exception {

        return billingHeadService.findBySubscriptionIdFetchBillingDetails();

    }

    @GetMapping("/billingHead/receivable")
    public Double findReceivableAggregate() {
        return infoService.getReceivableAggregate(EBillStatus.GENERATED.getStatus());
    }

    @GetMapping("/billingHead")
    public List<BillingHeadDTO> findAll(@RequestHeader("Comp-Key") Long compKey) {
        return toBillingHeadDTOs(billingHeadService.findAll(), false,
                blobService + "/" + storageContainer + "/" + tenant + "/" + compKey);
    }

    @GetMapping("/invoice/{billingHeadId}")
    public BillingInvoice generateInvoice(@PathVariable Long billingHeadId, @RequestHeader("Comp-Key") Long compKey,
                                          @RequestParam(value = "isLegacy", required = false, defaultValue = "true") Boolean isLegacy)
            throws Exception {

        String jobName = EBillingByType.INVOICE.getType();
        BillingHead billingHead = billingHeadService.findById(billingHeadId);
        SubscriptionRateMatrixHead subscriptionRateMatrixHead;
        SubscriptionMapping subscriptionMapping;
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("billingHeadId", billingHeadId);
        requestMessage.put("type", jobName);
        JobManagerTenant jobManagerTenant;
        if (isLegacy) {
            subscriptionRateMatrixHead = billingService.findGardenByBillIdLegacy(billingHeadId);
            jobManagerTenant = billingService.addJobManagerTenant(jobName + "_" + subscriptionRateMatrixHead.getSubscriptionCode() + "_"
                            + billingHead.getBillingMonthYear(), requestMessage,
                    EJobStatus.RUNNING.toString());
        } else {
            subscriptionMapping = billingService.findVariantByBillId(billingHeadId);
            jobManagerTenant = billingService.addJobManagerTenant(jobName + "_" + subscriptionMapping.getVariant().getCode() + "_"
                            + billingHead.getBillingMonthYear(), requestMessage,
                    EJobStatus.RUNNING.toString());
        }
        BillingInvoice billingInvoice = billInvoiceService.individualInvoice(billingHeadId, null, compKey);
        jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), null, LOGGER);
        return billingInvoice;
    }

    @GetMapping("/invoice/v1/{billingHeadId}")
    public Map generateInvoiceV1(@PathVariable Long billingHeadId, @RequestHeader("Comp-Key") Long compKey,
                                 @RequestParam(value = "isLegacy", required = false, defaultValue = "true") Boolean isLegacy) {
        Map response = new HashMap();
        if (billingHeadId != null && compKey != null) {
            return billInvoiceService.generateInvoiceV1(billingHeadId, compKey, isLegacy);
        } else {
            response = Utility.generateResponseMap(response, HttpStatus.PRECONDITION_FAILED.name(), "Parameters cannot be null", null);
        }
        return response;
    }

    @GetMapping("/invoice/v1/bulkGenerateInvoices")
    public Map generateInvoiceBulkV1(@RequestParam("billingHeadIds") String billingHeadIds, @RequestHeader("Comp-Key") Long compKey,
                                     @RequestParam(value = "isLegacy", required = false, defaultValue = "true") Boolean isLegacy) {
        Map response = new HashMap();
        if (billingHeadIds != null && compKey != null) {
            billInvoiceService.generateInvoiceBulkV1(billingHeadIds, compKey);
            response = Utility.generateResponseMap(response, HttpStatus.OK.name(), AppConstants.JOB_SUCCESS_SUBMISSION, null);
        } else {
            response = Utility.generateResponseMap(response, HttpStatus.PRECONDITION_FAILED.name(), "Parameters cannot be null", null);
        }
        return response;
    }

    @GetMapping("/invoice/{billingHeadId}/generate")
    public Object generateInvoicePDF(@PathVariable Long billingHeadId,
                                     @RequestParam(value = "isLegacy", required = false, defaultValue = "true") Boolean isLegacy)
            throws Exception {

        BillingHead billingHead = billingHeadService.findById(billingHeadId);
        SubscriptionRateMatrixHead subscriptionRateMatrixHead;
        SubscriptionMapping subscriptionMapping;
        JobManagerTenant jobManagerTenant;
        String jobName = EBillingByType.INVOICE_PDF.getType() + billingHeadId;
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("billingHeadId", billingHeadId);
        requestMessage.put("type", jobName);
        if (isLegacy) {
            subscriptionRateMatrixHead = billingService.findGardenByBillIdLegacy(billingHeadId);
            List<Long> subscriptionRateMatrixIds = new ArrayList<>();
            subscriptionRateMatrixIds.add(subscriptionRateMatrixHead.getId());
            billingService.checkForRunningJob(subscriptionRateMatrixIds, null, EBillingByType.INVOICE_PDF.getType(),
                    subscriptionRateMatrixHead.getSubscriptionCode(), billingHead.getBillingMonthYear(), true);
            jobManagerTenant = billingService.addJobManagerTenant(jobName + "_" + billingHeadId, requestMessage,
                    EJobStatus.RUNNING.toString());
        } else {
            subscriptionMapping = billingService.findVariantByBillId(billingHeadId);
            List<String> variantIds = new ArrayList<>();
            variantIds.add(subscriptionMapping.getVariant().getId().getOid());
            billingService.checkForRunningJob(null, variantIds, EBillingByType.INVOICE_PDF.getType(),
                    subscriptionMapping.getVariant().getCode(), billingHead.getBillingMonthYear(), false);
            jobManagerTenant = billingService.addJobManagerTenant(jobName + "_" + billingHeadId, requestMessage,
                    EJobStatus.RUNNING.toString());
        }
        BillingInvoice billingInvoice = billInvoiceService.generatePDF(billingHeadId);
        jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), null, LOGGER);
        return billingInvoice;
    }

    @GetMapping("/paymentTransaction/preprocess/download/{subscriptionType}/{subscriptionRateMatrixId}/{monthYear}")
    public ObjectNode getCustomerPrePaymentInfo(@PathVariable String subscriptionType,
                                                @PathVariable Long subscriptionRateMatrixId,
                                                @PathVariable String monthYear,
                                                @RequestHeader("Comp-Key") Long compKey) throws StorageException,
            IOException, URISyntaxException {
        String blobUrl = customerPaymentInfo.getCustomerPrePaymentInfo(subscriptionType, subscriptionRateMatrixId,
                monthYear, compKey);
        ObjectNode messageJson = new ObjectMapper().createObjectNode();
        messageJson.put("blobUrl", blobUrl);
        return messageJson;
    }

    //unpaid
    @GetMapping("/paymentTransaction/unpaid/paymentHeadDetail/{subscriptionType}/{billingMonthYear}")
    public PaymentTransactionHeadDetailMasterDTO getCustomerUnpaidInvoicePaymentDetailDTO(@PathVariable String subscriptionType,
                                                                                          @PathVariable String billingMonthYear) throws StorageException, IOException, URISyntaxException {
        List<PaymentTransactionHeadDetailDTO> paymentTransactionHeadDetailDTOList = billingHeadService.getUnpaidTransactionWithSubscriptionTypeAndMonth(subscriptionType, billingMonthYear);
        return PaymentTransactionHeadDetailMasterDTO.builder()
                .totalOutstandingAmount(paymentTransactionHeadDetailDTOList.stream()
                        .filter(ptd -> ptd.getOutstandingAmount() >= 0.0)
                        .mapToDouble(PaymentTransactionHeadDetailDTO::getOutstandingAmount).sum())
                .PaymentTransactionHeadDetailDTO(paymentTransactionHeadDetailDTOList).build();

    }

    //unreconciled records
    @GetMapping("/paymentTransaction/unreconciled/paymentHeadDetail/{subscriptionType}/{billingMonthYear}")
    public List<PaymentTransactionLineItemsDetailMasterDTO> getCustomerUnreconciledInvoicePaymentDetailDTO(@PathVariable String subscriptionType,
                                                                                                           @PathVariable String billingMonthYear) throws StorageException, IOException, URISyntaxException {
        return billingHeadService.getUnReconciledTransactionWithSubscriptionTypeAndMonth(subscriptionType, billingMonthYear);
    }

    // reverse records
    @GetMapping("/paymentTransaction/reverse/paymentHeadDetail/{subscriptionType}/{billingMonthYear}")
    public List<PaymentTransactionLineItemsDetailMasterDTO> getCustomerReverseInvoicePaymentDetailDTO(@PathVariable String subscriptionType,
                                                                                                      @PathVariable String billingMonthYear) throws StorageException, IOException, URISyntaxException {
        return billingHeadService.getReverseTransactionWithSubscriptionTypeAndMonth(subscriptionType, billingMonthYear);
    }

    // TODO: this will be modified for monthly graph
    @GetMapping("/paymentTransaction/graphData/{subscriptionType}/{billingYear}")
    public List<PaymentTransactionGraphDTO> getPaymentTransactionGraphData(@PathVariable String subscriptionType,
                                                                           @PathVariable String billingYear) {
        try {
            return billingHeadService.getPaymentGraphTransaction(subscriptionType, billingYear);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @GetMapping("/paymentTransaction/yearlyGraphData/{subscriptionType}/{billingYear}")
    public List<PaymentTransactionGraphDTO> getYearlyPaymentTransactionGraphData(@PathVariable String subscriptionType,
                                                                                 @PathVariable String billingYear) {
        try {
            return billingHeadService.getPaymentYearlyGraphTransaction(subscriptionType, billingYear);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @DeleteMapping("/billingHead/{id}")
    public ResponseEntity deleteById(@PathVariable Long id) {
        billingHeadService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/billingHead")
    public ResponseEntity deleteAll() {
        billingHeadService.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/invalidate/{billingHeadId}")
    public ObjectNode invalidateBillHead(@PathVariable Long billingHeadId, @RequestHeader("Comp-Key") Long compKey,
                                         @RequestParam(value = "isLegacy", required = false, defaultValue = "true") Boolean isLegacy)
            throws ParseException {
        ObjectNode messageJson = new ObjectMapper().createObjectNode();
        String response = billHeadAction.lockForInvalidation(billingHeadId, isLegacy);
        return messageJson.put("message", response);
    }

    @GetMapping("/skip/{billingHeadId}/{skipFlag}/{billSkip}")
    public ObjectNode skipBillHead(@PathVariable Long billingHeadId, @PathVariable Long skipFlag, @PathVariable Boolean billSkip) {
        ObjectNode messageJson = new ObjectMapper().createObjectNode();
        if (billingHeadId != null && skipFlag != null) {
            return messageJson.put("message", billingHeadService.skipBillHead(billingHeadId, skipFlag, billSkip));

        } else {
            return messageJson.put("message", "Parameters cannot be null");
        }
    }

    /**
     * Description: Method for bulk skipping/unskipping Bills
     *
     * @param billingHeadIds
     * @param skipFlag
     * @param billSkip
     * @return
     * @throws Exception
     */
    @GetMapping("/skipBulk/v1/{skipFlag}/{billSkip}")
    public Map bulkSkipBillHeadV1(@RequestParam("billingHeadIds") String billingHeadIds, @PathVariable Long skipFlag,
                                  @PathVariable Boolean billSkip) throws Exception {
        Map response = new HashMap();
        if (billingHeadIds != null && skipFlag != null) {
            billingHeadService.bulkSkipBillHeadV1(billingHeadIds, skipFlag, billSkip);
            response = Utility.generateResponseMap(response, HttpStatus.OK.name(), AppConstants.JOB_SUCCESS_SUBMISSION, null);
        } else {
            response = Utility.generateResponseMap(response, HttpStatus.PRECONDITION_FAILED.name(), "Parameters cannot be null", null);
        }
        return response;
    }

    @GetMapping("/publishInvoice/v1/{billHeadId}")
    public Map generatePublishInvoiceV1(@PathVariable("billHeadId") Long billHeadId, @RequestHeader("Comp-Key") Long compKey,
                                        @RequestParam(value = "isLegacy", required = false, defaultValue = "true") Boolean isLegacy) {
        Map response = new HashMap();
        if (billHeadId != null && compKey != null) {
            response = billInvoiceService.publishInvoice(response, billHeadId, isLegacy);
        } else {
            response = Utility.generateResponseMap(response, HttpStatus.PRECONDITION_FAILED.name(), "Parameters cannot be null", null);
        }

        return response;
    }

    @GetMapping("/publishBulkInvoice/v1")
    public Map generatePublishBulkInvoiceV1(@RequestParam("billingHeadIds") String billingHeadIds, @RequestHeader("Comp-Key") Long compKey,
                                            @RequestParam(value = "isLegacy", required = false, defaultValue = "true") Boolean isLegacy) {
        Map response = new HashMap();
        if (billingHeadIds != null && compKey != null) {
            billInvoiceService.publishBulkInvoice(billingHeadIds);
            response = Utility.generateResponseMap(response, HttpStatus.OK.name(), AppConstants.JOB_SUCCESS_SUBMISSION, null);
        } else {
            response = Utility.generateResponseMap(response, HttpStatus.PRECONDITION_FAILED.name(), "Parameters cannot be null", null);
        }
        return response;
    }

    //generate draft html
    @GetMapping("/billingInvoiceTest/{billHeadId}")
    public BillingInvoice billingInvoice(@PathVariable Long billHeadId, @RequestHeader("Comp-Key") Long compKey) {
        Date invoiceDate = Utility.getDate(new Date(), Utility.SYSTEM_DATE_FORMAT);
        BillingInvoice bi = null;
        try {
            BillingHead billingHead = billingHeadService.findById(billHeadId);
            billInvoiceService.generateDraftHTML(billingHead);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return bi;
    }


}
