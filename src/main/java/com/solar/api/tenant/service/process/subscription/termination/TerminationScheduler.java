package com.solar.api.tenant.service.process.subscription.termination;

//@Component
public class TerminationScheduler {

//    @Autowired
//    private SubscriptionTermination subscriptionTermination;

//    @Scheduled(cron = " 0 */60 * * * *?")
    public void cronJobSch() {
//        subscriptionTermination.getAllAutoTerminationNotification();
        System.out.println("Scheduler for Auto Termination Notification");
    }
}