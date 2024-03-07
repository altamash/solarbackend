package com.solar.api.saas.module.com.solar.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.JobExecutionParams;
import com.solar.api.saas.model.extended.BatchDefinition;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.module.com.solar.batch.service.BatchDefinitionService;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.saas.module.com.solar.scheduler.model.JobScheduler;
import com.solar.api.saas.module.com.solar.scheduler.service.JobExecutionParamsService;
import com.solar.api.saas.module.com.solar.scheduler.service.JobSchedulerService;
import com.solar.api.saas.module.com.solar.scheduler.v2.SyncSchedulerV2;
import com.solar.api.saas.module.com.solar.scheduler.v2.jobs.SolrenViewTaskConfig;
import com.solar.api.saas.service.EmailService;
import com.solar.api.saas.service.MasterTenantService;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.tenant.mapper.tiles.weatherTile.GardenDetail;
import com.solar.api.tenant.mapper.weather.WeatherDTO;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.repository.weather.WeatherDataRepository;
import com.solar.api.tenant.service.job.JobManagerTenantService;
import com.solar.api.tenant.service.preferences.TenantConfigService;
import com.solar.api.tenant.service.process.billing.EBillingByType;
import com.solar.api.tenant.service.weather.WeatherServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.LocalTime;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.*;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Service
public class BatchEngineUtilityServiceImpl implements BatchEngineUtilityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchEngineUtilityServiceImpl.class);

    @Autowired
    JobSchedulerService jobSchedulerService;

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    EmailService emailService;
    @Autowired
    JobExecutionParamsService jobExecutionParamsService;
    @Autowired
    JobManagerTenantService jobManagerTenantService;
    @Autowired
    BatchDefinitionService batchDefinitionService;
    @Autowired
    MasterTenantService masterTenantService;
    @Autowired
    SyncSchedulerV2 syncSchedulerV2;
    @Autowired
    private SolrenViewTaskConfig solrenViewTaskConfig;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WeatherDataRepository weatherDataRepository;
    @Autowired
    private WeatherServiceImpl weatherService;
    @Autowired
    private TenantConfigService tenantConfigService;
    @Autowired
    private DataExchange dataExchange;

    @Override
    public ObjectNode scheduleJobWithParameters(Map<String, String> hashmap) {
        ObjectNode response = new ObjectMapper().createObjectNode();
        ObjectNode message = new ObjectMapper().createObjectNode();
        BatchDefinition batchDefinition = batchDefinitionService.findByJobName(hashmap.get("jobName"));
        if (batchDefinition != null) {
            if (StringUtils.countMatches(hashmap.get("cronExpression"), " ") != 5) {
                return response.put("ERROR", " Cron expression must consist of 6 fields: (" + hashmap.get("cronExpression") + ")");
            }
            CronExpression cronExpression = null;
            if (!hashmap.containsKey("status") || !hashmap.containsKey("cronExpression") || !hashmap.containsKey("jobName") || !hashmap.containsKey("compKey")) {
                return response.put("ERROR", "Please provide compKey, jobName, cronExpression and status");
            } else {
                try {
                    cronExpression = new CronExpression(hashmap.get("cronExpression"));
                } catch (ParseException e) {
                    LOGGER.error(e.getMessage(), e);
                    try {
                        emailService.batchNotification(hashmap.get("jobName"), null,
                                "Invalid CronExpression" + hashmap.get("cronExpression") + "Stacktrace: " + e.getMessage(),
                                AppConstants.BATCH_EMAIL_SUBJECT_CRON_INVALID);
                    } catch (IOException i) {
                        LOGGER.error(e.getMessage(), e);
                    }
                    response.put("MESSAGE", e.getMessage() + hashmap.get("jobName"));
                    return response;
                }
                message.put("scheduledAt", new Date().toString());
                message.put("BatchName", hashmap.get("jobName"));
                message.put("PreDependencyBatchId", hashmap.get("PreDependencyBatchId"));
                JobScheduler finalJobScheduler = setJobScheduler(message, hashmap);
                setJobExecutionParams(hashmap, finalJobScheduler.getId());
                response.put("MESSAGE", "Job has been Scheduled for " + cronExpression.getNextValidTimeAfter(new Date()));
                try {
                    emailService.batchNotification(finalJobScheduler.getJobName(), finalJobScheduler.getId(),
                            "Job has been Scheduled for " + cronExpression.getNextValidTimeAfter(new Date()), AppConstants.BATCH_EMAIL_SUBJECT_JOB_SCHEDULED);
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                    response.put("MESSAGE", e.getMessage() + hashmap.get("jobName"));
                    return response;
                }
            }
        } else {
            response.put("MESSAGE", "No job definition found for JobName: " + hashmap.get("jobName"));
        }

        return response;
    }

    private void setJobExecutionParams(Map<String, String> hashmap, Long scheduledJobId) {
        hashmap.forEach((key, value) -> {
            JobExecutionParams jobExecutionParams = JobExecutionParams.builder()
                    .scheduledJobId(scheduledJobId)
                    .keyString(key)
                    .valueString(value)
                    .identifying("Y")
                    .build();
            jobExecutionParamsService.save(jobExecutionParams);
        });
    }

    private JobScheduler setJobScheduler(ObjectNode message, Map<String, String> hashmap) {
        return jobSchedulerService.saveOrUpdate(JobScheduler.builder()
                .jobName(hashmap.get("jobName"))
                .cronExpression(hashmap.get("cronExpression"))
                .status(hashmap.get("status"))
                .state(AppConstants.QUEUED)
                .message(message.toPrettyString())
                .tenantId(Long.valueOf(hashmap.get("compKey")))
                .build());
    }

    @Override
    public void scheduleJobWithDifferential(JobExecution jobExecution) {
        ObjectNode response = new ObjectMapper().createObjectNode();
        JobParameters jobParameters = jobExecution.getJobParameters();
        MasterTenant masterTenant = masterTenantService.findByCompanyKey(Long.valueOf(Objects.requireNonNull(jobParameters.getString("compKey"))));
        DBContextHolder.setTenantName(masterTenant.getDbName());
        DBContextHolder.setLegacy(masterTenant.getLegacyBilling());
        List<JobManagerTenant> jobManagerTenant = jobManagerTenantService.findByBatchId(jobExecution.getJobInstance().getInstanceId());


        Date newDate = DateUtils.addMinutes(new Date(), 30);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(newDate.toInstant(), ZoneId.systemDefault());

        Map<String, String> maps = new HashMap<>();
        maps.put("subscriptionCode", jobParameters.getString("subscriptionCode"));
        maps.put("subscriptionRateMatrixIdsCSV", jobParameters.getString("subscriptionRateMatrixIdsCSV"));
        maps.put("billingMonth", jobParameters.getString("billingMonth"));
        maps.put("date", jobParameters.getString("date"));
        maps.put("type", EBillingByType.INVOICE_PDF_SCHEDULED.getType());
        maps.put("compKey", jobParameters.getString("compKey"));
        maps.put("jobScheduleId", jobParameters.getString("jobId"));
        maps.put("PreDependencyBatchId", jobManagerTenant.get(0).getBatchId().toString());

        if (jobManagerTenant.get(0).getJobName().equals(AppConstants.BILLING_BY_TYPE)) {
            maps.put("jobName", AppConstants.INVOICE_PDF_BATCH_JOB);
        }
        maps.put("status", AppConstants.STATUS_ACTIVE);
        String cronExpression = generateCron(localDateTime);
        if (CronExpression.isValidExpression(cronExpression)) {
            maps.put("cronExpression", cronExpression);
            try {
                scheduleJobWithParameters(maps);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                LOGGER.error(e.getMessage());
            }
        } else {
            try {
                emailService.batchNotification(jobManagerTenant.get(0).getJobName(), jobManagerTenant.get(0).getBatchId(),
                        "Cron Expression to schedule INVOICE_PDF job is invalid", AppConstants.BATCH_EMAIL_SUBJECT_JOB_SCHEDULE_DYNAMIC);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public String generateCron(LocalDateTime localDateTime) {
        int year = localDateTime.getYear();
        int dayOfWeek = localDateTime.getDayOfWeek().getValue();
        int month = localDateTime.getMonthValue();
        int dayOfMonth = localDateTime.getDayOfMonth();
        int hour = localDateTime.getHour();
        int minute = localDateTime.getMinute();
        int second = localDateTime.getSecond();
        int millis = localDateTime.get(ChronoField.MILLI_OF_SECOND);
        return second + " " + minute + " " + hour + " " + dayOfMonth + " " + month + " ?";
    }

    @Override
    public List<JobScheduler> getAll() {
        return jobSchedulerService.findAll();
    }

    @Override
    public void emptyScheduledTaskList() {
        jobSchedulerService.emptyScheduledTaskList();
    }

    @Override
    public String timeTest() {
        JobManagerTenant jobManagerTenant = jobManagerTenantService.findById(126877L);
        LocalTime elapsedTime = new LocalTime("12:04:02.000");

        if (elapsedTime.getMinuteOfHour() <= 3 && elapsedTime.getMinuteOfHour() > 0 || elapsedTime.getMinuteOfHour() == 0) {

        }
        LocalTime now = LocalTime.now();
        //Get last run time - stored in db
        LocalTime updatedTime = now.minusHours(elapsedTime.getHourOfDay())
                .minusMinutes(elapsedTime.getMinuteOfHour())
                .minusSeconds(elapsedTime.getSecondOfMinute());
        return null;
    }

    @Override
    public BaseResponse<?> scheduleJob(Long jobId) {
        LOGGER.info("Entering scheduleJob with jobId: {}", jobId);
        JobScheduler jobScheduler;
        try {
            BatchDefinition batchDefinition = batchDefinitionService.findById(jobId);
            syncSchedulerV2.scheduleATask(batchDefinition.getTaskId(), solrenViewTaskConfig, batchDefinition.getCronExpression());
            jobScheduler = jobSchedulerMapping(batchDefinition);
            LOGGER.info("Exiting scheduleJob with scheduleId: {} and Next Execution Time: {}",
                    batchDefinition.getTaskId(), jobScheduler.getNextExecutionTime());
        } catch (Exception e) {
            LOGGER.error("Exception while scheduling: {}", e.getMessage());
            return BaseResponse.builder()
                    .code(HttpStatus.CONFLICT.value())
                    .message("ERROR")
                    .data(e)
                    .build();
        }
        return BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("SUCCESS, Job has been scheduled")
                .data(jobScheduler)
                .build();
    }

    private JobScheduler jobSchedulerMapping(BatchDefinition batchDefinition) {
        return jobSchedulerService.saveOrUpdate(JobScheduler.builder()
                .status("ACTIVE")
                .cronExpression(batchDefinition.getCronExpression())
                .jobName(batchDefinition.getJobName())
                .message(batchDefinition.getRunNotes())
                .taskId(String.valueOf(UUID.randomUUID()))
                .scheduled(new Date())
                .nextExecutionTime(jobSchedulerService.getNextValidTime(batchDefinition.getCronExpression()))
                .build());
    }

    @Override
    public void removeScheduledTask(String jobId) {
        syncSchedulerV2.removeScheduledTask(jobId);
    }

    @Override
    public List<WeatherDTO> getFifteenDayWeatherDay(Long compKey) {
        masterTenantService.setCurrentDb(compKey);
        Long days = 15L;
        List<GardenDetail> data = weatherDataRepository.findLatAndLog();
        List<WeatherDTO> weatherDataList = new ArrayList<>();
        Optional<TenantConfig> weatherApi = null;
        Optional<TenantConfig> weatherApiKey = null;
        try {
            weatherApi = tenantConfigService.findByParameter(Constants.WEATHER_API.isWeatherApiEnabledParam);
            weatherApiKey = tenantConfigService.findByParameter(Constants.WEATHER_API.WeatherApiKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (Constants.WEATHER_API.isWeatherApiEnabled.equals(weatherApi.get().getText())) {
            for (GardenDetail gardenDetail : data) {
                String response = dataExchange.getWeatherData(gardenDetail,weatherApiKey, days);
                try {
                    WeatherDTO weatherData = objectMapper.readValue(response, WeatherDTO.class);
                    weatherData.setGardenId(gardenDetail.getRefId());
                    weatherDataList.add(weatherData);
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.info("Weather data Error: " + e.getMessage());
                }
            }
        } else {
            return Collections.emptyList();
        }
        weatherService.saveWeatherData(weatherDataList,null);
        return weatherDataList;
    }

//    @Override
//    public void saveWeatherData() {
//        LOGGER.info("Entering add");
//        int num1=10;
//        int num2=20;
//        LOGGER.info("Num1 + Num2 = {}", num1 + num2);
//        LOGGER.info("Exiting add");
//    }
}