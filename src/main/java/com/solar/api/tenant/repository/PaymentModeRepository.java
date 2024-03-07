package com.solar.api.tenant.repository;

import com.solar.api.tenant.mapper.payment.info.PaymentModeDTO;
import com.solar.api.tenant.model.payment.info.PaymentMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentModeRepository extends JpaRepository<PaymentMode, Long> {
    @Query("select new com.solar.api.tenant.mapper.payment.info.PaymentModeDTO(" +
            "pm.id as id , pm.paymentMode as paymentMode, " +
            "case when pm.reconcileIndicator = 1 then 'Y' else 'N' end as reconcileIndicator, " +
            "case when pm.reversalIndicator = 1 then 'Y' else 'N' end as reversalIndicator) " +
            "from PaymentMode pm" )
    List<PaymentModeDTO> listPaymentMode();

    Optional<PaymentMode> findById(Long paymentModeId);

    Optional<PaymentMode> findByPaymentMode(String paymentMode);


}
