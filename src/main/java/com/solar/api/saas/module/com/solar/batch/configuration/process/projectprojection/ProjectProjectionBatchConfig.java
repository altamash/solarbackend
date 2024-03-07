package com.solar.api.saas.module.com.solar.batch.configuration.process.projectprojection;


import com.solar.api.AppConstants;
import com.solar.api.helper.Utility;

import com.solar.api.tenant.model.billing.billingHead.BillingHead;

import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.service.BillingCreditsService;
import com.solar.api.tenant.service.BillingHeadService;
import com.solar.api.tenant.service.SubscriptionService;
import com.solar.api.tenant.service.process.billing.BillingService;
import com.solar.api.tenant.service.process.billing.invoice.BillInvoiceService;
import com.solar.api.tenant.service.process.subscription.activation.SubscriptionActivation;
import com.solar.api.tenant.service.tansStage.TransStageTempService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;

import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;

import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Configuration
@EnableBatchProcessing
public class ProjectProjectionBatchConfig {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private BillingCreditsService billingCreditsService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private BillingHeadService billingHeadService;
    @Autowired
    private BillingService billingService;
    @Autowired
    private SubscriptionActivation subscriptionActivation;
    @Autowired
    private TransStageTempService transStageTempService;
    @Autowired
    private BillInvoiceService billInvoiceService;

    @Bean("ProjectProjectionRevenueBatch")
    public Job batchJob(JobBuilderFactory jobBuilderFactory,
                        StepBuilderFactory stepBuilderFactory) {

        Step step = stepBuilderFactory.get("GenerateBillingHeadAndCredits")
                .tasklet(generateCredits())
                .build();
        Step step1 = stepBuilderFactory.get("generateBillingHeads")
                .tasklet(generateBillingHeads())
                .build();
        Step step2 = stepBuilderFactory.get("ExecuteBilling")
                .tasklet(executeBilling())
                .build();
        Step step3 = stepBuilderFactory.get("GenerateHtml")
                .tasklet(generateHtml())
                .build();
        Step step4 = stepBuilderFactory.get("GeneratePDF")
                .tasklet(generatePdf())
                .build();
        return jobBuilderFactory.get(AppConstants.PROJECT_PROJECTION_REVENUE)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .next(step1)
                .next(step2)
                .next(step3)
                .next(step4)
                .build();

    }


    private Tasklet generateCredits() {
        return (contribution, chunkContext) -> {
            chunkContext.getStepContext().getStepExecutionContext();
            try {
                List<String> months = new ArrayList<>();
                String month = String.valueOf(chunkContext.getStepContext().getJobParameters().get("months"));
                months = Arrays.stream(month.split(","))
                        .filter(s -> !s.equals("null") && !s.trim().equals(""))
                        .collect(Collectors.toList());
                if (months.size() == 0) {
                    months = Utility.getNextThreeMonths();
                }
                List<CustomerSubscription> customerSubscriptions = subscriptionService.manageCustomerSubscriptionsForProjection();
                billingCreditsService.manageBillingCreditsForProjection(customerSubscriptions, formatDates(months), Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("jobScheduleId"))));

            } catch (Exception ex) {
                chunkContext.getStepContext().getStepExecution().setExitStatus(new ExitStatus("FAILED", ex.getMessage()));
                LOGGER.error(ex.getMessage(), ex);
            }
            return RepeatStatus.FINISHED;
        };
    }
    private Tasklet generateBillingHeads() {

        return (contribution, chunkContext) -> {
            chunkContext.getStepContext().getStepExecutionContext();
            try {
                List<String> months = new ArrayList<>();
                String month = String.valueOf(chunkContext.getStepContext().getJobParameters().get("months"));
                months = Arrays.stream(month.split(","))
                        .filter(s -> !s.equals("null") && !s.trim().equals(""))
                        .collect(Collectors.toList());
                if (months.size() == 0) {
                    months = Utility.getNextThreeMonths();
                }
                List<CustomerSubscription> customerSubscriptions = subscriptionService.findAllCustomerSubscriptionsForProjection();
                billingHeadService.manageBillingHeadForProjection(customerSubscriptions, months);
            } catch (Exception ex) {
                chunkContext.getStepContext().getStepExecution().setExitStatus(new ExitStatus("FAILED", ex.getMessage()));
                LOGGER.error(ex.getMessage(), ex);
            }
            return RepeatStatus.FINISHED;
        };
    }
    private Tasklet executeBilling() {

        return (contribution, chunkContext) -> {
            chunkContext.getStepContext().getStepExecutionContext();
            try {
                List<String> months = new ArrayList<>();
                String month = String.valueOf(chunkContext.getStepContext().getJobParameters().get("months"));
                months = Arrays.stream(month.split(","))
                        .filter(s -> !s.equals("null") && !s.trim().equals(""))
                        .collect(Collectors.toList());
                if (months.size() == 0) {
                    months = Utility.getNextThreeMonths();
                }
                List<BillingHead> billingHeads = billingHeadService.findAllBillingHeadForProjection(subscriptionService.findAllCustomerSubscriptionsForProjection(), months);
                billingService.fillTransStageTables(billingHeads);
                subscriptionActivation.generateBills(billingHeads, Long.valueOf(String.valueOf(chunkContext.getStepContext().getJobParameters().get("jobScheduleId"))), false);
                transStageTempService.deleteAll();
            } catch (Exception ex) {
                chunkContext.getStepContext().getStepExecution().setExitStatus(new ExitStatus("FAILED", ex.getMessage()));
                LOGGER.error(ex.getMessage(), ex);
            }
            return RepeatStatus.FINISHED;
        };
    }

    private Tasklet generateHtml() {

        return (contribution, chunkContext) -> {
            chunkContext.getStepContext().getStepExecutionContext();
            try {
                List<String> months = new ArrayList<>();
                String month = String.valueOf(chunkContext.getStepContext().getJobParameters().get("months"));
                months = Arrays.stream(month.split(","))
                        .filter(s -> !s.equals("null") && !s.trim().equals(""))
                        .collect(Collectors.toList());
                if (months.size() == 0) {
                    months = Utility.getNextThreeMonths();
                }
                List<CustomerSubscription> customerSubscriptions =subscriptionService.findAllCustomerSubscriptionsForProjection().stream()
                        .collect(Collectors.toMap(
                                CustomerSubscription::getSubscriptionTemplate,
                                x -> x,
                                (existing, replacement) -> existing.getId() < replacement.getId() ? existing : replacement
                        ))
                        .values()
                        .stream()
                        .collect(Collectors.toList());
                List<String> monthList2 = new ArrayList<>(months);
                if (months.size() > 1) {
                    months.subList(1, months.size()).clear();
                }
                List<BillingHead> billingHeads = billingHeadService.findAllBillingHeadForProjection(customerSubscriptions, months);
                billInvoiceService.generateDraftHTMLProjection(billingHeads,monthList2);
            } catch (Exception ex) {
                chunkContext.getStepContext().getStepExecution().setExitStatus(new ExitStatus("FAILED", ex.getMessage()));
                LOGGER.error(ex.getMessage(), ex);
            }
            return RepeatStatus.FINISHED;
        };
    }
    private Tasklet generatePdf() {

        return (contribution, chunkContext) -> {
            chunkContext.getStepContext().getStepExecutionContext();
            try {
                List<String> months = new ArrayList<>();
                String month = String.valueOf(chunkContext.getStepContext().getJobParameters().get("months"));
                months = Arrays.stream(month.split(","))
                        .filter(s -> !s.equals("null") && !s.trim().equals(""))
                        .collect(Collectors.toList());
                if (months.size() == 0) {
                    months = Utility.getNextThreeMonths();
                }
                List<CustomerSubscription> customerSubscriptions =subscriptionService.findAllCustomerSubscriptionsForProjection().stream()
                        .collect(Collectors.toMap(
                                CustomerSubscription::getSubscriptionTemplate,
                                x -> x,
                                (existing, replacement) -> existing.getId() < replacement.getId() ? existing : replacement
                        ))
                        .values()
                        .stream()
                        .collect(Collectors.toList());
                if (months.size() > 1) {
                    months.subList(1, months.size()).clear();
                }
                List<BillingHead> billingHeads = billingHeadService.findAllBillingHeadForProjection(customerSubscriptions, months);
                billInvoiceService.convertHTMLToPDF(billingHeads);
            } catch (Exception ex) {
                chunkContext.getStepContext().getStepExecution().setExitStatus(new ExitStatus("FAILED", ex.getMessage()));
                LOGGER.error(ex.getMessage(), ex);
            }
            return RepeatStatus.FINISHED;
        };
    }
    private List<String> formatDates(List<String> dateList) {
        List<String> formattedDates = new ArrayList<>();
        for (String date : dateList) {
            String[] parts = date.split("-");
            if (parts.length == 2 && parts[0].length() == 2 && parts[1].length() == 4) {
                String formattedDate = parts[1] + "-" + parts[0];
                formattedDates.add(formattedDate);
            } else if (date.matches("\\d{4}-\\d{2}")) {
                formattedDates.add(date);
            }
        }
        return formattedDates;
    }
}
