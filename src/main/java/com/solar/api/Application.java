package com.solar.api;

import com.solar.api.helper.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableBatchProcessing
@EnableScheduling
public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private static long APP_START_MILLIS = System.currentTimeMillis();

    static {
        System.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        if (System.getenv("PROFILE") != null) {
            System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, System.getenv("PROFILE"));
        }
    }

    public static void main(String[] args) {
        try {
            SpringApplication.run(Application.class, args);
            String appStartupTime = Utility.getFormattedMillis(System.currentTimeMillis() - APP_START_MILLIS);
            LOGGER.info("App startup time: {}", appStartupTime);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
