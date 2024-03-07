package com.solar.api.tenant.repository.order;

import com.solar.api.tenant.model.extended.order.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}
