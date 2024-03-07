package com.solar.api.saas.module.com.solar.batch.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.saas.model.JobExecutionParams;
import com.solar.api.saas.model.extended.BatchDefinition;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.saas.module.com.solar.scheduler.model.JobScheduler;
import com.solar.api.tenant.mapper.billing.billingHead.BillingDiscountDTO;
import com.solar.api.tenant.model.process.JobManagerTenant;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.NoSuchJobInstanceException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Async;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;


public interface BatchService {

    /**
     * JOB NOTIFICATION EMAIL
     *
     * @param jobName
     * @param jobId
     * @param stackTrace
     */
    void batchNotification(String jobName, Long jobId, String stackTrace, String subject);

    /**
     * TRIGGER
     * INVOICING BATCH
     * TENANT: NOVEL
     *
     * @param subscriptionCode
     * @param subscriptionRateMatrixIdsCSV
     * @param billingMonth
     * @param date
     * @param type
     * @param compKey
     * @param schedulerId
     */
    void billingBatch(String subscriptionCode, String subscriptionRateMatrixIdsCSV, String billingMonth, Date date,
                      String type, Long compKey, Long schedulerId);

    /**
     * EXECUTION
     * INVOICING BATCH
     * TENANT: NOVEL
     *
     * @param templateName
     * @param rateMatrixHeadIds
     * @param billingMonth
     * @param date
     * @param type
     * @param compKey
     * @param jobManagerTenantId
     * @param jobInstanceId
     * @throws Exception
     */
    void billingBatchJob(String templateName, String rateMatrixHeadIds, String billingMonth, Date date,
                         String type, Long compKey, Long jobManagerTenantId, Long jobInstanceId, Boolean isLegacy) throws Exception;

    /**
     * TRIGGER
     * BILLING CREDITS
     * TENANT: NOVEL
     *
     * @param compKey
     * @param fileName
     * @throws JobParametersInvalidException
     * @throws JobExecutionAlreadyRunningException
     * @throws JobRestartException
     * @throws JobInstanceAlreadyCompleteException
     */
    void BillingCreditsJob(String compKey, String fileName, String header,String timePath) throws JobParametersInvalidException, JobExecutionAlreadyRunningException
            , JobRestartException, JobInstanceAlreadyCompleteException;

    void stavemThroughCSGJob(String compKey, String fileName, String header) throws JobParametersInvalidException, JobExecutionAlreadyRunningException
            , JobRestartException, JobInstanceAlreadyCompleteException;

    void stavemRolesJob(String compKey, String fileName) throws JobParametersInvalidException, JobExecutionAlreadyRunningException
            , JobRestartException, JobInstanceAlreadyCompleteException;

    void TestJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException,
            JobInstanceAlreadyCompleteException;

    void agingReport() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException,
            JobInstanceAlreadyCompleteException;

    void InitiateJobScheduler(String file) throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException;

    int explorer(Long parserId);

    void Batches(String key) throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException;

    Long getLastJobInstanceId(String jobName);

    ObjectNode stopByExecutionId(Long jobExecutionId);

    BatchDefinition findById(Long id);

    void updateExecutionId();

    void publishInvoiceByMonth(String subscriptionCode, String rateMatrixHeadIds, String billingMonth, String type,
                               JobManagerTenant jobManagerTenant) throws Exception;

    void publishInvoicesBatch() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException;

    void publishInvoicesBatchAsync() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException;

    void replaceFlatItemReader(File file) throws IOException, JobParametersInvalidException,
            JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException;

    void rollOverJob(Long jobSchedulerId);
    void getSevenDayDailyWeatherData(Long compKey);
    void getHourlyData(Long compKey);
    void findAllGarInfoForWeatherApi();

    void adhocAllSubscriptionTermination(Long jobSchedulerId);

    void autoAllSubscriptionTermination(Long id) throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException;

    void autoAllSubscriptionTermination() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException;

    void executeNPVBatch() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException;

    void getAllAutoTerminationNotification() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException;

    void analyticalCalculation(JobScheduler jobScheduler);

    void calculateHoursByEmployee(Long compKey, Long employeeId, Long taskId) throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException, NoSuchJobInstanceException;

    void addMonitorReadings(List<JobExecutionParams> jobExecutionParams);

    void addMonitorReadingsBySubscriptions(List<Long> subscriptionIds);

    void addMonitorReadingsBatch(List<JobExecutionParams> jobExecutionParams);

    void generateBillsOnActivation(List<JobExecutionParams> jobExecutionParams);

    void eGaugeGraphDataBatch(List<JobExecutionParams> jobExecutionParams);

    void solrenViewGraphDataBatch(List<JobExecutionParams> jobExecutionParams);

    void eGaugeGraphData();

    /**
     * Workflow
     * Email_Notification
     * Scheduled_Job
     */
    String WFEmailBatch();

    JobManagerTenant findRunningInstance(String jobName, String status);

    void jobCheck(JobScheduler job, List<JobExecutionParams> jobExecutionParams);

    void individualInvoice(Long billingHeadId, Long compKey);

    void emailInvoice(Long billingHeadId, Long compKey);

    void disableProduct(String valueString, Long compKey, Long jobId);

    Map disableProductBatch(String productId, Long compkey, Map response);

    void disableProductBatch(List<JobExecutionParams> jobExecutionParams);

    void disableVariantBatch(List<JobExecutionParams> jobExecutionParams);

    Map disableVariant(String productId, String variantId, Map response, Long compKey);

    void disableVariant(String valueString, Long compKey, Long jobId);

    String manageOneTimeJob(String jobName, String keyString, String value, Long compKey, String message);

    @Async
    void dataIngestion();

    @Async
    void migrateDataIngestion();

    void dataIngestionBatch(List<JobExecutionParams> jobExecutionParams);

    void migrateDataIngestionBatch(List<JobExecutionParams> jobExecutionParams);

    /**
     * Description: Called from the jobScheduler to start the batch, The jobName is saved in execution params
     * currently same function is being used to start batch of  PENDING_BILLS and BULK_CALCULATE jobs
     * Created By: Ibtehaj
     * Created At: 08/04/2023
     *
     * @param jobExecutionParams
     */
    void billsBatchJob(List<JobExecutionParams> jobExecutionParams);

    /**
     * Description: Called from the BatchAnalyzer to submit the batch job,
     * currently same function is being used to submit batch of  PENDING_BILLS and BULK_CALCULATE
     * Created By: Ibtehaj
     * Created At: 08/04/2023
     *
     * @param jobName
     * @param compKey
     * @return
     */
    Map runBillsBatchJob(String jobName, Long compKey);
    /**
     * Description: ADHOC batch job to skip or unskip bills
     * Created By: Ibtehaj
     * Created At: 12/04/2023
     *
     * @param billingHeadIds
     * @param skipFlag
     * @param billSkip
     * @param compKey
     * @return
     */
    void skipBillsBatch(String billingHeadIds, Long skipFlag, Boolean billSkip, Long compKey);
    /**
     * Description: ADHOC batch job to add discount to bills
     * Created By: Ibtehaj
     * Created At: 12/04/2023
     *
     * @param billingHeadIds
     * @param billingDiscountDTO
     * @param compKey
     * @return
     */
    void addDiscountBatch(String billingHeadIds, Long compKey, BillingDiscountDTO billingDiscountDTO);
    /**
     * Description: ADHOC job to either generate invoices or publish invoices
     * Created By: Ibtehaj
     * Created At: 12/04/2023
     *
     * @param billingHeadIds
     * @param jobName
     * @param compKey
     * @return
     */
    void generateAndPublishInvoiceBatch(String billingHeadIds, Long compKey,String jobName);
    void updateProjectStatuses(List<JobExecutionParams> jobExecutionParams);
    void updateProjectStatusesADHOC(String jobName,Long compKey);
    void runBillingADHOCBatch(String jobName, Long compKey,String period);
    void monitorPlatformMonitorBatch(List<JobExecutionParams> jobExecutionParams);
    void generateBillingCreditsADHOC(String date);
    void generateBillingCredits(List<JobExecutionParams> jobExecutionParams);
    void ProjectionJob(String compKey, String fileName, String header, String mongoSubId) throws JobParametersInvalidException, JobExecutionAlreadyRunningException
            , JobRestartException, JobInstanceAlreadyCompleteException;
    void deActivateProjectionBatch(List<JobExecutionParams> jobExecutionParams);

    BaseResponse deActivateProjection(String variantId, String projectionId, Long compKey);

    void deActivateProjection(String valueString, Long compKey, Long jobId);

    void activateProjectionBatch(List<JobExecutionParams> jobExecutionParams);
    BaseResponse activateProjection(String variantId, String projectionId, Long compKey);
    void activateProjection(String variantId, String projectionId, Long compKey, Long jobId);
    void generateProjectProjectionRevenue(List<JobExecutionParams> jobExecutionParams);
    BaseResponse generateProjectProjectionRevenue(String months, Long compKey);
}
