package com.solar.api.tenant.service.process.customer;

import com.microsoft.azure.storage.StorageException;
import com.solar.api.tenant.mapper.billing.billingHead.PaymentTransactionHeadDetailDTO;
import com.solar.api.tenant.mapper.billing.billingHead.PaymentTransactionLineItemsDetailDTO;
import com.solar.api.tenant.mapper.payment.info.PaymentInfoTemplate;
import com.solar.api.tenant.mapper.payment.info.PaymentTransactionSummaryTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

public interface CustomerPaymentInfo {

    String getCustomerPrePaymentInfo(String subscriptionType, Long subscriptionRateMatrixId, String monthYear,
                                     Long compKey) throws IOException, URISyntaxException, StorageException;

    Double paymentBalance(Long billingHeadId);

    PaymentTransactionSummaryTemplate getPaymentTransactionSummary(Long billingHeadId);

    String processPayment(Long billingHeadId, Double amount, Date tranDate);

    String processPayment(List<PaymentInfoTemplate> paymentInfoTemplates, String jobId, List<MultipartFile> multipartFiles, String subscriptionType);

    Long checkReferenceId(String referenceId);

    String processReverse(List<PaymentInfoTemplate> paymentInfoTemplates, List<MultipartFile> multipartFiles);

    String reconcilePayment(List<PaymentInfoTemplate> paymentInfoTemplates,String jobId);

    String getAutoReconcileTenantDetail();

    String AutoReconcilePayment(String jobId);

    List<PaymentTransactionLineItemsDetailDTO>  verifyReversePaymentWithTenantSetting(List<PaymentTransactionLineItemsDetailDTO> paymentTransactionLineItemsDetailDTOs);

    List<PaymentTransactionLineItemsDetailDTO> verifyUnReconcilePaymentWithTenantSetting(List<PaymentTransactionLineItemsDetailDTO> paymentTransactionLineItemsDetailDTOs);

    Object doAttachmentToPaymentDetailItem(List<MultipartFile> multipartFiles, int fileIndex) throws URISyntaxException, IOException, StorageException ;

    List<PaymentTransactionHeadDetailDTO> roundOffUnpaidPaymentWithCompanyPreference(List<PaymentTransactionHeadDetailDTO> paymentTranLineItemDtlDTOs, List<PaymentTransactionHeadDetailDTO> paymentTranLineItemDtlAcctNoDTOs);

}
