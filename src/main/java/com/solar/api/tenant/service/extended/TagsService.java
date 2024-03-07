package com.solar.api.tenant.service.extended;

import com.solar.api.tenant.model.extended.Tags;

import java.util.List;

public interface TagsService {

    Tags save(Tags address);

    Tags update(Tags address);

    Tags findById(Long id);

    List<Tags> findAll();

    void delete(Long id);

    void deleteAll();
}
