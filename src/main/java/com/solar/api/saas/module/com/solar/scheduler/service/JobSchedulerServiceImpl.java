package com.solar.api.saas.module.com.solar.scheduler.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.AppConstants;
import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.JobExecutionParams;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.module.com.solar.batch.service.BatchService;
import com.solar.api.saas.module.com.solar.scheduler.mapper.JobSchedulerMapper;
import com.solar.api.saas.module.com.solar.scheduler.model.JobScheduler;
import com.solar.api.saas.module.com.solar.scheduler.repository.JobSchedulerRepository;
import com.solar.api.saas.service.MasterTenantService;
import com.solar.api.saas.service.SchedulerTestService;
import com.solar.api.tenant.service.RateCodesTempService;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Service
public class JobSchedulerServiceImpl implements JobSchedulerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobSchedulerServiceImpl.class);

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Autowired
    JobSchedulerRepository jobSchedulerRepository;

    @Autowired
    JobExecutionParamsService jobExecutionParamsService;

    @Autowired
    @Lazy
    BatchService batchService;

    @Autowired
    JobExplorer jobExplorer;

    @Autowired
    JobOperator jobOperator;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    private MasterTenantService masterTenantService;

    @Autowired
    private RateCodesTempService rateCodesTempService;

    @Autowired
    private SchedulerTestService schedulerTestService;

    List<ScheduledTask> scheduledTaskList = new ArrayList<>();

    public <T extends Object> String checkType(T object) {
        if (object instanceof Integer)
            return "INTEGER";
        else if (object instanceof Double)
            return "DOUBLE";
        else if (object instanceof String)
            return "STRING";
        else if (object instanceof Date)
            return "DATE";
        else if (object instanceof Long)
            return "LONG";
        return null;
    }

    @Override
    public JobScheduler findByJobNameAndCronExpression(String jobName, String cronExpression) {
        return jobSchedulerRepository.findByJobNameAndCronExpression(jobName, cronExpression);
    }

    /**
     * @param jobScheduler
     * @return
     */
    @Override
    public JobScheduler saveOrUpdate(JobScheduler jobScheduler) {
        LOGGER.info(">>>>>>>>>>>>>>> Before JobSchedulerServiceImpl#saveOrUpdate()");
        if (jobScheduler.getId() != null) {
            JobScheduler jobSchedulerData =
                    jobSchedulerRepository.findById(jobScheduler.getId()).orElseThrow(() -> new NotFoundException(JobScheduler.class, jobScheduler.getId()));
            JobScheduler jobSchedulerUpdate = JobSchedulerMapper.toUpdatedJobScheduler(jobSchedulerData, jobScheduler);
            return jobSchedulerRepository.save(jobSchedulerUpdate);
        }
        LOGGER.info("<<<<<<<<<<<<<<< After JobSchedulerServiceImpl#saveOrUpdate()");
        return jobSchedulerRepository.save(jobScheduler);
    }

    /**
     * @param jobScheduler
     * @return
     */
    @Override
    public JobScheduler createInstance(JobScheduler jobScheduler) {
        MasterTenant masterTenant = masterTenantService.findByCompanyKey(jobScheduler.getTenantId());
        DBContextHolder.setTenantName(masterTenant.getDbName());
        DBContextHolder.setLegacy(masterTenant.getLegacyBilling());
        JobInstance jobInstanceExist = null;
        try {
            jobInstanceExist = jobExplorer.getJobInstance(jobScheduler.getJobInstanceId());
        } catch (NullPointerException | IllegalStateException e) {
            LOGGER.error(e.toString());
        }
        if (jobInstanceExist == null) {
            Map<String, JobParameter> maps = new HashMap<>();
            maps.put("cron", new JobParameter(jobScheduler.getCronExpression()));
            JobParameters jobParameters = new JobParameters(maps);
            JobInstance jobInstance = jobExplorer.getLastJobInstance(jobScheduler.getJobName());
            if (jobInstance != null) {
                jobScheduler.setJobInstanceId(jobInstance.getInstanceId());
            } else {
                jobScheduler.setJobInstanceId(jobRepository.createJobInstance(jobScheduler.getJobName(), jobParameters).getInstanceId());
            }

//            try {
//                jobRepository.createJobInstance(jobScheduler.getJobName(), jobParameters);
//            } catch (NullPointerException | IllegalStateException e) {
//                LOGGER.error(e.getMessage());
//                batchService.batchNotification(jobScheduler.getJobName(), jobScheduler.getId(),
//                        "Batch job Instance already exist for JobScheduler with id: "
//                                + jobScheduler.getId().toString() + " Stacktrace: " + e.getMessage());
//            } finally {
//                JobInstance jobInstances = jobExplorer.getLastJobInstance(jobScheduler.getJobName());
//                jobScheduler.setJobInstanceId(jobInstances != null ? jobInstances.getInstanceId() : 0);
//            }
            return jobSchedulerRepository.save(jobScheduler);
        }
        return null;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public JobScheduler findByJobInstanceId(Long id) {
        return jobSchedulerRepository.findByJobInstanceId(id);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public JobScheduler findById(Long id) {
        return jobSchedulerRepository.findById(id).orElseThrow(() -> new NotFoundException(JobScheduler.class, id));
    }

    /**
     * @param jobName
     * @return
     */
    @Override
    public JobScheduler findByJobName(String jobName) {
        return jobSchedulerRepository.findByJobName(jobName);
    }

    /**
     * @param status
     * @return
     */
    @Override
    public List<JobScheduler> findByStatus(String status) {
        return jobSchedulerRepository.findByStatus(status);
    }

    @Override
    public List<JobScheduler> findByState(String state) {
        return jobSchedulerRepository.findByState(state);
    }

    /**
     * @return
     */
    @Override
    public List<JobScheduler> findAll() {
        return jobSchedulerRepository.findAll();
    }

    /**
     * @param second
     * @param minute
     * @param hour
     * @param date
     * @param month
     * @param day
     */
    @Override
    public void cronTest(String second, String minute, String hour, String date, String month, String day) {
        String cronExpression = second + "/5" + " " + minute + " " + hour + " " + date + " " + month + " " + day;
        System.out.println(cronExpression);

    }

    /**
     * @param jobScheduleId
     * @return
     */
    public CronTask cronTaskFunction(Long jobScheduleId) {
        JobScheduler job = findById(jobScheduleId);
        if (job == null) {
            return null;
        }
        job.setStartedAt(new Date());
        saveOrUpdate(job);
        CronTrigger cronTrigger = new CronTrigger(job.getCronExpression(), TimeZone.getDefault());
        return new CronTask(() -> {
            try {
                jobCheck(job.getId());
            } catch (Exception e) {
                job.setLogs(catchLogs(e).toPrettyString());
                LOGGER.error(e.getMessage());
            }
            job.setEndedAt(new Date());
            job.setState(AppConstants.SCHEDULED);
            job.setLastExecutionTime(job.getEndedAt());
            job.setNextExecutionTime(getNextValidTime(job.getCronExpression()));

            //When we remove jobs from JobScheduler table it won't trigger the job but will try to reschedule it
            // because job with respective id is in the task list
            try {
                saveOrUpdate(job);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }, cronTrigger);
    }

    private ObjectNode catchLogs(Exception e) {
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("Exception", e.getMessage());
        return requestMessage;
    }

    @Override
    public Date getNextValidTime(String cronExpression) {
        CronExpression springCron = null;
        try {
            springCron = new CronExpression(cronExpression);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage());
        }
        Date nextExecutionTime = null;
        if (springCron != null) {
            nextExecutionTime = springCron.getNextValidTimeAfter(new Date());
        }
        return nextExecutionTime;
    }

    @Override
    public void batchNotification(String jobName, Long jobId, String stackTrace, String subject) {
        batchService.batchNotification(jobName, jobId, stackTrace, subject);
    }

    /**
     * Function to identify BatchJobs
     * In Scheduler
     *
     * @param id
     */
    @Override
    public void jobCheck(Long id) {
        JobScheduler job = findById(id);
        LOGGER.info("Triggering {} job for tenant: {}", job.getJobName(), job.getTenantId());
        masterTenantService.setCurrentDb(job.getTenantId());
        if (job.getStatus().equals(AppConstants.STATUS_ACTIVE) && !job.getState().equals(AppConstants.RUNNING)) {
            List<JobExecutionParams> jobExecutionParams = jobExecutionParamsService.getByScheduledJobId(job.getId());
            try {
                setRunning(job);
                LOGGER.info("Execution starts for {} job with params schedule id: {}", job.getJobName(), (jobExecutionParams != null && jobExecutionParams.size() > 0) ? jobExecutionParams.get(0).getScheduledJobId() : null);
                batchService.jobCheck(job, jobExecutionParams);
                LOGGER.info("Execution ends for {} job with params schedule id: {}", job.getJobName(), (jobExecutionParams != null && jobExecutionParams.size() > 0) ? jobExecutionParams.get(0).getScheduledJobId() : null);
                setScheduled(job);
            } catch (Exception e) {
                setScheduled(job);
                LOGGER.error("Error cause: {}, message {} , stacktract: {} while running job: {}", e.getCause(), e.getMessage(), e.fillInStackTrace(), job.getJobName());
                job.setLogs(e.getMessage());
                batchService.batchNotification(job.getJobName(), job.getId(), e.getCause() + e.getMessage() + e.fillInStackTrace(), "SCHEDULED JOB ERROR");
            }
        }
    }

    private void setScheduled(JobScheduler job) {
        job.setState(AppConstants.SCHEDULED);
        saveOrUpdate(job);
    }

    private void setRunning(JobScheduler job) {
        job.setState(AppConstants.RUNNING);
        saveOrUpdate(job);
    }

    @Override
    public void emptyScheduledTaskList() {
        scheduledTaskList.clear();
    }

    /**
     * @param scheduledTaskRegistrar
     * @return
     */
    @Override
    public List<JobScheduler> analyzer(ScheduledTaskRegistrar scheduledTaskRegistrar, boolean viaStartup) {
        List<JobScheduler> jobSchedulers = findByStatus("ACTIVE");
        //Check if any jobs exists
        if (!jobSchedulers.isEmpty()) {

            //Loop to schedule all jobs
            for (JobScheduler jobScheduler : jobSchedulers) {

                //Check if CronTaskList is not empty
                //TRUE
                LOGGER.info(">>>>>>>>>>>>>>> Before JobScheduler main condition");
                if (jobScheduler.getNextExecutionTime() != null && jobScheduler.getNextExecutionTime().before(new Date())) {
                    LOGGER.info(">>>>>>>>>>>>>>> Before 'On jobScheduler.getNextExecutionTime().before(new Date())'");
                    scheduleJob(scheduledTaskRegistrar, jobScheduler);
                    LOGGER.info("<<<<<<<<<<<<<<< After 'On jobScheduler.getNextExecutionTime().before(new Date())'");
                } else {
                    if (viaStartup) {
                        LOGGER.info(">>>>>>>>>>>>>>> Before viaStartup");
                        scheduleJob(scheduledTaskRegistrar, jobScheduler);
                        LOGGER.info("<<<<<<<<<<<<<<< After viaStartup");
                    } else {
                        LOGGER.info(">>>>>>>>>>>>>>> Before legacy condition");
                        if (!scheduledTaskList.isEmpty()) {
                            //Check if task is not scheduled
                            if (jobScheduler.getState().equals(AppConstants.QUEUED)) {
                                LOGGER.info(">>>>>>>>>>>>>>> Before 'On jobScheduler.getState().equals(AppConstants.QUEUED)'");
                                scheduleJob(scheduledTaskRegistrar, jobScheduler);
                                LOGGER.info("<<<<<<<<<<<<<<< After 'On jobScheduler.getState().equals(AppConstants.QUEUED)'");
                            }
                            //FALSE
                        } else {
                            LOGGER.info(">>>>>>>>>>>>>>> Before 'On !jobScheduler.getState().equals(AppConstants.QUEUED)'");
                            scheduleJob(scheduledTaskRegistrar, jobScheduler);
                            LOGGER.info("<<<<<<<<<<<<<<< After 'On !jobScheduler.getState().equals(AppConstants.QUEUED)'");
                        }
                        LOGGER.info("<<<<<<<<<<<<<<< After legacy condition");
                    }
                }
                LOGGER.info("<<<<<<<<<<<<<<< After JobScheduler main condition");
            }
            LOGGER.info("{} Job(s) scheduled", scheduledTaskList.size());
        } else {
            LOGGER.info("Nothing to sync at this point");
        }
        return jobSchedulers;
    }

    @Override
    public void scheduleJob(ScheduledTaskRegistrar scheduledTaskRegistrar, JobScheduler jobScheduler) {
        LOGGER.info(">>>>>>>>>>>>>>> Before scheduleJob()");
        MasterTenant masterTenant = masterTenantService.findByCompanyKey(jobScheduler.getTenantId());
        DBContextHolder.setTenantName(masterTenant.getDbName());
        try {
            LOGGER.info(">>>>>>>>>>>>>>> Before cronTaskFunction()");
            scheduledTaskList.add(scheduledTaskRegistrar.scheduleCronTask(cronTaskFunction(jobScheduler.getId())));
            LOGGER.info("<<<<<<<<<<<<<<< After cronTaskFunction()");
            jobScheduler.setScheduled(new Date());
            jobScheduler.setState(AppConstants.SCHEDULED);
            jobScheduler.setNextExecutionTime(getNextValidTime(jobScheduler.getCronExpression()));
            saveOrUpdate(jobScheduler);
            LOGGER.info(">>>>>>>>>>>>>>> Before batchNotification()");
            batchNotification(jobScheduler.getJobName(), jobScheduler.getId(), "", "JOB SCHEDULED");
            LOGGER.info("<<<<<<<<<<<<<<< After batchNotification()");
            LOGGER.info(
                    "New Job Scheduled, id: {} name: {}",
                    jobScheduler.getJobInstanceId(),
                    jobScheduler.getJobName());
        } catch (NullPointerException | IllegalStateException e) {
            LOGGER.error(e.toString());
            batchService.batchNotification(jobScheduler.getJobName(), null, e.getMessage(), AppConstants.BATCH_EMAIL_SUBJECT_ADD_SCHEDULE);
        }
        LOGGER.info("<<<<<<<<<<<<<<< After scheduleJob()");
    }

    private void rescheduleJobs(ScheduledTaskRegistrar scheduledTaskRegistrar, JobScheduler jobScheduler) {
        if (scheduledTaskList.isEmpty()) {
            //Check if task is not scheduled
            if (jobScheduler.getState().equals(AppConstants.RUNNING)) {
                Set<JobExecution> jobExecutionSet = jobExplorer.findRunningJobExecutions(jobScheduler.getJobName());
                if (jobExecutionSet.size() != 1) {
                    jobExecutionSet.forEach(jobExecution -> {
                        try {
                            jobOperator.stop(jobExecution.getId());
                            jobScheduler.setState(AppConstants.SCHEDULED);
                            saveOrUpdate(jobScheduler);
                            batchService.batchNotification(jobScheduler.getJobName(), jobExecution.getId(), AppConstants.ABANDONED,
                                    AppConstants.BATCH_EMAIL_SUBJECT_JOB_MARKED_ABANDONED);
                        } catch (NoSuchJobExecutionException | JobExecutionNotRunningException e) {
                            batchService.batchNotification(jobScheduler.getJobName(), jobExecution.getId(), e.getMessage(),
                                    AppConstants.BATCH_EMAIL_SUBJECT_JOB_MARKED_ABANDONED);
                            LOGGER.error(e.getMessage(), e);
                        }
                    });
                } else {
                    jobExecutionSet.forEach(jobExecution -> {
                        try {
                            Long jobId = jobOperator.restart(jobExecution.getJobId());
                            jobScheduler.setState(AppConstants.RESTARTED);
                            saveOrUpdate(jobScheduler);
                            batchService.batchNotification(jobScheduler.getJobName(), jobExecution.getId(), AppConstants.RESTARTED,
                                    AppConstants.BATCH_EMAIL_SUBJECT_JOB_MARKED_RESTARTED);
                        } catch (JobInstanceAlreadyCompleteException | NoSuchJobExecutionException |
                                 NoSuchJobException | JobRestartException |
                                 JobParametersInvalidException exception) {
                            LOGGER.error(exception.getMessage(), exception);
                            batchService.batchNotification(jobScheduler.getJobName(), jobExecution.getId(), exception.getMessage(),
                                    AppConstants.BATCH_EMAIL_SUBJECT_JOB_MARKED_RESTARTED);
                        }
                    });
                }
//                scheduleJob(scheduledTaskRegistrar, jobScheduler);
            }
        }
    }

    @Override
    public CronTask trigger(Long id) {
        JobScheduler job = findByJobInstanceId(id);
        return this.createCronTask(() -> {
            System.out.println("Name: " + job.getJobName() + ", Cron: " + job.getCronExpression());
            System.out.println("Triggered at " + dateTimeFormatter.format(LocalDateTime.now()));
        }, job.getCronExpression());
    }

    @Override
    public void stopScheduler() {
    }

    @Override
    public String removeScheduledJob(Long id) {
        JobScheduler jobScheduler = jobSchedulerRepository.findById(id).orElseThrow(() -> new NotFoundException(JobScheduler.class, id));
        if (jobScheduler.getState().equals(AppConstants.SCHEDULED)) {
            jobSchedulerRepository.delete(jobScheduler);
            return jobScheduler.getJobName() + " job has been removed.";
        } else {
            return jobScheduler.getJobName() + " job is running!";
        }
    }

    @Override
    public void delete(Long id) {
        JobScheduler jobScheduler = jobSchedulerRepository.getOne(id);
        jobSchedulerRepository.delete(jobScheduler);
    }

    @Override
    public void markAsInactive(Long id) {
        JobScheduler jobScheduler = jobSchedulerRepository.findById(id).orElseThrow(() -> new NotFoundException(JobScheduler.class, id));
        jobScheduler.setStatus("INACTIVE");
        saveOrUpdate(jobScheduler);
    }

    public CronTask createCronTask(Runnable action, String expression) {
        return new CronTask(action, new CronTrigger(expression));
    }

    @Override
    public JobScheduler findLatestJobByJobName(String jobName) {

        return jobSchedulerRepository.findLatestJobByJobName(jobName);
    }
}


////TODO:send email notification 15days before actual termination
//            if (job.getJobName().equals(AppConstants.NOTIFICATION_FOR_AUTO_SUBSCRIPTION_TERMINATION)
//                    && !job.getState().equals(AppConstants.RUNNING)) {
//                    setRunning(job);
//                    batchService.getAllAutoTerminationNotification();
//                    setScheduled(job);
//                    }
//
//                    //TODO:adhoc termination
//                    if (job.getJobName().equals(AppConstants.ADHOC_SUBSCRIPTION_TERMINATION)
//                    && !job.getState().equals(AppConstants.RUNNING)) {
//                    setRunning(job);
//                    batchService.adhocAllSubscriptionTermination(job.getId());
//                    LOGGER.info("JobId: {},  Name: {}, Triggered at {}", job.getId(), job.getJobName(),
//                    dateTimeFormatter.format(LocalDateTime.now()));
//                    setScheduled(job);
//                    }
//
//                    //TODO:auto termination
//                    if (job.getJobName().equals(AppConstants.AUTO_SUBSCRIPTION_TERMINATION)
//                    && !job.getState().equals(AppConstants.RUNNING)) {
//                    setRunning(job);
//                    batchService.autoAllSubscriptionTermination(job.getId());
//                    setScheduled(job);
//                    }
//
//                    if (job.getJobName().equals(AppConstants.ADD_MONITOR_READINGS) && !job.getState().equals(AppConstants.RUNNING)) {
//                    List<JobExecutionParams> jobExecutionParams = jobExecutionParamsService.getByScheduledJobId(job.getId());
//        setRunning(job);
//        batchService.addMonitorReadingsBatch(jobExecutionParams);
//        setScheduled(job);
//        }
//
//        if (job.getJobName().equals("NPV")
//        && !job.getState().equals(AppConstants.RUNNING)) {
//        setRunning(job);
//        batchService.ExecuteNPVBatch();
//        setScheduled(job);
//        }
//
//        if (job.getJobName().equals("ANALYTICAL_CALCULATION")
//        && !job.getState().equals(AppConstants.RUNNING)) {
//        setRunning(job);
//        batchService.AnalyticalCalculation(job);
//        setScheduled(job);
//        }
//
//        /**
//         * Schedule Billing
//         * STEP1: Generation
//         * STEP2: Invoicing
//         * STEP3: Schedule INVOICE_PDF
//         */
//        if (job.getJobName().equals(AppConstants.BILLING_BY_TYPE) && !job.getState().equals(AppConstants.RUNNING)) {
//
//        List<JobExecutionParams> jobExecutionParams = jobExecutionParamsService.getByScheduledJobId(job.getId());
//        Date date = null;
//        String dateString;
//        if (jobExecutionParams.stream().anyMatch(params -> params.getKeyString().equals(AppConstants.date))) {
//        dateString = jobExecutionParams.stream().filter(params -> params.getKeyString().equals(AppConstants.date))
//        .findFirst().orElse(null).getValueString();
//        if (dateString != null) {
//        String[] monthYear = Objects.requireNonNull(jobExecutionParams.stream()
//        .filter(params -> params.getKeyString().equals(AppConstants.billingMonth)).findFirst()
//        .orElse(null)).getValueString().split("-");
//        dateString = monthYear[1] + "-" + monthYear[0] + "-" + dateString;
//        try {
//        date = dateString == null ? new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT).parse(dateString) : null;
//        } catch (ParseException e) {
//        LOGGER.error(e.getMessage(), e);
//        }
//        }
//        }
//        setRunning(job);
//        batchService.billingBatch(
//        Objects.requireNonNull(jobExecutionParams.stream()
//        .filter(params -> params.getKeyString().equals(AppConstants.subscriptionCode))
//        .findFirst().orElse(null)).getValueString(),
//        Objects.requireNonNull(jobExecutionParams.stream()
//        .filter(params -> params.getKeyString().equals(AppConstants.subscriptionRateMatrixIdsCSV))
//        .findFirst().orElse(null)).getValueString(),
//        Objects.requireNonNull(jobExecutionParams.stream()
//        .filter(params -> params.getKeyString().equals(AppConstants.billingMonth))
//        .findFirst().orElse(null)).getValueString(),
//        date,
//        AppConstants.SCHEDULED_INVOICING,
//        Long.valueOf(Objects.requireNonNull(jobExecutionParams.stream()
//        .filter(params -> params.getKeyString().equals(AppConstants.compKey))
//        .findFirst().orElse(null)).getValueString()),
//        job.getId());
//        setScheduled(job);
//        }
//
//        /**
//         * Schedule Billing
//         * STEP: INVOICE_PDF
//         */
//        if (job.getJobName().equals(AppConstants.INVOICE_PDF_BATCH_JOB) && !job.getState().equals(AppConstants.RUNNING)) {
//
//        List<JobExecutionParams> jobExecutionParams = jobExecutionParamsService.getByScheduledJobId(job.getId());
//        Date date = null;
//        String dateString;
//        if (jobExecutionParams.stream().anyMatch(params -> params.getKeyString().equals(AppConstants.date))) {
//        dateString = jobExecutionParams.stream().filter(params -> params.getKeyString().equals(AppConstants.date))
//        .findFirst().orElse(null).getValueString();
//        if (dateString != null) {
//        String[] monthYear = Objects.requireNonNull(jobExecutionParams.stream()
//        .filter(params -> params.getKeyString().equals(AppConstants.billingMonth)).findFirst()
//        .orElse(null)).getValueString().split("-");
//        dateString = monthYear[1] + "-" + monthYear[0] + "-" + dateString;
//        try {
//        date = dateString == null ? new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT).parse(dateString) : null;
//        } catch (ParseException e) {
//        LOGGER.error(e.getMessage(), e);
//        }
//        }
//        }
//        setRunning(job);
//        batchService.billingBatch(
//        Objects.requireNonNull(jobExecutionParams.stream()
//        .filter(params -> params.getKeyString().equals(AppConstants.subscriptionCode))
//        .findFirst().orElse(null)).getValueString(),
//        Objects.requireNonNull(jobExecutionParams.stream()
//        .filter(params -> params.getKeyString().equals(AppConstants.subscriptionRateMatrixIdsCSV))
//        .findFirst().orElse(null)).getValueString(),
//        Objects.requireNonNull(jobExecutionParams.stream()
//        .filter(params -> params.getKeyString().equals(AppConstants.billingMonth))
//        .findFirst().orElse(null)).getValueString(),
//        date,
//        Objects.requireNonNull(jobExecutionParams.stream()
//        .filter(params -> params.getKeyString().equals(AppConstants.jobName))
//        .findFirst().orElse(null)).getValueString(),
//        Long.valueOf(Objects.requireNonNull(jobExecutionParams.stream()
//        .filter(params -> params.getKeyString().equals(AppConstants.compKey))
//        .findFirst().orElse(null)).getValueString()),
//        job.getId());
//        setScheduled(job);
//        }