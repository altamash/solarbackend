package com.solar.api.tenant.controller.solrenview;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.solar.api.saas.module.com.solar.batch.service.SolrenviewService;
import com.solar.api.tenant.mapper.BaseResponse;
import com.solar.api.tenant.model.stage.monitoring.SolrenviewResponseDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("SolrenviewController")
@RequestMapping(value = "/sv")
public class SolrenviewController {

    @Autowired
    SolrenviewService solrenviewService;

    @ApiOperation(value = "get SolrenView historic data")
    @GetMapping("/getSVHistoricData")
    public ResponseEntity<String> getSVHistoricData(@RequestParam(name = "startTime") @ApiParam("yyyy-MM-dd HH:mm") String startTime,
                                                    @RequestParam(name = "endTime") @ApiParam("yyyy-MM-dd HH:mm") String endTime) {
        return solrenviewService.getSVHistoricData(startTime, endTime);
    }

    @ApiOperation(value = "get SolrenView current data")
    @GetMapping("/getSVData")
    public BaseResponse getSVData() {
        return solrenviewService.getSVData();
    }

    @ApiOperation(value = "Test solrenview API")
    @GetMapping("/hitURL")
    public SolrenviewResponseDTO getURL() throws JsonProcessingException {
        return solrenviewService.hitURL();
    }
}
