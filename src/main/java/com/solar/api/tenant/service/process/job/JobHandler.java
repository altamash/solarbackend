package com.solar.api.tenant.service.process.job;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.saas.model.JobManager;
import com.solar.api.saas.service.process.ExecuteParams;
import com.solar.api.saas.service.process.rule.RulesInitiator;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import org.springframework.batch.core.JobExecution;

import java.util.List;

public interface JobHandler {


    JobExecution getJobExecutionContext(String name);

    Long getLastJobInstanceId(String jobName);

    void billingBySubscriptionType(CustomerSubscription subscription, String billingMonth,
                                   RulesInitiator initiator, ExecuteParams params, JobManager jobManager);

    void billingBySubscriptionType(String subscriptionCode, List<Long> rateMatrixHeadIds, String billingMonth,
                                   String type);

    void billingBySubscriptionType(String subscriptionCode, String billingMonth, String type);

    JobManager addJobManager(String status, ObjectNode requestMessage);

    void updateJobManager(JobManager jobManager);
}
