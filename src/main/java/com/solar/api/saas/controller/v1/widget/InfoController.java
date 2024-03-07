package com.solar.api.saas.controller.v1.widget;

import com.solar.api.saas.model.chart.views.BillingDetailView;
import com.solar.api.saas.repository.BillingDetailViewRepository;
import com.solar.api.saas.service.widget.InfoService;
import com.solar.api.tenant.mapper.subscription.SubscriptionInfoTemplate;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.repository.CustomerSubscriptionRepository;
import com.solar.api.tenant.service.SubscriptionService;
import com.solar.api.tenant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("InfoController")
@RequestMapping(value = "/widget/info")
public class InfoController {

    @Autowired
    private UserService userService;
    @Autowired
    private CustomerSubscriptionRepository subscriptionRepository;
    @Autowired
    private InfoService infoService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    BillingDetailViewRepository billingDetailViewRepository;

    @GetMapping
    public List<SubscriptionInfoTemplate> getSubscriptionInfos(Authentication authentication, @RequestParam(
            "account_id") Long accountId) {
        User requestUser = userService.findById(accountId);
        return subscriptionService.findActiveSubscriptionInfos(requestUser);
    }

    @GetMapping("getBV/{billStatus}")
    public List<BillingDetailView> findByStatus(@PathVariable String billStatus) {
        return billingDetailViewRepository.findByBillStatus(billStatus);
    }

    @GetMapping("lifeTimeSum")
    public Double findLifeTimeSum(@RequestParam("billing_code") String billingCode,
                                  @RequestParam("account_id") Long accountId,
                                  @RequestParam("subscription_id") Long subscriptionId) {
        return infoService.findLifeTimeSum(billingCode, accountId, subscriptionId);
    }

    @GetMapping("findNPV")
    public Double findNPV(@RequestParam("account_id") Long accountId,
                          @RequestParam("subscription_id") Long subscriptionId) {
        Map<String, Object> map = infoService.calculateNPV(accountId, subscriptionId);
        if(map != null){
            return Double.valueOf(String.valueOf(map.get("NPV")));
        }
        return null;
    }

//    @GetMapping("findAllNPV")
//    public List<Map<String, Object>> findAllNPV() {
//        List<Map<String, Object>> mappings = new ArrayList<>();
//        infoService.calculateNPV();
//        return mappings;
//    }

    @GetMapping("lifeTimeSumBatch")
    public void lifeTimeSumBatch() {
        infoService.lifeTimeSumBatch();
    }
}
