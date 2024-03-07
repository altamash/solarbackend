package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.payment.billing.PaymentDetailsView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentDetailsViewRepository extends JpaRepository<PaymentDetailsView, String>,
        PaymentDetailsViewRepositoryCustom {
}
