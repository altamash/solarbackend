package com.solar.api.tenant.service.extended.order;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.extended.order.OrderHead;
import com.solar.api.tenant.repository.order.OrderHeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderHeadServiceImpl implements OrderHeadService {

    @Autowired
    private OrderHeadRepository repository;

    @Override
    public OrderHead save(OrderHead orderHead) {
        return repository.save(orderHead);
    }

    @Override
    public OrderHead update(OrderHead orderHead) {
        return repository.save(orderHead);
    }

    @Override
    public OrderHead findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(OrderHead.class, id));
    }

    @Override
    public List<OrderHead> findAll() {
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
