package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.payment.billing.PaymentDetailsView;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentDetailsViewRepositoryCustom {

    List<PaymentDetailsView> getByAccount(@Param("accountId") List<String> accountId,
                                          @Param("billingMonthYear") List<String> billingMonthYear, @Param(
            "billStatus") List<String> billStatus,
                                          @Param("source") List<String> source);

    List<PaymentDetailsView> getBySubscriptionType(@Param("subscriptionType") List<String> subscriptionType, @Param(
            "billingMonthYear") List<String> billingMonthYear, @Param("billStatus") List<String> billStatus, @Param(
            "source") List<String> source);

    List<PaymentDetailsView> getBySubscriptionId(@Param("subscriptionId") List<String> subscriptionId, @Param(
            "billingMonthYear") List<String> billingMonthYear, @Param("billStatus") List<String> billStatus, @Param(
            "source") List<String> source);

    List<PaymentDetailsView> getByGardenSRC(@Param("gardenSRC") List<String> gardenSRC,
                                            @Param("billingMonthYear") List<String> billingMonthYear, @Param(
            "billStatus") List<String> billStatus,
                                            @Param("source") List<String> source);

    List<PaymentDetailsView> getByPremiseNumber(@Param("premiseNumber") List<String> premiseNumber, @Param(
            "billingMonthYear") List<String> billingMonthYear, @Param("billStatus") List<String> billStatus, @Param(
            "source") List<String> source);

    List<PaymentDetailsView> getByInvoiceId(@Param("invoiceId") List<String> invoiceId,
                                            @Param("billingMonthYear") List<String> billingMonthYear, @Param(
            "billStatus") List<String> billStatus,
                                            @Param("source") List<String> source);

    List<PaymentDetailsView> getAll();
}
