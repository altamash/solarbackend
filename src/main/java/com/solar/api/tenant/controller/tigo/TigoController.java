package com.solar.api.tenant.controller.tigo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.solar.api.saas.module.com.solar.batch.service.tigo.TigoService;
import com.solar.api.saas.module.com.solar.batch.service.tigo.TigoServiceV2;
import com.solar.api.tenant.model.stage.monitoring.tigo.TigoResponseDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("TigoController")
@RequestMapping(value = "/tigo")
public class TigoController {

    final TigoService tigoService;
    final TigoServiceV2 tigoServiceV2;

    public TigoController(TigoService tigoService, TigoServiceV2 tigoServiceV2) {
        this.tigoService = tigoService;
        this.tigoServiceV2 = tigoServiceV2;
    }

    @Deprecated
    @ApiOperation(value = "get Tigo historic data")
    @GetMapping("/getHistoricData")
    public ResponseEntity<String> getHistoricData(@RequestParam(name = "startTime", required = true) String startTime,
                                                  @RequestParam(name = "endTime", required = true) String endTime) {
        return tigoService.getHistoricData(startTime, endTime);
    }

    @Deprecated
    @ApiOperation(value = "get Tigo current data")
    @GetMapping("/getMinuteData")
    public ResponseEntity<String> getMinuteData(@RequestParam(name = "startTime", required = false) String startTime,
                                                @RequestParam(name = "endTime", required = false) String endTime) {
        return tigoService.getMinuteData(startTime, endTime);
    }

    @ApiOperation(value = "get Tigo current data")
    @GetMapping("/getMinuteDataV2")
    public ResponseEntity<String> getMinuteDataV2(@RequestParam(name = "startTime", required = false) @ApiParam("yyyy-MM-dd'T'HH:mm:ss") String startTime,
                                                  @RequestParam(name = "endTime", required = false) @ApiParam("yyyy-MM-dd'T'HH:mm:ss") String endTime,
                                                  @RequestParam(name = "subsIds", required = false) String subsIds,
                                                  @RequestParam(name = "forceUpdate", required = true) Boolean forceUpdate) {
        return tigoServiceV2.getMinuteData(startTime, endTime, subsIds, forceUpdate);
    }

    @ApiOperation(value = "Test tigo API")
    @GetMapping("/hitURL")
    public TigoResponseDTO getURL() throws JsonProcessingException {
        return tigoService.hitURL();
    }
}
