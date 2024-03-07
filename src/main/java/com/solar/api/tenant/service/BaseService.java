package com.solar.api.tenant.service;

import com.solar.api.exception.NotFoundException;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface BaseService<T, O> {

    T save(T t);
    T update(T t);
    Optional<T> findById(Long id);
    List<T> findAll();
    boolean delete(Long id);
    void deleteAll();

    default O findById(Long id, Class clazz, CrudRepository repository) {
        Optional<O> o = repository.findById(id);
        if (!o.isPresent()) {
            throw new NotFoundException(clazz, id);
        }
        return o.get();
    }
}
