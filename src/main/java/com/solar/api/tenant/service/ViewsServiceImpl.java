package com.solar.api.tenant.service;

import com.solar.api.tenant.model.billing.BillingInvoice.InvoiceCustomerV;
import com.solar.api.tenant.repository.InvoiceCustomerViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormatSymbols;
import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class ViewsServiceImpl implements ViewsService {

    @Autowired
    private InvoiceCustomerViewRepository invoiceCustomerViewRepository;

    @Override
    public List<InvoiceCustomerV> getCustomerInvoiceData(Long accountId, Long customerSubscriptionId) {
        List<InvoiceCustomerV> list;
        if (customerSubscriptionId == -1 && accountId == -1) {
            list = invoiceCustomerViewRepository.findAll();
        } else if (accountId != -1 && customerSubscriptionId == -1) {
            list = invoiceCustomerViewRepository.findByAccountId(accountId);
        } else {
            list = invoiceCustomerViewRepository.findByAccountIdAndCustomerSubscriptionId(accountId,
                    customerSubscriptionId);
        }
        for (InvoiceCustomerV invoiceCustomerV : list) {
            invoiceCustomerV.setBillingMonthYear(new DateFormatSymbols().getShortMonths()[Integer.parseInt(invoiceCustomerV.getBillingMonthYear().split("-")[0]) - 1] +
                    " " + invoiceCustomerV.getBillingMonthYear().split("-")[1]);
        }
        return list;
    }
}
