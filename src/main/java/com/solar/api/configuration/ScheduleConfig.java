package com.solar.api.configuration;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@EnableScheduling
public class ScheduleConfig {

    @PostConstruct
    public void init() {
        System.getProperties();
    }
}
