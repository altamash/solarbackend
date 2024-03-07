package com.solar.api.tenant.repository.order;

import com.solar.api.tenant.model.extended.order.OrderHead;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderHeadRepository extends JpaRepository<OrderHead, Long> {
}
