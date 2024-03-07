package com.solar.api.saas.module.com.solar.scheduler.v2.jobs;

import com.solar.api.saas.model.extended.BatchDefinition;
import com.solar.api.saas.module.com.solar.batch.service.SolrenviewService;
import com.solar.api.saas.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SolrenViewTaskConfig implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolrenViewTaskConfig.class);

    @Autowired
    SolrenviewService solrenviewService;
    @Autowired
    TaskScheduler taskScheduler;
    @Autowired
    EmailService emailService;

    @Override
    public void run() {
        LOGGER.info("Entering Solrenview Half Hourly Task");
        try {
            solrenviewService.getSVData();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            try {
                emailService.batchNotification("Solrenview", 34L, e.getMessage(), "Solrenview Error");
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage());
            }
        }
        LOGGER.info("Exiting Solrenview Half Hourly Task");
    }
}
