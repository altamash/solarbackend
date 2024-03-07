package com.solar.api.tenant.service.process.billing.publish;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.publishInfo.PublishInfoMapper;
import com.solar.api.tenant.model.billing.BillingInvoice.PublishInfo;
import com.solar.api.tenant.model.billing.BillingInvoice.PublishInfoArchive;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.billingHead.EBillStatus;
import com.solar.api.tenant.model.billing.calculation.CalculationDetails;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.repository.BillingHeadRepository;
import com.solar.api.tenant.service.job.JobManagerTenantService;
import com.solar.api.tenant.service.process.billing.invoice.BillInvoiceService;
import com.solar.api.tenant.service.process.reporting.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
//@Transactional("masterTransactionManager")
public class BillingInvoicePublishServiceImpl implements BillingInvoicePublishService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    PublishInfoService publishInfoService;
    @Autowired
    PublishInfoArchiveService publishInfoArchiveService;
    @Autowired
    private BillingHeadRepository billingHeadRepository;
    @Lazy
    @Autowired
    private BillInvoiceService billInvoiceService;
    @Autowired
    ReportService reportService;
    @Autowired
    JobManagerTenantService jobManagerTenantService;

    @Async
    @Override
    public void publishInvoiceByMonth(String subscriptionCode, String rateMatrixHeadIds, String billingMonth, String type, Long jobId) {

        int totalCounter = 0;
        int successCounter = 0;
//        List<Long> corruptCounter = new ArrayList<>();
        List<Long> notAvailableCounter = new ArrayList<>();

        List<Long> subscriptionRateMatrixIds =
                Arrays.stream(rateMatrixHeadIds.split(",")).map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
        ObjectNode publishString = new ObjectMapper().createObjectNode();
        for (BillingHead billingHead : billInvoiceService.findForInvoicing(billingMonth, subscriptionCode,
                subscriptionRateMatrixIds,
                Arrays.asList(EBillStatus.INVOICED.getStatus()))) {
            totalCounter++;
            try {
                if (billingHead.getInvoice().getPublishInfoId() == null) {
                    publishInfoService.save(billingHead.getId());
                    publishString = reportService.publishInvoice(billingHead.getId());
                    PublishInfo successfulInvoices = publishInfoService.findByStatus(EPublishBillStatus.SUCCESS.getStatus());
                    PublishInfo corruptedInvoices = publishInfoService.findByStatus(EPublishBillStatus.CORRUPTED.getStatus());
                    if (successfulInvoices != null) {
                        PublishInfoArchive publishInfoArchive = PublishInfoMapper.toPublishInfo(successfulInvoices);
                        publishInfoArchiveService.save(publishInfoArchive);
                        billingHead.getInvoice().setPublishInfoId(publishInfoArchive.getId());
                        billingHead.getInvoice().setPublishDate(Date.valueOf(LocalDateTime.now().toLocalDate()));
                        billingHead.getInvoice().setPublishIndicator(true);
                        billingHeadRepository.save(billingHead);
                        successCounter++;
                    }
                    if (corruptedInvoices != null) {
                        notAvailableCounter.add(billingHead.getSubscriptionId());
                    }
                    publishInfoService.deleteAll();
                }
                //send email async
            } catch (Exception e) {
                ObjectNode messageJson = new ObjectMapper().createObjectNode();
//                messageJson.put("job_id", jobManager.getId());
                messageJson.put("subscriptionCode", subscriptionCode);
                messageJson.put("rateMatrixHeadIds", Joiner.on(", ").join(subscriptionRateMatrixIds));
                messageJson.put("billingMonthYear", billingMonth);
                messageJson.put("type", type);
                messageJson.set("response", publishString);
                LOGGER.error(messageJson.toPrettyString(), e);
            }
        }
        String corruptedIds = Joiner.on(",").join(notAvailableCounter.stream().map(String::valueOf).collect(Collectors.toList()));
        publishString.put("Total No. of Invoices: ", totalCounter);
        publishString.put("Total No. of Invoices Successfully Published: ", successCounter);
        publishString.put("Run 'GENERATE PDF' process for Subscription ID(s): ", corruptedIds);
        JobManagerTenant jobManagerTenant = jobManagerTenantService.findById(jobId);
        jobManagerTenant.setResponseMessage(publishString.toPrettyString());
        jobManagerTenantService.toUpdateMapper(jobManagerTenant);
        publishInfoService.deleteAll();
    }

    @Override
    public void validateInvoices() {
        ObjectNode response = new ObjectMapper().createObjectNode();

        List<PublishInfo> publishInfos = publishInfoService.getAll();
        int TotalSize = publishInfos.size();
        int FAIL = 0;
        publishInfos.forEach(invoice -> {

            BillingHead billingHead = billingHeadRepository.findById(invoice.getReferenceId()).orElseThrow(() ->
                    new NotFoundException(BillingHead.class, invoice.getReferenceId()));

            if (invoice.getStatus().equals("FAILED")) {

                if (invoice.getChannelRecipient().equals("testna@solarinformatics.com")) {

                    try {
                        reportService.publishInvoice(billingHead.getId());
                        publishInfoService.save(billingHead.getId());


                    } catch (Exception e) {

                        publishInfoService.save(billingHead.getId());


                        response.put("warning", "Failed to publish Invoice");

                        response.put("invoice_id", invoice.getId());
                        response.put("Failed", FAIL + 1);

                        LOGGER.error(e.getMessage(), e);
                    }
                    response.put("message", TotalSize - FAIL + "Invoices has been published");
                    response.put("validation_failure", FAIL + "Invoices has still failed to publish");
                }
            }
        });
    }

    @Override
    public ObjectNode publishIndividualInvoice(Long headId) {

        ObjectNode publishString = new ObjectMapper().createObjectNode();
        List<PublishInfo> publishInfoExists = publishInfoService.findByReferenceId(headId);
        if (publishInfoExists.isEmpty()) {
            publishInfoService.save(headId);
        }
        publishString = reportService.publishInvoice(headId);
        PublishInfo successfulInvoices = publishInfoService.findByStatus(EPublishBillStatus.SUCCESS.getStatus());
        PublishInfo corruptedInvoices = publishInfoService.findByStatus(EPublishBillStatus.CORRUPTED.getStatus());
        if (successfulInvoices != null) {
            PublishInfoArchive publishInfoArchive = PublishInfoMapper.toPublishInfo(successfulInvoices);
            publishInfoArchiveService.save(publishInfoArchive);
        }
        if (corruptedInvoices != null) {
            publishString.put("Invoice is corrupted", headId);
        }
        publishInfoService.deleteAll();
        return publishString;
    }

    @Override
    public ObjectNode publishIndividualHTMLInvoice(BillingHead billingHead, CalculationDetails calculationDetails) {

        ObjectNode publishString = new ObjectMapper().createObjectNode();
        List<PublishInfo> publishInfoExists = publishInfoService.findByReferenceId(billingHead.getId());
        if (publishInfoExists.isEmpty()) {
            publishInfoService.save(billingHead.getId());
        }
        publishString = reportService.publishHTMLInvoice(billingHead, calculationDetails);
        PublishInfo successfulInvoices = publishInfoService.findByStatus(EPublishBillStatus.SUCCESS.getStatus());
        PublishInfo corruptedInvoices = publishInfoService.findByStatus(EPublishBillStatus.CORRUPTED.getStatus());
        if (successfulInvoices != null) {
            PublishInfoArchive publishInfoArchive = PublishInfoMapper.toPublishInfo(successfulInvoices);
            publishInfoArchiveService.save(publishInfoArchive);
            billingHead.getInvoice().setPublishInfoId(publishInfoArchive.getId());
            billingHead.getInvoice().setPublishDate(Date.valueOf(LocalDateTime.now().toLocalDate()));
            billingHead.getInvoice().setPublishIndicator(true);
            BillingHead result = billingHeadRepository.save(billingHead);
        }
        if (corruptedInvoices != null) {
            publishString.put("Invoice is corrupted", billingHead.getId());
        }
        publishInfoService.deleteAll();
        return publishString;
    }
}
