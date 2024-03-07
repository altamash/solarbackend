package com.solar.api.tenant.service.process.billing;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.saas.service.integration.mongo.response.subscription.SubscriptionMapping;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.calculation.CalculationTracker;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHead;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface BillingService {

    String checkForRunningJob(List<Long> rateMatrixHeadIds, List<String> variantIds, String type,
                              String subscriptionCode,
                              String billingMonth, boolean isLegacy);

    String checkForRunningJob(String jobName);

    String checkForRunningJobIndividualInvoice(String jobName);

    //Strictly for INVOICE_PDF job
    String checkForLastJobInstance(List<Long> rateMatrixHeadIds, List<String> variantIds, String type, String subscriptionCode,
                                   String billingMonth, boolean isLegacy);

    //Strictly for INVOICE_PDF job
    Date checkForLastJobInstance(String jobName);

    String getJobNameLegacy(List<Long> rateMatrixHeadIds, String type, String subscriptionCode, String billingMonth);

    String getJobName(List<String> variantIds, String type, String subscriptionCode, String billingMonth);

    JobManagerTenant addJobManagerTenantLegacy(String jobName, String subscriptionCode, List<Long> rateMatrixHeadIds,
                                         String billingMonth, String type, Long compKey);

    JobManagerTenant addJobManagerTenant(String jobName, String subscriptionCode, List<String> variantIds,
                                         String billingMonth, String type, Long compKey);

    JobManagerTenant addJobManagerTenant(String jobName, ObjectNode requestMessage, String jobStatus);

//    String enqueJobBillingByType(String templateName, List<Long> rateMatrixHeadIds, String billingMonth, Date date,
//                                 String type, Long compKey) throws Exception;

    void enqueJobBillingByType(String templateName, List<Long> rateMatrixHeadIds, List<String> variantIds,
                               String billingMonth, Date date,
                               String type, Long compKey, JobManagerTenant jobManager, Boolean isLegacy) throws Exception;

//    String enqueActivation(Long userAccountId, Long subscriptionId) throws ParseException;

    void enqueActivation(Long userAccountId, String subscriptionId, String startDate, JobManagerTenant jobManager,
                         Boolean isLegacy) throws ParseException;

    ObjectNode generateBillsOnActivation(Long userAccountId, String subscriptionId, String start, Boolean isLegacy) throws ParseException;

    ObjectNode bulkActivation(String customToDate, String startDate, String variantId, Boolean isLegacy) throws ParseException;

//    String enqueActivation(String toDate) throws ParseException;

    void enqueActivation(String toDate, String startDate, String variantId, JobManagerTenant jobManager,
                         Boolean isLegacy) throws ParseException;

//    String billingByAction(Long billingHeadId, String action);

    void billingByAction(Long billingHeadId, String action, JobManagerTenant jobManager, Boolean isLegacy);

    Map billingByActionV1(Map response, Long billingHeadId, String action, Boolean isLegacy);
    void bulkBillingByActionV1(List<Long> billingHeadId, String action, Boolean isLegacy) ;
    List<String> generateACHCSV(List<Long> subscriptionRateMatrixIdsCSV, Long ccd, String billingMonthYear,
                                Long compKey, String subscriptionCode, String userName);

    SubscriptionRateMatrixHead findGardenByBillIdLegacy(Long billHeadId);

    SubscriptionMapping findVariantByBillId(Long billHeadId);
    void fillTransStageTables (List<BillingHead> billingHeads);

    /**
     * Description: called from BulkCalculationBatchConfig to perform calculations on pending bills in
     * calculation details table
     * Created By: Ibtehaj
     * Created At: 08/04/2023
     *
     * @param jobId
     */
    void calculatePendingBillsInCalTracker(Long jobId,String period);

}
