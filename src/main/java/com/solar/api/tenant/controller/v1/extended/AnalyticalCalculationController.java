package com.solar.api.tenant.controller.v1.extended;

import com.solar.api.tenant.mapper.AnalyticalCalculationDTO;
import com.solar.api.tenant.service.AnalyticalCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.solar.api.tenant.mapper.AnalyticalCalculationMapper.toAnalyticalCalculationDTO;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("AnalyticalCalculationController")
@RequestMapping(value = "/analytics")
public class AnalyticalCalculationController {

    @Autowired
    private AnalyticalCalculationService analyticalCalculationService;

    @GetMapping("/findByAccountIdAndSubscriptionIdAndAnalysis/{accountId}/{subscriptionId}/{analysis}")
    public AnalyticalCalculationDTO findByAccountIdAndSubscriptionIdAndAnalysis(
            @PathVariable Long accountId,
            @PathVariable Long subscriptionId,
            @PathVariable String analysis) {
        return toAnalyticalCalculationDTO(analyticalCalculationService.findByAccountIdAndSubscriptionIdAndAnalysis(
                accountId,
                subscriptionId,
                analysis));
    }
}
