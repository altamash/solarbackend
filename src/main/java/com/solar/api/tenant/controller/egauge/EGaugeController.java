package com.solar.api.tenant.controller.egauge;

import com.solar.api.helper.Utility;
import com.solar.api.saas.module.com.solar.batch.service.EGaugeService;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("EGaugeController")
@RequestMapping(value = "/egauge")
public class EGaugeController {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    EGaugeService eGaugeService;

    @ApiOperation(value = "get eGauge current data")
    @GetMapping("/getGraphData")
    public ResponseEntity<String> getGraphData() throws Exception {
        return eGaugeService.getEGaugeData();
    }

    @ApiOperation(value = "get historic data by date from and to")
    @GetMapping("/getHistoricGraphData")
    public BaseResponse getHistoricGraphData(@RequestParam(value = "extSubscriptionIds", required = false) List<String> subscriptionIds,
                                             @RequestParam(name = "fromDate") @ApiParam("yyyy-MM-dd HH:mm") String fromDate,
                                             @RequestParam(name = "toDate") @ApiParam("yyyy-MM-dd HH:mm") String toDate) {
        try {
            LocalDateTime startOfDay = Utility.toLocalDate(fromDate);
            LocalDateTime endOfDay = Utility.toLocalDate(toDate);
            LOGGER.info("--------------------------------------------------------------------------");
            LOGGER.info("Start Of Day: " + startOfDay);
            LOGGER.info("End Of Day:  " + endOfDay);
            LOGGER.info("--------------------------------------------------------------------------");
            return eGaugeService.getHistoricGraphData(subscriptionIds, fromDate, toDate);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return BaseResponse.builder().code(HttpStatus.UNPROCESSABLE_ENTITY.value()).build();
    }
}
