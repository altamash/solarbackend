package com.solar.api.configuration;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@EnableBatchProcessing
public class BatchConfig {

    @PostConstruct
    public void init() {
        System.getProperties();
    }
}
