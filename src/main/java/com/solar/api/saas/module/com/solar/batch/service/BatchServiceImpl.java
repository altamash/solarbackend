package com.solar.api.saas.module.com.solar.batch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sendgrid.Response;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Utility;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.JobExecutionParams;
import com.solar.api.saas.model.extended.BatchDefinition;
import com.solar.api.saas.model.process.rule.RuleHead;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.tenant.service.alerts.PerformanceAlert;
import com.solar.api.saas.module.com.solar.batch.service.tigo.TigoServiceV2;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.saas.module.com.solar.scheduler.model.EJobCheck;
import com.solar.api.saas.module.com.solar.scheduler.model.JobScheduler;
import com.solar.api.saas.module.com.solar.scheduler.service.JobExecutionParamsService;
import com.solar.api.saas.module.com.solar.scheduler.service.JobSchedulerService;
import com.solar.api.saas.module.com.solar.utility.BatchEngineUtilityService;
import com.solar.api.saas.repository.MasterTenantRepository;
import com.solar.api.saas.repository.RuleHeadRepository;
import com.solar.api.saas.service.EmailService;
import com.solar.api.saas.service.MasterTenantService;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.saas.service.job.EJobName;
import com.solar.api.saas.service.job.EJobStatus;
import com.solar.api.saas.service.workflow.ECommType;
import com.solar.api.tenant.mapper.billing.billingHead.BillingDiscountDTO;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIAuthBody;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.workflow.EWorkflowNotificationLog;
import com.solar.api.tenant.model.workflow.WorkflowNotificationLog;
import com.solar.api.tenant.repository.BillingCreditsRepository;
import com.solar.api.tenant.repository.StavemRolesRepository;
import com.solar.api.tenant.repository.StavemThroughCSGRepository;
import com.solar.api.tenant.repository.weather.WeatherDataRepository;
import com.solar.api.tenant.repository.workflow.WorkflowNotificationLogRepository;
import com.solar.api.tenant.service.job.JobManagerTenantService;
import com.solar.api.tenant.service.preferences.TenantConfigService;
import com.solar.api.tenant.service.process.billing.BillingService;
import com.solar.api.tenant.service.process.billing.EBillingByType;
import com.solar.api.tenant.service.process.billing.invoice.BillInvoiceService;
import com.solar.api.tenant.service.process.billing.postBillilng.PostBillingCalculation;
import com.solar.api.tenant.service.process.billing.postBillilng.PostBillingParserFactory;
import com.solar.api.tenant.service.process.pvmonitor.MonitorWrapperService;
import com.solar.api.tenant.service.process.subscription.billHead.BillHeadAction;
import com.solar.api.tenant.service.process.subscription.termination.SubscriptionTermination;
import com.solar.api.tenant.service.weather.WeatherServiceImpl;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.batch.operations.JobExecutionNotRunningException;
import javax.batch.operations.NoSuchJobExecutionException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Service
public class BatchServiceImpl implements BatchService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private EmailService emailService;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobExplorer jobExplorer;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobManagerTenantService jobManagerTenantService;
    @Autowired
    private MasterTenantRepository masterTenantRepository;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private BatchDefinitionService batchDefinitionService;
    @Autowired
    private BillingCreditsRepository billingCreditsRepository;
    @Autowired
    private StavemThroughCSGRepository stavemThroughCSGRepository;
    @Autowired
    private StavemRolesRepository stavemRolesRepository;
    @Autowired
    private SubscriptionTermination subscriptionTermination;
    @Autowired
    private RuleHeadRepository ruleHeadRepository;
    @Autowired
    private PostBillingParserFactory postBillingParserFactory;
    @Autowired
    private BillHeadAction billHeadAction;
    @Autowired
    private BillingService billingService;
    @Autowired
    private BillInvoiceService billInvoiceService;
    @Autowired
    private MonitorWrapperService monitorWrapperService;
    @Autowired
    private WorkflowNotificationLogRepository workflowNotificationLogRepository;
    @Autowired
    EGaugeService eGaugeService;
    @Autowired
    PerformanceAlert performanceAlert;

    @Autowired
    SolrenviewService solrenviewService;
    @Autowired
    TigoServiceV2 tigoServiceV2;
    @Autowired
    DataExchange dataExchange;
    @Autowired
    private JobSchedulerService jobSchedulerService;
    @Autowired
    private JobExecutionParamsService jobExecutionParamsService;
    @Autowired
    private BatchEngineUtilityService batchEngineUtilityService;
    @Autowired
    private TenantConfigService tenantConfigService;
    @Autowired
    private StageMonitorService stageMonitorService;
    @Autowired
    private MasterTenantService masterTenantService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WeatherDataRepository weatherDataRepository;
    @Autowired
    private WeatherServiceImpl weatherService;

    @Override
    public void batchNotification(String jobName, Long jobId, String stackTrace, String subject) {
        try {
            emailService.batchNotification(jobName, jobId, stackTrace, subject);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * @param subscriptionCode
     * @param subscriptionRateMatrixIdsCSV
     * @param billingMonth
     * @param date
     * @param type
     * @param compKey
     */
    @Async
    @Override
    public void billingBatch(String subscriptionCode, String subscriptionRateMatrixIdsCSV, String billingMonth, Date date,
                             String type, Long compKey, Long schedulerId) {

        //Get TenantSchema
        //TODO: Make method in Utility
        System.out.println(DBContextHolder.getTenantName());
        MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(compKey);
        DBContextHolder.setTenantName(masterTenant.getDbName());

        BatchDefinition batchDefinition = batchDefinitionService.findByJobName(type);
        Map<String, JobParameter> maps = new HashMap<>();
//        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("subscriptionCode", new JobParameter(subscriptionCode));
        maps.put("subscriptionRateMatrixIdsCSV", new JobParameter(subscriptionRateMatrixIdsCSV));
        maps.put("billingMonth", new JobParameter(billingMonth));
        maps.put("date", new JobParameter(date));
        maps.put("type", new JobParameter(type));
        maps.put("compKey", new JobParameter(compKey));
        maps.put("jobScheduleId", new JobParameter(schedulerId != null ? schedulerId : null));
        maps.put("startTime", new JobParameter(System.currentTimeMillis()));
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("startTime", System.currentTimeMillis());
        requestMessage.put("BatchName", batchDefinition.getJobName());
        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName()
                , requestMessage, EJobStatus.RUNNING.toString(), null, LOGGER);
        maps.put("jobId", new JobParameter(jobManagerTenant.getId()));
        JobParameters jobParameters = new JobParameters(maps);

        //triggers the batch
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            LOGGER.error(e.getMessage(), e);
            LOGGER.error(e.toString());
            batchNotification(jobManagerTenant.getJobName(), jobManagerTenant.getBatchId(), e.getMessage(), AppConstants.BATCH_EMAIL_SUBJECT_RUNTIME);
        }
        requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
        requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
        if (jobExecution.getExitStatus().getExitCode().equals("STOPPED") || jobExecution.getExitStatus().getExitCode().equals("UNKNOWN")
                || jobExecution.getExitStatus().getExitCode().equals("FAILED")) {
            jobManagerTenantService.update(jobManagerTenant, jobExecution.getExitStatus().getExitCode(), jobExecution.getJobInstance().getInstanceId(), LOGGER);
        } else {
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobInstance().getInstanceId(), LOGGER);
        }
    }

    /**
     * @param subscriptionCode
     * @param rateMatrixHeadIds
     * @param billingMonth
     * @param date
     * @param type
     * @param compKey
     * @param jobManagerTenantId
     * @param jobInstanceId
     */
    @Override
    public void billingBatchJob(String subscriptionCode, String rateMatrixHeadIds, String billingMonth, Date date,
                                String type, Long compKey, Long jobManagerTenantId, Long jobInstanceId, Boolean isLegacy) {

        String jobNameJC = null;
        ObjectNode requestMessageJC = new ObjectMapper().createObjectNode();
        JobManagerTenant jobManagerTenantJC = new JobManagerTenant();
        JobManagerTenant jobManagerTenant = jobManagerTenantService.findById(jobManagerTenantId);
        if (jobManagerTenant.getBatchId() == null) {
            jobManagerTenant.setBatchId(jobInstanceId);
            jobManagerTenantService.toUpdateMapper(jobManagerTenant);
        }
        List<Long> subscriptionRateMatrixIdsLong = null;
        List<String> variantIds = null;
        if (isLegacy) {
            subscriptionRateMatrixIdsLong =
                    Arrays.stream(rateMatrixHeadIds.split(",")).map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
        } else {
            variantIds =
                    Arrays.stream(rateMatrixHeadIds.split(",")).map(id -> id.trim()).collect(Collectors.toList());
        }
        EBillingByType billingType = EBillingByType.get(type);
        switch (billingType) {
            case GENERATE:
                jobNameJC = billingService.getJobNameLegacy(subscriptionRateMatrixIdsLong, EBillingByType.GENERATE.getType(), subscriptionCode, billingMonth);
                jobManagerTenantJC = billingService.addJobManagerTenantLegacy(jobNameJC, subscriptionCode,
                        subscriptionRateMatrixIdsLong, billingMonth, type, compKey);
                if (rateMatrixHeadIds.isEmpty()) {
                    billHeadAction.billingBySubscriptionType(subscriptionCode, billingMonth, type, jobManagerTenantJC, isLegacy);
                } else {
                    billHeadAction.billingBySubscriptionType(subscriptionCode, subscriptionRateMatrixIdsLong, variantIds, billingMonth, type,
                            jobManagerTenantJC, isLegacy);
                }
                break;
            case INVOICE:
                try {
                    jobNameJC = billingService.getJobNameLegacy(subscriptionRateMatrixIdsLong, EBillingByType.INVOICE.getType(), subscriptionCode, billingMonth);
                    jobManagerTenantJC = billingService.addJobManagerTenantLegacy(jobNameJC, subscriptionCode,
                            subscriptionRateMatrixIdsLong, billingMonth, type, compKey);
                    billInvoiceService.invoiceByMatrixId(subscriptionCode, subscriptionRateMatrixIdsLong, billingMonth, date, type,
                            jobManagerTenantJC, compKey);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    LOGGER.error(e.toString());
                    jobManagerTenantService.update(jobManagerTenantJC, EJobStatus.FAILED.toString(), jobInstanceId, LOGGER);
                }
                break;
//            case INVOICE_PDF:
//                jobNameJC = billingService.getJobNameLegacy(subscriptionRateMatrixIdsLong, EBillingByType.INVOICE_PDF.getType(), subscriptionCode, billingMonth);
//                jobManagerTenantJC = billingService.addJobManagerTenantLegacy(jobNameJC, subscriptionCode,
//                        subscriptionRateMatrixIdsLong, billingMonth, type, compKey);
//                billInvoiceService.generatePDFByMatrixId(subscriptionCode, subscriptionRateMatrixIdsLong, billingMonth, type,
//                        jobManagerTenantJC);
//                break;
            case PUBLISH_INVOICE:
                try {
                    publishInvoiceByMonth(subscriptionCode, rateMatrixHeadIds, billingMonth, type,
                            jobManagerTenant);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    LOGGER.error(e.toString());
                }
                break;
            case POST_BILLING_CALCULATIONS:
                RuleHead ruleHead = ruleHeadRepository.findByBillingCodeAndRuleDependency("PICAL", subscriptionCode);
                if (ruleHead == null) {
                    throw new NotFoundException("RuleHead not found for action " + type);
                }
                PostBillingCalculation postBillingCalculation =
                        postBillingParserFactory.getParser(ruleHead.getMethod());
                if (rateMatrixHeadIds.isEmpty()) {
                    postBillingCalculation.calculate(subscriptionCode, billingMonth, ruleHead.getId(), type,
                            jobManagerTenant);
                } else {
                    postBillingCalculation.calculate(subscriptionCode, subscriptionRateMatrixIdsLong, billingMonth,
                            ruleHead.getId(), type, jobManagerTenant);
                }
        }


    }

    @Async
    @Override
    public void BillingCreditsJob(String compKey, String fileName, String header, String timePath) {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.BILLING_CREDITS);
        AtomicInteger readCount = new AtomicInteger();
        AtomicInteger filterCount = new AtomicInteger();
        AtomicInteger writeCount = new AtomicInteger();
        AtomicReference<String> step1Exception = new AtomicReference<>("");
        AtomicReference<String> step2Exception = new AtomicReference<>("");
        AtomicReference<String> step3Exception = new AtomicReference<>("");
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();
        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName()
                , requestMessage, EJobStatus.RUNNING.toString(), null, LOGGER);
        JobParameters parameters = new JobParameters(mapJobParameters(compKey, jobManagerTenant, fileName, header, timePath, null));
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();
            stepExecutions.forEach(step -> {
                if (step.getStepName().equals("EXTRACTION")) {
                    readCount.set(step.getReadCount());
                    filterCount.set(step.getFilterCount());
                    writeCount.set(step.getWriteCount());
                    step1Exception.set(step.getExitStatus().getExitCode());
                }
            });
            String fileNameAfter = String.valueOf(jobExecution.getJobParameters().getParameters().get("fileName"));
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
            requestMessage.put("File name", fileNameAfter);
            requestMessage.put("Lines read", String.valueOf(readCount));
            requestMessage.put("Lines filtered", String.valueOf(filterCount));
            requestMessage.put("Lines written", String.valueOf(writeCount));
            requestMessage.put("Lines processed", String.valueOf(billingCreditsRepository.count()));
            requestMessage.put("Step1 ExitCode", String.valueOf(step1Exception));
            requestMessage.put("Step2 ExitCode", String.valueOf(step2Exception));
            requestMessage.put("Step3 ExitCode", String.valueOf(step3Exception));
//        requestMessage.put("Execution Id", jobExecution.getJobId());
            jobManagerTenant.setRequestMessage(requestMessage.toPrettyString());
            jobManagerTenantService.update(jobManagerTenant, jobExecution.getExitStatus().getExitCode(),
                    jobExecution.getJobId(), LOGGER);
        } catch (Exception e) {
            catchLogs(batchDefinition, null, responseMessage, e.getMessage(), jobManagerTenant);

        }
        //***   Mark Status ***//
        if (billingCreditsRepository.findAll().isEmpty()) {
            jobExecution.setStatus(BatchStatus.FAILED);
            jobRepository.update(jobExecution);
        }
        //***               ***//
    }

    private Map<String, JobParameter> mapJobParameters(String compKey, JobManagerTenant jobManagerTenant,
                                                       String fileName, String header, String timePath, String mongoSubId) {
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("fileName", new JobParameter(fileName));
        maps.put("CompanyKey", new JobParameter(compKey));
        maps.put("headers", new JobParameter(header));
        maps.put("JobManagerId", new JobParameter(jobManagerTenant.getId()));
        maps.put("timePath", new JobParameter((timePath)));
        maps.put("mongoSubId", new JobParameter(mongoSubId));
        return maps;
    }

    @Override
    public void stavemThroughCSGJob(String compKey, String fileName, String header) {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.STAVEM_THROUGH_CSG);
        AtomicInteger readCount = new AtomicInteger();
        AtomicInteger filterCount = new AtomicInteger();
        AtomicInteger writeCount = new AtomicInteger();
        AtomicReference<String> step1Exception = new AtomicReference<>("");
        AtomicReference<String> step2Exception = new AtomicReference<>("");
        AtomicReference<String> step3Exception = new AtomicReference<>("");
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("fileName", new JobParameter(fileName));
        maps.put("Company Key", new JobParameter(compKey));
        maps.put("headers", new JobParameter(header));
        JobParameters parameters = new JobParameters(maps);
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName()
                , requestMessage, EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            LOGGER.error(e.toString());

        }
        Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();
        stepExecutions.forEach(step -> {
            if (step.getStepName().equals("EXTRACT")) {
                readCount.set(step.getReadCount());
                filterCount.set(step.getFilterCount());
                writeCount.set(step.getWriteCount());
                step1Exception.set(step.getExitStatus().getExitDescription());
//            } else if(step.getStepName().equals("VALIDATE")){
//                step2Exception.set(step.getExitStatus().getExitDescription());
//            } else if(step.getStepName().equals("LOAD")) {
//                step3Exception.set(step.getExitStatus().getExitDescription());
            }
        });
        String fileNameAfter = String.valueOf(jobExecution.getJobParameters().getParameters().get("fileName"));
        requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
        requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
        requestMessage.put("File name", fileNameAfter);
        requestMessage.put("Lines read", String.valueOf(readCount));
        requestMessage.put("Lines filtered", String.valueOf(filterCount));
        requestMessage.put("Lines written", String.valueOf(writeCount));
        requestMessage.put("Lines processed", String.valueOf(stavemThroughCSGRepository.count()));
        requestMessage.put("Step1 ExitCode", String.valueOf(step1Exception));
        requestMessage.put("Step2 ExitCode", String.valueOf(step2Exception));
        requestMessage.put("Step3 ExitCode", String.valueOf(step3Exception));
//        requestMessage.put("Execution Id", jobExecution.getJobId());
        jobManagerTenant.setRequestMessage(requestMessage.toPrettyString());
        jobManagerTenantService.update(jobManagerTenant, jobExecution.getExitStatus().getExitCode(),
                jobExecution.getJobId(), LOGGER);
    }

    @Override
    public void stavemRolesJob(String compKey, String fileName) {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.STAVEM_ROLES);
        AtomicInteger readCount = new AtomicInteger();
        AtomicInteger filterCount = new AtomicInteger();
        AtomicInteger writeCount = new AtomicInteger();
        AtomicReference<String> step1Exception = new AtomicReference<>("");
        AtomicReference<String> step2Exception = new AtomicReference<>("");
        AtomicReference<String> step3Exception = new AtomicReference<>("");
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("fileName", new JobParameter(fileName));
        maps.put("Company Key", new JobParameter(compKey));
        JobParameters parameters = new JobParameters(maps);
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName()
                , requestMessage, EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            LOGGER.error(e.toString());

        }
        Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();
        stepExecutions.forEach(step -> {
            if (step.getStepName().equals("EXTRACT")) {
                readCount.set(step.getReadCount());
                filterCount.set(step.getFilterCount());
                writeCount.set(step.getWriteCount());
                step1Exception.set(step.getExitStatus().getExitDescription());
//            } else if(step.getStepName().equals("VALIDATE")){
//                step2Exception.set(step.getExitStatus().getExitDescription());
//            } else if(step.getStepName().equals("LOAD")) {
//                step3Exception.set(step.getExitStatus().getExitDescription());
            }
        });
        String fileNameAfter = String.valueOf(jobExecution.getJobParameters().getParameters().get("fileName"));
        requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
        requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
        requestMessage.put("File name", fileNameAfter);
        requestMessage.put("Lines read", String.valueOf(readCount));
        requestMessage.put("Lines filtered", String.valueOf(filterCount));
        requestMessage.put("Lines written", String.valueOf(writeCount));
        requestMessage.put("Lines processed", String.valueOf(stavemRolesRepository.count()));
        requestMessage.put("Step1 ExitCode", String.valueOf(step1Exception));
        requestMessage.put("Step2 ExitCode", String.valueOf(step2Exception));
        requestMessage.put("Step3 ExitCode", String.valueOf(step3Exception));
//        requestMessage.put("Execution Id", jobExecution.getJobId());
        jobManagerTenant.setRequestMessage(requestMessage.toPrettyString());
        jobManagerTenantService.update(jobManagerTenant, jobExecution.getExitStatus().getExitCode(),
                jobExecution.getJobId(), LOGGER);
    }

    @Async
    @Override
    public void TestJob() {
        Map<String, JobParameter> testMaps = new HashMap<>();
        testMaps.put("testTime", new JobParameter(System.currentTimeMillis()));
        JobParameters testParameters = new JobParameters(testMaps);
        ObjectNode testRequestMessage = new ObjectMapper().createObjectNode();
        testRequestMessage.put("testStartTime", System.currentTimeMillis());
        testRequestMessage.put("testFileName", "BillingCredits.csv");
        JobManagerTenant testJobManagerTenant =
                jobManagerTenantService.add(EJobName.BILLING_CREDITS_ETL_BATCH.toString(), testRequestMessage,
                        EJobStatus.RUNNING.toString(), null, LOGGER);
//        JobExecution testJobExecution = jobLauncher.run((Job) applicationContext.getBean("TestBatch"), testParameters);
//        jobManagerTenantService.update(testJobManagerTenant, EJobStatus.COMPLETED.toString(),
//                testJobExecution.getJobId(), LOGGER);
    }

    @Override
    public void agingReport() {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.AGING_REPORT);
        Map<String, JobParameter> testMaps = new HashMap<>();
        testMaps.put("startTime", new JobParameter(System.currentTimeMillis()));
        JobParameters testParameters = new JobParameters(testMaps);
        ObjectNode testRequestMessage = new ObjectMapper().createObjectNode();
        testRequestMessage.put("startTime", System.currentTimeMillis());
        JobManagerTenant testJobManagerTenant =
                jobManagerTenantService.add(EJobName.AGING_REPORT_BATCH.toString(), testRequestMessage,
                        EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), testParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            LOGGER.error(e.toString());

        }
        jobManagerTenantService.update(testJobManagerTenant, EJobStatus.COMPLETED.toString(),
                jobExecution.getJobId(), LOGGER);
    }

    @Override
    public void InitiateJobScheduler(String file) throws JobParametersInvalidException,
            JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        BillingCreditsJob("1001", file, "", dtf.format(now));
    }

    @Override
    public int explorer(Long parserId) {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.BILLING_CREDITS);
        JobInstance jobInstance = jobExplorer.getLastJobInstance(batchDefinition.getJobName());
        if (jobInstance != null) {
            List<JobExecution> jobExecution = jobExplorer.getJobExecutions(jobInstance);
            if (!jobExecution.isEmpty()) {
                if (jobExecution.get(0).getStatus().equals("STARTED")) {
                    return 0;
                }
            }
        }
        return 1;
    }

    @Async
    @Override
    public void Batches(String key) throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException {
        Map<String, JobParameter> mappedparam = new HashMap<>();
        mappedparam.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters parameters = new JobParameters(mappedparam);
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("startTime", System.currentTimeMillis());
        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(EJobName.BILLING_CREDITS_ETL_BATCH.toString()
                , requestMessage, EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = jobLauncher.run((Job) applicationContext.getBean(key), parameters);
        jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(),
                LOGGER);
    }

    @Override
    public Long getLastJobInstanceId(String jobName) {
        JobInstance jobInstance = jobExplorer.getLastJobInstance(AppConstants.BILLING_CREDITS_IMPORT_JOB);
        if (jobInstance != null) {
            return jobInstance.getInstanceId();
        }
        return 0L;
    }

    @Override
    public ObjectNode stopByExecutionId(Long jobExecutionId) {
        ObjectNode response = new ObjectMapper().createObjectNode();
        try {
//            jobOperator.stop(jobExecutionId);
            response.put("message", "Job Execution Stopped!");
        } catch (NoSuchJobExecutionException e) {
            LOGGER.info(e.getMessage());
            response.put("executionId", jobExecutionId);
            response.put("error", "No Such Job Execution");
        } catch (JobExecutionNotRunningException e) {
            LOGGER.info(e.getMessage());
            response.put("executionId", jobExecutionId);
            response.put("error", "Job Execution Not Running");
        }
        return response;
    }

    @Override
    public BatchDefinition findById(Long id) {
        return batchDefinitionService.findById(AppConstants.BILLING_CREDITS);
    }

    @Override
    public void updateExecutionId() {
        JobInstance jobInstance = jobExplorer.getLastJobInstance(AppConstants.BILLING_CREDITS_IMPORT_JOB);
        if (jobInstance != null) {
            JobExecution jobExecution = jobExplorer.getLastJobExecution(jobInstance);
        }
    }

    @Override
    public void publishInvoiceByMonth(String subscriptionCode, String rateMatrixHeadIds, String billingMonth,
                                      String type, JobManagerTenant jobManagerTenant) {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.PUBLISH_INVOICE);
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("subscriptionCode", new JobParameter(subscriptionCode));
        maps.put("rateMatrixHeadIds", new JobParameter(String.valueOf(rateMatrixHeadIds)));
        maps.put("billingMonth", new JobParameter(billingMonth));
        maps.put("jobId", new JobParameter(jobManagerTenant.getId()));
        maps.put("type", new JobParameter(type));
        JobParameters parameters = new JobParameters(maps);
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("startTime", System.currentTimeMillis());
        requestMessage.put("BatchName", batchDefinition.getJobName());
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);
        } catch (NoSuchBeanDefinitionException | JobExecutionAlreadyRunningException | JobRestartException |
                 JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            catchLogs(batchDefinition, null, responseMessage, e.getMessage(), jobManagerTenant);
        }
    }

    @Async
    @Override
    public void publishInvoicesBatch() {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.PUBLISH_INVOICE);
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters parameters = new JobParameters(maps);
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("startTime", System.currentTimeMillis());
        requestMessage.put("BatchName", batchDefinition.getJobName());
        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(),
                requestMessage, EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            LOGGER.error(e.toString());

        }
        jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(),
                LOGGER);
    }

    @Async
    @Override
    public void publishInvoicesBatchAsync() throws
            JobParametersInvalidException, JobExecutionAlreadyRunningException
            , JobRestartException, JobInstanceAlreadyCompleteException {
        publishInvoicesBatch();
    }

    @Override
    public void replaceFlatItemReader(File file) throws IOException, JobParametersInvalidException,
            JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        String fileString = Utility.convertFileToBase64(file);
        File base64ToFile = Utility.convertBase64ToCSV(fileString);
        System.out.println(base64ToFile);
        applicationContext.getBean("BillingCreditsBatchAddFile");
    }

    @Override
    public void rollOverJob(Long jobSchedulerId) {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.ROLL_OVER_ALL);
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("jobScheduleId", new JobParameter(jobSchedulerId));
        JobParameters parameters = new JobParameters(maps);
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        requestMessage.put("jobScheduleId", jobSchedulerId);
        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, jobSchedulerId, responseMessage, e.getMessage(), jobManagerTenant);
        }
    }

    /**
     * Batch Trigger
     *
     * @param compKey
     */
    @Override
    public void getSevenDayDailyWeatherData(Long compKey) {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.WEATHER_API_FOR_SEVEN_DAY);
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("compKey", new JobParameter(compKey));
        JobParameters parameters = new JobParameters(maps);
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        requestMessage.put("compKey", compKey);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, compKey, responseMessage, e.getMessage(), null);
        }
    }

    @Override
    public void getHourlyData(Long compKey) {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.WEATHER_API_FOR_HOURLY_DATA_STORING_IN_DATABASE_ID);
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("compKey", new JobParameter(compKey));
        JobParameters parameters = new JobParameters(maps);
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        requestMessage.put("compKey", compKey);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, compKey, responseMessage, e.getMessage(), null);
        }
    }

    public void findAllGarInfoForWeatherApi() {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.WEATHER_API_FOR_FETCHING_SEVEN_DAY);
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters parameters = new JobParameters(maps);
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, null, responseMessage, e.getMessage(), null);
        }
    }


    @Override
    public void adhocAllSubscriptionTermination(Long jobSchedulerId) {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.ADHOC_SUBSCRIPTION_TERMINATION_ID);
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("jobScheduleId", new JobParameter(jobSchedulerId));
        JobParameters parameters = new JobParameters(maps);
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        requestMessage.put("jobScheduleId", jobSchedulerId);
        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, jobSchedulerId, responseMessage, e.getMessage(), jobManagerTenant);
        }
    }

    @Override
    public void autoAllSubscriptionTermination(Long jobSchedulerId) {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.AUTO_SUBSCRIPTION_TERMINATION_ID);
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("jobScheduleId", new JobParameter(jobSchedulerId));
        JobParameters parameters = new JobParameters(maps);
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            LOGGER.error(e.toString());

        }
        jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);
    }

    @Override
    public void autoAllSubscriptionTermination() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.AUTO_SUBSCRIPTION_TERMINATION_ID);
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters parameters = new JobParameters(maps);
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            LOGGER.error(e.toString());

        }
        jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);
    }

    /**
     * Weekly Automated Analytics
     *
     * @throws JobParametersInvalidException
     * @throws JobExecutionAlreadyRunningException
     * @throws JobRestartException
     * @throws JobInstanceAlreadyCompleteException
     */
    @Override
    public void executeNPVBatch() {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.NPVBatch);
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters parameters = new JobParameters(maps);
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
//        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage, EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            LOGGER.error(e.toString());

        }
//        jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);
    }

    @Override
    public void getAllAutoTerminationNotification() throws
            JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        subscriptionTermination.getAllAutoTerminationNotification();
    }

    @Override
    public void analyticalCalculation(JobScheduler jobScheduler) {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.LIFETIME_CALCULATION);
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("jobScheduleId", new JobParameter(jobScheduler.getId()));
        JobParameters parameters = new JobParameters(maps);
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        requestMessage.put("jobScheduleId", jobScheduler.getId());
//        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage, EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            LOGGER.error(e.toString());
            batchNotification(batchDefinition.getJobName(), jobScheduler.getId(), e.getMessage(), AppConstants.BATCH_EMAIL_SUBJECT_RUNTIME);
        }
//        jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);
    }

    @Override
    public void calculateHoursByEmployee(Long compKey, Long employeeId, Long taskId) {

        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.HOURS_CALCULATION);
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("EmployeeId", new JobParameter(employeeId));
        maps.put("TaskId", new JobParameter(taskId));
        maps.put("CompKey", new JobParameter(compKey));
        JobParameters parameters = new JobParameters(maps);
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            LOGGER.error(e.toString());

        }
    }

    @Async
    @Override
    public void addMonitorReadings(List<JobExecutionParams> jobExecutionParams) {
        //To be fetch from jobExecParams
//        SLXR,SLXC,SLSR,SLSC,GWDC,GWDR
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
            String dateTime = dateFormat.format(new Date()).concat(" 00:00:00");
            String dateTimeParam = jobExecutionParams.stream().filter(params -> params.getKeyString().equals("date")).findFirst().orElse(null).getValueString();
            String time = dateTimeParam == null || dateTimeParam.equals("") ? dateTime : dateTimeParam + " 00:00:00";
            List<String> monitorPlatforms = jobExecutionParams.stream().filter(params -> params.getKeyString().equals("monitorPlatform")).map(param -> param.getValueString()).collect(Collectors.toList());

            Optional<TenantConfig> tenantConfig = tenantConfigService.findByParameter(Constants.TENANT_CONFIG_CATEGORY.PARAM_ISMONGOENABLED);
            if (tenantConfig.isPresent() && tenantConfig.get().getText().equals("1")) {
                monitorPlatforms.forEach(monitorPlatform -> {
                    List<String> subscriptionIds = stageMonitorService.getAllSubscriptions(monitorPlatform).stream()
                            .map(ExtDataStageDefinition::getSubsId).collect(Collectors.toList());
                    monitorWrapperService.addMonitorReadingsMongo(MonitorAPIAuthBody.builder().subscriptionIdsMongo(subscriptionIds).fromDateTime(time).build());
                });
            } else {
                List<Long> subscriptionIds = billInvoiceService.findBySubscriptionTypesIn(Arrays.stream(Objects.requireNonNull(jobExecutionParams.stream()
                                .filter(params -> params.getKeyString().equals(AppConstants.subscriptionType_PW))
                                .findFirst().orElse(null)).getValueString().split(","))
                        .map(String::trim).collect(Collectors.toList()));

                monitorWrapperService.addMonitorReadings(MonitorAPIAuthBody.builder().subscriptionIds(subscriptionIds).fromDateTime(time).build());
            }
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }

    }

    @Override
    public void addMonitorReadingsBySubscriptions(List<Long> subscriptionIds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
        String dateTime = dateFormat.format(new Date()).concat(" 00:00:00");
        monitorWrapperService.addMonitorReadings(MonitorAPIAuthBody.builder()
                .subscriptionIds(subscriptionIds)
                .time(dateTime)
                .build());
    }

    @Override
    public void addMonitorReadingsBatch(List<JobExecutionParams> jobExecutionParams) {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.ADD_MONITOR_READINGS_ID);
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("jobScheduleId", new JobParameter(jobExecutionParams.get(0).getScheduledJobId()));
        maps.put(AppConstants.subscriptionType_PW, new JobParameter(AppConstants.subscriptionType_PW));
        JobParameters parameters = new JobParameters(maps);
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        requestMessage.put("jobScheduleId", jobExecutionParams.get(0).getScheduledJobId());
        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, jobExecutionParams.get(0).getScheduledJobId(), responseMessage, e.getMessage(), jobManagerTenant);
        }
    }

    @Override
    public void generateBillsOnActivation(List<JobExecutionParams> jobExecutionParams) {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.GENERATE_BILLS_ON_ACTIVATION_ID);
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("jobScheduleId", new JobParameter(jobExecutionParams.get(0).getScheduledJobId()));
        JobParameters parameters = new JobParameters(maps);
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        requestMessage.put("jobScheduleId", jobExecutionParams.get(0).getScheduledJobId());
        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, jobExecutionParams.get(0).getScheduledJobId(), responseMessage, e.getMessage(), jobManagerTenant);
        }
    }

    @Override
    public void eGaugeGraphDataBatch(List<JobExecutionParams> jobExecutionParams) {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.EGAUGE_BATCH_ID);

        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("jobScheduleId", new JobParameter(jobExecutionParams.get(0).getScheduledJobId()));
        JobParameters parameters = new JobParameters(maps);

        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        requestMessage.put("jobScheduleId", jobExecutionParams.get(0).getScheduledJobId());

        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = null;
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, jobExecutionParams.get(0).getScheduledJobId(), responseMessage, e.getMessage(), jobManagerTenant);
        }
    }

    @Override
    public void solrenViewGraphDataBatch(List<JobExecutionParams> jobExecutionParams) {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.SOLRENVIEW_BATCH_ID);

        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("jobScheduleId", new JobParameter(jobExecutionParams.get(0).getScheduledJobId()));
        JobParameters parameters = new JobParameters(maps);

        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        requestMessage.put("jobScheduleId", jobExecutionParams.get(0).getScheduledJobId());

        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = null;
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, jobExecutionParams.get(0).getScheduledJobId(), responseMessage, e.getMessage(), jobManagerTenant);
        }
    }

    @Async
    @Override
    public void eGaugeGraphData() {
        try {
            BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.EGAUGE_BATCH_ID);

            Map<String, JobParameter> maps = new HashMap<>();
            maps.put("time", new JobParameter(System.currentTimeMillis()));
            JobParameters parameters = new JobParameters(maps);

            ObjectNode requestMessage = new ObjectMapper().createObjectNode();
            requestMessage.put("Start Time", System.currentTimeMillis());
            requestMessage.put("Job Name", batchDefinition.getJobName());

            JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                    EJobStatus.RUNNING.toString(), null, LOGGER);
            JobExecution jobExecution = null;
            ObjectNode responseMessage = new ObjectMapper().createObjectNode();
            try {
                jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
                requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
                requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
                jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

            } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                     JobParametersInvalidException e) {
                catchLogs(batchDefinition, null, responseMessage, e.getMessage(), jobManagerTenant);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    @Override
    public String WFEmailBatch() {
        List<WorkflowNotificationLog> workflowNotificationLogList =
                workflowNotificationLogRepository.findByCommTypeAndStatus(ECommType.e.name(), EWorkflowNotificationLog.PENDING.name());
        workflowNotificationLogList.forEach(l -> {
            try {
                Response response = emailService.sendEmail(new Email(l.getRecipient().getEmailAddress()), new Content("text/html", l.getMessage()),
                        "Test Email WorkFlow Notification");
//                200	No error
//                201	Successfully created
//                204	Successfully deleted
//                400	Bad request
//                401	Requires authentication
//                403   From address doesn't match Verified Sender Identity.
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
        return null;
    }

    @Override
    public JobManagerTenant findRunningInstance(String jobName, String status) {
        Long id = jobManagerTenantService.findIdOfLastJobByJobNameAndStatus(jobName, status);
        if (id != null) {
            return jobManagerTenantService.findByIdNoThrow(id);
        } else {
            return null;
        }
    }

    @Async
    @Override
    public void individualInvoice(Long billingHeadId, Long compKey) {

        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.INDIVIDUAL_INVOICING);

        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("billingHeadId", new JobParameter(billingHeadId));
        maps.put("compKey", new JobParameter(compKey));

        JobParameters parameters = new JobParameters(maps);

        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();

        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        requestMessage.put("billingHeadId", billingHeadId);

        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));

            responseMessage.put("Message", "Bill with ID: " + billingHeadId + " has been invoiced.");
            jobManagerTenant.setResponseMessage(responseMessage.toPrettyString());
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, null, responseMessage, e.getMessage(), jobManagerTenant);
        }

    }

    @Override
    public void emailInvoice(Long billingHeadId, Long compKey) {

        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.EMAIL_INVOICING);

        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("billingHeadId", new JobParameter(billingHeadId));
        maps.put("compKey", new JobParameter(compKey));

        JobParameters parameters = new JobParameters(maps);

        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();

        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        requestMessage.put("billingHeadId", billingHeadId);

        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));

            responseMessage.put("Message", "Email Sent");
            jobManagerTenant.setResponseMessage(responseMessage.toPrettyString());
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, null, responseMessage, e.getMessage(), jobManagerTenant);
        }
    }

    /**
     * @param job
     * @param jobExecutionParams
     * @author : Shariq
     * Updated at : 18-07-2023
     * Updated By: Shariq
     */
    @Override
    public void jobCheck(JobScheduler job, List<JobExecutionParams> jobExecutionParams) {
        LOGGER.info("Entering jobCheck for: {}", job.getJobName());
        EJobCheck eJobCheck = EJobCheck.get(job.getJobName());
        switch (eJobCheck) {
            case ACTIVATION:
                break;
            case ROLL_OVER_ALL:
                rollOverJob(job.getId());
                break;
            case AUTO_SUBSCRIPTION_TERMINATION:
                autoAllSubscriptionTermination(job.getId());
                break;
            case ADHOC_SUBSCRIPTION_TERMINATION:
                adhocAllSubscriptionTermination(job.getId());
                break;
            case NPV:
                executeNPVBatch();
                break;
            case ANALYTICAL_CALCULATION:
                analyticalCalculation(job);
                break;
            case ADD_MONITOR_READINGS:
                addMonitorReadingsBatch(jobExecutionParams);
                break;
            case GENERATE_BILLS_ON_ACTIVATION:
                generateBillsOnActivation(jobExecutionParams);
                break;
            case EGAUGE_GRAPH_DATA:
                eGaugeService.getEGaugeData();
                break;
            case SOLRENVIEW_GRAPH_DATA:
                solrenviewService.getSVData();
                break;
            case DATA_INGESTION_BATCH:
                dataIngestionBatch(jobExecutionParams);
                break;
            case MIGRATE_DATA_INGESTION_BATCH:
                migrateDataIngestionBatch(jobExecutionParams);
                break;
            case DISABLE_VARIANT:
                disableVariantBatch(jobExecutionParams);
                break;
            case DISABLE_PRODUCT:
                disableProductBatch(jobExecutionParams);
                break;
            case PENDING_BILLS:
                billsBatchJob(jobExecutionParams);
                break;
            case BULK_CALCULATE:
                billsBatchJob(jobExecutionParams);
                break;
            case UPDATE_PROJECT_STATUS:
                updateProjectStatuses(jobExecutionParams);
                break;
            case SOLAX_GRAPH_DATA:
            case SOLIS_GRAPH_DATA:
            case GOODWEE_GRAPH_DATA:
                monitorPlatformMonitorBatch(jobExecutionParams);
                break;
            case TIGO_GRAPH_DATA:
                tigoServiceV2.getMinuteData(null, null,null,false);
                break;
            case SOLAREDGE_GRAPH_DATA:
                //Arguments for the following job are hardcoded for half hourly execution
                monitorWrapperService.addMonitorReadingsMongo(null, List.of(Constants.RATE_CODES.BRAND_SOLAR_EDGE), null, null, true);
                break;
            case ENPHASE_GRAPH_DATA:
                //Arguments for the following job are hardcoded for 15 minutes execution
                monitorWrapperService.addMonitorReadingsMongo(null, List.of(Constants.RATE_CODES.BRAND_ENPHASE), null, null, true);
                break;
            case DEACTIVATE_PROJECTION:
                deActivateProjectionBatch(jobExecutionParams);
                break;
            case ACTIVATE_PROJECTION:
                activateProjectionBatch(jobExecutionParams);
                break;
            case BILLING_CREDITS_BATCH:
                generateBillingCredits(jobExecutionParams);
                break;
            case WEATHER_API:
                getSevenDayDailyWeatherData(job.getTenantId());
                break;
            case WEATHER_API_FOR_STORING_HOURLY_DATA_IN_DATABASE:
                getHourlyData(job.getTenantId());
                break;
            case WEATHER_API_TO_FETCH_DATA:
                findAllGarInfoForWeatherApi();
                break;
            case PROJECT_PROJECTION_REVENUE:
                generateProjectProjectionRevenue(jobExecutionParams);
                break;
            case UNDER_PERFORMANCE_JOB:
                performanceAlert.generate();
                break;
        }
        LOGGER.info("Exiting jobCheck for: {}", job.getJobName());
//        batchNotification(job.getJobName(), job.getId(), " With Parameters: " + jobExecutionParams, " Triggered at: " + new Date());
    }

    private void catchLogs(BatchDefinition batchDefinition, Long jobSchedulerId, ObjectNode responseMessage, String e, JobManagerTenant jobManagerTenant) {
        LOGGER.error(e);
        batchNotification(batchDefinition.getJobName(), jobSchedulerId, e, AppConstants.BATCH_EMAIL_SUBJECT_RUNTIME);
        responseMessage.put("ERROR: ", "Batch not started");
        responseMessage.put("Batch Definition Id: ", batchDefinition.getId());
        responseMessage.put("Batch Name: ", batchDefinition.getJobName());
        responseMessage.put("Batch Bean name: ", batchDefinition.getBean());
        jobManagerTenant.setStatus(EJobStatus.FAILED.toString());
        jobManagerTenant.setResponseMessage(responseMessage.toPrettyString());
        jobManagerTenantService.toUpdateMapper(jobManagerTenant);
    }

    @Override
    public Map disableProductBatch(String productId, Long compKey, Map response) {
        String message = "";
        String valueString = productId;
        String keyString = AppConstants.productId;
        Integer count = dataExchange.getActiveCount(productId, null);
        if (count > 0) {
            message = "Can not mark product as disable due to active gardens/subscriptions";
            response = Utility.generateResponseMap(response, HttpStatus.UNPROCESSABLE_ENTITY.toString(), message, null);
        } else {
            //TODO create and add disbale batch method here
            message = manageOneTimeJob(EJobName.DISABLE_PRODUCT.name(), keyString, valueString, compKey, message);
            response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), message, null);
        }
        return response;
    }

    @Async
    @Override
    public void disableProduct(String valueString, Long compKey, Long jobId) {
        JobManagerTenant jobManagerTenant;
        jobManagerTenant = jobManagerTenantService.findById(jobId);
        List<String> valueList;
        valueList = new ArrayList<>(Arrays.asList(valueString.split(",")));
        List<Map<String, String>> mapList = new ArrayList<>();
        valueList.stream().forEach(value -> {
            String productId = value;
            String status = "";
            String message = null;
            Map<String, String> map = new HashMap<>();
            map.put(AppConstants.productId, productId);
            Integer count = dataExchange.getActiveCount(productId, null);
            if (count > 0) {
                message = "product has active / in-active gardens/subscriptions";
                status = EJobStatus.FAILED.toString();
                dataExchange.removeDisableDate(productId, null);
            } else {
                message = dataExchange.disableProduct(productId, LocalDateTime.now().toString()).getMessage();
                if (message != null) {
                    message = "Product has been disabled successfully";
                    status = "SUCCESS";
                } else {
                    message = "Unable to disable product";
                    status = EJobStatus.FAILED.toString();
                }
            }
            map.put("Status", status);
            map.put("Message", message);
            mapList.add(map);
        });
        jobManagerTenant.setResponseMessage(mapList.toString());
        jobManagerTenant.setStatus(EJobStatus.COMPLETED.toString());
        jobManagerTenantService.toUpdateMapper(jobManagerTenant);
    }

    @Override
    public void disableVariantBatch(List<JobExecutionParams> jobExecutionParams) {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.DISABLE_VARIANT);
        Map<String, JobParameter> maps = new HashMap<>();
        AtomicReference<Long> jobScheduleId = new AtomicReference<>();
        AtomicReference<String> productIdVariantIdStr = new AtomicReference<>();
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        requestMessage.put("type", EJobName.DISABLE_VARIANT.name());
        jobExecutionParams.forEach(param -> {
            if (param.getKeyString().equals("jobName")) {
                jobScheduleId.set(param.getScheduledJobId());
                maps.put("jobScheduleId", new JobParameter(jobScheduleId.get()));
                requestMessage.put("jobScheduleId", jobScheduleId.get());
            }
            if (param.getKeyString().equals("compKey")) {
                maps.put("compKey", new JobParameter(param.getValueString()));
            }
            if (param.getKeyString().equals(AppConstants.PRODUCT_VARIANT)) {
                productIdVariantIdStr.set(param.getValueString());
                maps.put(AppConstants.PRODUCT_VARIANT, new JobParameter(productIdVariantIdStr.get()));
                requestMessage.put("ProductId-VariantId", productIdVariantIdStr.get());
            }
        });

        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getBean(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        maps.put("jobId", new JobParameter(jobManagerTenant.getId()));
        JobParameters parameters = new JobParameters(maps);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
            jobManagerTenant = jobManagerTenantService.findById(jobManagerTenant.getId());
            jobManagerTenantService.update(jobManagerTenant, jobManagerTenant.getStatus(), jobExecution.getJobId(),
                    LOGGER);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, jobExecutionParams.get(0).getScheduledJobId(), responseMessage, e.getMessage(), jobManagerTenant);
        }
    }

    @Override
    public void disableProductBatch(List<JobExecutionParams> jobExecutionParams) {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.DISABLE_PRODUCT_BATCH);
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        AtomicReference<Long> jobScheduleId = new AtomicReference<>();
        AtomicReference<String> productIdStr = new AtomicReference<>();
        jobExecutionParams.forEach(param -> {
            if (param.getKeyString().equals("jobName")) {
                jobScheduleId.set(param.getScheduledJobId());
                maps.put("jobScheduleId", new JobParameter(jobScheduleId.get()));
            }
            if (param.getKeyString().equals("compKey")) {
                maps.put("compKey", new JobParameter(param.getValueString()));
            }
            if (param.getKeyString().equals(AppConstants.productId)) {
                productIdStr.set(param.getValueString());
                maps.put(AppConstants.productId, new JobParameter(productIdStr.get()));
            }
        });
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        requestMessage.put("jobScheduleId", jobScheduleId.get());
        requestMessage.put(AppConstants.productId, productIdStr.get());
        requestMessage.put("type", EJobName.DISABLE_PRODUCT.name());
        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getBean(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        maps.put("jobId", new JobParameter(jobManagerTenant.getId()));
        JobParameters parameters = new JobParameters(maps);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
            jobManagerTenant = jobManagerTenantService.findById(jobManagerTenant.getId());
            jobManagerTenantService.update(jobManagerTenant, jobManagerTenant.getStatus(), jobExecution.getJobId(),
                    LOGGER);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, jobExecutionParams.get(0).getScheduledJobId(), responseMessage, e.getMessage(), jobManagerTenant);
        }
    }


    @Override
    public Map disableVariant(String productId, String variantId, Map response, Long compKey) {
        String message = "";
        String valueString = productId + "-" + variantId;
        String keyString = AppConstants.PRODUCT_VARIANT;
        Integer count = dataExchange.getActiveCount(null, variantId);
        if (count > 0) {
            message = "Can not mark variant as disable due to active subscriptions";
            response = Utility.generateResponseMap(response, HttpStatus.UNPROCESSABLE_ENTITY.toString(), message, null);
        } else {
            message = manageOneTimeJob(EJobName.DISABLE_VARIANT.name(), keyString, valueString, compKey, message);
            response = Utility.generateResponseMap(response, HttpStatus.OK.toString(), message, null);
        }
        return response;
    }


    @Async
    @Override
    public void disableVariant(String valueString, Long compKey, Long jobId) {
        JobManagerTenant jobManagerTenant = jobManagerTenantService.findById(jobId);
        List<String> valueList = new ArrayList<String>(Arrays.asList(valueString.split(",")));
        List<Map<String, String>> mapList = new ArrayList<>();
        valueList.stream().forEach(value -> {
            String[] parts = value.split("-");
            String productId = parts[0];
            String variantId = parts[1];
            String status = "";
            String message = null;
            Map<String, String> map = new HashMap<>();
            map.put(AppConstants.productId, productId);
            map.put(AppConstants.variantId, variantId);
            Integer count = dataExchange.getActiveCount(null, variantId);
            if (count > 0) {
                message = "Variant has active / in-active subscriptions";
                status = EJobStatus.FAILED.toString();
                dataExchange.removeDisableDate(productId, variantId);
            } else {
                message = dataExchange.disableVariant(productId, variantId);
                if (message != null) {
                    message = "Variant has been disabled successfully";
                    status = "SUCCESS";
                } else {
                    message = "Unable to disable variant";
                    status = EJobStatus.FAILED.toString();
                }
            }
            map.put("Status", status);
            map.put("Message", message);
            mapList.add(map);
        });
        jobManagerTenant.setResponseMessage(mapList.toString());
        jobManagerTenant.setStatus(EJobStatus.COMPLETED.toString());
        jobManagerTenantService.toUpdateMapper(jobManagerTenant);
    }

    @Override
    public String manageOneTimeJob(String jobName, String keyString, String value, Long compKey, String message) {
        JobScheduler job = jobSchedulerService.findLatestJobByJobName(jobName);
        if (job != null) {
            Date today = Utility.getTodayUTC();
            Date comingSat = Utility.getComingSaturday(); //finding coming saturday from today's date
            Date nextSat = Utility.getNextSaturday(job.getCreatedAt());// finding next saturday from the created job of the job
            if (dateCheck(today, Utility.localDateTimeToDate(job.getCreatedAt()), comingSat, nextSat)) {
                message = updateExecutionValueString(job.getId(), keyString, value, message, jobName, compKey);
            } else {
                message = createOneTimeJob(jobName, value, keyString, compKey, message);
            }
        } else {
            message = createOneTimeJob(jobName, value, keyString, compKey, message);
        }
        return message;
    }

    private String createOneTimeJob(String jobName, String valueString, String keyString, Long compKey, String message) {
        Map<String, String> paramMap = new HashMap<>();
        Date comingSat = Utility.getComingSaturday();
        List<String> parts = Arrays.asList(valueString.split("-"));
        String productId = parts.get(0);
        String variantId = null;
        if (parts.size() > 1) {
            variantId = parts.get(1);
        }
        LocalDateTime localDateTime = LocalDateTime.ofInstant(comingSat.toInstant(), ZoneId.of("UTC"));
        String cronExpression = batchEngineUtilityService.generateCron(localDateTime); //Generate one time cron
        paramMap.put("jobName", jobName);
        if (CronExpression.isValidExpression(cronExpression)) {
            paramMap.put("cronExpression", cronExpression);
        }
        paramMap.put("status", AppConstants.STATUS_ACTIVE);
        paramMap.put("compKey", compKey.toString());
        paramMap.put(keyString, valueString);
        try {
            batchEngineUtilityService.scheduleJobWithParameters(paramMap);
            message = "Disable job has been submitted successfully";
            dataExchange.setDisableDate(productId, variantId);
        } catch (Exception e) {
            message = "Could not create / schedule job";
        }

        return message;
    }

    private boolean dateCheck(Date today, Date createdAt, Date comingSat, Date nextSat) {
        boolean result = false;
        boolean sameDaySat = Utility.areInSameDay(comingSat, nextSat); //Checking if they are the same day that means we are in the same week
        boolean sameDayCreated = Utility.areInSameDay(today, createdAt); //checking if we are disabling a variant in the same day the job was created
        boolean afterCreatedDay = Utility.isAfter(today, createdAt); //checking if we are disabling a variant after the job has been created
        boolean beforeSat = Utility.isBefore(today, nextSat);
        if (sameDaySat) {
            if ((sameDayCreated || afterCreatedDay) && beforeSat) {
                result = true;
            }
        }
        return result;
    }

    private String updateExecutionValueString(Long jobId, String keyString, String value, String message, String jobName, Long compKey) {
        List<String> parts = Arrays.asList(value.split("-"));
        String productId = parts.get(0);
        String variantId = null;
        if (parts.size() > 1) {
            variantId = parts.get(1);
        }
        JobExecutionParams jobExecutionParams = jobExecutionParamsService.getByScheduleJobIdAndKeyString(jobId, keyString);
        List<String> valueList = new ArrayList<>();
        if (jobExecutionParams.getValueString().length() > 0 && jobExecutionParams.getValueString().length() < 2500) {
            valueList = Arrays.asList(jobExecutionParams.getValueString());
            if (!valueList.contains(value)) {
                String valueString = jobExecutionParams.getValueString() + "," + value;
                jobExecutionParams.setValueString(valueString);
                jobExecutionParamsService.save(jobExecutionParams);
                dataExchange.setDisableDate(productId, variantId);
            }
            message = "Disable job has been submitted successfully";
        } else {
            // creating a new job to store more items since old job limit has exceded
            message = createOneTimeJob(jobName, value, keyString, compKey, message);
        }
        return message;
    }

    @Async
    @Override
    public void dataIngestion() {
        try {
            BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.DATA_INGESTION);

            Map<String, JobParameter> maps = new HashMap<>();
            maps.put("time", new JobParameter(System.currentTimeMillis()));
            JobParameters parameters = new JobParameters(maps);

            ObjectNode requestMessage = new ObjectMapper().createObjectNode();
            requestMessage.put("Start Time", System.currentTimeMillis());
            requestMessage.put("Job Name", batchDefinition.getJobName());

            JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                    EJobStatus.RUNNING.toString(), null, LOGGER);
            JobExecution jobExecution = null;
            ObjectNode responseMessage = new ObjectMapper().createObjectNode();
            try {
                jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
                requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
                requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
                jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

            } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                     JobParametersInvalidException e) {
                catchLogs(batchDefinition, null, responseMessage, e.getMessage(), jobManagerTenant);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    @Override
    public void migrateDataIngestion() {
        try {
            BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.MIGRATE_DATA_INGESTION);

            Map<String, JobParameter> maps = new HashMap<>();
            maps.put("time", new JobParameter(System.currentTimeMillis()));
            JobParameters parameters = new JobParameters(maps);

            ObjectNode requestMessage = new ObjectMapper().createObjectNode();
            requestMessage.put("Start Time", System.currentTimeMillis());
            requestMessage.put("Job Name", batchDefinition.getJobName());

            JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                    EJobStatus.RUNNING.toString(), null, LOGGER);
            JobExecution jobExecution = null;
            ObjectNode responseMessage = new ObjectMapper().createObjectNode();
            try {
                jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
                requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
                requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
                jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

            } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                     JobParametersInvalidException e) {
                catchLogs(batchDefinition, null, responseMessage, e.getMessage(), jobManagerTenant);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void dataIngestionBatch(List<JobExecutionParams> jobExecutionParams) {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.DATA_INGESTION);

        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("jobScheduleId", new JobParameter(jobExecutionParams.get(0).getScheduledJobId()));
        JobParameters parameters = new JobParameters(maps);

        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        requestMessage.put("jobScheduleId", jobExecutionParams.get(0).getScheduledJobId());

        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = null;
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, jobExecutionParams.get(0).getScheduledJobId(), responseMessage, e.getMessage(), jobManagerTenant);
        }

    }

    @Override
    public void migrateDataIngestionBatch(List<JobExecutionParams> jobExecutionParams) {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.MIGRATE_DATA_INGESTION);

        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("jobScheduleId", new JobParameter(jobExecutionParams.get(0).getScheduledJobId()));
        JobParameters parameters = new JobParameters(maps);

        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        requestMessage.put("jobScheduleId", jobExecutionParams.get(0).getScheduledJobId());

        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = null;
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, jobExecutionParams.get(0).getScheduledJobId(), responseMessage, e.getMessage(), jobManagerTenant);
        }

    }

    @Override
    public void billsBatchJob(List<JobExecutionParams> jobExecutionParams) {
        AtomicReference<String> jobName = new AtomicReference<>();
        jobExecutionParams.forEach(param -> {
            if (param.getKeyString().equals("jobName")) {
                jobName.set(param.getValueString());
            }
        });
        BatchDefinition batchDefinition = batchDefinitionService.findByJobName(String.valueOf(jobName));
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("jobScheduleId", new JobParameter(jobExecutionParams.get(0).getScheduledJobId()));


        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        requestMessage.put("jobScheduleId", jobExecutionParams.get(0).getScheduledJobId());

        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        maps.put("jobId", new JobParameter(jobManagerTenant.getId()));
        JobParameters parameters = new JobParameters(maps);
        JobExecution jobExecution = null;
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, jobExecutionParams.get(0).getScheduledJobId(), responseMessage, e.getMessage(), jobManagerTenant);
        }
    }

    @Override
    public Map runBillsBatchJob(String jobName, Long compKey) {
        Map response = new HashMap();
        String message = null;
        String status = null;
        jobName = getFormattedJobName(jobName);
        try {
            JobScheduler job = jobSchedulerService.findLatestJobByJobName(jobName);
            String cronExpression = getCronExpressionFromJobName(jobName);
            if (CronExpression.isValidExpression(cronExpression) && job != null && !job.getCronExpression().equalsIgnoreCase(cronExpression) && job.getStatus().equalsIgnoreCase(AppConstants.STATUS_ACTIVE)) {
                message = updateCronInExistingBillsBatchJob(job, cronExpression, compKey);
                status = HttpStatus.OK.toString();
            } else if (CronExpression.isValidExpression(cronExpression) && job == null) {
                message = createBillsBatchJob(jobName, cronExpression, compKey);
                status = HttpStatus.OK.toString();
            } else {
                message = "Invalid cronExpression";
                status = HttpStatus.PRECONDITION_FAILED.toString();
            }
            response = Utility.generateResponseMap(response, status, message, null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            message = "Failed to create job: " + jobName;
            response = Utility.generateResponseMap(response, HttpStatus.INTERNAL_SERVER_ERROR.toString(), message, null);
        }
        return response;
    }

    /**
     * Description: This method is called if there is change in cron expression in tenantConfig table
     * It first deletes the existing job and then submits a new job based on the new cronExpression
     * Created By: Ibtehaj
     * Created At: 08/04/2023
     *
     * @param job
     * @param cronExpression
     * @param compKey
     * @return
     */
    private String updateCronInExistingBillsBatchJob(JobScheduler job, String cronExpression, Long compKey) {
        String message = null;
        String jobName = job.getJobName();
        try {
            List<JobExecutionParams> jobExecutionParams = jobExecutionParamsService.getByScheduledJobId(job.getId());
            jobSchedulerService.delete(job.getId());
            jobExecutionParamsService.deleteAll(jobExecutionParams);
            message = createBillsBatchJob(jobName, cronExpression, compKey);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            message = "Error updating cronExpression in job: " + job.getJobName();
        }
        return message;
    }

    /**
     * Description: This method is called submit a job to the scheduler
     * Created By: Ibtehaj
     * Created At: 08/04/2023
     *
     * @param jobName
     * @param cronExpression
     * @param compKey
     * @return
     */
    private String createBillsBatchJob(String jobName, String cronExpression, Long compKey) {
        Map<String, String> paramMap = new HashMap<>();
        String message = null;
        try {
            paramMap.put("jobName", jobName);
            paramMap.put("cronExpression", cronExpression);
            paramMap.put("status", AppConstants.STATUS_ACTIVE);
            paramMap.put("compKey", compKey.toString());
            ObjectNode response = batchEngineUtilityService.scheduleJobWithParameters(paramMap);
            message = response.get("MESSAGE").textValue();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            message = "Failed to create job: " + jobName;
        }
        return message;
    }

    /**
     * Description: This method is used to fetch cronExpression from the tenantConfig table based on the job Name
     * Created By: Ibtehaj
     * Created At: 08/04/2023
     *
     * @param jobName
     * @return
     */
    private String getCronExpressionFromJobName(String jobName) {
        String result = null;
        try {
            if (jobName.equalsIgnoreCase(EJobName.PENDING_BILLS.name())) {
                result = tenantConfigService.findByParameter(AppConstants.AutoCalculationScheduleTenantConfig).get().getText();
            } else if (jobName.equalsIgnoreCase(EJobName.BULK_CALCULATE.name())) {
                result = tenantConfigService.findByParameter(AppConstants.BillCalcSchedule).get().getText();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            result = "Cron not found";
        }
        return result;
    }

    /**
     * Description: This method is used to set the format of the jobName
     * Created By: Ibtehaj
     * Created At: 08/04/2023
     *
     * @param jobName
     * @return
     */
    private String getFormattedJobName(String jobName) {

        jobName = jobName.equalsIgnoreCase(EJobName.PENDING_BILLS.name()) ?
                EJobName.PENDING_BILLS.name() : jobName.equalsIgnoreCase(EJobName.BULK_CALCULATE.name())
                ? EJobName.BULK_CALCULATE.name() : jobName.equalsIgnoreCase(EJobName.BULK_GENERATE_INVOICE.name())
                ? EJobName.BULK_GENERATE_INVOICE.name() : jobName.equalsIgnoreCase(EJobName.BULK_PUBLISH_INVOICE.name())
                ? EJobName.BULK_PUBLISH_INVOICE.name() : "Job Not Found";
        return jobName;
    }

    @Override
    public void skipBillsBatch(String billingHeadIds, Long skipFlag, Boolean billSkip, Long compKey) {

        BatchDefinition batchDefinition = batchDefinitionService.findByJobName(EJobName.BULK_SKIP_UNSKIP.name());
        String jobName = billSkip ? "SKIP_BILL" : "UNSKIP_BILL";
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("billingHeadIds", new JobParameter(billingHeadIds));
        maps.put("skipFlag", new JobParameter(skipFlag));
        maps.put("billSkip", new JobParameter(String.valueOf(billSkip)));
        maps.put("compKey", new JobParameter(compKey));

        JobParameters parameters = new JobParameters(maps);

        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();

        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", jobName);
        requestMessage.put("billingHeadIds", billingHeadIds);

        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(jobName, requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));

            responseMessage.put("Message", (billSkip ? " Skipping" : "Un-Skipping") + " Bills");
            jobManagerTenant.setResponseMessage(responseMessage.toPrettyString());
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, null, responseMessage, e.getMessage(), jobManagerTenant);
        }
    }

    @Override
    public void addDiscountBatch(String billingHeadIds, Long compKey, BillingDiscountDTO billingDiscountDTO) {
        BatchDefinition batchDefinition = batchDefinitionService.findByJobName(EJobName.BULK_DISCOUNT.name());
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("billingHeadIds", new JobParameter(billingHeadIds));
        maps.put("compKey", new JobParameter(compKey));
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();

        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        requestMessage.put("billingHeadIds", billingHeadIds);

        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = null;
        try {
            String billDiscountDTO = new ObjectMapper().writeValueAsString(billingDiscountDTO);
            maps.put("billingDiscountDTO", new JobParameter(billDiscountDTO));
            JobParameters parameters = new JobParameters(maps);
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));

            responseMessage.put("Message", "Adding Discount To Bills ");
            jobManagerTenant.setResponseMessage(responseMessage.toPrettyString());
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException | JsonProcessingException e) {
            catchLogs(batchDefinition, null, responseMessage, e.getMessage(), jobManagerTenant);
        }
    }

    @Override
    public void generateAndPublishInvoiceBatch(String billingHeadIds, Long compKey, String jobName) {
        BatchDefinition batchDefinition = batchDefinitionService.findByJobName(getFormattedJobName(jobName));
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("billingHeadIds", new JobParameter(billingHeadIds));
        maps.put("compKey", new JobParameter(compKey));
        JobParameters parameters = new JobParameters(maps);
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();

        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        requestMessage.put("billingHeadIds", billingHeadIds);

        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));

            responseMessage.put("Message", "Generating Invoices");
            jobManagerTenant.setResponseMessage(responseMessage.toPrettyString());
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, null, responseMessage, e.getMessage(), jobManagerTenant);
        }
    }

    @Override
    public void updateProjectStatuses(List<JobExecutionParams> jobExecutionParams) {
        AtomicReference<String> jobName = new AtomicReference<>();
        jobExecutionParams.forEach(param -> {
            if (param.getKeyString().equals("jobName")) {
                jobName.set(param.getValueString());
            }
        });
        BatchDefinition batchDefinition = batchDefinitionService.findByJobName(String.valueOf(jobName));
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("jobScheduleId", new JobParameter(jobExecutionParams.get(0).getScheduledJobId()));


        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        requestMessage.put("jobScheduleId", jobExecutionParams.get(0).getScheduledJobId());

        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        maps.put("jobId", new JobParameter(jobManagerTenant.getId()));
        JobParameters parameters = new JobParameters(maps);
        JobExecution jobExecution = null;
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, jobExecutionParams.get(0).getScheduledJobId(), responseMessage, e.getMessage(), jobManagerTenant);
        }
    }

    @Override
    public void updateProjectStatusesADHOC(String jobName, Long compKey) {
        BatchDefinition batchDefinition = batchDefinitionService.findByJobName(jobName);
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("compKey", new JobParameter(compKey));
        JobParameters parameters = new JobParameters(maps);
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();

        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());


        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));

            responseMessage.put("Message", "Updating statuses in project");
            jobManagerTenant.setResponseMessage(responseMessage.toPrettyString());
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, null, responseMessage, e.getMessage(), jobManagerTenant);
        }
    }

    @Override
    public void runBillingADHOCBatch(String jobName, Long compKey, String period) {
        BatchDefinition batchDefinition = batchDefinitionService.findByJobName(getFormattedJobName(jobName));
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));

        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();

        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());


        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        maps.put("jobId", new JobParameter(jobManagerTenant.getId()));
        maps.put("period", new JobParameter(period));
        JobParameters parameters = new JobParameters(maps);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));

            responseMessage.put("Message", "Running " + jobName + " Job");
            jobManagerTenant.setResponseMessage(responseMessage.toPrettyString());
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, null, responseMessage, e.getMessage(), jobManagerTenant);
        }
    }

    @Override
    public void monitorPlatformMonitorBatch(List<JobExecutionParams> jobExecutionParams) {
        AtomicReference<String> jobName = new AtomicReference<>();
        AtomicReference<String> monitorPlatform = new AtomicReference<>();
        jobExecutionParams.forEach(param -> {
            if (param.getKeyString().equals(AppConstants.jobName)) {
                jobName.set(param.getValueString());
            }
            if (param.getKeyString().equals(AppConstants.monitorPlatform)) {
                monitorPlatform.set(param.getValueString());
            }
        });
        BatchDefinition batchDefinition = batchDefinitionService.findByJobName(String.valueOf(jobName));

        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("jobScheduleId", new JobParameter(jobExecutionParams.get(0).getScheduledJobId()));
        maps.put(AppConstants.monitorPlatform, new JobParameter(String.valueOf(monitorPlatform)));
        JobParameters parameters = new JobParameters(maps);

        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        requestMessage.put("jobScheduleId", jobExecutionParams.get(0).getScheduledJobId());

        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = null;
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, jobExecutionParams.get(0).getScheduledJobId(), responseMessage, e.getMessage(), jobManagerTenant);
        }

    }

    @Override
    public void generateBillingCredits(List<JobExecutionParams> jobExecutionParams) {
        AtomicReference<String> jobName = new AtomicReference<>();
        jobExecutionParams.forEach(param -> {
            if (param.getKeyString().equals(AppConstants.jobName)) {
                jobName.set(param.getValueString());
            }
        });
        BatchDefinition batchDefinition = batchDefinitionService.findByJobName(String.valueOf(jobName));

        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put("jobScheduleId", new JobParameter(jobExecutionParams.get(0).getScheduledJobId()));
        maps.put(AppConstants.date, new JobParameter(Utility.getPreviousYearMonth()));


        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        requestMessage.put("jobScheduleId", jobExecutionParams.get(0).getScheduledJobId());
        requestMessage.put("date", Utility.getPreviousYearMonth());

        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        maps.put("jobId", new JobParameter(jobManagerTenant.getId()));
        JobParameters parameters = new JobParameters(maps);
        JobExecution jobExecution = null;
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, jobExecutionParams.get(0).getScheduledJobId(), responseMessage, e.getMessage(), jobManagerTenant);
        }

    }

    @Override
    public void generateBillingCreditsADHOC(String date) {
        BatchDefinition batchDefinition = batchDefinitionService.findByJobName(EJobName.BILLING_CREDITS_BATCH.toString());
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put(AppConstants.date, new JobParameter(date));
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();

        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        requestMessage.put("date", date);

        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        maps.put("jobId", new JobParameter(jobManagerTenant.getId()));
        JobParameters parameters = new JobParameters(maps);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));

            responseMessage.put("Message", "Running " + EJobName.BILLING_CREDITS_BATCH.toString() + " Job");
            jobManagerTenant.setResponseMessage(responseMessage.toPrettyString());
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, null, responseMessage, e.getMessage(), jobManagerTenant);
        }
    }

    @Async
    @Override
    public void ProjectionJob(String compKey, String fileName, String header, String mongoSubId) {
        BatchDefinition batchDefinition = batchDefinitionService.findById(AppConstants.PROJECTION_IMPORT);
        AtomicInteger readCount = new AtomicInteger();
        AtomicInteger filterCount = new AtomicInteger();
        AtomicInteger writeCount = new AtomicInteger();
        AtomicReference<String> step1Exception = new AtomicReference<>("");
        AtomicReference<String> step2Exception = new AtomicReference<>("");
        AtomicReference<String> step3Exception = new AtomicReference<>("");
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();
        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName() + "_" + mongoSubId
                , requestMessage, EJobStatus.RUNNING.toString(), null, LOGGER);
        JobParameters parameters = new JobParameters(mapJobParameters(compKey, jobManagerTenant, fileName, header, null, mongoSubId));
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();
            stepExecutions.forEach(step -> {
                if (step.getStepName().equals("EXTRACTION")) {
                    readCount.set(step.getReadCount());
                    filterCount.set(step.getFilterCount());
                    writeCount.set(step.getWriteCount());
                    step1Exception.set(step.getExitStatus().getExitCode());
                }
            });
            String fileNameAfter = String.valueOf(jobExecution.getJobParameters().getParameters().get("fileName"));
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
            requestMessage.put("File name", fileNameAfter);
            requestMessage.put("Lines read", String.valueOf(readCount));
            requestMessage.put("Lines filtered", String.valueOf(filterCount));
            requestMessage.put("Lines written", String.valueOf(writeCount));
//            requestMessage.put("Lines processed", String.valueOf(billingCreditsRepository.count()));
            requestMessage.put("Step1 ExitCode", String.valueOf(step1Exception));
            requestMessage.put("Step2 ExitCode", String.valueOf(step2Exception));
            requestMessage.put("Step3 ExitCode", String.valueOf(step3Exception));
            jobManagerTenant.setRequestMessage(requestMessage.toPrettyString());
            jobManagerTenantService.update(jobManagerTenant, jobExecution.getExitStatus().getExitCode(),
                    jobExecution.getJobId(), LOGGER);
        } catch (Exception e) {
            catchLogs(batchDefinition, null, responseMessage, e.getMessage(), jobManagerTenant);

        }
    }

    @Override
    public void deActivateProjectionBatch(List<JobExecutionParams> jobExecutionParams) {
        AtomicReference<String> jobName = new AtomicReference<>();
        jobExecutionParams.forEach(param -> {
            if (param.getKeyString().equals("jobName")) {
                jobName.set(param.getValueString());
            }
        });
        BatchDefinition batchDefinition = batchDefinitionService.findByJobName(String.valueOf(jobName));
        Map<String, JobParameter> maps = new HashMap<>();
        AtomicReference<Long> jobScheduleId = new AtomicReference<>();
        AtomicReference<String> variantIdProjectionIdStr = new AtomicReference<>();
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        requestMessage.put("type", EJobName.DEACTIVATE_PROJECTION.name());
        jobExecutionParams.forEach(param -> {
            if (param.getKeyString().equals("jobName")) {
                jobScheduleId.set(param.getScheduledJobId());
                maps.put("jobScheduleId", new JobParameter(jobScheduleId.get()));
                requestMessage.put("jobScheduleId", jobScheduleId.get());
            }
            if (param.getKeyString().equals("compKey")) {
                maps.put("compKey", new JobParameter(param.getValueString()));
            }
            if (param.getKeyString().equals(AppConstants.VARIANT_PROJECTION)) {
                variantIdProjectionIdStr.set(param.getValueString());
                maps.put(AppConstants.VARIANT_PROJECTION, new JobParameter(variantIdProjectionIdStr.get()));
                requestMessage.put("VariantId-ProjectionId", variantIdProjectionIdStr.get());
            }
        });

        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getBean(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        maps.put("jobId", new JobParameter(jobManagerTenant.getId()));
        JobParameters parameters = new JobParameters(maps);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
            jobManagerTenant = jobManagerTenantService.findById(jobManagerTenant.getId());
            jobManagerTenantService.update(jobManagerTenant, jobManagerTenant.getStatus(), jobExecution.getJobId(),
                    LOGGER);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, jobExecutionParams.get(0).getScheduledJobId(), responseMessage, e.getMessage(), jobManagerTenant);
        }
    }

    @Override
    public BaseResponse deActivateProjection(String variantId, String projectionId, Long compKey) {
        String message = "";
        String valueString = variantId + "-" + projectionId;
        String keyString = AppConstants.VARIANT_PROJECTION;
        try {
            message = manageOneTimeProjectionJob(EJobName.DEACTIVATE_PROJECTION.name(), keyString, valueString, compKey, message);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message("ERROR").data(e.getMessage()).build();
        }

        return BaseResponse.builder().code(HttpStatus.OK.value()).message(message).data(null).build();
    }

    @Async
    @Override
    public void deActivateProjection(String valueString, Long compKey, Long jobId) {
        JobManagerTenant jobManagerTenant = jobManagerTenantService.findById(jobId);
        List<String> valueList = new ArrayList<String>(Arrays.asList(valueString.split(",")));
        List<Map<String, String>> mapList = new ArrayList<>();
        valueList.stream().forEach(value -> {
            String[] parts = value.split("-");
            String variantId = parts[0];
            String projectionId = parts[1];
            String status = "";
            String message = null;
            Map<String, String> map = new HashMap<>();
            map.put(AppConstants.variantId, variantId);
            map.put(AppConstants.projectionId, projectionId);
            message = dataExchange.activateProjection(variantId, projectionId, false);
            if (message != null) {
                message = "Projection has been deactivated successfully";
                status = "SUCCESS";
            } else {
                message = "Unable to deactivate projection";
                status = EJobStatus.FAILED.toString();
            }

            map.put("Status", status);
            map.put("Message", message);
            mapList.add(map);
        });
        jobManagerTenant.setResponseMessage(mapList.toString());
        jobManagerTenant.setStatus(EJobStatus.COMPLETED.toString());
        jobManagerTenantService.toUpdateMapper(jobManagerTenant);
    }

    @Override
    public BaseResponse activateProjection(String variantId, String projectionId, Long compKey) {
        BatchDefinition batchDefinition = batchDefinitionService.findByJobName(EJobName.ACTIVATE_PROJECTION.name());
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        maps.put(AppConstants.variantId, new JobParameter(variantId));
        maps.put(AppConstants.projectionId, new JobParameter(projectionId));
        maps.put("compKey", new JobParameter(compKey));
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();

        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());


        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        maps.put("jobId", new JobParameter(jobManagerTenant.getId()));
        JobParameters parameters = new JobParameters(maps);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));

            responseMessage.put("Message", "Activating projection");
            jobManagerTenant.setResponseMessage(responseMessage.toPrettyString());
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, null, responseMessage, e.getMessage(), jobManagerTenant);
        }

        return BaseResponse.builder().code(HttpStatus.OK.value()).message("Activating projection").data(null).build();
    }

    @Async
    @Override
    public void activateProjection(String variantId, String projectionId, Long compKey, Long jobId) {
        String status = "";
        String message = null;
        JobManagerTenant jobManagerTenant = jobManagerTenantService.findById(jobId);
        message = dataExchange.activateProjection(variantId, projectionId, true);
        List<Map<String, String>> mapList = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put(AppConstants.variantId, variantId);
        map.put(AppConstants.projectionId, projectionId);
        if (message != null) {
            message = "Projection has been deactivated successfully";
            status = "SUCCESS";
        } else {
            message = "Unable to deactivate projection";
            status = EJobStatus.FAILED.toString();
        }
        map.put("Status", status);
        map.put("Message", message);
        mapList.add(map);
        jobManagerTenant.setResponseMessage(mapList.toString());
        jobManagerTenant.setStatus(EJobStatus.COMPLETED.toString());
        jobManagerTenantService.toUpdateMapper(jobManagerTenant);
    }

    @Override
    public void activateProjectionBatch(List<JobExecutionParams> jobExecutionParams) {
        AtomicReference<String> jobName = new AtomicReference<>();
        jobExecutionParams.forEach(param -> {
            if (param.getKeyString().equals("jobName")) {
                jobName.set(param.getValueString());
            }
        });
        BatchDefinition batchDefinition = batchDefinitionService.findByJobName(String.valueOf(jobName));
        Map<String, JobParameter> maps = new HashMap<>();
        AtomicReference<Long> jobScheduleId = new AtomicReference<>();
        AtomicReference<String> variantIdStr = new AtomicReference<>();
        AtomicReference<String> projectionIdStr = new AtomicReference<>();
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        requestMessage.put("type", EJobName.ACTIVATE_PROJECTION.name());
        jobExecutionParams.forEach(param -> {
            if (param.getKeyString().equals("jobName")) {
                jobScheduleId.set(param.getScheduledJobId());
                maps.put("jobScheduleId", new JobParameter(jobScheduleId.get()));
                requestMessage.put("jobScheduleId", jobScheduleId.get());
            }
            if (param.getKeyString().equals("compKey")) {
                maps.put("compKey", new JobParameter(param.getValueString()));
            }
            if (param.getKeyString().equals(AppConstants.variantId)) {
                variantIdStr.set(param.getValueString());
                maps.put(AppConstants.variantId, new JobParameter(variantIdStr.get()));
                requestMessage.put("VariantId", variantIdStr.get());
            }
            if (param.getKeyString().equals(AppConstants.projectionId)) {
                projectionIdStr.set(param.getValueString());
                maps.put(AppConstants.projectionId, new JobParameter(projectionIdStr.get()));
                requestMessage.put("ProjectionId", projectionIdStr.get());
            }
        });

        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getBean(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        maps.put("jobId", new JobParameter(jobManagerTenant.getId()));
        JobParameters parameters = new JobParameters(maps);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, jobExecutionParams.get(0).getScheduledJobId(), responseMessage, e.getMessage(), jobManagerTenant);
        }
    }

    private String manageOneTimeProjectionJob(String jobName, String keyString, String value, Long compKey, String message) {
        JobScheduler job = jobSchedulerService.findLatestJobByJobName(jobName);
        if (job != null) {
            Date today = Utility.getTodayUTC();
            if (dateCheckProjection(today, Utility.localDateTimeToDate(job.getCreatedAt()))) {
                message = updateExecutionValueStringProjection(job.getId(), keyString, value, message, jobName, compKey);
            } else {
                message = createOneTimeJobProjection(jobName, value, keyString, compKey, message);
            }
        } else {
            message = createOneTimeJobProjection(jobName, value, keyString, compKey, message);
        }
        return message;
    }

    private String updateExecutionValueStringProjection(Long jobId, String keyString, String value, String message, String jobName, Long compKey) {
        List<String> parts = Arrays.asList(value.split("-"));
        String variantId = parts.get(0);
        String projectionId = null;
        if (parts.size() > 1) {
            projectionId = parts.get(1);
        }
        JobExecutionParams jobExecutionParams = jobExecutionParamsService.getByScheduleJobIdAndKeyString(jobId, keyString);
        List<String> valueList = new ArrayList<>();
        if (jobExecutionParams.getValueString().length() > 0 && jobExecutionParams.getValueString().length() < 2500) {
            valueList = Arrays.asList(jobExecutionParams.getValueString());
            if (!valueList.contains(value)) {
                String valueString = jobExecutionParams.getValueString() + "," + value;
                jobExecutionParams.setValueString(valueString);
                jobExecutionParamsService.save(jobExecutionParams);

                //TODO: Add method to set deactivation date
            }
            message = "Deactivation job has been submitted successfully";
        } else {
            // creating a new job to store more items since old job limit has exceded
            message = createOneTimeJobProjection(jobName, value, keyString, compKey, message);
        }
        return message;
    }

    private String createOneTimeJobProjection(String jobName, String valueString, String keyString, Long compKey, String message) {
        Map<String, String> paramMap = new HashMap<>();
        Date endOfDay = Utility.getEndOfDayUTC();
        List<String> parts = Arrays.asList(valueString.split("-"));
        String variantId = parts.get(0);
        String projectionId = null;
        if (parts.size() > 1) {
            variantId = parts.get(1);
        }
        LocalDateTime localDateTime = LocalDateTime.ofInstant(endOfDay.toInstant(), ZoneId.of("UTC"));
        String cronExpression = batchEngineUtilityService.generateCron(localDateTime); //Generate one time cron
        paramMap.put("jobName", jobName);
        if (CronExpression.isValidExpression(cronExpression)) {
            paramMap.put("cronExpression", cronExpression);
        }
        paramMap.put("status", AppConstants.STATUS_INACTIVE);
        paramMap.put("compKey", compKey.toString());
        paramMap.put(keyString, valueString);
        try {
            batchEngineUtilityService.scheduleJobWithParameters(paramMap);
            message = "Deactivation job has been submitted successfully";
            //TODO: Add method to set deactivation date
//            dataExchange.setDisableDate(variantId, projectionId);
        } catch (Exception e) {
            message = "Could not create / schedule job";
        }

        return message;
    }

    private boolean dateCheckProjection(Date today, Date createdAt) {
        return Utility.areInSameDay(today, createdAt);
    }

    @Override
    public void generateProjectProjectionRevenue(List<JobExecutionParams> jobExecutionParams) {
        AtomicReference<String> jobName = new AtomicReference<>();
        jobExecutionParams.forEach(param -> {
            if (param.getKeyString().equals("jobName")) {
                jobName.set(param.getValueString());
            }
        });
        BatchDefinition batchDefinition = batchDefinitionService.findByJobName(String.valueOf(jobName));
        Map<String, JobParameter> maps = new HashMap<>();
        AtomicReference<Long> jobScheduleId = new AtomicReference<>();
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
        jobExecutionParams.forEach(param -> {
            if (param.getKeyString().equals("jobName")) {
                jobScheduleId.set(param.getScheduledJobId());
                maps.put("jobScheduleId", new JobParameter(jobScheduleId.get()));
                requestMessage.put("jobScheduleId", jobScheduleId.get());
            }
            if (param.getKeyString().equals("compKey")) {
                maps.put("compKey", new JobParameter(param.getValueString()));
            }
        });

        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getBean(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        maps.put("jobId", new JobParameter(jobManagerTenant.getId()));
        JobParameters parameters = new JobParameters(maps);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, jobExecutionParams.get(0).getScheduledJobId(), responseMessage, e.getMessage(), jobManagerTenant);
        }
    }

    @Override
    public BaseResponse generateProjectProjectionRevenue(String months, Long compKey) {
        BatchDefinition batchDefinition = batchDefinitionService.findByJobName(EJobName.PROJECT_PROJECTION_REVENUE.name());

        if (!isValid(months)) {
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message("The processed list does not meet the requirements.").data(null).build();
        }
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));

        maps.put("compKey", new JobParameter(compKey));
        maps.put("months", new JobParameter(months));
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        ObjectNode responseMessage = new ObjectMapper().createObjectNode();

        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());


        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage,
                EJobStatus.RUNNING.toString(), null, LOGGER);
        maps.put("jobScheduleId", new JobParameter(jobManagerTenant.getId()));
        JobParameters parameters = new JobParameters(maps);
        JobExecution jobExecution = null;
        try {
            jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
            requestMessage.put("Started at", String.valueOf(jobExecution.getStartTime()));
            requestMessage.put("Ended at", String.valueOf(jobExecution.getEndTime()));

            responseMessage.put("Message", "Activating projection");
            jobManagerTenant.setResponseMessage(responseMessage.toPrettyString());
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            catchLogs(batchDefinition, null, responseMessage, e.getMessage(), jobManagerTenant);
        }

        return BaseResponse.builder().code(HttpStatus.OK.value()).message("Generating Project Projection Revenue").data(null).build();
    }

    private Boolean isValid(String str) {
        Pattern pattern = Pattern.compile("^(0[1-9]|1[0-2])-\\d{4}$");

        long count = Stream.of(str.split(","))
                .filter(s -> s != null && !s.trim().isEmpty())
                .filter(pattern.asPredicate())
                .count();

        return count >= 3;
    }
}

