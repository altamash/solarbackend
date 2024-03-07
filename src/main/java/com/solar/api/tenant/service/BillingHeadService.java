package com.solar.api.tenant.service;

import com.solar.api.tenant.mapper.billing.billingHead.*;
import com.solar.api.tenant.model.billing.BillingInvoice.BillingInvoice;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.service.process.billing.BillingService;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface BillingHeadService {

    List<BillingHead> findByBillStatus(String status);

    BillingHead addOrUpdateBillingHead(BillingHead billingHead);

    BillingHead save(BillingHead billingHead);

    BillingHead toUpdateMapper(BillingHead billingHead);

    List<BillingHead> addOrUpdateBillingHeads(List<BillingHead> billingHeads);

    List<BillingHead> addBillingHeadsBySubscriptionAndMonth(List<BillingHead> billingHeads);

    BillingHead findById(Long id);

    List<BillingHead> findAllByIds(List<Long> ids);

    /**
     * LazyLoading Alternate
     *
     * @param id
     * @return
     */
    BillingHead findByIdFetchBillingDetails(Long id);

    List<BillingHead> findByUserAccountId(Long userAccountId);

    List<BillingHead> findByUserAccount(User userAccount);

    List<BillingHead> findBySubscriptionId(Long subscriptionId);

    List<BillingHead> findLastTwelveMonths(List<String> monthYears,Long subscriptionId);

    List<BillingHead> findBySubscriptionIdFetchBillingDetails(Long subscriptionId);

    Map findBySubscriptionIdFetchBillingDetails() throws Exception;

    BillingHead findBySubscriptionIdAndBillingMonthYear(Long subscriptionId, String billingMonthYear);

    BillingHead findByCustProdIdAndBillingMonthYear(String custProdId, String billingMonthYear);

    List<Object> findBySubscriptionIdAndIdGtEqId(Long subscriptionId, Long id);

    List<BillingHead> findBySubscriptionStatus(String subscriptionStatus);

    BillingHead findLastBillHead(Long subscriptionId, Long billHeadId);

    List<BillingHead> findAllWithoutPaymentTransactionHead();

    BillingHead findByInvoice(BillingInvoice invoice);

    List<PaymentTransactionPreprocess> getPreprocessTransactionWithSubscriptionType(String subscriptionType,
                                                                                    String billingMonthYear);

    List<PaymentTransactionHeadDetailDTO> getUnpaidTransactionWithSubscriptionTypeAndMonth(String subscriptionType,
                                                                                           String billingMonthYear);

    List<PaymentTransactionLineItemsDetailMasterDTO> getUnReconciledTransactionWithSubscriptionTypeAndMonth(String subscriptionType,
                                                                                                            String billingMonthYear);

    List<PaymentTransactionLineItemsDetailMasterDTO> getReverseTransactionWithSubscriptionTypeAndMonth(String subscriptionType,
                                                                                                       String billingMonthYear);

    List<PaymentTransactionPreprocess> getPreprocessTransactionWithSubscriptionRateMatrixId(Long subscriptionId,
                                                                                            String billingMonthYear);

    List<PaymentTransactionGraphDTO> getPaymentGraphTransaction(String subscriptionType, String billingYear) throws ParseException;

    List<PaymentTransactionGraphDTO> getPaymentYearlyGraphTransaction(String subscriptionType, String billingYear) throws ParseException;

    List<PaymentTransactionLineItemsDetailDTO> getUnReconciledTransactionForAutoReconcile();

    List<BillingHead> findAll();

    void delete(Long id);

    void deleteAll();


    /**
     * JasperInvoice
     * TestFunction
     */
    List<BillingHead> invoiceBySubscription(Long subscriptionId);

    String skipBillHead(Long billingHeadId, Long skipFlag, Boolean billSkip);

    void skipBillHeadV1(BillingHead billingHead, Long skipFlag, Boolean billSkip);

    void bulkSkipBillHeadV1(String billingHeadIds, Long skipFlag, Boolean billSkip);

    void updateBillingHeadProcessPaymentLock(List<Long> ids, Boolean lockInd);

    void updateBillingHeadProcessReconcileLock(List<Long> ids, Boolean lockInd);

    void updateBillingHeadProcessReverseLock(List<Long> ids, Boolean lockInd);

    List<Long> findByPaymentLockedInd(Boolean paymentLockedInd);

    List<Long> findByReverseLockedInd(Boolean reverseLockedInd);

    List<Long> findByReconcileLockedInd(Boolean reconcileLockedInd);

    Map<String, String> getInvoicePlaceholders(BillingHead billingHead);

    /**
     * Description: called from PendingBillsBatchConfig to extract bills of previous months and
     * save them in calculation details table
     * Created By: Ibtehaj
     * Created At: 08/04/2023
     */
    void extractPendingBillsForCalTracker(String period);

    Map getBillingByStatusData(Map response, List<String> periodList);

    Map getCustomerTypeGroupByBillingStatusData(Map response, List<String> periodList);

    Map getBillingStatusComparisonData(Map response);
    List<BillingHead> saveAll(List<BillingHead> billingHeads);

    CustomerSubscription findCustomerSubscriptionBySubscriptionId(Long SubscriptionId);

    List<BillingHead> getBillingInfo( List<Long> accountId);

    List<BillingHead> manageBillingHeadForProjection(List<CustomerSubscription> customerSubscriptions,List<String> months);
    List<BillingHead> findAllBillingHeadForProjection(List<CustomerSubscription> customerSubscriptions,List<String> months);
    Map<String, String> getProjectionPlaceholders(BillingHead billingHead,List<String> months);
}
