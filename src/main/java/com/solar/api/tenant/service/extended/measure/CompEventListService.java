package com.solar.api.tenant.service.extended.measure;

import com.solar.api.tenant.model.extended.measure.CompEventList;

import java.util.List;

public interface CompEventListService {

    CompEventList save(CompEventList compEventList);

    CompEventList update(CompEventList compEventList);

    CompEventList findById(Long id);

    List<CompEventList> findAll();

    void delete(Long id);

    void deleteAll();
}
