package com.solar.api.tenant.controller.stage.monitor;

import com.solar.api.saas.module.com.solar.batch.service.StageMonitorService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("StageMonitor")
@RequestMapping(value = "/stageMonitor")
public class StageMonitorController {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    StageMonitorService stageMonitorService;

    @ApiOperation(value = "Get Subscription Data from Mongo")
    @GetMapping("/getMongoSubscriptionsAndMeasures")
    public ResponseEntity<String> getMongoSubscriptionsAndMeasuresData() throws Exception {
           return stageMonitorService.getMongoSubscriptionsAndMeasures();
    }


    @ApiOperation(value = "Merge Stage Definition with Extra Temp Stage")
    @GetMapping("/mergeStageDefinition")
    public void mergeStageDefinition() {
        stageMonitorService.transferSubscriptionsToStageDefinition();
    }

}
