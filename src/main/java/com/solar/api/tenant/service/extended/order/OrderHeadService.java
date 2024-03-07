package com.solar.api.tenant.service.extended.order;

import com.solar.api.tenant.model.extended.order.OrderHead;

import java.util.List;

public interface OrderHeadService {

    OrderHead save(OrderHead orderHead);

    OrderHead update(OrderHead orderHead);

    OrderHead findById(Long id);

    List<OrderHead> findAll();

    void delete(Long id);

    void deleteAll();
}
