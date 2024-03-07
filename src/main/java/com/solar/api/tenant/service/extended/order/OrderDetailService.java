package com.solar.api.tenant.service.extended.order;

import com.solar.api.tenant.model.extended.order.OrderDetail;

import java.util.List;

public interface OrderDetailService {

    OrderDetail save(OrderDetail orderDetail);

    OrderDetail update(OrderDetail orderDetail);

    OrderDetail findById(Long id);

    List<OrderDetail> findAll();

    void delete(Long id);

    void deleteAll();
}
