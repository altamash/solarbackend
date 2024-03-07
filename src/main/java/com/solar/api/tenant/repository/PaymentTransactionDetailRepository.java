package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.payment.billing.PaymentTransactionDetail;
import com.solar.api.tenant.model.payment.billing.PaymentTransactionHead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentTransactionDetailRepository extends JpaRepository<PaymentTransactionDetail, Long> {
    List<PaymentTransactionDetail> findByPaymentTransactionHead(PaymentTransactionHead paymentTransactionHead);
    Long findByReferenceId(String referenceId);
    PaymentTransactionDetail findByPayDetId(Long id);
    @Query(value = "SELECT MAX(ptd.batch_no) FROM payment_transaction_detail ptd where ptd.batch_no like :subscriptionTypeAndDate%",
            nativeQuery =true)
    String getLastBatchNo(@Param("subscriptionTypeAndDate") String subscriptionTypeAndDate);

}
