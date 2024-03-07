package com.solar.api.saas.module.com.solar.scheduler.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Utility;
import com.solar.api.saas.module.com.solar.batch.service.BatchService;
import com.solar.api.saas.module.com.solar.scheduler.configuration.SyncScheduler;
import com.solar.api.saas.module.com.solar.scheduler.model.JobScheduler;
import com.solar.api.saas.module.com.solar.utility.BatchEngineUtilityService;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.repository.BillingHeadRepository;
import com.solar.api.tenant.service.process.billing.invoice.BillInvoiceService;
import com.solar.api.tenant.service.process.subscription.termination.SubscriptionTermination;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.launch.NoSuchJobInstanceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@PreAuthorize("checkAccess()")
@RestController("ScheduleController")
@RequestMapping("/schedule")
public class SchedulerController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private BatchEngineUtilityService batchEngineUtilityService;
    @Autowired
    private BatchService batchService;
    @Autowired
    private SubscriptionTermination subscriptionTermination;
    @Autowired
    private SyncScheduler syncScheduler;

    private final BillInvoiceService billInvoiceService;

    private final BillingHeadRepository billingHeadRepository;

    public SchedulerController(BillInvoiceService billInvoiceService, BillingHeadRepository billingHeadRepository) {
        this.billInvoiceService = billInvoiceService;
        this.billingHeadRepository = billingHeadRepository;
    }

    @PostMapping("/addTask")
    public ObjectNode addTask(@RequestParam Map<String, String> map) throws NoSuchJobInstanceException {
        return batchEngineUtilityService.scheduleJobWithParameters(map);
    }

    @RequestMapping(value = "/jobCheck", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void jobCheck (@RequestBody JobScheduler jobScheduler) {
        batchService.jobCheck(jobScheduler, null);
    }


    @RequestMapping(value = "/triggerThread", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void triggerThread(@RequestParam("scheduleInSeconds") Integer scheduleInSeconds) {
        syncScheduler.trigger(scheduleInSeconds);
    }

    //TODO: To be called Async
    @DeleteMapping(value = "/removeScheduledJob")
    public String removeScheduledJob(@RequestParam("id") Long id, @RequestParam("timeInSeconds") int timeInSeconds) {
        String response = syncScheduler.removeJob(id);
        try {
            syncScheduler.destroy(timeInSeconds);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return response;
    }

    @GetMapping("/abort")
    public void emptyScheduledTaskList() {
        try {
            syncScheduler.abortSync();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @GetMapping("/adhoc/executeSubscriptionTermination")
    public void executeAdhocTermination() {
        subscriptionTermination.executeAdhocTermination();
    }

    @RequestMapping(value = "/evaluateCron", method = RequestMethod.GET)
    public void evaluateCron(@RequestParam("cronExp") String cronExp) throws ParseException {
        Date endDateTime = new Date();
        Date newDate = DateUtils.addHours(endDateTime, 1);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(newDate.toInstant(), ZoneId.systemDefault());
        String cron = batchEngineUtilityService.generateCron(localDateTime);
        CronExpression springCron = new CronExpression(cron);
        Date date = springCron.getFinalFireTime();
        System.out.println("Cron Expression: " + springCron);
        System.out.println("Cron Summary: " + springCron.getExpressionSummary());
        System.out.println("Cron TimeZone: " + springCron.getTimeZone());
        System.out.println("getFinalFireTime: " + date);
    }

    @GetMapping(value = "/cancelAllScheduledJob")
    public ResponseEntity<HttpStatus> cancelAllScheduledJob() {
        syncScheduler.abortSync();
        batchEngineUtilityService.emptyScheduledTaskList();
        return new ResponseEntity<HttpStatus>(HttpStatus.OK);
    }

//    @GetMapping("/task/{second}/{minute}/{hour}/{date}/{month}/{day}")
//    public void scheduleTask(@PathVariable String second,
//                             @PathVariable String minute,
//                             @PathVariable String hour,
//                             @PathVariable String date,
//                             @PathVariable String month,
//                             @PathVariable String day) {
//        jobSchedulerService.cronTest(second, minute, hour, date, month, day);
//    }

    @GetMapping("/getTime")
    public LocalDateTime timeZone() {
        batchEngineUtilityService.timeTest();
        return LocalDateTime.now();
    }

    @GetMapping("/getAll")
    public List<JobScheduler> getAll() {
        return batchEngineUtilityService.getAll();
    }

    @GetMapping("/testTemplate/{billingMonthYear}")
    public List<String> getDaysInMonths(@PathVariable String billingMonthYear) {
        Date date = Utility.getDate(billingMonthYear, Utility.SYSTEM_DATE_FORMAT);
        List<String> monthYears = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
        monthYears.add(format.format(date));
        Date daysInMonths = Utility.getEndOfMonth(date, Utility.SYSTEM_DATE_FORMAT);
        for (int i = 1; i < daysInMonths.getDate(); i++) {
            date = Utility.addDays(date, 1);
            monthYears.add(format.format(date));
        }
        return monthYears;
    }
}
