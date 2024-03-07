package com.solar.api.tenant.controller.v1;

import com.solar.api.helper.Utility;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.service.CalculationDetailsService;
import com.solar.api.tenant.service.CalculationTrackerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("CalculationTrackerController")
@RequestMapping(value = "/calculationTracker")
public class CalculationTrackerController {
    @Autowired
    private CalculationDetailsService calculationDetailsService;
    @Autowired
    private CalculationTrackerService calculationTrackerService;

    /**
     * Description: Method to get Invoice Details from Calculation Detail Table
     *
     * @param billingHeadId
     * @return
     */
    @GetMapping("/invoiceTemplate/v1/{billingHeadId}")
    public Map invoiceTemplate(@PathVariable Long billingHeadId) {
        Map response = new HashMap();
        if (billingHeadId != null) {
            return calculationDetailsService.getInvoiceTemplate(billingHeadId);
        } else {
            response.put("code", HttpStatus.PRECONDITION_FAILED);
            response.put("message", "Parameters cannot be null");
            response.put("data", null);
        }
        return response;
    }

    /**
     * Description: Method to get Invoice Details from Calculation Detail Table
     *
     * @param billingHeadId
     * @return
     */
    @GetMapping("/getInvoiceTemplate/v1/{billingHeadId}")
    public Map getInvoiceTemplate(@PathVariable Long billingHeadId) {
        Map response = new HashMap();
        if (billingHeadId != null) {
            return calculationDetailsService.getInvoiceTemplate(billingHeadId);
        } else {
            response.put("code", HttpStatus.PRECONDITION_FAILED);
            response.put("message", "Parameters cannot be null");
            response.put("data", null);
        }
        return response;
    }

    /**
     * Description: Method to return data for status wise chart with count for last 12 months
     *
     * @return
     */
    @GetMapping("/getStatusWiseBillingGraph/v1")
    public Map getStatusWiseBillingGraph(@RequestParam("period") String period) {
        Map response = new HashMap();
        if (period == null) {
            response = Utility.generateResponseMap(response, HttpStatus.PRECONDITION_FAILED.toString(), "Parameters cannot be null", null);
        } else {
            List<String> periodList = null;
            if (period.contains(",")) {
                periodList = Arrays.stream(period.split(",")).map(id -> id.trim()).collect(Collectors.toList());
            } else {
                periodList = Arrays.asList(period);
            }
            response = calculationDetailsService.getStatusWiseGraph(response, periodList);
        }
        return response;
    }

    /**
     * Description: Method to return data for billing status chart with count for last 12 months
     *
     * @return
     */
    @GetMapping("/getBillingStatusGraph/v1")
    public Map getBillingStatusGraph(@RequestParam("period") String period) {
        Map response = new HashMap();
        if (period == null) {
            response = Utility.generateResponseMap(response, HttpStatus.PRECONDITION_FAILED.toString(), "Parameters cannot be null", null);
        } else {
            List<String> periodList = null;
            if (period.contains(",")) {
                periodList = Arrays.stream(period.split(",")).map(id -> id.trim()).collect(Collectors.toList());
            } else {
                periodList = Arrays.asList(period);
            }
            response = calculationDetailsService.getBillingStatusGraph(response, periodList);
        }
        return response;
    }

    /**
     * Description: Method to return data for status wise pie chart with count for last month
     *
     * @return
     */
    @GetMapping("/getStatusWisePieLM/v1")
    public Map getStatusWisePieLM(@RequestParam("period") String period) {
        Map response = new HashMap();
        if (period == null) {
            response = Utility.generateResponseMap(response, HttpStatus.PRECONDITION_FAILED.toString(), "Parameters cannot be null", null);
        } else {
            List<String> periodList = null;
            if (period.contains(",")) {
                periodList = Arrays.stream(period.split(",")).map(id -> id.trim()).collect(Collectors.toList());
            } else {
                periodList = Arrays.asList(period);
            }
            response = calculationDetailsService.getStatusWisePieLM(response, periodList);
        }
        return response;
    }

    /**
     * Description: Method to return data for status wise pie chart with count for current month
     *
     * @return
     */
    @GetMapping("/getStatusWisePieCM/v1")
    public Map getStatusWisePieCM() {
        return calculationDetailsService.getStatusWisePieCM();
    }

    /**
     * Description: Method to return data for status wise graph with amount for current month
     *
     * @return
     */
    @GetMapping("/getStatusWiseGraph/v1")
    public Map getStatusWiseGraph() {
        return calculationDetailsService.getStatusWiseGraphAmountCM();
    }

    /**
     * Description: Method to return data for comparitive analysis graph with amount for last 12 month
     *
     * @return
     */
    @GetMapping("/getCompAnalysisGraph/v1")
    public Map getCompAnalysisGraph(@RequestParam("period") String period) {
        Map response = new HashMap();
        if (period == null) {
            response = Utility.generateResponseMap(response, HttpStatus.PRECONDITION_FAILED.toString(), "Parameters cannot be null", null);
        } else {
            List<String> periodList = null;
            if (period.contains(",")) {
                periodList = Arrays.stream(period.split(",")).map(id -> id.trim()).collect(Collectors.toList());
            } else {
                periodList = Arrays.asList(period);
            }
            response = calculationDetailsService.getCompAnalysisGraph(response,periodList);
        }
        return response;
    }

    @GetMapping("/getBillingByGardenTable/v1")
    public Map getBillingByGardenTable() {
        return calculationDetailsService.getBillingByGardenTable();
    }

    @GetMapping("/billingHead/v2/calcultionTrackerList")
    public Map calcultionTrackerList(@RequestHeader("Comp-Key") Long compKey,
                                     @RequestParam("groupBy") String groupBy,
                                     @RequestParam("period") String period) {
        Map response = new HashMap();
        if (groupBy == null || period == null) {
            response = Utility.generateResponseMap(response, HttpStatus.PRECONDITION_FAILED.toString(), "Parameters cannot be null", null);
        } else {
            List<String> periodList = null;
            if (period.contains(",")) {
                periodList = Arrays.stream(period.split(",")).map(id -> id.trim()).collect(Collectors.toList());
            } else {
                periodList = Arrays.asList(period);
            }
            response = calculationTrackerService.getCalculationTrackerList(response, groupBy, periodList);
        }

        return response;

    }

    @GetMapping("/v1/getBillingPeriods")
    public Map getBillingPeriods(@RequestHeader("Comp-Key") Long compKey) {

        return calculationTrackerService.getBillingPeriodList();

    }

    @GetMapping("/billingHead/v2/calcultionTrackerListByUserId")
    public Map calcultionTrackerList(@RequestHeader("Comp-Key") Long compKey,
                                     @RequestParam("userId") Long userId,
                                     @RequestParam("period") String period) {
        Map response = new HashMap();
        if (userId == null || period == null) {
            response = Utility.generateResponseMap(response, HttpStatus.PRECONDITION_FAILED.toString(), "Parameters cannot be null", null);
        } else {
            List<String> periodList = null;
            if (period.contains(",")) {
                periodList = Arrays.stream(period.split(",")).map(id -> id.trim()).collect(Collectors.toList());
            } else {
                periodList = Arrays.asList(period);
            }
            response = calculationTrackerService.getCalculationTrackerListByUserId(response, periodList, userId);
        }
        return response;
    }

    @PostMapping("/addManualCredits")
    public BaseResponse addManualCredits(@RequestParam(value = "credits") String credits) {
        return calculationTrackerService.addManualCredits(credits);
    }
}
