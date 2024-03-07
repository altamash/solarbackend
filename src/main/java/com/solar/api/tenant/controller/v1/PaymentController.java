package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.helper.Utility;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.mapper.payment.info.PaymentInfoTemplate;
import com.solar.api.tenant.mapper.payment.info.PaymentMessageDTO;
import com.solar.api.tenant.mapper.payment.info.PaymentTransactionSummaryTemplate;
import com.solar.api.tenant.service.PaymentInfoService;
import com.solar.api.tenant.service.process.customer.CustomerPaymentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("PaymentController")
@RequestMapping(value = "/payment")
public class PaymentController {

    @Autowired
    private PaymentInfoService paymentInfoService;

    private final CustomerPaymentInfo customerPaymentInfo;

    PaymentController(CustomerPaymentInfo customerPaymentInfo) {
        this.customerPaymentInfo = customerPaymentInfo;
    }

    @GetMapping("/balance/{billingHeadId}")
    public ObjectNode paymentBalance(@PathVariable Long billingHeadId) {
        ObjectNode response = new ObjectMapper().createObjectNode();
        response.put("message", customerPaymentInfo.paymentBalance(billingHeadId));
        return response;
    }

    @GetMapping("/summary/{billingHeadId}")
    public PaymentTransactionSummaryTemplate getPaymentTransactionSummary(@PathVariable Long billingHeadId) {
        return customerPaymentInfo.getPaymentTransactionSummary(billingHeadId);
    }

    @GetMapping("/process/{billingHeadId}/{amount}/{tranDate}")
    public ObjectNode processPayment(@PathVariable Long billingHeadId, @PathVariable Double amount,
                                     @PathVariable String tranDate) throws ParseException {
        ObjectNode response = new ObjectMapper().createObjectNode();
        response.put("message", customerPaymentInfo.processPayment(billingHeadId, amount,
                new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT).parse(tranDate)));
        return response;
    }

    @GetMapping("/paymentModes")
    public List<String> paymentModes() {
        return paymentInfoService.getAllPaymentModes();
    }

    @GetMapping("/paymentStatus")
    public List<String> paymentStatus() {
        return paymentInfoService.getAllPaymentStatus();
    }

    @GetMapping("/paymentDetailReferenceIdUnique")
    public Long uniquePaymentDetailReferenceID(String referenceId) {
        return customerPaymentInfo.checkReferenceId(referenceId);
    }

    @PostMapping("/processPayment")
    public ObjectNode processPayment(//@RequestBody List<PaymentInfoTemplate> paymentInfoTemplates
                                     @RequestParam(name = "paymentInfoTemplates", required = true) String paymentInfoTemplates,
                                     @RequestParam(name = "multipartFiles", required = false) List<MultipartFile> multipartFiles,
                                     @RequestParam(name = "subscriptionType", required = true) String subscriptionType

    ) throws JsonProcessingException {
        String jobId = null; //this will come from scheduler
//         String msg = AppConstants.PAYMENT_INITIAL_MSG;
//        List<PaymentMessageDTO> paymentMessageDTO = new ArrayList<>();
//        paymentMessageDTO.add(PaymentMessageDTO.builder().message(msg).build());
        ObjectNode response = new ObjectMapper().createObjectNode();
        List<PaymentInfoTemplate> PaymentInfoTemplateList = new ObjectMapper().readValue(paymentInfoTemplates, new TypeReference<List<PaymentInfoTemplate>>() {
        });
        String msg = customerPaymentInfo.processPayment(PaymentInfoTemplateList, jobId, multipartFiles, subscriptionType);
        return response.put("message", msg);
    }

    @PostMapping(value = "/processReverse", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ObjectNode processReverse(@RequestParam(name = "paymentInfoTemplates", required = true) String paymentInfoTemplates,
                                     @RequestParam(name = "multipartFiles", required = false) List<MultipartFile> multipartFiles) throws JsonProcessingException {
        // String jobId =""; //this will come from scheduler
//         String msg = AppConstants.PAYMENT_INITIAL_MSG;
//         List<PaymentMessageDTO> paymentMessageDTO = new ArrayList<>();
//        paymentMessageDTO.add(PaymentMessageDTO.builder().message(msg).build());
        ObjectNode response = new ObjectMapper().createObjectNode();
        List<PaymentInfoTemplate> PaymentInfoTemplateList = new ObjectMapper().readValue(paymentInfoTemplates, new TypeReference<List<PaymentInfoTemplate>>() {
        });
        String msgResponse = customerPaymentInfo.processReverse(PaymentInfoTemplateList, multipartFiles);
        return response.put("message", msgResponse);
    }

    @PostMapping(value = "/processReconcile", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ObjectNode processReconcile(@RequestParam(name = "paymentInfoTemplates", required = true) String paymentInfoTemplates,
                                       @RequestParam(name = "multipartFiles", required = false) List<MultipartFile> multipartFiles) throws JsonProcessingException {
        List<PaymentMessageDTO> paymentMessageDTO = new ArrayList<>();
        List<PaymentInfoTemplate> PaymentInfoTemplateList = new ObjectMapper().readValue(paymentInfoTemplates, new TypeReference<List<PaymentInfoTemplate>>() {
        });
        String jobId = null; //this will come from scheduler
        ObjectNode response = new ObjectMapper().createObjectNode();
//        String msg = AppConstants.PAYMENT_INITIAL_MSG;
//        paymentMessageDTO.add(PaymentMessageDTO.builder().message(msg).build());
        String msgResponse = customerPaymentInfo.reconcilePayment(PaymentInfoTemplateList, jobId);
        return response.put("message", msgResponse);
    }

    @GetMapping("autoReconcileDetail")
    public ObjectNode getAutoReconcile() {
        ObjectNode response = new ObjectMapper().createObjectNode();
        String msg = customerPaymentInfo.getAutoReconcileTenantDetail();
        return response.put("message", msg);
    }

    @GetMapping("/loadFilterPaymentData")
    public BaseResponse loadFilterPaymentData(@RequestParam(value = "exportDTO", required = false) String exportDTO) {
        return paymentInfoService.loadFilterEmployeeData(exportDTO);
    }

    @GetMapping("/getPaymentExportData")
    public BaseResponse getPaymentExportData(@RequestParam("customerType") String customerType,
                                             @RequestParam("accountId") String customerId,
                                             @RequestParam("period") String period,
                                             @RequestParam("billId") String billId,
                                             @RequestParam("status") String status,
                                             @RequestParam("source") String source,
                                             @RequestParam("error") String error,
                                             @RequestParam("pageNumber") Integer pageNumber,
                                             @RequestParam("pageSize") Integer pageSize) {
        return paymentInfoService.getBillingPaymentExportData(customerType, customerId, period, billId, status, source, error, pageNumber, pageSize);
    }
}
