package com.solar.api.tenant.service;

import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.mapper.payment.info.PaymentInfoWrapper;
import com.solar.api.tenant.model.payment.billing.PaymentDetailsView;
import com.solar.api.tenant.model.payment.info.PaymentInfo;
import com.solar.api.tenant.model.paymentDetailView.SearchParams;
import com.solar.api.tenant.model.user.User;

import java.util.List;

public interface PaymentInfoService {

    PaymentInfo addOrUpdate(PaymentInfo paymentInfo);

    PaymentInfo findById(Long id);

    PaymentInfo findByIdNoThrow(Long id);

    List<PaymentInfo> findByUserId(Long userId);

    PaymentInfo findByPortalAccountAndPaymentSrcAlias(User portalAccount, String paymentSrcAlias);

     PaymentInfo findByPortalAccountAndPaymentSource(Long userId, String paymentSrc) ;

     String getMaskedReferenceId(Long userId , String paymentSource);

     PaymentInfoWrapper getPaymentInfoByGardenId(String gardenId, String month, String paymentSource, String billStatus);

    List<PaymentInfo> findAll();

    void delete(Long subscriptionTypeId);

    void deleteAll();

    // ComprehensiveSearch /////////////////////////////////////////////
    List<PaymentDetailsView> comprehensiveSearch(SearchParams searchParams);

    List<PaymentDetailsView> getAllPaymentDetailsView();

    List<String> getAllPaymentModes();

    List<String> getAllPaymentStatus();

    PaymentInfo findByAcctNoAndRoutingNoAndAcctType(String acctNo, String routingNo, String accountType);

    BaseResponse loadFilterEmployeeData(String exportDTO);
    BaseResponse getBillingPaymentExportData(String custType,String custId, String period, String billId,String status, String source, String error,Integer pageNumber, Integer pageSize);
}

