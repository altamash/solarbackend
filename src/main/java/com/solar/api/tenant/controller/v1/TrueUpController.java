package com.solar.api.tenant.controller.v1;

import com.solar.api.tenant.model.extended.CsgBillcreRecon;
import com.solar.api.tenant.service.trueup.TrueUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("TrueUpController")
@RequestMapping(value = "/trueup")
public class TrueUpController {

    @Autowired
    private TrueUpService trueUpService;

    @GetMapping("/generate/gardenId/{gardenId}/subscriptionIds/{subscriptionIdsCSV}/start/{startMonthYear}/end" +
            "/{endMonthYear}")
    public List<CsgBillcreRecon> generate(@PathVariable String gardenId,
                                          @PathVariable String subscriptionIdsCSV,
                                          @PathVariable String startMonthYear,
                                          @PathVariable String endMonthYear) throws ParseException {
        List<Long> subscriptionIds = null;
        if (!"-1".equals(subscriptionIdsCSV)) {
            subscriptionIds =
                    Arrays.stream(subscriptionIdsCSV.split(",")).map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
        }
        return trueUpService.generate(gardenId, subscriptionIds, startMonthYear, endMonthYear);
    }

    @GetMapping
    public List<CsgBillcreRecon> findAll(
            @RequestParam(value = "subscriptionId", required = false) Long subscriptionId,
            @RequestParam(value = "gardenId", required = false) String gardenId,
            @RequestParam(value = "premiseNo", required = false) String premiseNo,
            @RequestParam(value = "periodStartDate", required = false) String periodStartDate,
            @RequestParam(value = "periodEndDate", required = false) String periodEndDate) {
        return trueUpService.view(subscriptionId, gardenId, premiseNo, periodStartDate, periodEndDate);
    }

//    @PostMapping("/testEmail")
//    public void add(@RequestBody AnalyticalCalculationDTO analyticalCalculationDTO) throws IOException {
//        trueUpService.testEmail(analyticalCalculationDTO.getScope());
//    }
//
//    @GetMapping("/testEmail")
//    public void add(@RequestParam(value = "template", required = false) String template) throws IOException {
//        trueUpService.testEmail(template);
//    }
}
