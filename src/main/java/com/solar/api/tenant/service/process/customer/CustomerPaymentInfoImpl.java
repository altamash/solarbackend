package com.solar.api.tenant.service.process.customer;

import com.microsoft.azure.storage.StorageException;
import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.helper.Utility;
import com.solar.api.saas.service.StorageService;
import com.solar.api.tenant.mapper.billing.billingHead.PaymentTransactionHeadDetailDTO;
import com.solar.api.tenant.mapper.billing.billingHead.PaymentTransactionLineItemsDetailDTO;
import com.solar.api.tenant.mapper.billing.billingHead.PaymentTransactionPreprocess;
import com.solar.api.tenant.mapper.payment.info.PaymentInfoTemplate;
import com.solar.api.tenant.mapper.payment.info.PaymentMessageDTO;
import com.solar.api.tenant.mapper.payment.info.PaymentModeDTO;
import com.solar.api.tenant.mapper.payment.info.PaymentTransactionSummaryTemplate;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.billingHead.EBillStatus;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import com.solar.api.tenant.model.payment.billing.PaymentTransactionDetail;
import com.solar.api.tenant.model.payment.billing.PaymentTransactionHead;
import com.solar.api.tenant.model.payment.info.PaymentMode;
import com.solar.api.tenant.repository.*;
import com.solar.api.tenant.service.BillingHeadService;
import com.solar.api.tenant.service.extended.document.DocuLibraryService;
import com.solar.api.tenant.service.preferences.EConfigParameter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
//@Transactional("masterTransactionManager")
public class CustomerPaymentInfoImpl implements CustomerPaymentInfo {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String SUCCESS_RESPONSE = "SUCCESS";
    private static final String FAILURE_RESPONSE = "FAILURE";
    private static final String WARN_RESPONSE = "File not uploaded";
    private static final String DETAIL_ID_NULL = "Detail id is null";


    @Autowired
    private PaymentTransactionHeadRepository headRepository;
    @Autowired
    private PaymentTransactionDetailRepository detailRepository;
    @Lazy
    @Autowired
    private BillingHeadService billingHeadService;
    @Autowired
    private StorageService storageService;
    @Value("${app.profile}")
    private String appProfile;
    @Autowired
    private Utility utility;
    @Autowired
    private TenantConfigRepository tenantConfigRepository;
    @Autowired
    private PaymentModeRepository paymentModeRepository;
    @Autowired
    private ConversationHistoryRepository conversationHistoryRepository;
    @Autowired
    private DocuLibraryService docuLibraryService;
//    @Autowired
//    private static final ThreadLocal<List<Long>> invoiceBillingHeadLockedStatus = new ThreadLocal<>();

    // Find unpaid invoices data
    @Override
    public String getCustomerPrePaymentInfo(String subscriptionType, Long subscriptionRateMatrixId, String monthYear,
                                            Long compKey) throws IOException, URISyntaxException, StorageException {
        List<PaymentTransactionPreprocess> transactionPreprocesses;
        if (subscriptionRateMatrixId == -1) {
            transactionPreprocesses =
                    billingHeadService.getPreprocessTransactionWithSubscriptionType(subscriptionType, monthYear);
        } else {
            transactionPreprocesses =
                    billingHeadService.getPreprocessTransactionWithSubscriptionRateMatrixId(subscriptionRateMatrixId,
                            monthYear);
        }
        byte[] byteArray = createCsvContent(transactionPreprocesses);
        try (ByteArrayOutputStream os = new ByteArrayOutputStream(byteArray.length)) {
            os.write(byteArray, 0, byteArray.length);
            try (ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray())) {
                return storageService.uploadInputStream(is, (long) os.size(), appProfile,
                        "tenant/" + utility.getCompKey()
                                + "/billing/payment_transaction/preprocess",
                        "CustomerPayments-" + subscriptionType +
                                (subscriptionRateMatrixId == -1 ? "" : "-" + subscriptionRateMatrixId) +
                                "-" + monthYear + ".csv", compKey, false);
            }
        }
    }

    @Override
    public Double paymentBalance(Long billingHeadId) {
        return headRepository.getPaymentBalance(billingHeadId);
    }

    @Override
    public PaymentTransactionSummaryTemplate getPaymentTransactionSummary(Long billingHeadId) {
        PaymentTransactionSummaryTemplate template = headRepository.getPaymentTransactionSummary(billingHeadId);
        int rounding = utility.getCompanyPreference().getRounding();
        template.setBilledAmount(utility.round(template.getBilledAmount(), rounding));
        template.setAmountAlreadyPaid(utility.round(template.getAmountAlreadyPaid(), rounding));
        template.setPaymentAmount(utility.round(template.getPaymentAmount(), rounding));
        template.setBalance(utility.round(template.getBalance(), rounding));
        return template;
    }

    @Override
    public String processPayment(Long billingHeadId, Double amount, Date tranDate) {
        try {
            BillingHead billingHead = billingHeadService.findById(billingHeadId);
            Double paymentBalance = utility.round(paymentBalance(billingHeadId),
                    utility.getCompanyPreference().getRounding());
            PaymentTransactionHead transactionHead = null;
            if (amount.doubleValue() <= paymentBalance.doubleValue()) {
                transactionHead = headRepository.findByInvoice(billingHead.getInvoice());
                PaymentTransactionDetail transactionDetail = PaymentTransactionDetail.builder()
                        .paymentTransactionHead(transactionHead)
                        .amt(amount)
                        .lineSeqNo((long) (transactionHead.getPaymentTransactionDetails().size() + 1))
                        .tranDate(tranDate)
                        .build();
                detailRepository.save(transactionDetail);
                transactionHead.setNet(transactionHead.getNet() + amount);
                headRepository.save(transactionHead);
            }
            if (amount.doubleValue() == paymentBalance.doubleValue()) {
                billingHead.setBillStatus(EBillStatus.PAID.getStatus());
                billingHeadService.addOrUpdateBillingHead(billingHead);
            }
        } catch (Exception e) {
            return FAILURE_RESPONSE;
        }
        return SUCCESS_RESPONSE;
    }

    //@Async
    @Override
    //jobid will come from scheduler job
    //for new payment service
    public String processPayment(List<PaymentInfoTemplate> paymentInfoTemplates, String jobId, List<MultipartFile> multipartFiles, String subscriptionType) {
        int i = -1;
        String processType = "payment";
        DocuLibrary docuLibrary = null;
        List<PaymentMessageDTO> paymentMessageDTOs = new ArrayList<PaymentMessageDTO>();
        Integer companyRoundOffPreference = utility.getCompanyPreference().getRounding();
        //List<Long> billingHeadIds = billingHeadService.findByPaymentLockedInd(true);
        //paymentInfoTemplates = processBillingHeadLocking(paymentInfoTemplates, true,billingHeadIds,processType);
        for (PaymentInfoTemplate paymentInfoTemplate : paymentInfoTemplates) {
            try {
                Optional<PaymentMode> paymentModeOptional = paymentModeRepository.findByPaymentMode(paymentInfoTemplate.getPaymentMode().toUpperCase().trim());
                Long paymentModeId = paymentModeOptional.isPresent() ? paymentModeOptional.get().getId() : null;
                Long billingHeadId = paymentInfoTemplate.getHeadId();
                Double paymentAmount = paymentInfoTemplate.getPaymentAmount();
                Date paymentDate = paymentInfoTemplate.getPaymentDate();
                //for cash we have direct reconciled status
                String reconStatus = Constants.PAYMENT_STATUS.paid;
                BillingHead billingHead = billingHeadService.findById(billingHeadId);
                Double paymentBalance = utility.round(paymentBalance(billingHeadId), companyRoundOffPreference);
                PaymentTransactionHead transactionHead = null;
                PaymentTransactionDetail transactionDetail = null;
                if (getAmountRoundOffBasedOnCompanyPreference(companyRoundOffPreference, paymentInfoTemplate.getPaymentAmount()).doubleValue() <=
                        paymentBalance.doubleValue()) {
                    transactionHead = headRepository.findByInvoiceFetchDetail(billingHead.getInvoice());
                    transactionDetail = PaymentTransactionDetail.builder()
                            .paymentTransactionHead(transactionHead)
                            .amt(paymentAmount)
                            .lineSeqNo((long) (transactionHead.getPaymentTransactionDetails().size() + 1))
                            .tranDate(paymentDate)
                            .notes(paymentInfoTemplate.getNotes())
                            .issuer(paymentInfoTemplate.getPaymentMode())
                            .issuerId(String.valueOf(paymentModeId))
                            .referenceId(paymentInfoTemplate.getReferenceId())
                            .batchNo(getNextBatchNo(subscriptionType))
                            .build();
                    if (paymentInfoTemplate.getPaymentMode().equalsIgnoreCase(Constants.PAYMENT_MODES.cash)) {
                        transactionDetail.setStatus(Constants.INVOICE_STATUS.paid_reconciled); //reconciled or unreconciled
                        transactionDetail.setIssuerReconStatus(Constants.PAYMENT_STATUS.completed); //completed, paid etc
                    } else {
                        transactionDetail.setStatus(Constants.INVOICE_STATUS.paid_unreconciled); //reconciled or unreconciled
                        transactionDetail.setIssuerReconStatus(reconStatus); //completed, paid etc
                    }
                    transactionDetail = detailRepository.save(transactionDetail);
                    if (transactionDetail.getPayDetId() != null)
                        paymentInfoTemplate.setPaymentDetailId(transactionDetail.getPayDetId());

                    if (paymentInfoTemplate.getIsAttachment()) {
                        i++;
                        docuLibrary = doAttachmentToPaymentDetailItem(multipartFiles, i);
                        //if file not uploaded then add msg
                        if (docuLibrary == null) {
                            paymentMessageDTOs.add(addMessage(paymentInfoTemplate, WARN_RESPONSE, Constants.MessageTypes.WARNING));
                        }
                    }

                    transactionHead.setNet(transactionHead.getNet() + paymentAmount);
                    transactionHead.setJobId(jobId);
                    headRepository.save(transactionHead);
                    if (docuLibrary != null) {
                        docuLibrary.setCodeRefId(String.valueOf(transactionDetail.getPayDetId()));
                        docuLibraryService.saveOrUpdate(docuLibrary);
                    }
                    //LOGGER.info("processPayment=" + transactionDetail.getPayDetId() + " " + transactionDetail.getStatus());
                    if ((utility.round(paymentAmount, companyRoundOffPreference)).doubleValue() == paymentBalance.doubleValue() &&
                            reconStatus.equalsIgnoreCase(Constants.PAYMENT_STATUS.completed)) {
                        billingHead.setBillStatus(EBillStatus.PAID.getStatus());
                        billingHeadService.addOrUpdateBillingHead(billingHead);
                    }
                } else {
                    LOGGER.error("can't processPayment=" + " " + transactionDetail.getStatus());
                    paymentMessageDTOs.add(addMessage(paymentInfoTemplate, FAILURE_RESPONSE, Constants.MessageTypes.ERROR));
                    continue;
                }
            } catch (Exception e) {
                LOGGER.error("can't processPayment=" + e.getStackTrace());
                //  processBillingHeadUnLocking(paymentInfoTemplates, false, processType);
                paymentMessageDTOs.add(addMessage(paymentInfoTemplate, e.getMessage(), Constants.MessageTypes.ERROR));
                continue;
            }
            if (!(paymentMessageDTOs.stream().anyMatch(p -> p.getPaymentDetailId().equals(paymentInfoTemplate.getPaymentDetailId())))) {
                paymentMessageDTOs.add(addMessage(paymentInfoTemplate, SUCCESS_RESPONSE, Constants.MessageTypes.MESSAGE));
            }
        }
        //processBillingHeadUnLocking(paymentInfoTemplates,false,processType);
        LOGGER.info("process payment detail response=" + paymentMessageDTOs);

        return getMessage(paymentMessageDTOs);
    }

    //    @Async
    @Override
    //jobid will come from scheduler job
    public String reconcilePayment(List<PaymentInfoTemplate> paymentInfoTemplates, String jobId) {
        String processType = "reconcile";
        List<PaymentMessageDTO> paymentMessageDTOs = new ArrayList<PaymentMessageDTO>();
        //  List<PaymentInfoTemplate> paymentInfoTemplatesTemp = paymentInfoTemplates;
        // List<Long> billingHeadIds = billingHeadService.findByReconcileLockedInd(true);
        // paymentInfoTemplates = processBillingHeadLocking(paymentInfoTemplates, true,billingHeadIds,processType);
        for (PaymentInfoTemplate paymentInfoTemplate : paymentInfoTemplates) {
            try {
                Long billingHeadId = paymentInfoTemplate.getHeadId();
                Double paymentAmount = paymentInfoTemplate.getPaymentAmount();
                //for cash we have direct reconciled status
                BillingHead billingHead = billingHeadService.findById(billingHeadId);
                PaymentTransactionHead transactionHead = null;
                PaymentTransactionDetail transactionDetail = null;
                if (paymentInfoTemplate.getPaymentDetailId() != null) {
                    transactionHead = headRepository.findByInvoiceFetchDetail(billingHead.getInvoice());
                    Optional<PaymentTransactionDetail> transactionDetailReconciledOptional = transactionHead.getPaymentTransactionDetails().stream().filter(ptd -> ptd.getPayDetId().equals(paymentInfoTemplate.getPaymentDetailId())).findFirst();
                    if (transactionDetailReconciledOptional.isPresent()) {
                        transactionDetail = transactionDetailReconciledOptional.get();
                        if (paymentInfoTemplate.getStatus().equalsIgnoreCase(Constants.PAYMENT_STATUS.completed)) {
                            transactionDetail.setStatus(Constants.INVOICE_STATUS.paid_reconciled); //reconciled or unreconciled
                            transactionDetail.setIssuerReconStatus(Constants.PAYMENT_STATUS.completed); //completed, paid etc
                        } else {
                            transactionDetail.setStatus(Constants.INVOICE_STATUS.paid_unreconciled); //reconciled or unreconciled
                            transactionDetail.setIssuerReconStatus(paymentInfoTemplate.getStatus()); //completed, paid etc
                        }
                        // detailRepository.save(transactionDetail);
                        LOGGER.info("reconcilePayment=" + transactionDetail.getPayDetId() + " " + transactionDetail.getStatus());
                        if (paymentInfoTemplate.getStatus().equalsIgnoreCase(Constants.PAYMENT_STATUS.failed)) {
                            transactionHead.setNet(transactionHead.getNet() - paymentAmount);
                            transactionHead.setJobId(jobId);
                        }
                        headRepository.save(transactionHead);

                    }
                } else {
                    //processBillingHeadUnLocking(paymentInfoTemplates, false, processType);
                    paymentMessageDTOs.add(addMessage(paymentInfoTemplate, FAILURE_RESPONSE, Constants.MessageTypes.MESSAGE));
                    continue;
                }
            } catch (Exception e) {
                LOGGER.error("ReconcilePayment=" + e.getMessage());
                //  processBillingHeadUnLocking(paymentInfoTemplates, false, processType);
                paymentMessageDTOs.add(addMessage(paymentInfoTemplate, e.getMessage(), Constants.MessageTypes.ERROR));
                continue;
            }
            //if not any warning msg added for this detail then add success msg
            if (!(paymentMessageDTOs.stream().anyMatch(p -> p.getPaymentDetailId().equals(paymentInfoTemplate.getPaymentDetailId())))) {
                paymentMessageDTOs.add(addMessage(paymentInfoTemplate, SUCCESS_RESPONSE, Constants.MessageTypes.MESSAGE));
            }
        }
        // processBillingHeadUnLocking(paymentInfoTemplates,false,processType);
        return getMessage(paymentMessageDTOs);
    }

    //    @Async
    @Override
    public String processReverse(List<PaymentInfoTemplate> paymentInfoTemplates, List<MultipartFile> multipartFiles) {
        int i = -1;
        DocuLibrary docuLibrary = null;
        List<PaymentMessageDTO> paymentMessageDTOs = new ArrayList<PaymentMessageDTO>();
        String processType = "reverse";
        // List<Long> billingHeadIds = billingHeadService.findByReverseLockedInd(true);
        // paymentInfoTemplates = processBillingHeadLocking(paymentInfoTemplates, true,billingHeadIds,processType);
        for (PaymentInfoTemplate paymentInfoTemplate : paymentInfoTemplates) {
            try {
                Long billingHeadId = paymentInfoTemplate.getHeadId();
                String reconStatus = Constants.PAYMENT_STATUS.reversal;
                BillingHead billingHead = billingHeadService.findById(billingHeadId);
                PaymentTransactionHead transactionHead = null;
                PaymentTransactionDetail transactionDetailReversed = null; //trans that was paid
                PaymentTransactionDetail transactionDetailReversal = null; // trans thats taken against paid amount
                if (paymentInfoTemplate.getPaymentDetailId() != null) {
                    transactionHead = headRepository.findByInvoiceFetchDetail(billingHead.getInvoice());
                    Optional<PaymentTransactionDetail> transactionDetailReversedOptional = transactionHead.getPaymentTransactionDetails().stream().filter(ptd -> ptd.getPayDetId().equals(paymentInfoTemplate.getPaymentDetailId())).findFirst();
                    if (transactionDetailReversedOptional.isPresent()) {
                        transactionDetailReversed = transactionDetailReversedOptional.get();
                        transactionDetailReversed.setPaymentTransactionHead(transactionHead);
                        transactionDetailReversed.setIssuerReconStatus(Constants.PAYMENT_STATUS.reversed);
                        transactionDetailReversal = PaymentTransactionDetail.builder().build();
                        transactionDetailReversal.setPaymentTransactionHead(transactionHead);
                        transactionDetailReversal.setAmt(transactionDetailReversed.getAmt());
                        transactionDetailReversal.setLineSeqNo((long) (transactionHead.getPaymentTransactionDetails().size() + 1));
                        transactionDetailReversal.setTranDate(transactionDetailReversed.getTranDate());
                        transactionDetailReversal.setNotes(paymentInfoTemplate.getNotes());//reversal reason
                        transactionDetailReversal.setIssuer(transactionDetailReversed.getIssuer()); //payment mode
                        transactionDetailReversal.setReferenceId(paymentInfoTemplate.getReferenceId());
                        transactionDetailReversal.setStatus(transactionDetailReversed.getStatus()); //reconciled or unreconciled
                        transactionDetailReversal.setIssuerReconStatus(reconStatus); //completed, paid,reversed etc
                        transactionDetailReversal.setBatchNo(transactionDetailReversed.getBatchNo());
                        transactionDetailReversal.setSourceId(String.valueOf(paymentInfoTemplate.getPaymentDetailId()));
                        transactionDetailReversal.setSource("Reversed payment tran detail id");
                        //here adding attachment with reversal record
                        if (paymentInfoTemplate.getIsAttachment()) {
                            i++;
                            docuLibrary = doAttachmentToPaymentDetailItem(multipartFiles, i);
                            //if file not uploaded then add msg
                            if (docuLibrary == null) {
                                paymentMessageDTOs.add(addMessage(paymentInfoTemplate, WARN_RESPONSE, Constants.MessageTypes.WARNING));
                            }
                        }
                        LOGGER.info("processReversal=" + transactionDetailReversal.getPayDetId() + " " + transactionDetailReversal.getStatus());
                        transactionHead.setNet(transactionHead.getNet() - transactionDetailReversal.getAmt());
                        transactionDetailReversal = detailRepository.save(transactionDetailReversal);
                        headRepository.save(transactionHead);
                        if (docuLibrary != null) {
                            docuLibrary.setCodeRefId(String.valueOf(transactionDetailReversal.getPayDetId()));
                            docuLibraryService.saveOrUpdate(docuLibrary);
                        }
                    }
                } else {
                    LOGGER.error("can't process reverse payment detail id is null"); //transactionDetailReversed.getPayDetId());
                    paymentMessageDTOs.add(addMessage(paymentInfoTemplate, FAILURE_RESPONSE, Constants.MessageTypes.ERROR));
                    continue;
                }
            } catch (Exception e) {
                LOGGER.error("reverse=" + e.getMessage());
                // processBillingHeadUnLocking(paymentInfoTemplates, false, processType);
                paymentMessageDTOs.add(addMessage(paymentInfoTemplate, e.getMessage(), Constants.MessageTypes.ERROR));
                continue;
            }
            //if not any warning msg added for this detail then add success msg
            if (!(paymentMessageDTOs.stream().anyMatch(p -> p.getPaymentDetailId().equals(paymentInfoTemplate.getPaymentDetailId())))) {
                paymentMessageDTOs.add(addMessage(paymentInfoTemplate, SUCCESS_RESPONSE, Constants.MessageTypes.MESSAGE));
            }
        }
        // processBillingHeadUnLocking(paymentInfoTemplates,false,processType);
        return getMessage(paymentMessageDTOs);
    }

    //this method gets the un-reconcile records and set the action flag Y/N( can we reverse it or not)
    @Override
    public List<PaymentTransactionLineItemsDetailDTO> verifyReversePaymentWithTenantSetting(List<PaymentTransactionLineItemsDetailDTO> paymentTransactionLineItemsDetailDTOs) {
        List<PaymentTransactionLineItemsDetailDTO> updatedPaymentTransactionLineItemsDetailDTO = new ArrayList<>();
        Integer companyRoundOffPreference = utility.getCompanyPreference().getRounding();

        List<PaymentModeDTO> paymentModes = paymentModeRepository.listPaymentMode();
        Long reversalDays = 0l;
        Optional<TenantConfig> reversalDaysLimit = tenantConfigRepository.findByParameter(EConfigParameter.REVERSAL_DAYS.getName());
        if (reversalDaysLimit.isPresent()) {
            reversalDays = reversalDaysLimit.get().getText() != null ? Long.parseLong(reversalDaysLimit.get().getText()) : 0l;
        }
        for (PaymentTransactionLineItemsDetailDTO paymentTransLineItemDetail : paymentTransactionLineItemsDetailDTOs) {
            Optional<PaymentModeDTO> paymentModeDTO = paymentModes.stream().filter(pm -> pm.getPaymentMode().equalsIgnoreCase(paymentTransLineItemDetail.getPaymentMode())).findFirst();
            String reversalInd = paymentModeDTO.isPresent() ? paymentModeDTO.get().getReversalIndicator() : "N";//set the action flag according to payment mode reversal ind
            Date paymentDt = Utility.getDate(paymentTransLineItemDetail.getPaymentDate(), Utility.SYSTEM_DATE_FORMAT);
            Date todayDate = Utility.getDate(new Date(), Utility.SYSTEM_DATE_FORMAT);
            long daysBetween = Utility.chronoUnitBetween(ChronoUnit.DAYS, paymentDt, todayDate);
            long remainingDays = reversalDays - daysBetween;
            // reverse checks to set action flag Y
            if ((paymentTransLineItemDetail.getStatus().equalsIgnoreCase(Constants.PAYMENT_STATUS.paid) ||
                    paymentTransLineItemDetail.getStatus().equalsIgnoreCase(Constants.PAYMENT_STATUS.inprogress)) &&
                    remainingDays > 0 &&
                    reversalInd.equalsIgnoreCase("Y")
            ) {
                paymentTransLineItemDetail.setAction("Y");
                //round of amount according to tenant company preference setting
                paymentTransLineItemDetail.setAmount(getAmountRoundOffBasedOnCompanyPreference(companyRoundOffPreference, paymentTransLineItemDetail.getAmount()));
                paymentTransLineItemDetail.setOutstandingAmount(getAmountRoundOffBasedOnCompanyPreference(companyRoundOffPreference, paymentTransLineItemDetail.getOutstandingAmount()));
                paymentTransLineItemDetail.setInvoiceAmount(getAmountRoundOffBasedOnCompanyPreference(companyRoundOffPreference, paymentTransLineItemDetail.getInvoiceAmount()));

            } else {
                paymentTransLineItemDetail.setAction("N");
                //round of amount according to tenant company preference setting
                paymentTransLineItemDetail.setAmount(getAmountRoundOffBasedOnCompanyPreference(companyRoundOffPreference, paymentTransLineItemDetail.getAmount()));
                paymentTransLineItemDetail.setOutstandingAmount(getAmountRoundOffBasedOnCompanyPreference(companyRoundOffPreference, paymentTransLineItemDetail.getOutstandingAmount()));
                paymentTransLineItemDetail.setInvoiceAmount(getAmountRoundOffBasedOnCompanyPreference(companyRoundOffPreference, paymentTransLineItemDetail.getInvoiceAmount()));

            }
            updatedPaymentTransactionLineItemsDetailDTO.add(paymentTransLineItemDetail);
        }
        return updatedPaymentTransactionLineItemsDetailDTO;
    }

    //this method gets the un-reconcile records and set the action flag Y/N( can we reconcile it or not)
    @Override
    public List<PaymentTransactionLineItemsDetailDTO> verifyUnReconcilePaymentWithTenantSetting(List<PaymentTransactionLineItemsDetailDTO> paymentTransactionLineItemsDetailDTOs) {
        List<PaymentTransactionLineItemsDetailDTO> updatedPaymentTransactionLineItemsDetailDTO = new ArrayList<>();
        List<PaymentModeDTO> paymentModes = paymentModeRepository.listPaymentMode();
        Integer companyRoundOffPreference = utility.getCompanyPreference().getRounding();

        for (PaymentTransactionLineItemsDetailDTO paymentTransLineItemDetail : paymentTransactionLineItemsDetailDTOs) {
            Optional<PaymentModeDTO> paymentModeDTO = paymentModes.stream().filter(pm -> pm.getPaymentMode().equalsIgnoreCase(paymentTransLineItemDetail.getPaymentMode())).findFirst();
            //set the action flag according to payment mode reconcile ind
            String reconcileInd = paymentModeDTO.isPresent() ? paymentModeDTO.get().getReconcileIndicator() : "N";
            // reconcile checks/validations to set action flag Y
            if ((paymentTransLineItemDetail.getStatus().equalsIgnoreCase(Constants.PAYMENT_STATUS.paid) ||
                    paymentTransLineItemDetail.getStatus().equalsIgnoreCase(Constants.PAYMENT_STATUS.inprogress) ||
                    paymentTransLineItemDetail.getStatus().equalsIgnoreCase(Constants.PAYMENT_STATUS.failed)) &&
                    reconcileInd.equalsIgnoreCase("Y")
            ) {
                paymentTransLineItemDetail.setAction("Y");
                //round of amount according to tenant company preference setting
                paymentTransLineItemDetail.setAmount(getAmountRoundOffBasedOnCompanyPreference(companyRoundOffPreference, paymentTransLineItemDetail.getAmount()));
                paymentTransLineItemDetail.setOutstandingAmount(getAmountRoundOffBasedOnCompanyPreference(companyRoundOffPreference, paymentTransLineItemDetail.getOutstandingAmount()));
                paymentTransLineItemDetail.setInvoiceAmount(getAmountRoundOffBasedOnCompanyPreference(companyRoundOffPreference, paymentTransLineItemDetail.getInvoiceAmount()));


            } else {
                paymentTransLineItemDetail.setAction("N");
                //round of amount according to tenant company preference setting
                paymentTransLineItemDetail.setAmount(getAmountRoundOffBasedOnCompanyPreference(companyRoundOffPreference, paymentTransLineItemDetail.getAmount()));
                paymentTransLineItemDetail.setOutstandingAmount(getAmountRoundOffBasedOnCompanyPreference(companyRoundOffPreference, paymentTransLineItemDetail.getOutstandingAmount()));
                paymentTransLineItemDetail.setInvoiceAmount(getAmountRoundOffBasedOnCompanyPreference(companyRoundOffPreference, paymentTransLineItemDetail.getInvoiceAmount()));

            }
            updatedPaymentTransactionLineItemsDetailDTO.add(paymentTransLineItemDetail);
        }
        return updatedPaymentTransactionLineItemsDetailDTO;
    }

    @Override
    public Long checkReferenceId(String referenceId) {
        Long transDetailId = detailRepository.findByReferenceId(referenceId);
        return transDetailId;
    }

    private byte[] createCsvContent(List<PaymentTransactionPreprocess> transactionPreprocesses) {
        List<String> headers = Arrays.asList("customer_name", "invoice_ref_id", "payment_code", "pay_det_id", "amt",
                "source",
                "source_id", "tran_date_time", "instrument_num", "issuer_type", "issuer_ref_num",
                "issuer_recon_status", "issuer_recon_date");

        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            OutputStreamWriter out = new OutputStreamWriter(os, Charset.forName("UTF-8"));
            CSVPrinter printer = new CSVPrinter(out, CSVFormat.RFC4180);
            printer.printRecord(headers);
            for (PaymentTransactionPreprocess transactionPreprocess : transactionPreprocesses) {
                printer.printRecord(Arrays.asList(transactionPreprocess.getCustomerName(),
                        transactionPreprocess.getInvoiceRefId(),
                        transactionPreprocess.getPaymentCode(), null, transactionPreprocess.getAmt(), null, null,
                        new SimpleDateFormat(Utility.SYSTEM_DATE_TIME_FORMAT).format(Utility.getStartOfDate(new Date())), null, null, null, null, null));
            }
            out.close();
            return os.toByteArray();

        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String getAutoReconcileTenantDetail() {
        StringBuilder finalString = new StringBuilder();
        Optional<TenantConfig> isAutoReconcileEnabled = tenantConfigRepository.findByParameter(EConfigParameter.AUTO_RECONCILE_IND.getName());
        isAutoReconcileEnabled.ifPresent(h -> {
            if (h.getText() != null) {
                finalString.append(h.getText());
            }
        });
        Optional<TenantConfig> autoReconcileDays = tenantConfigRepository.findByParameter(EConfigParameter.AUTO_RECONCILE_DAYS.getName());
        autoReconcileDays.ifPresent(f -> {
            if (f.getText() != null) {
                finalString.append(f.getText());
            }
        });
        return finalString.toString();
    }

    @Override
    public String AutoReconcilePayment(String jobId) {
        List<PaymentInfoTemplate> transLineItemsDTOs = new ArrayList<>();
        String autoReconcileTenantData = getAutoReconcileTenantDetail();
        String autoReconcileInd = autoReconcileTenantData.length() > 1 ? autoReconcileTenantData.split(" ")[0] : "N";
        if (autoReconcileInd.equalsIgnoreCase("Y")) {
            Long reconcileDays = autoReconcileTenantData.length() > 1 ? Long.parseLong(autoReconcileTenantData.split(" ")[1]) : 0l;
            List<PaymentTransactionLineItemsDetailDTO> paymentTransactionLineItemsDetailDTOs = verifyUnReconcilePaymentWithTenantSetting(billingHeadService.getUnReconciledTransactionForAutoReconcile());
            List<PaymentTransactionLineItemsDetailDTO> paymentTransactionLineItemsDetailFilterList =
                    paymentTransactionLineItemsDetailDTOs.stream().filter(lineItem -> lineItem.getStatus().equalsIgnoreCase("Y")).collect(Collectors.toList());
            for (PaymentTransactionLineItemsDetailDTO paymentTransLineItemDetail : paymentTransactionLineItemsDetailFilterList) {
                Date todayDate = Utility.getDate(new Date(), Utility.SYSTEM_DATE_FORMAT);
                Date paymentDt = Utility.getDate(paymentTransLineItemDetail.getPaymentDate(), Utility.SYSTEM_DATE_FORMAT);
                long daysBetween = Utility.chronoUnitBetween(ChronoUnit.DAYS, paymentDt, todayDate);
                long remainingDays = reconcileDays - daysBetween;
                if (remainingDays > 0) {
                    PaymentInfoTemplate PaymentInfoTemplate = new PaymentInfoTemplate(paymentTransLineItemDetail.getHeadId(),
                            paymentTransLineItemDetail.getStatus(), paymentTransLineItemDetail.getAmount(), paymentTransLineItemDetail.getInvoiceAmount(),
                            paymentTransLineItemDetail.getInvoiceId(), paymentTransLineItemDetail.getNotes(),
                            paymentTransLineItemDetail.getSubscriptionId(), paymentTransLineItemDetail.getOutstandingAmount(),
                            paymentTransLineItemDetail.getPaymentDate(),
                            paymentTransLineItemDetail.getPaymentMode(),
                            paymentTransLineItemDetail.getRef(),
                            paymentTransLineItemDetail.getPaymentDetailId(),
                            paymentTransLineItemDetail.getPaymentModeId());
                    transLineItemsDTOs.add(PaymentInfoTemplate);
                }
            }
            return reconcilePayment(transLineItemsDTOs, jobId);
        }
        return null;
    }

    @Override
    public DocuLibrary doAttachmentToPaymentDetailItem(List<MultipartFile> multipartFiles, int fileIndex) {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());
        String uri = null;
        DocuLibrary docuLibrary = null;
        try {
            MultipartFile multipartFile = multipartFiles.get(fileIndex);
            uri = storageService.storeInContainer(multipartFile, appProfile, "tenant/"
                            + utility.getCompKey() + AppConstants.PAYMENT_DETAIL_PATH,
                    timeStamp + "-" + multipartFile.getOriginalFilename(), utility.getCompKey(), true);
            docuLibrary = docuLibraryService.saveOrUpdate(DocuLibrary.builder()
                    .docuName(multipartFile.getOriginalFilename())
                    .uri(uri)
                    .docuType(multipartFile.getContentType())
                    .visibilityKey(true)
                    .codeRefType(Constants.PAYMENT_DTL_DOC_REF_TYP.PAYMENT_TRANSACTION_DETAIL)
                    .referenceTime(timeStamp)
                    .build());
        } catch (URISyntaxException | StorageException | IOException e) {
            LOGGER.error(e.getMessage());
        }
        return docuLibrary;
    }

    @Override
    public List<PaymentTransactionHeadDetailDTO> roundOffUnpaidPaymentWithCompanyPreference(List<PaymentTransactionHeadDetailDTO> paymentTranLineItemsDtlPremiseDTOs, List<PaymentTransactionHeadDetailDTO> paymentTranLineItemsDtlAcctDTOs) {
        List<PaymentTransactionHeadDetailDTO> updatedPaymentTransactionLineItemsDetailDTO = new ArrayList<>();
        Integer roundOffCompanyPreference = utility.getCompanyPreference().getRounding();
        for (PaymentTransactionHeadDetailDTO pn : paymentTranLineItemsDtlPremiseDTOs) {
            Optional<PaymentTransactionHeadDetailDTO> transactionDtlDtoOptional = paymentTranLineItemsDtlAcctDTOs.stream().filter(psrc -> psrc.getSubscriptionId().longValue() == pn.getSubscriptionId().longValue()).findFirst();
            if (transactionDtlDtoOptional.isPresent()) {
                PaymentTransactionHeadDetailDTO transactionDtlDTO = transactionDtlDtoOptional.get();
                pn.setAccountNumber(Utility.getMaskedString(transactionDtlDTO.getAccountNumber()));
                pn.setPaymentMode(transactionDtlDTO.getPaymentMode());
            }
            Double outstandingAmount = getAmountRoundOffBasedOnCompanyPreference(roundOffCompanyPreference, pn.getOutstandingAmount());
            pn.setInvoiceAmount(getAmountRoundOffBasedOnCompanyPreference(roundOffCompanyPreference, pn.getInvoiceAmount()));
            pn.setOutstandingAmount(outstandingAmount);
            if (outstandingAmount > 0) {
                updatedPaymentTransactionLineItemsDetailDTO.add(pn);
            }
        }
        return updatedPaymentTransactionLineItemsDetailDTO;
    }

//    private List<PaymentInfoTemplate> processBillingHeadLocking(List<PaymentInfoTemplate> paymentInfoTemplates, Boolean lockInd, List<Long> billingHeadLockedIds, String type) {
//        paymentInfoTemplates = paymentInfoTemplates.stream().filter(pt -> !billingHeadLockedIds.contains(pt.getHeadId())).collect(Collectors.toList());
//        List<Long> billingIdsToLock = paymentInfoTemplates.stream().map(PaymentInfoTemplate::getHeadId).collect(Collectors.toList());
//        switch (type) {
//            case "payment":
//                billingHeadService.updateBillingHeadProcessPaymentLock(billingIdsToLock, lockInd);
//                return paymentInfoTemplates;
//            case "reconcile":
//                billingHeadService.updateBillingHeadProcessReconcileLock(billingIdsToLock, lockInd);
//                return paymentInfoTemplates;
//            case "reverse":
//                billingHeadService.updateBillingHeadProcessReverseLock(billingIdsToLock, lockInd);
//                return paymentInfoTemplates;
//        }
//        return null;
//    }

//    private List<PaymentInfoTemplate> processBillingHeadUnLocking(List<PaymentInfoTemplate> paymentInfoTemplates, Boolean lockInd, String type) {
//        List<Long> billingIdsToUnLock = paymentInfoTemplates.stream().map(PaymentInfoTemplate::getHeadId).collect(Collectors.toList());
//        switch (type) {
//            case "payment":
//                billingHeadService.updateBillingHeadProcessPaymentLock(billingIdsToUnLock, lockInd);
//                return paymentInfoTemplates;
//            case "reconcile":
//                billingHeadService.updateBillingHeadProcessReconcileLock(billingIdsToUnLock, lockInd);
//                return paymentInfoTemplates;
//            case "reverse":
//                billingHeadService.updateBillingHeadProcessReverseLock(billingIdsToUnLock, lockInd);
//                return paymentInfoTemplates;
//            default:
//        }
//        return paymentInfoTemplates;
//    }

    private Double getAmountRoundOffBasedOnCompanyPreference(Integer roundOffCompanyPreference, Double amount) {
        if (amount.doubleValue() > 0)
            return utility.round(amount, roundOffCompanyPreference);
        else
            return amount;
    }

    private PaymentMessageDTO addMessage(PaymentInfoTemplate paymentInfoTemplate, String msg, String msgType) {
        PaymentMessageDTO paymentMsgDTO = PaymentMessageDTO.builder().build();
        paymentMsgDTO.setPaymentId(paymentInfoTemplate.getPaymentId());
        paymentMsgDTO.setInvoiceId(paymentInfoTemplate.getInvoice_Id());
        paymentMsgDTO.setPaymentDetailId(paymentInfoTemplate.getPaymentDetailId());

        switch (msgType) {
            case "MESSAGE":
                paymentMsgDTO.setMessage(msg);
                break;
            case "WARNING":
                paymentMsgDTO.setWarning(msg);
                break;
            case "ERROR":
                paymentMsgDTO.setError(msg);
                break;
        }
        return paymentMsgDTO;
    }

    private String getNextBatchNo(String subscriptionType) {
//        CSGF-20220531-100001
        SimpleDateFormat df = new SimpleDateFormat(Utility.YEAR_MONTH_DATE);
        String date = df.format(new Date());
        String default_Batch = subscriptionType + "-" + date + "-" + "100001";
        StringBuilder stringBuilder = new StringBuilder();
        String batchNo = detailRepository.getLastBatchNo(subscriptionType + "-" + date);
        if (batchNo != null) {
            String[] value = batchNo.split("-");
            Integer nextValue = Integer.parseInt(value[2]) + 1;
            stringBuilder.append(subscriptionType + "-");
            stringBuilder.append(date + "-");
            stringBuilder.append(nextValue);
            return stringBuilder.toString();
        } else {
            return default_Batch;
        }
    }

    private String getMessage(List<PaymentMessageDTO> paymentMessageDTOs) {
        if (paymentMessageDTOs.stream().allMatch(p -> p.getError() != null)) {
            return "failure";
        } else if ((paymentMessageDTOs.stream().anyMatch(p -> p.getWarning() != null)) ||
                (paymentMessageDTOs.stream().anyMatch(p -> p.getError() != null))) {
            return "partial-success";
        } else if (paymentMessageDTOs.stream().allMatch(p -> p.getMessage() != null)) {
            return "success";
        } else {
            return "failure";
        }
    }

}