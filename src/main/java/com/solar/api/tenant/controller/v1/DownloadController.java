package com.solar.api.tenant.controller.v1;

import com.solar.api.saas.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

import static com.solar.api.saas.model.SaasSchema.Template.Billing.CUSTOMER_PAYMENTS;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("DownloadController")
@RequestMapping(value = "/download")
public class DownloadController {

    @Autowired
    private StorageService storageService;
    @Value("${app.storage.container}")
    private String storageContainer;

    @GetMapping("/paymentTemplate")
    public String getPaymentTemplate() throws UnsupportedEncodingException {
        return storageService.getBlobUrl(storageContainer, CUSTOMER_PAYMENTS.getDirectoryReference(),
                CUSTOMER_PAYMENTS.getFileName());
    }
}
