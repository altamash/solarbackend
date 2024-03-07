package com.solar.api.tenant.controller.customersupport;

import com.solar.api.saas.module.com.solar.batch.service.StageMonitorService;
import com.solar.api.tenant.model.user.EUserStatus;
import com.solar.api.tenant.service.process.pvmonitor.ExtDataStageDefinitionService;
import io.swagger.annotations.ApiOperation;
import org.hibernate.loader.entity.CacheEntityLoaderHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("CustomerSupportController")
@RequestMapping(value = "/customerSupport")
public class CustomerSupportController {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    ExtDataStageDefinitionService extDataStageDefinitionService;

    @ApiOperation(value = "Get Customers Data Along with Subscription")
    @GetMapping("/findAllCustomersWithSubscriptions")
    public ResponseEntity<?> findAllCustomersWithSubscriptions() {
      return extDataStageDefinitionService.findAllCustomersWithSubscriptions(EUserStatus.ACTIVE.toString());
    }

    @ApiOperation(value = "Get Variants/gardens Data By product Id")
    @GetMapping("/findAllVariantsByProductId")
    public ResponseEntity<?> findAllVariantsByProductId(@RequestParam("productId") String productId) {
        return extDataStageDefinitionService.findAllVariantsByProductId(productId);
    }

}
