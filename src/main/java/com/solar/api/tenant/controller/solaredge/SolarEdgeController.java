package com.solar.api.tenant.controller.solaredge;

import com.solar.api.saas.module.com.solar.batch.service.SolarEdgeService;
import io.swagger.v3.oas.annotations.Hidden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Date;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("SolarEdgeController")
@RequestMapping(value = "/solarEdge")
@Deprecated
public class SolarEdgeController {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    SolarEdgeService edgeService;

    @Deprecated
    @GetMapping("/runDaily")
    public ResponseEntity<?> runDaily() {
        try {
            return edgeService.runDaily(new Date());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @Deprecated
    @GetMapping("/runAtIntervals")
    public ResponseEntity<?> runAtIntervals() {
        try {
            return edgeService.runAtIntervals(new Date());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @Deprecated
    @GetMapping("/runDailyHistoric")
    public ResponseEntity<?> runDailyHistoric(@RequestParam(name = "fromDate", required = true) String fromDate,
                                              @RequestParam(name = "toDate", required = true) String toDate) {
        try {
            return edgeService.runDailyHistoric(fromDate, toDate);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/runAtIntervalsHistoricBetween")
    public ResponseEntity<?> runAtIntervalsHistoricBetween(@RequestParam(name = "fromDate") String fromDate,
                                              @RequestParam(name = "toDate", required = false) String toDate) {
        try {
            return edgeService.runAtIntervalsHistoricBetween(fromDate, toDate);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/getCurrentData")
    public ResponseEntity<?> getCurrentData() {
        try {
            return edgeService.getCurrentData();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @Deprecated
    @GetMapping("/runAtIntervalsHistoric")
    public ResponseEntity<?> runAtIntervalsHistoric(@RequestParam(name = "date", required = true) String date) {
        try {
            return edgeService.runAtIntervalsHistoric(date);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

}
