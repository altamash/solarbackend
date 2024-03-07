package com.solar.api.saas.controller.v1.widget;

import com.solar.api.saas.model.chart.views.BillingDetailView;
import com.solar.api.saas.repository.BillingDetailViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("BillingDetailViewController")
@RequestMapping(value = "/view")
public class BillingDetailViewController {

    @Autowired
    private BillingDetailViewRepository billingDetailViewRepository;

    @GetMapping("getBV/{billStatus}")
    public List<BillingDetailView> findByStatus(@PathVariable String billStatus) {
        return billingDetailViewRepository.findByBillStatus(billStatus);
    }

    @GetMapping("lifeTimeSum")
    public Double findLifeTimeSum(@RequestParam("billing_code") String billingCode,
                                  @RequestParam("account_id") Long accountId,
                                  @RequestParam("subscription_id") Long subscriptionId) {
        return billingDetailViewRepository.lifeTimeSum(billingCode, accountId, subscriptionId);
    }
}
