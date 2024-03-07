package com.solar.api.saas.service;

import java.util.List;

public interface CrudService <T> {

    T save(T obj);

    T update(T obj);

    String delete(Long id);

    List<T> getAll();

    T getById(Long id);

}
