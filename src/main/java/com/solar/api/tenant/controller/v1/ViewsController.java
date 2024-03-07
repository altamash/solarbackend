package com.solar.api.tenant.controller.v1;

import com.solar.api.tenant.model.billing.BillingInvoice.InvoiceCustomerV;
import com.solar.api.tenant.service.ViewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("ViewsController")
@RequestMapping(value = "/view")
public class ViewsController {

    @Value("${app.storage.blobService}")
    private String blobService;
    @Value("${app.storage.container}")
    private String storageContainer;
    @Value("${app.storage.tenantDirectory}")
    private String tenant;

    @Autowired
    private ViewsService viewsService;

    @GetMapping("/billing/invoiceCustomer/{accountId}/{subscriptionId}")
    public List<InvoiceCustomerV> getCustomerInvoiceData(@PathVariable Long accountId, @PathVariable Long subscriptionId, @RequestHeader("Comp-Key") Long compKey) {

        String invoiceUrl = blobService + "/" + storageContainer + "/" + tenant + "/" + compKey;
        List<InvoiceCustomerV> invoiceCustomerVS = viewsService.getCustomerInvoiceData(accountId, subscriptionId);

        invoiceCustomerVS.forEach(ic -> {
            if(ic.getInvoiceUrl() != null) {
                String temp = invoiceUrl + ic.getInvoiceUrl();
                ic.setInvoiceUrl(temp);
            }
        });

        return invoiceCustomerVS;
    }

}
