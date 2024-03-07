package com.solar.api.tenant.repository;

import com.solar.api.tenant.mapper.payment.info.PaymentTransactionSummaryTemplate;
import com.solar.api.tenant.model.billing.BillingInvoice.BillingInvoice;
import com.solar.api.tenant.model.payment.billing.PaymentTransactionHead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaymentTransactionHeadRepository extends JpaRepository<PaymentTransactionHead, Long> {
    PaymentTransactionHead findByInvoice(BillingInvoice invoice);

    @Query("SELECT p FROM PaymentTransactionHead p  LEFT JOIN FETCH p.paymentTransactionDetails where p.invoice = :invoice")
    PaymentTransactionHead findByInvoiceFetchDetail(BillingInvoice invoice);

    List<PaymentTransactionHead> findByPaymentIdIn(List<Long> PaymentIdList);

    List<PaymentTransactionHead> findByPaymentCodeAndInvoice(String paymentCode, BillingInvoice invoice);

    @Query("SELECT (bh.amount - pth.net) as balance" +
            " from BillingHead bh, PaymentTransactionHead pth" +
            " where bh.id = :billingHeadId" +
            " and pth.invoice = bh.invoice")
    Double getPaymentBalance(Long billingHeadId);

    @Query("SELECT new com.solar.api.tenant.mapper.payment.info.PaymentTransactionSummaryTemplate(bh.userAccountId," +
            " u.firstName, u.lastName, bh.subscriptionId, bh.invoice.id, bh.amount, pth.net, (bh.amount - pth.net)," +
            " (bh.amount - pth.net))" +
            " from BillingHead bh, User u, PaymentTransactionHead pth" +
            " where bh.id = :billingHeadId" +
            " and u.acctId = bh.userAccountId" +
            " and pth.invoice.id = bh.invoice.id")
    PaymentTransactionSummaryTemplate getPaymentTransactionSummary(Long billingHeadId);
}
