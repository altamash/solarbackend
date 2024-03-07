package com.solar.api.tenant.service.process.subscription.termination;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.helper.Utility;
import com.solar.api.saas.service.EmailService;
import com.solar.api.saas.service.job.EJobName;
import com.solar.api.saas.service.job.EJobStatus;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.billingHead.EBillStatus;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.subscription.SubscriptionTerminationTemplate;
import com.solar.api.tenant.repository.BillingHeadRepository;
import com.solar.api.tenant.repository.CustomerSubscriptionMappingRepository;
import com.solar.api.tenant.repository.CustomerSubscriptionRepository;
import com.solar.api.tenant.service.BillingHeadService;
import com.solar.api.tenant.service.SubscriptionService;
import com.solar.api.tenant.service.job.JobManagerTenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionTermination {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private BillingHeadService billingHeadService;
    @Autowired
    private BillingHeadRepository billingHeadRepository;
    @Autowired
    private CustomerSubscriptionRepository customerSubscriptionRepository;
    @Autowired
    private CustomerSubscriptionMappingRepository customerSubscriptionMappingRepository;
    @Autowired
    private JobManagerTenantService jobManagerTenantService;
    @Autowired
    private EmailService emailService;

    @Async
    //TODO: run batch for adhoc termination
    public void executeAdhocTermination() {

        try {
            //fetch subscriptions status ACTIVE and mapping equal to ROLLDT
            JobManagerTenant jobManagerTenant =
                    jobManagerTenantService.add(EJobName.ADHOC_SUBSCRIPTION_TERMINATION.toString(), null,
                            EJobStatus.RUNNING.toString(), null, LOGGER);
            //run batch where termination date <= current date and termination date < roll over date
            /*subscriptionService.getAllAdhocSubscriptionTermination(Constants.CUSTOMER_SUBSCRIPTION_STATUS.ACTIVE,
                    Constants.SUBSCRIPTION_TERMINATION_RATE_CODES.ROLL_DT)
                    .forEach(cs -> adhocPostTerminationProcess(cs, jobManagerTenant.getId(),
                            Constants.TERMINATION_TYPE.ADHOC));*/
            subscriptionService.terminationBatchQuery().forEach(cs -> adhocPostTerminationProcess(cs,jobManagerTenant.getId(),null));
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), null, LOGGER);
        } catch (Exception ex) {
            ex.getStackTrace();
        }
    }

    //TODO: steps after termination scheduling done for adhoc and auto
    private void adhocPostTerminationProcess(SubscriptionTerminationTemplate csTemplate, Long jobId, String terminationType) {
        //TODO: steps after termination scheduling done for adhoc and auto

        try {

            if (csTemplate != null) {

                LOGGER.info("Termination for subscription id {} started.", csTemplate.getId());
                CustomerSubscription cs = customerSubscriptionRepository.findById(csTemplate.getId()).get();
                List<BillingHead> billingHeads = billingHeadService.findBySubscriptionId(csTemplate.getId());
                String[] terminationDate = csTemplate.getAutoDate().toString().split("-");
                String termBillingMonthYear = terminationDate[1].concat("-").concat(terminationDate[0]);
                Long id = billingHeads.stream().filter(termDate -> termDate.getBillingMonthYear().equals(termBillingMonthYear)).map(BillingHead::getId).findFirst().get();
                List<Long> billHeadIds = null;

                if (id != null) {
                    List<Long> updateBillHeads = billingHeads.stream().filter(futureIds -> futureIds.getId() >= id).map(BillingHead::getId).collect(Collectors.toList());

                    billHeadIds = billingHeads.stream().filter(headId -> updateBillHeads.contains(headId.getId()))
                            .filter(st -> !st.getBillStatus().equalsIgnoreCase(EBillStatus.PAID.getStatus()))
                            .map(BillingHead::getId).collect(Collectors.toList());

                    //billingDetailRepository.updateBillDetailForTermination(billHeadIds);
                    billingHeadRepository.updateBillHeadForTermination(EBillStatus.DISCONTINUED.getStatus(),
                            billHeadIds, LocalDateTime.now());
                }

                SimpleDateFormat sdf = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
                //set customer subscription status to ENDED
                cs.setSubscriptionStatus(Constants.SUBSCRIPTION_TERMINATION_STATUS.CUSTOMER_SUBSCRIPTION_STATUS_ENDED);
                cs.setClosedDate(sdf.parse(sdf.format(new Date())));
                customerSubscriptionRepository.save(cs);

                //set csm status NO for ROLL
                CustomerSubscriptionMapping csm =
                        customerSubscriptionMappingRepository.findByRateCodeAndSubscription(Constants.SUBSCRIPTION_TERMINATION_RATE_CODES.ROLL, cs);
                csm.setValue(Constants.SUBSCRIPTION_TERMINATION_STATUS.CUSTOMER_SUBSCRIPTION_MAPPING_STATUS_ROLL);
                customerSubscriptionMappingRepository.save(csm);

                //for auto termination ROLLDT will be NULL
                if (cs.getTerminationDate() == null) { //terminationType.equals(Constants.TERMINATION_TYPE.AUTO)) {
                    CustomerSubscriptionMapping csmRollOverDate =
                            customerSubscriptionMappingRepository.findByRateCodeAndSubscription(Constants.SUBSCRIPTION_TERMINATION_RATE_CODES.ROLL_DT, cs);
                    csmRollOverDate.setDefaultValue(null);
                    customerSubscriptionMappingRepository.save(csm);
                }

                LOGGER.info("Termination for subscription id {} completed.", cs.getId());
            }

        } catch (Exception ex) {
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            messageJson.put("job_id", jobId);
            messageJson.put("subscription_id", csTemplate.getId());
            messageJson.put("message", ex.getMessage());
            LOGGER.error(messageJson.toPrettyString(), ex);
        }
        LOGGER.info("Subscription {} Adhoc Termination");
    }

    //TODO: 15days before maturity, subscribers will notify via email for the auto termination
    @Async
    public List<SubscriptionTerminationTemplate> getAllAutoTerminationNotification() {

        List<SubscriptionTerminationTemplate> cs = null;
        try {
            cs = subscriptionService.getAllAutoTerminationNotification(Constants.SUBSCRIPTION_TERMINATION_RATE_CODES.ROLL, Constants.SUBSCRIPTION_TERMINATION_STATUS.CUSTOMER_SUBSCRIPTION_MAPPING_STATUS_ROLL);
            String templateId = AppConstants.AUTO_TERMINATION_NOTIFICATION_TEMPLATE;

            if (!cs.isEmpty() && cs.size() != 0) {
                cs.forEach(cust -> {
                    try {
                        Personalization personalization = new Personalization();
                        personalization.addDynamicTemplateData("subscription_id", cust.getId());
                        personalization.addDynamicTemplateData("termination_date",
                                cust.getTerminationDate().toString() != null ? cust.getTerminationDate().toString() :
                                        "");
                        personalization.addTo(new Email(AppConstants.SOLAR_TEST_EMAIL));

                        emailService.emailDynamicTemplateWithNoFile(templateId, personalization);

                        CustomerSubscription customerSubscription =
                                customerSubscriptionRepository.findById(cust.getId()).get();
                        customerSubscription.setTerminationNotificationSent("1");
                        customerSubscriptionRepository.save(customerSubscription);

                    } catch (IOException ioException) {
                        LOGGER.error(ioException.getMessage(), ioException);
                    }
                });
            }

        } catch (Exception ex) {
            ex.getStackTrace();
            LOGGER.error(ex.getMessage(), ex);
        }
        return cs;
    }

    @Async
    //TODO:auto termination run after 1 day of endDate (maturity)
    public void executeAutoTermination() {

        try {
            //current date equal to enddate+1
            JobManagerTenant jobManagerTenant =
                    jobManagerTenantService.add(EJobName.AUTO_SUBSCRIPTION_TERMINATION.toString(), null,
                            EJobStatus.RUNNING.toString(), null, LOGGER);
            /*subscriptionService.getAllAutoSubscriptionTerminationOnEndDate(Constants.SUBSCRIPTION_TERMINATION_RATE_CODES.ROLL,
                    Constants.SUBSCRIPTION_TERMINATION_STATUS.CUSTOMER_SUBSCRIPTION_MAPPING_STATUS_ROLL)
                    .forEach(cs -> adhocPostTerminationProcess(cs, jobManagerTenant.getId(),
                            Constants.TERMINATION_TYPE.AUTO));*/
            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), null, LOGGER);
        } catch (Exception ex) {
            ex.getStackTrace();
        }
    }

}
