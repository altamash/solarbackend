package com.solar.api.tenant.controller.v1.paymentManagement;


import com.solar.api.helper.Utility;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.mapper.billing.paymentManagement.StripePaymentIntentDTO;
import com.solar.api.tenant.mapper.extended.project.EmployeeManagementDTO;
import com.solar.api.tenant.repository.PaymentTransactionDetailRepository;
import com.solar.api.tenant.service.paymentManagement.PaymentManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("PaymentManagementController")
@RequestMapping(value = "/paymentManagement")
public class PaymentManagementController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private PaymentManagementService paymentManagementService;
    @Autowired
    private PaymentTransactionDetailRepository paymentTransactionDetailRepository;

    /**
     * Description: Api to return data for customer payment dashboard
     * Created by: Ibtehaj
     * Created at: 2/28/2023
     *
     * @return
     */
    @GetMapping("/v1/getCustomerPaymentDashboard")
    public Map findAllEmployee(@RequestHeader("Comp-Key") Long compKey,
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
            response = paymentManagementService.getCustomerPaymentDashboard(response, groupBy, periodList);
        }
        return response;
    }

    /**
     * Description: Api to return data for customer type graph
     * Created by: Ibtehaj
     * Created at: 2/28/2023
     *
     * @return
     */
    @GetMapping("/v1/getCustomerTypeGraph")
    public Map getCustomerTypeGraph() {
        return paymentManagementService.getCustomerTypeGraph();
    }

    /**
     * Description: Api to return data for new customer graph
     * Created by: Ibtehaj
     * Created at: 2/28/2023
     *
     * @return
     */
    @GetMapping("/v1/getNewCustomerGraph")
    public Map getNewCustomerGraph() {
        return paymentManagementService.getNewCustomerGraph();
    }

    /**
     * Description: Api to return data for customer project graph
     * Created by: Ibtehaj
     * Created at: 03/03/2023
     *
     * @return
     */
    @GetMapping("/v1/getCustomerProjectGraph")
    public Map getCustomerProjectGraph() {
        return paymentManagementService.getCustomersByProjectGraph();
    }

    /**
     * Description: Api to create payment intent and return client secret
     * Created by: Ibtehaj
     * Created at: 03/03/2023
     *
     * @return
     */
    @PostMapping("/v1/createPaymentIntent")
    public Map createPaymentIntent(@RequestBody StripePaymentIntentDTO stripePaymentIntentDTO,
                                   @RequestHeader("Comp-Key") Long compKey) {
        return paymentManagementService.generatePaymentIntent(stripePaymentIntentDTO, compKey);
    }

    /**
     * Description: Api to process response of payment
     * Created by: Ibtehaj
     * Created at: 03/06/2023
     *
     * @return
     */
    @PostMapping("/v1/paymentResponse")
    public Map paymentResponse(@RequestBody String paymentIntentId,
                               @RequestHeader("Comp-Key") Long compKey) {
        return paymentManagementService.paymentResponse(paymentIntentId, compKey);
    }
    @GetMapping("/decodeBase64String")
    public String decodeBase64String(@RequestParam String encodedString) {
        return paymentManagementService.decodeBase64String(encodedString);
    }
    @PostMapping("/encodeStripeKey")
    public BaseResponse encodeStripeKey(@RequestParam String stripeKey) {
        return paymentManagementService.encodeStripeKey(stripeKey);
    }
}
