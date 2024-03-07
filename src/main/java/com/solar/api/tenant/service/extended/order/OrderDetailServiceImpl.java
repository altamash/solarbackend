package com.solar.api.tenant.service.extended.order;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.extended.order.OrderDetail;
import com.solar.api.tenant.repository.order.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    @Autowired
    private OrderDetailRepository repository;

    @Override
    public OrderDetail save(OrderDetail orderDetail) {
        return repository.save(orderDetail);
    }

    @Override
    public OrderDetail update(OrderDetail orderDetail) {
        return repository.save(orderDetail);
    }

    @Override
    public OrderDetail findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(OrderDetail.class, id));
    }

    @Override
    public List<OrderDetail> findAll() {
        return repository.findAll();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
