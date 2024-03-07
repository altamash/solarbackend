package com.solar.api.saas.module.com.solar.batch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.azure.storage.StorageException;
import com.solar.api.AppConstants;
import com.solar.api.helper.Utility;
import com.solar.api.saas.module.com.solar.batch.service.BatchService;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.saas.module.com.solar.utility.ETLUtilityService;
import com.solar.api.saas.service.job.JobHandler;
import com.solar.api.tenant.mapper.billing.billingHead.BillingDiscountDTO;
import com.solar.api.tenant.model.externalFile.ExternalFile;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.service.process.billing.BillingService;
import com.solar.api.tenant.service.upload.ExternalFileService;
import io.swagger.annotations.ApiParam;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.NoSuchJobInstanceException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.*;

import org.springframework.http.HttpStatus;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("BatchAnalyzer")
@RequestMapping(value = "/runbatch")
public class BatchAnalyzer {

    @Autowired
    private BatchService batchService;
    @Autowired
    private ExternalFileService externalFileService;
    @Autowired
    private BillingService billingService;
    @Autowired
    private ETLUtilityService etlUtilityService;

    @Autowired
    private JobHandler jobHandler;

    @GetMapping("/testBatch")
    public ObjectNode testBatch() throws Exception {
        jobHandler.getLastJobInstanceId("abc");
        return null;
    }

    @PostMapping("/stopRunningInstance/{jobExecutionId}")
    public ObjectNode stopRunningInstance(@PathVariable Long jobExecutionId) throws Exception {
        return batchService.stopByExecutionId(jobExecutionId);
    }

    @PostMapping("/etl/{compKey}/{importTypeId}")
    public ObjectNode load(@RequestParam("file") MultipartFile file,
                           @PathVariable String compKey,
                           @PathVariable Long importTypeId,
                           @RequestParam(value = "mongoSubId", required = false) String mongoSubId) {

        ExternalFile externalFile = externalFileService.findByImportTypeId(importTypeId);
        if (compKey != null || importTypeId != null) {
            return etlUtilityService.etl(externalFile, file, compKey, mongoSubId);
        }
        return new ObjectMapper().createObjectNode().put("message", "Company Key or Import Type Id is not provided");
    }

    @PostMapping("/billingBySubscriptionType/{subscriptionCode}/{subscriptionRateMatrixIdsCSV}/{billingMonth}/{type}")
    public ObjectNode invoicingBySubscriptionType(@PathVariable("subscriptionCode") String subscriptionCode,
                                                  @PathVariable(value = "subscriptionRateMatrixIdsCSV", required = false) String subscriptionRateMatrixIdsCSV,
                                                  @PathVariable(value = "billHeadId", required = false) String billHeadId,
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
                    Arrays.stream(subscriptionRateMatrixIdsCSV.split(",")).map(String::trim).collect(Collectors.toList());
            jobName = billingService.getJobName(variantIds, type, subscriptionCode, billingMonth);
            jobManager = billingService.addJobManagerTenant(jobName, subscriptionCode, variantIds, billingMonth, type, compKey);
        }

        String message = billingService.checkForRunningJob(subscriptionRateMatrixIds, variantIds, type, subscriptionCode, billingMonth, isLegacy);
        if (message != null) {
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("message", message);
            return messageJson;
        }
        Date date = null;
        if (dateString != null) {
            String[] monthYear = billingMonth.split("-");
            dateString = monthYear[1] + "-" + monthYear[0] + "-" + dateString;
            date = dateString != null ? new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT).parse(dateString) : null;
        }
        billingService.enqueJobBillingByType(subscriptionCode, subscriptionRateMatrixIds, variantIds, billingMonth,
                date, type, compKey, jobManager, isLegacy);
//        batchService.billingBatch(subscriptionCode, subscriptionRateMatrixIdsCSV, billingMonth, date, type, compKey, null);
        ObjectNode messageJson = new ObjectMapper().createObjectNode();
        messageJson.put("message", AppConstants.JOB_SUCCESS_SUBMISSION);
        return messageJson;
    }

    /**
     * BillingBatch
     * Invoicing
     * Schedule PDF Generation
     */
    @PostMapping("/billingBySubscriptionTypeBatch/{subscriptionCode}/{subscriptionRateMatrixIdsCSV}/{billingMonth}/{type}")
    public ObjectNode billingBySubscriptionTypeBatch(@PathVariable("subscriptionCode") String subscriptionCode,
                                                     @PathVariable(value = "subscriptionRateMatrixIdsCSV", required = false) String subscriptionRateMatrixIdsCSV,
                                                     @PathVariable("billingMonth") String billingMonth,
                                                     @PathVariable("type") String type,
                                                     @RequestParam(value = "date", required = false) String dateString,
                                                     @RequestHeader("Comp-Key") Long compKey,
                                                     @RequestParam(value = "isLegacy", required = false, defaultValue = "true") Boolean isLegacy) throws Exception {
        List<Long> subscriptionRateMatrixIds = null;
        List<String> variantIds = null;
        if (isLegacy) {
            subscriptionRateMatrixIds =
                    Arrays.stream(subscriptionRateMatrixIdsCSV.split(",")).map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
        } else {
            variantIds =
                    Arrays.stream(subscriptionRateMatrixIdsCSV.split(",")).map(String::trim).collect(Collectors.toList());
        }
        String message;
        message = billingService.checkForRunningJob(subscriptionRateMatrixIds, variantIds, type, subscriptionCode, billingMonth, isLegacy);
        if (message != null) {
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("message", message);
            return messageJson;
        }
        Date date = null;
        if (dateString != null) {
            String[] monthYear = billingMonth.split("-");
            dateString = monthYear[1] + "-" + monthYear[0] + "-" + dateString;
            date = dateString != null ? new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT).parse(dateString) : null;
        }
        batchService.billingBatch(subscriptionCode, subscriptionRateMatrixIdsCSV, billingMonth, date, type, compKey, null);
        ObjectNode messageJson = new ObjectMapper().createObjectNode();
        messageJson.put("message", AppConstants.JOB_SUCCESS_SUBMISSION);
        return messageJson;
    }

    @PostMapping("/publishInvoices")
    public ObjectNode publishInvoicesBatch() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException, StorageException, IOException,
            URISyntaxException {
        ObjectNode response = new ObjectMapper().createObjectNode();
        batchService.publishInvoicesBatch();
        response.put("message", "Publish Invoices Batch is in progress.");
        return response;
    }

    @PostMapping("/billingTest/{subscriptionCode}/{subscriptionRateMatrixIdsCSV}/{billingMonth}/{type}")
    public ObjectNode test(@PathVariable("subscriptionCode") String subscriptionCode,
                           @PathVariable(value = "subscriptionRateMatrixIdsCSV", required = false) String subscriptionRateMatrixIdsCSV,
                           @PathVariable("billingMonth") String billingMonth,
                           @PathVariable("type") String type,
                           @RequestParam(value = "date", required = false) String dateString,
                           @RequestHeader("Comp-Key") Long compKey) throws JobParametersInvalidException,
            JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, StorageException,
            IOException, URISyntaxException, ParseException {
//        List<Long> subscriptionRateMatrixIds =
//                Arrays.stream(subscriptionRateMatrixIdsCSV.split(",")).map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
//        String message;
//        message = billingService.checkForRunningJob(subscriptionRateMatrixIds, type, subscriptionCode, billingMonth);
//        if (message != null) {
//            ObjectNode messageJson = new ObjectMapper().createObjectNode();
//            messageJson.put("message", message);
//            return messageJson;
//        }
        Date date = null;
        if (dateString != null) {
            String[] monthYear = billingMonth.split("-");
            dateString = monthYear[1] + "-" + monthYear[0] + "-" + dateString;
            date = dateString != null ? new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT).parse(dateString) : null;
        }
//        String jobName = billingService.getJobName(subscriptionRateMatrixIds, type, subscriptionCode, billingMonth);
//        JobManagerTenant jobManager = billingService.addJobManagerTenant(jobName, subscriptionCode,
//                subscriptionRateMatrixIds, billingMonth, type, compKey);
        batchService.billingBatch(subscriptionCode, subscriptionRateMatrixIdsCSV, billingMonth, date, type,
                compKey, null);
        ObjectNode messageJson = new ObjectMapper().createObjectNode();
        messageJson.put("message", AppConstants.JOB_SUCCESS_SUBMISSION);
        return messageJson;
    }


    @GetMapping("/adhocAllSubscriptionTermination/{compKey}")
    public ObjectNode adhocAllSubscriptionTermination(@PathVariable String compKey) throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException, IOException, URISyntaxException {
        ObjectNode response = new ObjectMapper().createObjectNode();
//        batchService.adhocAllSubscriptionTermination();
//        response.put("message", "Adhoc Subscription Termination Batch has started");
        return response;
    }

    @GetMapping("/autoAllSubscriptionTermination/{compKey}")
    public ObjectNode autoAllSubscriptionTermination(@PathVariable String compKey) throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException, IOException, URISyntaxException {
        ObjectNode response = new ObjectMapper().createObjectNode();
        batchService.autoAllSubscriptionTermination();
        response.put("message", "Auto Subscription Termination Batch has started");
        return response;
    }

    @GetMapping("/calculateHoursByEmployee/{employeeId}/{taskId}")
    public ObjectNode calculateHoursByEmployee(@PathVariable Long employeeId,
                                               @PathVariable Long taskId,
                                               @RequestHeader("Comp-Key") Long compKey) throws JobParametersInvalidException,
            JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, NoSuchJobInstanceException {
        ObjectNode response = new ObjectMapper().createObjectNode();
        batchService.calculateHoursByEmployee(compKey, employeeId, taskId);
        response.put("message", "Hours calculation Batch has started");
        return response;
    }

    @PostMapping("/invoice/{billingHeadId}")
    public ObjectNode invoice(@PathVariable Long billingHeadId, @RequestHeader("Comp-Key") Long compKey) throws JobParametersInvalidException,
            JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, NoSuchJobInstanceException {
        ObjectNode response = new ObjectMapper().createObjectNode();
        batchService.individualInvoice(billingHeadId, compKey);
        response.put("message", "Invoicing is in progress, Please check job queue.");
        return response;
    }

    @PostMapping("/invoice/email/{billingHeadId}")
    public ObjectNode emailInvoice(@PathVariable Long billingHeadId, @RequestHeader("Comp-Key") Long compKey) throws JobParametersInvalidException,
            JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, NoSuchJobInstanceException {
        ObjectNode response = new ObjectMapper().createObjectNode();
        batchService.emailInvoice(billingHeadId, compKey);
        response.put("message", "Email Sent");
        return response;
    }

    @PostMapping("/disableProductByProductIdBatch/{productId}")
    public Map disableProductByProductIdBatch(@PathVariable("productId") String productId,
                                              @RequestHeader("Comp-Key") Long compKey) throws Exception {
        Map response = new HashMap();
        if (productId != null) {
            response = batchService.disableProductBatch(productId, compKey, response);
        } else {
            response.put("data", null);
            response.put("message", "Parameters cannot be null");
            response.put("code", HttpStatus.PRECONDITION_FAILED);
        }
        return response;
    }

    @PostMapping("/v1/disableVariant")
    public Map disableVariant(@RequestParam("productId") String productId,
                              @RequestParam("variantId") String variantId,
                              @RequestHeader("Comp-Key") Long compKey) {
        Map response = new HashMap();
        if (productId != null && variantId != null) {
            response = batchService.disableVariant(productId, variantId, response, compKey);
        } else {
            response.put("data", null);
            response.put("message", "Parameters cannot be null");
            response.put("code", HttpStatus.PRECONDITION_FAILED);
        }
        return response;
    }

    @PostMapping("/runEGaugeData")
    public ObjectNode runEGaugeData() {
        ObjectNode response = new ObjectMapper().createObjectNode();
        batchService.eGaugeGraphData();
        response.put("message", "Batch successfully run");
        return response;
    }

    @PostMapping("/dataIngestion")
    public ObjectNode dataIngestion() {
        ObjectNode response = new ObjectMapper().createObjectNode();
        batchService.dataIngestion();
        response.put("message", "Batch successfully run");
        return response;
    }

    @PostMapping("/migrateDataIngestion")
    public ObjectNode migrateDataIngestion() {
        ObjectNode response = new ObjectMapper().createObjectNode();
        batchService.migrateDataIngestion();
        response.put("message", "Batch successfully run");
        return response;
    }

    @PostMapping("/v1/runBillsBatchJob")
    public Map pendingBills(@RequestParam("jobName") String jobName,
                            @RequestHeader("Comp-Key") Long compKey) {
        Map response = batchService.runBillsBatchJob(jobName, compKey);
        return response;
    }

    @PostMapping("/billing/skipBill/v2/{skipFlag}/{billSkip}")
    public Map bulkSkipBillHead(@RequestParam("billingHeadIds") String billingHeadIds, @PathVariable Long skipFlag,
                                @PathVariable Boolean billSkip, @RequestHeader("Comp-Key") Long compKey) {
        Map response = new HashMap();
        if (billingHeadIds != null && skipFlag != null) {
            batchService.skipBillsBatch(billingHeadIds, skipFlag, billSkip, compKey);
            response = Utility.generateResponseMap(response, HttpStatus.OK.name(), AppConstants.JOB_SUCCESS_SUBMISSION, null);
        } else {
            response = Utility.generateResponseMap(response, HttpStatus.PRECONDITION_FAILED.name(), "Parameters cannot be null", null);
        }
        return response;
    }

    @PostMapping("/billing/addDiscount/v2")
    public Map bulkAddDiscount(@RequestBody BillingDiscountDTO billingDiscountDTO, @RequestParam("billingHeadIds") String billingHeadIds
            , @RequestHeader("Comp-Key") Long compKey) {
        Map response = new HashMap();
        if (billingDiscountDTO != null && billingHeadIds != null) {
            batchService.addDiscountBatch(billingHeadIds, compKey, billingDiscountDTO);
            response = Utility.generateResponseMap(response, HttpStatus.OK.name(), AppConstants.JOB_SUCCESS_SUBMISSION, null);
        } else {
            response = Utility.generateResponseMap(response, HttpStatus.PRECONDITION_FAILED.name(), "Parameters cannot be null", null);
        }
        return response;

    }

    @PostMapping("/billing/generatePublishInvoice/v1")
    public Map bulkGeneratePublishInvoice(@RequestParam("billingHeadIds") String billingHeadIds, @RequestParam("jobName") String jobName, @RequestHeader("Comp-Key") Long compKey) {
        Map response = new HashMap();
        if (billingHeadIds != null && compKey != null) {
            batchService.generateAndPublishInvoiceBatch(billingHeadIds, compKey, jobName);
            response = Utility.generateResponseMap(response, HttpStatus.OK.name(), AppConstants.JOB_SUCCESS_SUBMISSION, null);
        } else {
            response = Utility.generateResponseMap(response, HttpStatus.PRECONDITION_FAILED.name(), "Parameters cannot be null", null);
        }
        return response;
    }

    @PostMapping("/project/UpdateProjectStatus/v1")
    public Map UpdateProjectStatus(@RequestParam("jobName") String jobName, @RequestHeader("Comp-Key") Long compKey) {
        Map response = new HashMap();
        if (compKey != null) {
            batchService.updateProjectStatusesADHOC(jobName, compKey);
            response = Utility.generateResponseMap(response, HttpStatus.OK.name(), AppConstants.JOB_SUCCESS_SUBMISSION, null);
        } else {
            response = Utility.generateResponseMap(response, HttpStatus.PRECONDITION_FAILED.name(), "Parameters cannot be null", null);
        }
        return response;
    }

    @PostMapping("/runBillingADHOCBatch/v1")
    public Map runBillingADHOCBatch(@RequestParam("jobName") String jobName,
                                    @RequestHeader("Comp-Key") Long compKey,
                                    @RequestParam(value = "period", required = false) String period) {
        Map response = new HashMap();
        if (compKey != null) {
            batchService.runBillingADHOCBatch(jobName, compKey, period);
            response = Utility.generateResponseMap(response, HttpStatus.OK.name(), AppConstants.JOB_SUCCESS_SUBMISSION, null);
        } else {
            response = Utility.generateResponseMap(response, HttpStatus.PRECONDITION_FAILED.name(), "Parameters cannot be null", null);
        }
        return response;
    }

    @PostMapping("/generateBillingCreditsADHOC/v1")
    public Map generateBillingCreditsADHOC(
            @ApiParam(value = "Date in format yyyy-MM", example = "2023-08")
            @RequestParam("date") String date, @RequestHeader("Comp-Key") Long compKey) {
        Map response = new HashMap();
        if (compKey != null) {
            batchService.generateBillingCreditsADHOC(date);
            response = Utility.generateResponseMap(response, HttpStatus.OK.name(), AppConstants.JOB_SUCCESS_SUBMISSION, null);
        } else {
            response = Utility.generateResponseMap(response, HttpStatus.PRECONDITION_FAILED.name(), "Parameters cannot be null", null);
        }
        return response;
    }

    @PostMapping("/v1/deActivateProjection")
    public BaseResponse deActivateProjection(@RequestParam("variantId") String variantId,
                                       @RequestParam("projectionId") String projectionId,
                                       @RequestHeader("Comp-Key") Long compKey) {

        if (variantId != null && variantId != null) {
            return batchService.deActivateProjection(variantId, projectionId, compKey);
        } else {
            return BaseResponse.builder().code(HttpStatus.PRECONDITION_FAILED.value()).message("Parameters cannot be null").data(null).build();
        }
    }
    @PostMapping("/v1/activateProjection")
    public BaseResponse activateProjection(@RequestParam("variantId") String variantId,
                                             @RequestParam("projectionId") String projectionId,
                                             @RequestHeader("Comp-Key") Long compKey) {

        if (variantId != null && variantId != null) {
            return batchService.activateProjection(variantId, projectionId, compKey);
        } else {
            return BaseResponse.builder().code(HttpStatus.PRECONDITION_FAILED.value()).message("Parameters cannot be null").data(null).build();
        }
    }
    @PostMapping("/v1/generateProjectProjectionRevenue")
    public BaseResponse generateProjectProjectionRevenue(@RequestParam("months") String months,
                                           @RequestHeader("Comp-Key") Long compKey) {

        if (months != null ) {
            return batchService.generateProjectProjectionRevenue(months, compKey);
        } else {
            return BaseResponse.builder().code(HttpStatus.PRECONDITION_FAILED.value()).message("Parameters cannot be null").data(null).build();
        }
    }

//    @GetMapping("/saveWeatherData")
//    public void batchDemo(){
//         batchService.batchDemo(null);
//    }
}
