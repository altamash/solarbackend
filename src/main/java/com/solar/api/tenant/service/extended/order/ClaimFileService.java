package com.solar.api.tenant.service.extended.order;

import com.solar.api.tenant.model.extended.order.ClaimFile;

import java.util.List;

public interface ClaimFileService {

    ClaimFile save(ClaimFile claimFile);

    ClaimFile update(ClaimFile claimFile);

    ClaimFile findById(Long id);

    List<ClaimFile> findAll();

    void delete(Long id);

    void deleteAll();
}
