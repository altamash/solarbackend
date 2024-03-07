package com.solar.api.tenant.controller.v1.extended;

import com.solar.api.tenant.mapper.stripe.SubmitInterestDTO;
import com.solar.api.tenant.service.solarAmps.SolarAmpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController("SubmitInterestController")
@RequestMapping(value = "/submitInterest")
public class SubmitInterestController {
    @Autowired
    private SolarAmpsService solarAmpsService;

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody @Valid SubmitInterestDTO body) {
        return solarAmpsService.submitInterest(body);
    }
}
